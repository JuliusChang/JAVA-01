import java.util.concurrent.CountDownLatch;
//使用countdownlatch
public class GetReturnValue07 {
    public static void main(String[] args) throws InterruptedException {
        long start=System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        System.out.println("开始执行");
        Thread thread = new Thread(()->{
            result = sum();
            countDownLatch.countDown();
        });
        thread.start();
        countDownLatch.await();
//        Thread.currentThread().join();
        // 确保  拿到result 并输出
        System.out.println("异步计算结果为："+result);

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int result = -1;

    private static int sum() {
        return fibonacci(36);
    }

    private static int fibonacci(int a) {
        if ( a < 2)
            return 1;
        return fibonacci(a-1) + fibonacci(a-2);
    }
}
