package dynamicProxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class dynamicProxy implements MethodInterceptor{
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("开始");
        Object ret = methodInvocation.proceed();
        System.out.println("结束");
        return ret;
    }
}
