package dynamicProxy;

public class UserServiceImpl implements UserService {
    public void register(String username, String password) {
        System.out.println("用户注册");
    }

    public boolean login(String username, String password) {
        System.out.println("用户登录");
        return true;
    }
}
