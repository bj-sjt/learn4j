package com.itao.http;

import cn.hutool.json.JSONUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpSender {

    public static void main(String[] args)  {
        httpPostMultipart();
    }

    private static void httpPostMultipart() {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            String url = "http://localhost:8080/httpPost2";
            HttpPost httpPost = new HttpPost(url);
            ContentType textPlain = ContentType.create("text/plain", StandardCharsets.UTF_8);
            ContentType imageJpeg = ContentType.create("image/jpeg", StandardCharsets.UTF_8);
            ContentType type = ContentType.create("multipart/form-data", StandardCharsets.UTF_8);
            ContentBody fileBody = new FileBody(new File("C:\\Users\\32088\\Pictures\\Saved Pictures\\1.jpg"),imageJpeg);
            ContentBody stringBody = new StringBody("北京",textPlain);
            HttpEntity entity = MultipartEntityBuilder.create() // addPart 可以指定编码防止乱码
                    .setContentType(type)
                    .setCharset(StandardCharsets.UTF_8)
                    .addPart("file", fileBody) // 添加文件的方式 addPart 或 addBinaryBody
                    .addPart("address", stringBody)  // 添加普通表单属性 addTextBody 或 addPart
                    .addTextBody("age", "18")
                    .addTextBody("name", "sjt")
                    .build();
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String html = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
            System.out.println(html);
            EntityUtils.consume(httpEntity);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            close(httpClient, response);
        }
    }

    private static void httpPostJson() {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            String url = "http://localhost:8080/httpPost1";
            HttpPost httpPost = new HttpPost(url);
            // 设置表单参数
            Map<String, Object> params = new HashMap<>();
            params.put("name", "sjt");
            params.put("age", "18");
            params.put("address", "北京");
            //HttpEntity entity =new UrlEncodedFormEntity(params, "UTF-8");
            ContentType contentType = ContentType.create("application/json", StandardCharsets.UTF_8);
            HttpEntity entity = new StringEntity(JSONUtil.toJsonStr(params), contentType);
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String html = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
            System.out.println(html);
            EntityUtils.consume(httpEntity);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            close(httpClient, response);
        }
    }

    private static void httpPostForm() {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            String url = "http://localhost:8080/httpPost";
            HttpPost httpPost = new HttpPost(url);
            // 设置表单参数
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", "sjt"));
            params.add(new BasicNameValuePair("age", "18"));
            params.add(new BasicNameValuePair("address", "北京"));
            HttpEntity entity =new UrlEncodedFormEntity(params, "UTF-8");
            //HttpEntity entity = new StringEntity(JSONUtil.toJsonStr(params));
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String html = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
            System.out.println(html);
            EntityUtils.consume(httpEntity);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            close(httpClient, response);
        }
    }

    private static void httpGetProxy() {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            String password = "sjt+123";
            String encode = URLEncoder.encode(password, "UTF-8");
            String url = "http://localhost:8080/httpGet?userName=sjt&password=" + encode;
            HttpGet httpGet = new HttpGet(url);
            // 设置代理
            String hostName = "221.122.91.60";
            int port = 80;
            HttpHost httpHost = new HttpHost(hostName, port);
            RequestConfig config = RequestConfig.custom().setProxy(httpHost).build();
            httpGet.setConfig(config);
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String html = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
            System.out.println(html);
            EntityUtils.consume(httpEntity);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            close(httpClient, response);
        }
    }

    /**
     * 带参数的get请求并对参数进行编码
     */
    private static void httpGetAndEncoder() {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            String password = "sjt+123";
            String encode = URLEncoder.encode(password, "UTF-8");
            String url = "http://localhost:8080/httpGet?userName=sjt&password=" + encode;
            HttpGet httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String html = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
            System.out.println(html);
            EntityUtils.consume(httpEntity);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            close(httpClient, response);
        }
    }

    private static void httpGet() {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            String url = "http://www.baidu.com";
            HttpGet httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String html = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
            System.out.println(html);
            EntityUtils.consume(httpEntity);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            close(httpClient, response);
        }
    }

    private static void close(CloseableHttpClient httpClient, CloseableHttpResponse response){
        try {
            if (httpClient != null){
                httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
