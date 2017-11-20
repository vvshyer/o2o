package com.sun.o2o.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ShortNetAddressUtil {
    private static org.slf4j.Logger log = LoggerFactory.getLogger(ShortNetAddressUtil.class);

    public static int TIMEOUT = 30 * 1000;
    private static String ENCODING = "UTF-8";

    /**
     * 根据传入的URL，通过访问百度短视频的接口，将其转换成短URL
     * @param originURL
     * @return
     */
    public static String getShortURL(String originURL){
        String tinyUrl = null;
        try {
            //指定百度短视频的接口
            URL url = new URL("http://dwz.cn/create.php");
            //建立连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //设置连接的参数
            //使用连接进行输出
            connection.setDoOutput(true);
            //使用连接进行输入
            connection.setDoInput(true);
            //不使用缓存
            connection.setUseCaches(false);
            //设置连接超时时间为30s
            connection.setConnectTimeout(TIMEOUT);
            //设置请求模式为POST
            connection.setRequestMethod("POST");
            //设置POST信息，这里为传入的原始URL
            String postData = URLEncoder.encode(originURL.toString(),ENCODING);
            //输出原始的URL
            connection.getOutputStream().write(("url="+postData).getBytes());
            //连接百度短视频接口
            connection.connect();
            //获取返回的字符串
            String responseStr = getResponseStr(connection);
            log.info("response string:" + responseStr);
            //在字符串里获取tinyUrl
            tinyUrl = getValueByKey(responseStr,"tinyurl");
            log.info("tinyUrl:" + tinyUrl);
            //关闭连接
            connection.disconnect();
        } catch (IOException e) {
            log.error("getshortURL error:" + e.getMessage());
        }
        return tinyUrl;
    }

    /**
     * 通过HttpConnection获取返回的字符串
     * @param connection
     * @return
     * @throws IOException
     */
    private static String getResponseStr(HttpURLConnection connection) throws IOException{
        StringBuffer result = new StringBuffer();
        //从连接中获取http状态码
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
            //如果返回的状态码是OK，那么取出连接的输入流
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,ENCODING));
            String inputLine = "";
            while ((inputLine = reader.readLine())!=null){
                //将消息逐行读入结果中
                result.append(inputLine);
            }
        }
        //将结果转换成String并返回
        return String.valueOf(result);
    }

    /**
     * JSON依据传入的key获取value
     * @param replyText
     * @param key
     * @return
     */
    private static String getValueByKey(String replyText, String key) {
        ObjectMapper mapper = new ObjectMapper();
        //定义json节点
        JsonNode node;
        String targetValue = null;
        try {
            //把调用返回的消息串转换成json对象
            node = mapper.readTree(replyText);
            targetValue = node.get(key).asText();
        } catch (JsonProcessingException e) {
            log.error("getValueByKey error:"+e.getMessage());
        } catch (IOException e) {
            log.error("getValueByKey error:"+e.getMessage());
        }
        return targetValue;
    }
}
