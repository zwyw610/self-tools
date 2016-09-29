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

public class HttpClient{


    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private PoolingHttpClientConnectionManager cm = null;
    private RequestConfig requestConfig = null;
    private int timeout = 1000;


    public void initClientParam(){
        if(cm == null)
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
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
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
            if(null != response){
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
            if(null != response){
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
            if(null != response){
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
            if(null != response){
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
     * @param picPath 图片远程地址
     * @param localPath 照片本地路径
     * @param localPicName 本地图片名称,可以为空,如是如此则会将远程服务器的图片名称作为本地图片名称
     *
     * @throws IOException
     * @throws ClientProtocolException
     */
    public void downLoadPicture(String picPath, String localPath, String localPicName) throws ClientProtocolException, IOException{
        CloseableHttpClient client = getHttpClient();
        HttpGet get = new HttpGet(picPath);
        CloseableHttpResponse response = client.execute(get);
        String picName = "";
        if(StringUtils.isNotEmpty(localPicName)){
            picName = localPicName;
        }else {
            //获取远程服务器上面的图片名称作为本地图片名称
            int index = picPath.lastIndexOf("/");
            picName = picPath.substring(index);
        }
        if(localPath.endsWith("/") == false){
            localPath += "/";
        }

        File localFile = new File(localPath.concat(picName));
        FileOutputStream output = new FileOutputStream(localFile);
        output.write(EntityUtils.toByteArray(response.getEntity()));
        output.flush();
        output.close();
    }

    public void downLoadRegisterdelPicture(String remotePath, String localPath, String rename) throws ClientProtocolException, IOException{

        String[] pics = remotePath.split(",");
        int i = 1;
        for(String pic : pics){
            String picPath = pic.concat(".1000x.jpg");
            downLoadPicture(picPath, localPath, rename+"-"+i+".jpg");
            i++;
        }






    }



    public static void main(String[] args) throws ClientProtocolException, IOException{
        HttpClient client = new HttpClient();
        client.initClientParam();
/*    	client.downLoadPicture("http://bizhi.zhuoku.com/2011/10/11/jingxuan/jingxuan026.jpg",
    			"D://dailyData", "");*/
        client.downLoadRegisterdelPicture("http://img.ljcdn.com/120000-delegation/fce620f9-fb9a-48fb-9f73-09fd965f1740.jpg,http://img.ljcdn.com/120000-delegation/9abe450f-e34b-4af8-acb2-c2e62c83b02c.jpg,http://img.ljcdn.com/120000-delegation/a33d250d-fe17-45c0-bc72-39decbc6f78b.jpg,http://img.ljcdn.com/120000-delegation/49a341c2-8284-4b18-a1e4-190568a0c660.jpg,http://img.ljcdn.com/120000-delegation/ae74cc64-ae08-4b8e-8c3b-49c16b8b126e.jpg,http://img.ljcdn.com/120000-delegation/a5ab494d-5678-4adb-b428-483b4b2fb506.jpg,http://img.ljcdn.com/120000-delegation/e8082978-16f9-49f3-b57f-8c1d3e4b27b3.jpg,http://img.ljcdn.com/120000-delegation/ee4efaac-4f3a-406f-b570-5434bcf2396c.jpg,http://img.ljcdn.com/120000-delegation/0c78b216-a7c5-4c61-8d55-9813d11fc34f.jpg,http://img.ljcdn.com/120000-delegation/b5a1285e-1ec6-4308-8887-f8764a801213.jpg,http://img.ljcdn.com/120000-delegation/43ba5677-9f84-43ab-87a8-74d032ac5d02.jpg,http://img.ljcdn.com/120000-delegation/4f98feee-a185-409d-902e-10be9052b142.jpg,http://img.ljcdn.com/120000-delegation/b40b9be3-97d5-430e-ae9f-0270775b29a1.jpg,http://img.ljcdn.com/120000-delegation/0811caaf-5a0a-46ff-a53c-36f1c16209a7.jpg,http://img.ljcdn.com/120000-delegation/6631f7c0-421d-4c30-8e17-0afa4dbdeeed.jpg",
                "D://dailyData//pic", "101100586617");
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








}