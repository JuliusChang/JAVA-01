import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import springXmlBean.Classes;

public class test3 {
    @Test
    public void test1(){
        ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        Classes classes = (Classes)context.getBean("class");
        classes.foreach();

    }
}
