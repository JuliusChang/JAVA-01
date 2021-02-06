import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//使用Condition的await和signal
public class GetReturnValue04 {
    public static void main(String[] args) throws InterruptedException {
        long start=System.currentTimeMillis();
        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        System.out.println("开始执行");
        Thread thread = new Thread(()->{
            lock.lock();
            try {
                result = sum();
                condition.signal();

            } finally {
                lock.unlock();
            }
        });
        thread.start();
        lock.lock();
        try {
            if(result == -1){
                condition.await();
            }
        } finally {
            lock.unlock();
        }

        System.out.println("异步计算结果为："+result);

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int result = -1;
    private static Lock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();

    private static int sum() {
        return fibonacci(36);
    }

    private static int fibonacci(int a) {
        if ( a < 2)
            return 1;
        return fibonacci(a-1) + fibonacci(a-2);
    }
}
