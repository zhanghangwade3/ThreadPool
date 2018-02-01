package org.lenve.threadpool;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private ThreadPoolExecutor poolExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        poolExecutor = new ThreadPoolExecutor(3, 30,
                1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(6));
    }

    public void btnClick(View view) {
        for (int i = 0; i < 30; i++) {
            final int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(2000);
                    Log.d("google_lenve_fb", "run: " + Thread.currentThread().getName() + "-----" + finalI);
                }
            };
//            poolExecutor.execute(runnable);
            poolExecutor.submit(runnable);
        }
    }

    public void fixedThreadPool(View view) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 30; i++) {
            final int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(3000);
                    Log.d("google_lenve_fb", "run: " + Thread.currentThread().getName() + "-----" + finalI);
                }
            };
            fixedThreadPool.execute(runnable);
        }
    }

    public void singleThreadPool(View view) {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 30; i++) {
            final int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("google_lenve_fb", "run: " + Thread.currentThread().getName() + "-----" + finalI);
                    SystemClock.sleep(1000);
                }
            };
            singleThreadExecutor.execute(runnable);
        }
    }

    public void cachedThreadPool(View view) {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 30; i++) {
            final int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(2000);
                    Log.d("google_lenve_fb", "run: " + Thread.currentThread().getName() + "----" + finalI);
                }
            };
            cachedThreadPool.execute(runnable);
            SystemClock.sleep(1000);
        }
    }

    public void scheduledThreadPool(View view) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("google_lenve_fb", "run: ----");
            }
        };
        scheduledExecutorService.scheduleWithFixedDelay(runnable, 1, 2, TimeUnit.SECONDS);
//        scheduledExecutorService.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
//        scheduledExecutorService.schedule(runnable, 1, TimeUnit.SECONDS);
    }

    public void submit(View view) {
        List<Future<String>> futures = new ArrayList<>();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5, 1,
                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        for (int i = 0; i < 10; i++) {
            Future<String> taskFuture = threadPoolExecutor.submit(new MyTask(i));
            //将每一个任务的执行结果保存起来
            futures.add(taskFuture);
        }
        try {
            //遍历所有任务的执行结果
            for (Future<String> future : futures) {
                Log.d("google_lenve_fb", "submit: " + future.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void customThreadPool(View view) {
        final MyThreadPool myThreadPool = new MyThreadPool(3, 5, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>());
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            Runnable runnable = new Runnable(){
                @Override
                public void run() {
                    SystemClock.sleep(100);
                    Log.d("google_lenve_fb", "run: " + finalI);
                }
            };
            myThreadPool.execute(runnable);
        }
    }
    class MyThreadPool extends ThreadPoolExecutor{

        public MyThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            Log.d("google_lenve_fb", "beforeExecute: 开始执行任务！");
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            Log.d("google_lenve_fb", "beforeExecute: 任务执行结束！");
        }

        @Override
        protected void terminated() {
            super.terminated();
            //当调用shutDown()或者shutDownNow()时会触发该方法
            Log.d("google_lenve_fb", "terminated: 线程池关闭！");
        }
    }

    class MyTask implements Callable<String> {

        private int taskId;

        public MyTask(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public String call() throws Exception {
            SystemClock.sleep(1000);
            //返回每一个任务的执行结果
            return "call()方法被调用----" + Thread.currentThread().getName() + "-------" + taskId;
        }
    }
}
