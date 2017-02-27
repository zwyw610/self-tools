package msazure;

// // This sample uses the Apache HTTP client from HTTP Components (http://hc.apache.org/httpcomponents-client-ga/)

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import common.FileUtils;

/**
 * Created by wangshuqiang on 2017/2/24.
 */
public class MSAzureSample {

    public static void main(String[] args) {
        HttpClient httpclient = HttpClients.createDefault();
        String fileName = "/Users/wangshuqiang/audio/pic/random_label_1W/1705827675_1.jpg";
        byte[] bytes = FileUtils.readFileIntoBytes(fileName);
        try {
            URIBuilder builder = new URIBuilder(
                    "https://api.cognitive.azure.cn/vision/v1.0/analyze");

            builder.setParameter("visualFeatures", "Categories");
            //builder.setParameter("details", "{string}");
            builder.setParameter("language", "en");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            //request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", "8986589ba5954defa45cdc61da99d466");

            // Request body
            //StringEntity reqEntity = new StringEntity(new String(bytes, "utf-8"));
            //request.setEntity(reqEntity);

            request.setEntity(new ByteArrayEntity(bytes));

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
