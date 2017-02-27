//package com.duke.self;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.nio.ByteBuffer;
//import java.nio.channels.FileChannel;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.PostConstruct;
//
//import org.apache.http.Header;
//import org.apache.http.HttpEntity;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.apache.http.message.BasicHeader;
//import org.apache.http.util.EntityUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import com.google.gson.Gson;
//
//
//import sun.misc.BASE64Encoder;
//
//public class GoogleVisionHttpInvoker {
//
//    private static final Logger logger = LoggerFactory.getLogger(GoogleVisionHttpInvoker.class);
//    private static final String GOOGLE_API_KEY = "AIzaSyA2eAJIO4A2pd_b3O2AgZW0SYG-RSYIenc";
//    private static final String GOOGLE_VISION_SERVICE = "https://content-vision.googleapis.com/v1/images:annotate?alt=json&key="
//            + GOOGLE_API_KEY;
//    private PoolingHttpClientConnectionManager cm = null;
//    private RequestConfig requestConfig = null;
//    private int timeout = 5000;
//    private CloseableHttpClient httpClient = null;
//
//    @PostConstruct
//    public void initClientParam() {
//        if (cm == null) cm = new PoolingHttpClientConnectionManager();
//        // 将最大连接数增加到200
//        cm.setMaxTotal(100);
//        // 将每个路由基础的连接增加到20
//        cm.setDefaultMaxPerRoute(20);
//        // Increase max total connection to 200
//        cm.setMaxTotal(100);
//        // Increase default max connection per route to 20
//        cm.setDefaultMaxPerRoute(20);
//
//        requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
//                .setConnectionRequestTimeout(timeout).build();
//        httpClient = HttpClients.custom().setConnectionManager(cm).build();
//
//    }
//
//    private URI getURI(String url) throws MalformedURLException, URISyntaxException {
//        if (url != null && !url.startsWith("http")) {
//            url = "http://" + url;
//        }
//        URL tmp = new URL(url);
//        URI uri = new URI(tmp.getProtocol(), tmp.getUserInfo(), tmp.getHost(), tmp.getPort(),
//                tmp.getPath(), tmp.getQuery(), null);
//        return uri;
//    }
//
//    private static Header[] assemblyHeader(Map<String, String> headers) {
//        Header[] allHeader = new BasicHeader[headers.size()];
//        int i = 0;
//        for (String str : headers.keySet()) {
//            allHeader[i] = new BasicHeader(str, headers.get(str));
//            i++;
//        }
//        return allHeader;
//    }
//
//    public int getTimeout() {
//        return timeout;
//    }
//
//    public void setTimeout(int timeout) {
//        this.timeout = timeout;
//    }
//
//    public String invokeGoogleVisionService(String requestBody, String charSet) {
//        Map<String, String> header = new HashMap<>();
//        header.put("Content-Type", "application/json");
//        System.out.println(requestBody);
//        return executeRequestBodyPost(GOOGLE_VISION_SERVICE, requestBody, header, charSet);
//    }
//
//    private String executeRequestBodyPost(String url, String pic, Map<String, String> headers,
//            String charSet) {
//        CloseableHttpResponse response = null;
//        try {
//            HttpPost post = new HttpPost(getURI(url));
//            if (headers != null) {
//                post.setHeaders(assemblyHeader(headers));
//            }
//            post.setConfig(requestConfig);
//            StringEntity entityPic = new StringEntity(pic, charSet);
//            post.setEntity(entityPic);
//            response = httpClient.execute(post);
//            HttpEntity entity = response.getEntity();
//            String content = EntityUtils.toString(entity, charSet);
//            return content;
//        } catch (Exception e) {
//            logger.error(e.getMessage() + " when url is:" + url, e);
//            return null;
//        } finally {
//            if (null != response) {
//                try {
//                    response.close();
//                } catch (IOException e) {
//                    logger.error(e.getMessage(), e);
//                }
//            }
//        }
//    }
//
//
//    public static void main(String[] args) throws ClientProtocolException, IOException{
//        GoogleVisionHttpInvoker client = new GoogleVisionHttpInvoker();
//        client.initClientParam();
//        client.invokeGoogle();
//    }
//
//
//    public void invokeGoogle(){
//
//
//        //bodyMap.put("key", GOOGLE_API_KEY);
//
//        String url ="https://content-vision.googleapis.com/v1/images:annotate?alt=json&key="+GOOGLE_API_KEY;
//
//        String file = "/Users/wangshuqiang/Downloads/label.jpeg";
//        FileInputStream input = null;
//        FileChannel channel = null;
//        Map<String, String> header = new HashMap<>();
//        header.put("Content-Type", "application/json");
//        try {
//            input = new FileInputStream(file);
//            channel = input.getChannel();
//            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
//            while ((channel.read(byteBuffer)) > 0) ;
//            String encoder = new BASE64Encoder().encode(byteBuffer.array());
//            GoogleVisionImage img = new GoogleVisionImage(encoder);
//            GoogleVisionRequest request = GoogleVisionRequest.build().setImage(img)
//                    .setFeatures(Arrays.asList(new GoogleVisionFeature(GoogleVisionFeatureType.LABEL_DETECTION, 1)));
//            Map<String, List<GoogleVisionRequest>> bodyMap = new HashMap<String, List<GoogleVisionRequest>>();
//            Gson gson = new Gson();
//            bodyMap.put("requests", Arrays.asList(request));
//            String joRet = gson.toJson(bodyMap);
//            System.out.println(joRet);
//            //String ret = executePost(url, joRet, header,"utf-8");
//            String ret = invokeGoogleVisionService(joRet, "utf-8");
//            System.out.println(ret);
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            try {
//                input.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//        Map<String, String> aa = new HashMap<>();
//        aa.put("123", "456");
//        System.out.println(new Gson().toJson(aa));
//
//
//    }
//
//    public String executePost(String url, String pic, Map<String, String> headers, String charSet) {
//        CloseableHttpResponse response = null;
//
//        try {
//            HttpPost post = new HttpPost(getURI(url));
//
//            if (headers != null) {
//                post.setHeaders(assemblyHeader(headers));
//            }//  设置HTTP POST请求参数必须用NameValuePair对象
//
//            post.setConfig(requestConfig);
//            StringEntity entityPic = new StringEntity(pic);
//            post.setEntity(entityPic);
//            response = httpClient.execute(post);
//            HttpEntity entity = response.getEntity();
//            String content = EntityUtils.toString(entity, charSet);
//            return content;
//        } catch (Exception e) {
//            logger.error(e.getMessage() + " when url is:" + url, e);
//            return null;
//        } finally {
//            if(null != response){
//                try {
//                    response.close();
//                } catch (IOException e) {
//                    logger.error(e.getMessage(), e);
//                }
//            }
//        }
//    }
//
//
//}