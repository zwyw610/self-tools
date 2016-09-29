package com.duke.self;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Read on 2016/9/29.
 */
public class PictureDownLoad {

    /**
     * 下载图片
     * @param picPath 图片远程地址
     * @param localPath 照片本地路径
     * @param localPicName 本地图片名称,可以为空,如是如此则会将远程服务器的图片名称作为本地图片名称
     *
     * @throws IOException
     * @throws ClientProtocolException
     */
    public void downLoadPicture(CloseableHttpClient client, String picPath, String localPath, String localPicName)
            throws ClientProtocolException, IOException {

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

    public static void main(String[] args) throws ClientProtocolException, IOException{
        HttpClient client = new HttpClient();
        client.initClientParam();
        PictureDownLoad pictureDownLoad = new PictureDownLoad();
        pictureDownLoad.downLoadPicture(client.getHttpClient(), "http://bizhi.zhuoku.com/2011/10/11/jingxuan/jingxuan026.jpg",
    			"D://dailyData", "");

    }


}
