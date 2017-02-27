package com.duke.self;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.google.gson.Gson;

import google.GoogleVisionFeature;
import google.GoogleVisionFeatureType;
import google.GoogleVisionImage;
import google.GoogleVisionRequest;
import sun.misc.BASE64Encoder;

public class HttpClient{


    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private static final String GOOGLE_API_KEY = "AIzaSyA2eAJIO4A2pd_b3O2AgZW0SYG-RSYIenc";
    //private static final String GOOGLE_API_KEY = "AIzaSyD-a9IF8KKYgoC3cpgS-Al7hLQDbugrDcw";

    private PoolingHttpClientConnectionManager cm = null;
    private RequestConfig requestConfig = null;
    private int timeout = 5000;


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
        client.invokeGoogle();
    }


    public void invokeGoogle(){


        //bodyMap.put("key", GOOGLE_API_KEY);

        String url ="https://content-vision.googleapis.com/v1/images:annotate?alt=json&key="+GOOGLE_API_KEY;

        String file = "/Users/wangshuqiang/Downloads/label.jpeg";
        FileInputStream input = null;
        FileChannel channel = null;
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        try {
            input = new FileInputStream(file);
            channel = input.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) ;
            String encoder = new BASE64Encoder().encode(byteBuffer.array());
            GoogleVisionImage img = new GoogleVisionImage(encoder);
            GoogleVisionRequest request = GoogleVisionRequest.build().setImage(img)
                    .setFeatures(Arrays.asList(new GoogleVisionFeature(GoogleVisionFeatureType.LABEL_DETECTION, 1)));
            Map<String, List<GoogleVisionRequest>> bodyMap = new HashMap<String, List<GoogleVisionRequest>>();
            Gson gson = new Gson();
            bodyMap.put("requests", Arrays.asList(request));
            String joRet = gson.toJson(bodyMap);
            System.out.println(joRet);
            String ret = executePost(url, joRet, header,"utf-8");
            System.out.println(ret);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Map<String, String> aa = new HashMap<>();
        aa.put("123", "456");
        System.out.println(new Gson().toJson(aa));


    }

    public String executePost(String url, String pic, Map<String, String> headers, String charSet) {
        CloseableHttpClient httpclient = getHttpClient();
        CloseableHttpResponse response = null;

        try {
            HttpPost post = new HttpPost(getURI(url));

            if (headers != null) {
                post.setHeaders(assemblyHeader(headers));
            }//  设置HTTP POST请求参数必须用NameValuePair对象

            post.setConfig(requestConfig);
            StringEntity entityPic = new StringEntity(pic);
            post.setEntity(entityPic);
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




}