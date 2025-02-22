package com.nuoson.modulith;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.nuoson.modulith.infra.utils.ConsolePrinter;

public class ThreadPoolTest {

    /**
     * CoreSize, MaxSize, QueueSize的表现行为验证：
     * 1，当有新请求进入时，线程值会持续创建新的线程，而不是考虑利用已有的空闲线程，直到线程个数达到 CoreSize
     * 2，当线程池的个数达到CoreSize且每个线程都不是空闲时，新的请求将进入队列，而不是创建新的线程，直到队列里的请求到达 queueSize
     * 3，当排队请求数达到 queueSize 时，再有新的请求时，会创建新的线程处理排队中请求，直到线程数达到 MaxSize
     * 4，当线程数达到 MaxSize 且 排队的线程数达到 QueueSize时，新的请求会被拒绝
     */
    @Test
    public void verifyCoreSize() throws InterruptedException {
        CountDownLatch currentLatch = null;
        boolean rejectionHappened = false;
        int coreSize = 2;
        int maxSize = 5;
        int queueSize = 5;
        // 需要启动的线程数
        int startedThreadCount = 0;

        // 初始化线程池
        ConsolePrinter.printGreen(String.format("-- 初始化线程池（coreSize: %d, maxSize: %d, queueSize: %d），并验证线程池大小为 0",
                coreSize, maxSize, queueSize));
        ThreadPoolTaskExecutor executor = initThreadPool(coreSize, maxSize, queueSize);
        // 验证初始状态
        ConsolePrinter.printYellow("验证线程池初始化线程数为 0");
        printPoolState(executor);
        assertEquals(0, executor.getActiveCount(), "线程池活跃数不为0");
        assertEquals(0, executor.getPoolSize(), "线程池大小不为0");
        assertEquals(0, executor.getQueueSize(), "队列大小不为0");
        ConsolePrinter.print("\r\n ------- \r\n");

        // 先后提交两个任务（第一个执行完毕再提交第二个），验证在达到coreSize前，不会重用空闲进程
        ConsolePrinter.printGreen("启动第一个线程，并执行完毕");
        currentLatch = new CountDownLatch(1);
        executor.execute(new ThreadTask("T1", currentLatch));
        // 等待一段时间以观察线程池状态
        currentLatch.await();
        ConsolePrinter.printYellow("验证线程池大小变为 1");
        printPoolState(executor);
        assertEquals(0, executor.getActiveCount(), "线程池活跃数不为0");
        assertEquals(1, executor.getPoolSize(), "线程池大小不为1");
        assertEquals(0, executor.getQueueSize(), "队列大小不为0");
        printDelimiter();

        ConsolePrinter.printGreen("启动第二个线程，并等待其执行完毕");
        currentLatch = new CountDownLatch(1);
        executor.execute(new ThreadTask("T2", currentLatch));
        currentLatch.await();
        ConsolePrinter.printYellow("验证线程池大小变为 2, 即：没有重用空闲线程，而是创建了新线程");
        printPoolState(executor);
        assertEquals(0, executor.getActiveCount(), "线程池活跃数不为0");
        assertEquals(2, executor.getPoolSize(), "线程池大小不为2");
        assertEquals(0, executor.getQueueSize(), "队列大小不为0");
        printDelimiter();

        // 启动第三、四个任务，并等待它们执行完毕
        ConsolePrinter.printGreen("启动第三、四个线程，并等待它们执行完毕");
        currentLatch = new CountDownLatch(2);
        executor.execute(new ThreadTask("T3", currentLatch));
        executor.execute(new ThreadTask("T4", currentLatch));
        // 等待任务完成
        currentLatch.await();
        ConsolePrinter.printYellow("验证线程池大小保持 2 不变，即重用已有空闲线程，而不是创建新线程");
        printPoolState(executor);
        assertEquals(0, executor.getActiveCount(), "线程池活跃数不为0");
        assertEquals(2, executor.getPoolSize(), "线程池大小不为2");
        assertEquals(0, executor.getQueueSize(), "队列大小不为0");
        printDelimiter();

        ConsolePrinter.printGreen("同时启动 3 个线程，验证第 3 个线程进入等待队列");
        startedThreadCount = 3;
        currentLatch = new CountDownLatch(startedThreadCount);
        for (int loop = 0; loop < startedThreadCount; loop++) {
            executor.execute(new ThreadTask("T_" + loop, currentLatch, 1000));
        }
        ConsolePrinter.printYellow("验证线程池大小保持 2 不变，且有一个请求在等待队列中，即在等待的请求数达到maxQueueSize之前，不会创建新线程");
        printPoolState(executor);
        assertEquals(1, executor.getQueueSize(), "队列大小不为1");
        assertEquals(2, executor.getPoolSize(), "线程池大小不为2");
        currentLatch.await();
        ConsolePrinter.printYellow("三个线程执行完毕，验证线程池大小保持 2 不变");
        printPoolState(executor);
        assertEquals(0, executor.getActiveCount(), "线程池活跃数不为0");
        assertEquals(2, executor.getPoolSize(), "线程池大小不为2");
        assertEquals(0, executor.getQueueSize(), "队列大小不为0");
        printDelimiter();

        startedThreadCount = coreSize + queueSize + 1;
        ConsolePrinter.printGreen(String.format("同时启动 %d 个线程，验证第等待队列满了以后，会创建新的线程",
                startedThreadCount));
        currentLatch = new CountDownLatch(startedThreadCount);
        for (int loop = 0; loop < startedThreadCount; loop++) {
            executor.execute(new ThreadTask("T" + loop, currentLatch, 1000));
        }
        printPoolState(executor);
        currentLatch.await();
        ConsolePrinter.printYellow(String.format("所有线程执行完毕，验证线程池大小变为 %d, ",
                coreSize + 1));
        printPoolState(executor);
        assertEquals(0, executor.getActiveCount(), "线程池活跃数异常");
        assertEquals(coreSize + 1, executor.getPoolSize(), "线程池大小异常");
        assertEquals(0, executor.getQueueSize(), "队列大小异常");
        printDelimiter();

        startedThreadCount = maxSize + queueSize + 1;
        ConsolePrinter.printGreen(String.format("同时提交 %d 个任务，验证等待队列满且线程池个数也达到 maxPoolSize 以后，提交新任务失败",
                startedThreadCount));
        // 有一个任务会被拒绝，所以需要减去一个任务数
        currentLatch = new CountDownLatch(startedThreadCount - 1);
        rejectionHappened = false;
        for (int loop = 0; loop < startedThreadCount; loop++) {
            String taskName = "T" + loop;
            try {
                executor.execute(new ThreadTask(taskName, currentLatch, 1000));
            } catch (TaskRejectedException ex) {
                ConsolePrinter.printRed(String.format("验证：任务（%s）被拒绝", taskName));
                rejectionHappened = true;
            }
        }
        assertEquals(true, rejectionHappened, "未发生任务拒绝异常");
        printPoolState(executor);
        currentLatch.await();
        ConsolePrinter.printYellow(String.format("所有线程执行完毕，验证线程池大小变为 %d", maxSize));
        printPoolState(executor);
        assertEquals(0, executor.getActiveCount(), "线程池活跃数异常");
        assertEquals(maxSize, executor.getPoolSize(), "线程池大小异常");
        assertEquals(0, executor.getQueueSize(), "队列大小异常");
        printDelimiter();
    }

    /**
     * 验证瞬态峰值
     * <p>
     * 瞬态峰值是指线程池在短时间内突然增加的线程数量，通常是由于任务提交速度过快导致的。通过监控和分析瞬态峰值，可以了解线程池的性能瓶颈和资源使用情况。
     * </p>
     */
    @Test
    public void verifyTransientPeak() throws InterruptedException {

        CountDownLatch currentLatch = null;
        boolean rejectionHappened = false;
        int coreSize = 2;
        int maxSize = 5;
        int queueSize = 5;
        // 需要启动的线程数
        int startedThreadCount = 0;
        ThreadPoolTaskExecutor executor = initThreadPool(coreSize, maxSize, queueSize);
        startedThreadCount = maxSize + queueSize;
        ConsolePrinter.printGreen(String.format(
                "同时提交 %d 个任务，验证等待队列满且线程池个数也达到 maxPoolSize",
                startedThreadCount));
        currentLatch = new CountDownLatch(startedThreadCount);
        for (int loop = 0; loop < startedThreadCount; loop++) {
            executor.execute(new ThreadTask("T" + loop, currentLatch, 500));
        }
        currentLatch.await();
        ConsolePrinter.printYellow(String.format("验证线程池线程已满：%d", maxSize));
        printPoolState(executor);

        startedThreadCount = queueSize + 1;
        ConsolePrinter.printGreen(String.format(
                "同时提交 %d 个任务，验证有提交被拒绝",
                startedThreadCount));
        currentLatch = new CountDownLatch(startedThreadCount - 1);
        for (int loop = 0; loop < startedThreadCount; loop++) {
            String taskName = "T" + loop;
            try {
                executor.execute(new ThreadTask("T" + loop, currentLatch, 2000));
                // printPoolState(executor);
            } catch (TaskRejectedException ex) {
                ConsolePrinter.printRed(String.format("验证：任务（%s）被拒绝", taskName));
                rejectionHappened = true;
            }
        }
        printPoolState(executor);
        currentLatch.await();
        ConsolePrinter.printYellow(String.format("验证有 %d 个任务被拒绝", startedThreadCount - queueSize));
        assertEquals(true, rejectionHappened, "未发生任务拒绝异常");
        printPoolState(executor);

    }

    // #region -- Private Methods and Classes
    private ThreadPoolTaskExecutor initThreadPool(int coreSize, int maxSize, int queueSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(coreSize);
        // 设置最大线程数
        executor.setMaxPoolSize(maxSize);
        // 设置队列容量
        executor.setQueueCapacity(queueSize);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(5);
        // 设置线程名称前缀
        executor.setThreadNamePrefix("ExampleExecutor-");
        executor.initialize();
        return executor;
    }

    /**
     * 打印线程池状态
     */
    private void printPoolState(ThreadPoolTaskExecutor executor) {
        ConsolePrinter.print(String.format("poolSize: %d, activeCount: %d, queueSize: %d",
                executor.getPoolSize(), executor.getActiveCount(), executor.getQueueSize()));
    }

    private void printDelimiter() {
        ConsolePrinter.print("\r\n ------- \r\n");
    }

    /**
     *
     * @param time
     */
    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    private class ThreadTask implements Runnable {
        private final CountDownLatch latch;
        private final String name;
        private final Integer durationMs;

        public ThreadTask(String name, CountDownLatch latch) {
            this(name, latch, null);
        }

        /**
         * 构造函数
         * 
         * @param name 任务名称
         * @param latch 倒计时锁
         * @param durationMs 任务执行时间（毫秒）
         */
        public ThreadTask(String name, CountDownLatch latch, Integer durationMs) {
            this.name = name;
            this.latch = latch;
            this.durationMs = durationMs;
        }

        @Override
        public void run() {
            // 执行任务的代码
            ConsolePrinter.print(String.format("executing task %s by ThreadName: %s",
                    this.name, Thread.currentThread().getName()));
            if (this.durationMs != null) {
                sleep(durationMs);
            }
            this.latch.countDown();
        }
    }

    // #endregion

    //

}
