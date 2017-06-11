package com.duke.self;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class HttpClient {


    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private PoolingHttpClientConnectionManager cm = null;
    private RequestConfig requestConfig = null;
    private int timeout = 1000;

    CloseableHttpClient httpClient = null;

    public static BlockingQueue<CloseableHttpResponse> queue = new ArrayBlockingQueue<CloseableHttpResponse>(100000);


    public void initClientParam() {
        if (cm == null)
            cm = new PoolingHttpClientConnectionManager();
        // 将最大连接数增加到200
        cm.setMaxTotal(200);
        // 将每个路由基础的连接增加到20
        cm.setDefaultMaxPerRoute(20);
        // Increase max total connection to 200
        cm.setMaxTotal(200);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);

//		HttpHost localhost = new HttpHost("www.yeetrack.com", 80);
//		cm.setMaxPerRoute(new HttpRoute(localhost), 50);

        requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();

    }


    public CloseableHttpClient getHttpClient() {

        if (httpClient == null) {
            httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .build();
        }
        //CloseableHttpClient
        return httpClient;
    }


    private static URI getURI(String url) throws MalformedURLException, URISyntaxException {
        if (url != null && !url.startsWith("http")) {
            url = "http://" + url;
        }
        URL tmp = new URL(url);
        URI uri = new URI(tmp.getProtocol(), tmp.getUserInfo(), tmp.getHost(), tmp.getPort(), tmp.getPath(), tmp.getQuery(), null);
        return uri;
    }

    public String getRealUrl(String url) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        HttpContext httpContext = new BasicHttpContext();
        try {
            HttpGet httpGet = new HttpGet(getURI(url));
            //将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
            response = httpClient.execute(httpGet, httpContext);
            //获取重定向之后的主机地址信息,即"http://127.0.0.1:8088"
            HttpHost targetHost = (HttpHost) httpContext.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
            //获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"
            HttpUriRequest realRequest = (HttpUriRequest) httpContext.getAttribute(HttpCoreContext.HTTP_REQUEST);
            //            System.out.println("主机地址:" + targetHost);
            //            System.out.println("URI信息:" + realRequest.getURI());
            return targetHost.toString() + realRequest.getURI().toString();
            //            HttpEntity entity = response.getEntity();
            //            if(null != entity){
            //                System.out.println("响应内容:" + EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset()));
            //                EntityUtils.consume(entity);
            //            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    private static Header[] assemblyHeader(Map<String, String> headers) {
        Header[] allHeader = new BasicHeader[headers.size()];
        int i = 0;
        for (String str : headers.keySet()) {
            allHeader[i] = new BasicHeader(str, headers.get(str));
            i++;
        }
        return allHeader;
    }

    public String executePost(String url, Map<String, String> params, Map<String, String> headers, String charSet) {
        CloseableHttpClient httpclient = getHttpClient();
        CloseableHttpResponse response = null;

        try {
            HttpPost post = new HttpPost(getURI(url));
            if (headers != null) {
                post.setHeaders(assemblyHeader(headers));
            }

            //  设置HTTP POST请求参数必须用NameValuePair对象
            List<NameValuePair> lst = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                lst.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            //  设置HTTP POST请求参数
            UrlEncodedFormEntity posEntity = new UrlEncodedFormEntity(lst, charSet);
            if (logger.isDebugEnabled()) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                posEntity.writeTo(os);
                logger.debug("url:{},content:{}", url, os.toString());
            }
            post.setEntity(posEntity);
            post.setConfig(requestConfig);

            response = httpclient.execute(post);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, charSet);
            return content;
        } catch (Exception e) {
            logger.error(e.getMessage() + " when url is:" + url, e);
            return null;
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public String executePostWithCookie(String url, Map<String, String> params, Map<String, String> headers, String charSet, String cookie) {
        CloseableHttpClient httpclient = getHttpClient();
        CloseableHttpResponse response = null;

        try {
            //HttpPost post = new HttpPost(getURI(url));
            HttpGet post = new HttpGet(getURI(url));
            if (headers != null) {
                post.setHeaders(assemblyHeader(headers));
            }
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(cookie)) {
                post.setHeader("Cookie", cookie);
            }

            //post.setHeader("Cache-Control", "no-cache");
//            post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//            post.setHeader("Accept-Encoding", "gzip, deflate");
//            post.setHeader("Connection", "keep-alive");
//            post.setHeader("Content-Length", "0");
//            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
//            post.setHeader("Origin", "http://ptcms.csdn.net");
//            post.setHeader("X-Requested-With", "XMLHttpRequest");
            //红包系统
            post.setHeader("Host", "mp.weixin.qq.com");
            post.setHeader("Connection", "keep-alive");
            //post.setHeader("X-Requested-With", "");
            //post.setHeader("platform", "iOS");
            post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            //post.setHeader("Origin", "http://m.hongbao.link.lianjia.com");
            post.setHeader("Referer", "https://mp.weixin.qq.com/misc/webpageanalysis?action=listintfstat&token=756669216&lang=zh_CN");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.8");

            //  设置HTTP POST请求参数必须用NameValuePair对象
            List<NameValuePair> lst = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                lst.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            //  设置HTTP POST请求参数
            //UrlEncodedFormEntity posEntity = new UrlEncodedFormEntity(lst, charSet);
            //post.setEntity(posEntity);
            post.setConfig(requestConfig);
            long t1 = System.currentTimeMillis();
            response = httpclient.execute(post);
            long t2 = System.currentTimeMillis();
            System.out.println("方法调用时间:" + (t2 - t1));
            if (response != null) {
                queue.put(response);
            }
//            return "";
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, charSet);
            return content;
        } catch (Exception e) {
            logger.error(e.getMessage() + " when url is:" + url, e);
            return null;
        } finally {

        }
    }

    /**
     * 京东黑心客服，年后一定要找个时间给他来一段
     */
    public String executeSendPostWithCookie(String url, Map<String, String> params, Map<String, String> headers, String charSet, String cookie) {
        CloseableHttpClient httpclient = getHttpClient();
        CloseableHttpResponse response = null;

        try {
            HttpPost post = new HttpPost(getURI(url));
            if (headers != null) {
                post.setHeaders(assemblyHeader(headers));
            }
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(cookie)) {
                post.setHeader("Cookie", cookie);
            }

            post.setHeader("Cache-Control", "no-cache");
            post.setHeader("Accept", "*/*");
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Connection", "keep-alive");
//            post.setHeader("Content-Length", "82");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            post.setHeader("Host", "memberprod.alipay.com");
            post.setHeader("Origin", "https://memberprod.alipay.com");
            post.setHeader("X-Requested-With", "XMLHttpRequest");
            post.setHeader("Pragma", "no-cache");
            post.setHeader("Referer", "https://memberprod.alipay.com/account/reg/index.htm");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");
            //post.setHeader("Referer", "https://memberprod.alipay.com/account/reg/index.htm");


            //  设置HTTP POST请求参数必须用NameValuePair对象
            List<NameValuePair> lst = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                lst.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            //  设置HTTP POST请求参数
            UrlEncodedFormEntity posEntity = new UrlEncodedFormEntity(lst, charSet);
            if (logger.isDebugEnabled()) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                posEntity.writeTo(os);
                logger.debug("url:{},content:{}", url, os.toString());
            }
            post.setEntity(posEntity);
            post.setConfig(requestConfig);

            response = httpclient.execute(post);
            return response.getStatusLine().getStatusCode() + "-" + response.getStatusLine().getReasonPhrase();
//            HttpEntity entity = response.getEntity();
//            String content = EntityUtils.toString(entity, charSet);
//            return content;
        } catch (Exception e) {
            logger.error(e.getMessage() + " when url is:" + url, e);
            return null;
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }


    public String executePost(String url, Map<String, String> params, String charSet) {
        return executePost(url, params, null, charSet);
    }

    public String executeGet(String url, String charSet) {
        CloseableHttpClient httpclient = getHttpClient();
        CloseableHttpResponse response = null;

        try {
            HttpGet getHtml = new HttpGet(getURI(url));
            getHtml.setConfig(requestConfig);
            //			String html = httpclient.execute(getHtml, responseHandler);
            response = httpclient.execute(getHtml);
            HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity, charSet);
            return html;
        } catch (Exception e) {
            logger.error(e.getMessage() + " when url is:" + url);
            return null;
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }


    public String executeGetHouseInfo(String url, String charSet, String cookie) {
        CloseableHttpClient httpclient = getHttpClient();
        CloseableHttpResponse response = null;

        try {
            HttpGet getHtml = new HttpGet(getURI(url));
            getHtml.setConfig(requestConfig);
            //getHtml.setHeader("Cookie", "lianjia_uuid=a58a6ea1-03ce-4591-9a51-d8207b954bb7; _jzqa=1.360641581121633500.1473069915.1473069915.1473069915.1; lianjia_token=2.000d7e7a7277f6bae21cd353438ab3e992; Hm_lvt_efa595b768cc9dc7d7f9823368e795f1=1479464947,1480226521,1481683510; _smt_uid=575253a0.486ffec7; _ga=GA1.2.469462526.1465013155; _lianjia_link_snid=1000000020049906%3Ajingjiren%3A%E7%BB%8F%E7%BA%AA%E4%BA%BA%3AA12P64%3A%E5%A5%A5%E5%8C%97%E4%B8%AD%E5%BF%83%E5%8D%97%E5%8C%BA%E5%BA%97A%E5%BA%97; _UC_agent=1; lianjia_ssid=3786e966-250c-8ace-2f51-2d76ec1b2136; BUSINESSJSESSIONID=8c51c0b2-ecce-4df6-9ddf-6e4f9f1d503e");
            //getHtml.setHeader("Cookie", "lianjia_uuid=a58a6ea1-03ce-4591-9a51-d8207b954bb7; _jzqa=1.360641581121633500.1473069915.1473069915.1473069915.1; lianjia_token=2.000d7e7a7277f6bae21cd353438ab3e992; Hm_lvt_efa595b768cc9dc7d7f9823368e795f1=1479464947,1480226521,1481683510; _smt_uid=575253a0.486ffec7; _ga=GA1.2.469462526.1465013155; HOUSEJSESSIONID=d3d80401-1de9-45c9-b274-b93ec36cd9ab; _UC_agent=1; lianjia_ssid=3786e966-250c-8ace-2f51-2d76ec1b2136; _lianjia_link_snid=1000000020096554%3Ajingjiren%3A%E7%BB%8F%E7%BA%AA%E4%BA%BA%3AN11234%3A%E6%A2%85%E8%8A%B1%E5%B1%B1%E5%BA%84%E5%BA%97A%E7%BB%84");
            //getHtml.setHeader("Cookie", "lianjia_uuid=a58a6ea1-03ce-4591-9a51-d8207b954bb7; _jzqa=1.360641581121633500.1473069915.1473069915.1473069915.1; lianjia_token=2.000d7e7a7277f6bae21cd353438ab3e992; Hm_lvt_efa595b768cc9dc7d7f9823368e795f1=1479464947,1480226521,1481683510; _smt_uid=575253a0.486ffec7; _ga=GA1.2.469462526.1465013155; _UC_agent=1; lianjia_ssid=3786e966-250c-8ace-2f51-2d76ec1b2136; _lianjia_link_snid=1000000010022794%3Ajingjiren%3A%E7%BB%8F%E7%BA%AA%E4%BA%BA%3AA11863%3A%E9%87%91%E6%B3%B0%E4%B8%BD%E6%B9%BE%E5%BA%97A%E5%BA%97; _se_customer_snid=E59D2B489F224B4E8A5E1665A372D591; ke-link-lianjia=0f728747b6781c43ba2cefa98b52168c\n" +
            //"Host:ke.link.lianjia.com");
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(cookie)) {
                getHtml.setHeader("Cookie", cookie);
            }
            getHtml.setHeader("Cache-Control", "no-cache");
            getHtml.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            getHtml.setHeader("Accept-Encoding", "gzip, deflate, sdch");
            getHtml.setHeader("Connection", "keep-alive");
            //getHtml.setHeader("Upgrade-Insecure-Requests", "1");
            getHtml.setHeader("X-Requested-With", "XMLHttpRequest");
            response = httpclient.execute(getHtml);
            HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity, charSet);
            return html;
        } catch (Exception e) {
            logger.error(e.getMessage() + " when url is:" + url);
            return null;
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    //去掉所有ssl验证
    public String postBody(String url, String postBody, Map<String, String> header, String charsetName) {
        CloseableHttpClient httpclient = getHttpClient();
        CloseableHttpResponse response = null;
        String content = "";

        try {
            HttpPost httppost = new HttpPost(getURI(url));
            if (header != null && !header.isEmpty()) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    httppost.setHeader(entry.getKey(), entry.getValue());
                }
            }

            if (postBody == null) {
                postBody = "";
            }
            HttpEntity postEntity = new StringEntity(postBody, charsetName);
            httppost.setEntity(postEntity);
            httppost.setConfig(requestConfig);

            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            StringBuilder sb = new StringBuilder();
            BufferedReader red = new BufferedReader(new InputStreamReader(entity.getContent(), charsetName));
            String line;
            while ((line = red.readLine()) != null) {
                sb.append(line + "\n");
            }
            EntityUtils.consume(entity);
            content = sb.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            //释放连接
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return content;
    }


    public int getTimeout() {
        return timeout;
    }


    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 下载图片
     *
     * @param picPath      图片远程地址
     * @param localPath    照片本地路径
     * @param localPicName 本地图片名称,可以为空,如是如此则会将远程服务器的图片名称作为本地图片名称
     * @throws IOException
     * @throws ClientProtocolException
     */
    public void downLoadPicture(String picPath, String localPath, String localPicName) throws ClientProtocolException, IOException {
        CloseableHttpClient client = getHttpClient();
        HttpGet get = new HttpGet(picPath);
        CloseableHttpResponse response = client.execute(get);
        String picName = "";
        if (StringUtils.isNotEmpty(localPicName)) {
            picName = localPicName;
        } else {
            //获取远程服务器上面的图片名称作为本地图片名称
            int index = picPath.lastIndexOf("/");
            picName = picPath.substring(index);
        }
        if (localPath.endsWith("/") == false) {
            localPath += "/";
        }

        File localFile = new File(localPath.concat(picName));
        FileOutputStream output = new FileOutputStream(localFile);
        output.write(EntityUtils.toByteArray(response.getEntity()));
        output.flush();
        output.close();
    }

    public void downLoadRegisterdelPicture(String remotePath, String localPath, String rename) throws ClientProtocolException, IOException {

        String[] pics = remotePath.split(",");
        int i = 1;
        for (String pic : pics) {
            String picPath = pic.concat(".1000x.jpg");
            downLoadPicture(picPath, localPath, rename + "-" + i + ".jpg");
            i++;
        }
    }


    public static void main(String[] args) throws ClientProtocolException, IOException {
        final HttpClient client = new HttpClient();
        client.initClientParam();
/*        final Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("username", "xiaohanluo");
        paraMap.put("un", "wsqduanqiaocanxue");
        Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        for(int i = 0; i < 1000000; i++){
            executor.execute(new Runnable() {
                public void run() {
                    String ret = client.executePost("https://zhidao.baidu.com/question/2012213626400207268.html", paraMap, null);
                    System.out.println(ret);
                }
            });
        }*/

/*    	client.downLoadPicture("http://bizhi.zhuoku.com/2011/10/11/jingxuan/jingxuan026.jpg",
                "D://dailyData", "");*/
        client.downLoadRegisterdelPicture("http://img.ljcdn.com/110000-delegation/ca4bc821-294c-49dd-a3aa-fd1200096d07.jpg,http://img.ljcdn.com/110000-delegation/6a65a285-68f7-415b-9d1c-cce26e59a56e.jpg,http://img.ljcdn.com/110000-delegation/de5c34a3-be40-4520-a549-3c41dfa053f3.jpg,http://img.ljcdn.com/110000-delegation/ae0d9796-98b2-4f0f-bc0b-ffb54670ff03.jpg,http://img.ljcdn.com/110000-delegation/15493c1a-2a93-41c7-9b78-c89484900104.jpg,http://img.ljcdn.com/110000-delegation/4680aef5-c849-49f7-a22a-6fe4b1e93a77.jpg,http://img.ljcdn.com/110000-delegation/65d68ef6-0518-45b5-a9d7-3851fc9a95df.jpg,http://img.ljcdn.com/110000-delegation/492afb3a-f7ef-42e9-a600-ea968df1ac43.jpg,http://img.ljcdn.com/110000-delegation/e2b3ee2c-b88d-43f4-8797-339cc48d2a95.jpg,http://img.ljcdn.com/110000-delegation/f816b584-5865-467a-976b-71b27837180d.jpg,http://img.ljcdn.com/110000-delegation/a3b7885f-bea5-4203-acff-c6194ef9df8e.jpg,http://img.ljcdn.com/110000-delegation/4c1ed3f7-412b-4425-a2f4-08fa5dd202f1.jpg",
                "D://dailyData//pic", "101100531710");
    }


//		public static class Response {
//			private String content;
//			private int status;
//			private Header[] headers;
//
//			public Response(String content, int status, Header[] headers) {
//				this.content = content;
//				this.status = status;
//				this.headers = headers;
//			}
//
//			public String getContent() {
//				return this.content;
//			}
//
//			public int getStatus() {
//				return this.status;
//			}
//
//			private String getValueFromArr(String[] arr) {
//				if (arr == null || arr.length < 2) {
//					return "";
//				} else {
//					return arr[1];
//				}
//			}
//
//			private Map<String, String> parseCookieString(String str) {
//				Map<String, String> map = new HashMap<String, String>();
//				String[] arr = str.split(";");
//				for (String s : arr) {
//					if (StringUtils.isNotBlank(s) && s.contains("=")) {
//						String[] kv = s.split("=");
//						if ("path".equals(kv[0].trim())) {
//							String value = getValueFromArr(kv);
//							if (StringUtils.isNotBlank(value)) {
//								map.put("path", value);
//							}
//						} else if ("domain".equals(kv[0].trim())) {
//							String value = getValueFromArr(kv);
//							if (StringUtils.isNotBlank(value)) {
//								map.put("domain", value);
//							}
//						} else if ("expires".equals(kv[0].trim())) {
//							//						map.put("expires", -1);
//						} else {
//							map.put("name", kv[0].trim());
//							map.put("value", getValueFromArr(kv));
//						}
//					}
//				}
//				return map;
//			}
//
//			public List<Cookie> getCookies() {
//				List<Cookie> list = new ArrayList<Cookie>();
//				for (Header header : headers) {
//					if (header.getName().equals("Set-Cookie")) {
//						String cookieContent = header.getValue();
//						Map<String, String> map = parseCookieString(cookieContent);
//						Cookie cookie = new Cookie(map.get("name"), map.get("value"));
//						if (StringUtils.isNotBlank(map.get("path")))
//							cookie.setPath(map.get("path"));
//						cookie.setMaxAge(-1);
//						if (StringUtils.isNotBlank(map.get("domain")))
//							cookie.setDomain(map.get("domain"));
//						list.add(cookie);
//					}
//				}
//				return list;
//			}
//
//			public List<Cookie> getCookies(String... names) {
//				List<Cookie> all = getCookies();
//				List<Cookie> list = new ArrayList<Cookie>();
//				for (String name : names) {
//					for (Cookie c : all) {
//						if (c.getName().equals(name)) {
//							list.add(c);
//						}
//					}
//				}
//				return list;
//			}
//
//		}
//
//		public static Response getResponseByPost(String url, String charsetName, Map<String, String> header, Map<String, String> paras) {
//			HttpClient httpclient = new DefaultHttpClient();
//			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
//			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
//			String content = "";
//			Response resp = null;
//			HttpPost httppost = new HttpPost(url);
//			try {
//				httpclient = WebClientDevWrapper.wrapClient(httpclient);
//				if (header != null && !header.isEmpty()) {
//					for (Map.Entry<String, String> entry : header.entrySet()) {
//						httppost.setHeader(entry.getKey(), entry.getValue());
//					}
//				}
//
//				List<NameValuePair> form = new ArrayList<NameValuePair>();
//				if (paras != null && !paras.isEmpty()) {
//					for (Map.Entry<String, String> entry : paras.entrySet()) {
//						form.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//					}
//				}
//				UrlEncodedFormEntity paraentity = new UrlEncodedFormEntity(form, charsetName);
//				httppost.setEntity(paraentity);
//
//				HttpResponse response = httpclient.execute(httppost);
//
//				HttpEntity entity = response.getEntity();
//				StringBuilder sb = new StringBuilder();
//				BufferedReader red = new BufferedReader(new InputStreamReader(entity.getContent(), charsetName));
//				String line;
//				while ((line = red.readLine()) != null) {
//					sb.append(line + "\n");
//				}
//				content = sb.toString();
//				resp = new Response(content, response.getStatusLine().getStatusCode(), response.getAllHeaders());
//				EntityUtils.consume(entity);
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//			} finally {
//				//释放连接
//				httppost.releaseConnection();
//				httpclient.getConnectionManager().shutdown();
//			}
//
//			return resp;
//		}
//
//		public static Response getResponseByPostBody(String url, String postBody, Map<String, String> header, String charsetName) {
//			HttpClient httpclient = new DefaultHttpClient();
//			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
//			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
//			String content = "";
//			Response resp = null;
//			HttpPost httppost = new HttpPost(url);
//			try {
//				httpclient = WebClientDevWrapper.wrapClient(httpclient);
//				if (header != null && !header.isEmpty()) {
//					for (Map.Entry<String, String> entry : header.entrySet()) {
//						httppost.setHeader(entry.getKey(), entry.getValue());
//					}
//				}
//
//				if (postBody == null) {
//					postBody = "";
//				}
//				HttpEntity postEntity = new StringEntity(postBody, charsetName);
//				httppost.setEntity(postEntity);
//
//				HttpResponse response = httpclient.execute(httppost);
//
//				HttpEntity entity = response.getEntity();
//				StringBuilder sb = new StringBuilder();
//				BufferedReader red = new BufferedReader(new InputStreamReader(entity.getContent(), charsetName));
//				String line;
//				while ((line = red.readLine()) != null) {
//					sb.append(line + "\n");
//				}
//				content = sb.toString();
//				resp = new Response(content, response.getStatusLine().getStatusCode(), response.getAllHeaders());
//				EntityUtils.consume(entity);
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//			} finally {
//				//释放连接
//				httppost.releaseConnection();
//				httpclient.getConnectionManager().shutdown();
//			}
//
//			return resp;
//		}


    public String executeSubmitPostWithCookie(String url, Map<String, String> params, Map<String, String> headers,
                                              String charSet, String cookie) {
        CloseableHttpClient httpclient = getHttpClient();
        CloseableHttpResponse response = null;

        try {
            HttpPost post = new HttpPost(getURI(url));
            if (headers != null) {
                post.setHeaders(assemblyHeader(headers));
            }
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(cookie)) {
                post.setHeader("Cookie", cookie);
            }
            post.setHeader("Cache-Control", "no-cache");
            post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Connection", "keep-alive");
            //post.setHeader("Content-Length", "0");
            post.setHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundarydKxMDOrzhrZgkX2e");
            post.setHeader("Origin", "http://zw.enorth.com.cn");
            post.setHeader("X-Requested-With", "XMLHttpRequest");

            //  设置HTTP POST请求参数必须用NameValuePair对象
            List<NameValuePair> lst = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                lst.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            //  设置HTTP POST请求参数
            UrlEncodedFormEntity posEntity = new UrlEncodedFormEntity(lst, charSet);
            if (logger.isDebugEnabled()) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                posEntity.writeTo(os);
                logger.debug("url:{},content:{}", url, os.toString());
            }
            post.setEntity(posEntity);
            post.setConfig(requestConfig);

            response = httpclient.execute(post);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, charSet);
            return content;
        } catch (Exception e) {
            logger.error(e.getMessage() + " when url is:" + url, e);
            return null;
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }


}