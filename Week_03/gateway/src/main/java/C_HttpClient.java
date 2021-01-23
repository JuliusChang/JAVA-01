import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class C_HttpClient {
    public String doGet(String uri) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet httpGet = new HttpGet(uri);

        CloseableHttpResponse response = null;
        String value = null;

        try {
            response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            value = EntityUtils.toString(responseEntity);
//            System.out.println("响应状态为：" + response.getStatusLine());
//            if (responseEntity != null) {
//                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
//                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
//            }

        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
        } finally {

            if (httpClient != null) {
                httpClient.close();
            }
            if (response != null) {
                response.close();
            }
            if(value != null){
                return value;
            }else{
                return null;
            }
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(new C_HttpClient().doGet("http://127.0.0.1:8801"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
