import java.util.concurrent.locks.LockSupport;
//使用LockSupport工具类
public class GetReturnValue05 {
    public static void main(String[] args) throws InterruptedException {
        long start=System.currentTimeMillis();
        mainThread = Thread.currentThread();
        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        System.out.println("开始执行");
        Thread thread = new Thread(()->{
            result = sum();
            LockSupport.unpark(mainThread);
        });
        thread.start();
        LockSupport.park();
        System.out.println("异步计算结果为："+result);

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int result = -1;
    private static Thread mainThread;

    private static int sum() {
        return fibonacci(36);
    }

    private static int fibonacci(int a) {
        if ( a < 2)
            return 1;
        return fibonacci(a-1) + fibonacci(a-2);
    }
}
