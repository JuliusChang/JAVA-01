import java.util.Arrays;
import java.util.List;

public class test {

    public static void main(String[] args) {
        String proxyServers = System.getProperty("proxyServers","http://localhost:8801,http://localhost:8802");
        System.out.println(proxyServers);
        List a = Arrays.asList(proxyServers.split(","));
        for (Object i:
             a) {
            System.out.println(i);
        }
    }
}
