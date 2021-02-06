import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

//使用cyclicbarrier
public class GetReturnValue08 {
    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        long start=System.currentTimeMillis();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        System.out.println("开始执行");
        Thread thread = new Thread(()->{
            result = sum();
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        cyclicBarrier.await();
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
