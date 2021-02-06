import java.util.concurrent.*;

//使用future
public class GetReturnValue10 {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        long start=System.currentTimeMillis();
        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        System.out.println("开始执行");
        Thread thread = new Thread(()->{
            result = sum();
        });
        Future future = executorService.submit(thread);

        try {
            future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
