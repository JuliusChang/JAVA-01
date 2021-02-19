import dynamicProxy.UserService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.net.www.http.ChunkedInputStream;

public class test1 {

    @Test
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");
        UserService Chauncy = (UserService) applicationContext.getBean("Chauncy");
        Chauncy.register("chauncy","123456");
        Chauncy.login("chauncy","123456");
    }
}
