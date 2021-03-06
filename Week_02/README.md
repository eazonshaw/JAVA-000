# 作业
1. 对 串行 / 并行 / CMS / G1 GC进行总结。

| GC类型  | 参数 | 特点 |
|---|---|---|
| 串行GC | -XX:+UseSerialGC | STW时间较长，不适合在服务器使用 |
| 并行GC | -XX:+UseParallelGC | 多线程运作，相较于串行GC，总的暂停时间变短 |
| CMS | -XX:+UseConcMarkSweepGC | 将耗时长的并发标记和并发清除操作和用户线程并行 |
| G1 | -XX:+UseG1GC | 可预测的停顿(允许程序通过参数约定垃圾收集的时间) |

2. 写一段代码，使用 HttpClient 或 OkHttp 访问 http://localhost:8801。

编写CustomHttpUtil
```java
public class CustomHttpUtil {


    /**
     * post请求
     * @param url 链接
     * @param jsonParam json字符串参数
     * @return 请求页面html
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
}
```
使用CustomHttpUtil访问 http://localhost:8801
```
CustomHttpUtil.httpGet("http://localhost:8801");
```

