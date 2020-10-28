import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author eazonshaw
 * @date 2020/10/28  21:27
 *
 * 使用 HttpClient 或 OkHttp 访问 http://localhost:8801
 *
 */
public class CustomHttpUtil {


    /**
     * post请求
     * @param url 链接
     * @param jsonParam json字符串参数
     * @return
     */
    public static String httpPost(String url, String jsonParam) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        //处理参数
        jsonParam = (null == jsonParam || jsonParam.isEmpty()) ? "" : jsonParam;
        StringEntity entity = new StringEntity(jsonParam, "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return handleResponse(response);
        }
    }

    /**
     * get请求
     * @param url 链接
     * @return 请求页面html
     * @throws IOException
     */
    public static String httpGet(String url) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return handleResponse(response);
        }
    }

    private static String handleResponse(CloseableHttpResponse response) throws IOException{
        HttpEntity entity = response.getEntity();
        String str = EntityUtils.toString(entity);
        //关闭流
        EntityUtils.consume(entity);
        return str;
    }

    public static void main(String[] args) {
        try {
            System.out.println(httpGet("http://localhost:8801"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
