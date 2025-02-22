## 目标
1. WebSever（Tomcat） 的线程池配置
1. 服务内部线程池配置（ThreadPoolTaskExecutor）
1. Hystrix 线程池配置

## WebServer（Tomcat）的线程池配置
### 配置原则
* CPU密集型的，那么你可能需要限制maxThreads以避免过多的上下文切换
* 对于I/O密集型的应用，你可以适当增加maxThreads，因为在这种情况下，更多的线程可以在等待I/O操作完成的同时执行其他任务
### 线程池配置示例
``` properties
# -- 如下是基于 SpringBoot 开发框架的配置样例 -- 

# 设定 Tomcat 连接器能够处理的最大并发请求线程数
# 对于 CPU 密集型应用 -- 建议将 maxThreads 设置为 server Cores * 2 ~ server Cores * 4 的范围内
#（例如，8 核 CPU 的服务器可以设置为 16 到 32）。
# 对于 I/O 密集型应用 -- 可以适当增加 maxThreads，因为在这种情况下，更多的线程可以在等待I/O操作完成的同时执行其他任务。
# 根据硬件资源进行计算： 一个Java线程大概需要 256KB 到 1MB 的栈空间（具体取决于JVM配置和操作系统），这会影响到你可以同时运行多少个线程而不耗尽内存。
# 
# CPU 密集
server.tomcat.threads.max=32
# I/O 密集
# server.tomcat.threads.max=200

# 设定最小空闲工作线程数。即使在没有负载的情况下，Tomcat也会保持至少这么多的空闲线程，以便快速响应新请求。
# 建议设置 server Cores 的一到两倍。例如，如果服务器有8个CPU核心，那么可以设置为8到16。
# 此处考虑请求的高峰低估，合理配置线程资源的创建和回收（配合 server.tomcat.threads.max 一起工作），同时减少内存溢出的可能
#
server.tomcat.threads.min-spare=8

# 某些系统（例如：macos）中，server.tomcat.accept-count 不生效，所以 SpringBoot 3.3.0 中引入此配置，用于控制 tomcat 请求达到最大连接后，可以继续接受新连接的数量
#
sever.tomcat.threads.max-queue-capacity=500

# 指定Tomcat接受的最大并发连接数。超过此数量的新连接将被挂起，直到现有连接减少到低于此限制
# 建议设置最大线程数的 10 到 100 倍
#
server.tomcat.max-connections=2000

# 当所有可能的请求处理线程都在使用中，并且已经达到max-connections限制时，允许的最大排队请求数。
# 换句话说，这是操作系统层面的一个队列大小，用于暂存那些暂时无法立即由Tomcat处理的连接请求。
#
server.tomcat.accept-count=100
```

## 服务内部线程池配置（ThreadPoolTaskExecutor）
### 原则
1. 没有特殊情况，服务内部共享同一个线程池，充分发挥统一配置优势，避免线程资源浪费

### ThreadPoolTaskExecutor 的用法
1. 三个重要的控制参数：
    * CorePoolSize -- 核心线程数
    * MaxPoolSize -- 最大线程数（能力极限） 
    * QueueCapacity -- 队列长度（缓冲器）

1. 三个参数控制下的线程池行为表现：
    * 当有新请求进入时，线程值会持续创建新的线程，而不是考虑利用已有的空闲线程，直到线程个数达到 CorePoolSize （**始化阶段** -- 核心线程初始化）
    * 当线程池的个数达到CoreSize且每个线程都不是空闲时，新的请求将进入队列，而不是创建新的线程，直到队列里的请求到达 queueSize （**缓冲阶段** -- 核心线程已满，但排队线程数未超标）
    * 当排队请求数达到 queueSize 时，再有新的请求时，会创建新的线程处理排队中请求，直到线程数达到 MaxSize （**全力处理阶段** -- 更多线程会被创建）
    * 当线程数达到 MaxSize 且 排队的线程数达到 QueueSize时，新的请求会被拒绝（**饱和阶段**）

1. 配置建议
    * CorePoolSize -- 参见 server.tomcat.threads.min-spare 的配置原则， 建议设置为 MaxPoolSize 的 1/10 到 1/5  
    * MaxPoolSize -- 和 server.tomcat.threads.max 的值关联，可以满足最大并发 web 请求下，所能创建的线程数总和。建议设置为 server.tomcat.threads.max 的 1 到 10 倍
    * QueueSize -- 缓冲期用于避免高频的创建和释放线程，建议设置为 CorePoolSize 的 1 到 2 倍


## Hystrix 线程池配置
### 原则
1. 没有特殊情况，服务内部共享同一个线程池，充分发挥统一配置优势，避免线程资源浪费。



