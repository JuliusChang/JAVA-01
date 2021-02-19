package springBean;


import org.springframework.stereotype.Component;

@Component
public class User {
    public User(){
        System.out.println("创建user");
    }
    public void print(){
        System.out.println("print");
    }
}
