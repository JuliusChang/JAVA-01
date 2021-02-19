import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import springBean.Dog;
import springBean.User;

public class test2 {


    //使用注解创建对象
    @Test
    public void test1() {
        ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        User user = (User)context.getBean("user");
        user.print();
    }

    //使用xml创建对象
    @Test
    public void test3(){
        ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        Dog wangcai = (Dog)context.getBean("wangcai");
        wangcai.bark();
    }
}
