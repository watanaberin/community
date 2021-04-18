# community
a social community platform based on SpringBoot

# 注册功能

###### Step1 首页跳转

1.Controller写个page跳转

2 .html模板用thmyleaf替换

3.html的跳转

thymeleaf的复用

```
<header class="bg-dark sticky-top" th:fragment="header">
```

首页的header

```
<header class="bg-dark sticky-top" th:replace="index::header">
```

其他的页面复用

## 

###### 3.点击链接激活

分析

1.一次激活成功

2.多次激活（提示已激活）

3.激活失败

# 生成验证码

-导入jar包

-编写Kaptcha配置类

-生成随机字符、生成图片

？context——path不成功





# 登录

first：会话管理。



###### 1.访问登录页面



###### 2.登录

-验证账户、密码、验证码

-成功生成登录凭证

-失败跳转回登录页

复盘：

service验证账户密码、controller层加一个验证验证码。

验证成功就生成一个ticket放入ticket。

验证失败的信息存到map里返回让html显示。

（给一个ticket，每次登录生成一个，并没有解决重复登录问题？？）

###### 退出

-登录凭证失效

-跳转网站首页

# 显示登录信息

登录和游客显示的信息不同，不同的用户显示自己的讯息

##### 拦截器

-定义拦截器，实现handlerInterceptor

-配置拦截器，指定拦截、排除的路径

###### 应用

-在请求开始时查询登录用户

-在本次请求中持有用户数据

-在模板视图中显示用户数据

-在请求结束时清理用户数据



![image-20210122194054651](C:\Users\linxiaolu\AppData\Roaming\Typora\typora-user-images\image-20210122194054651.png)

1.写个Inerceptor类

2.在WebMvc中注册（应用范围）

3.在静态页面使用Interceptor的数据。

###### 拦截器写法

```java
public class LoginTicketIntercepor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取凭证
        String ticket= CookieUtil.getValue(request,"ticket");
        if(ticket!=null){
            //查询凭证
            LoginTicket loginTicket=userService.findLoginTicket(ticket);
            if(loginTicket!=null && loginTicket.getStatus()==0 &&loginTicket.getExpired().after(new Date())){
                User user=userService.findUserById(loginTicket.getUserId());
                //threadlocal 线程隔离
                hostHolder.setUsers(user);
            }
        }
        return true;
    }
    //Controller--《postHandler》-model
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user=hostHolder.getUser();
        if(user!=null &&modelAndView!=null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
```

根据与controller和modelandView生成的时间

1.将User（登录状态）写入线程（*线程隔离*）

2.在Model加载之前就能将数据写到view里面

3.结束后销毁

###### 线程隔离

simplely：一个线程对应一个对象。因为服务器对用户是1对多。一个用户对应一个线程对应一个状态。

###### 账户设置

--上传文件

-请求必须是post

-表单：enctype=“multipart/form-data”

-Spring Mvc：通过MultipartFile处理上传数据

--步骤

-访问账户设置页面（然后全部复用index）

-上传头像

-获取头像

# 修改密码









# 检测登录状态

可以直接url跳转，不同的权限能去的页面应该限制。

1.拦截器

2.自定义注解

-常用的源注解

@Target：作用在什么上

@Retention：有效时间

@Document：生成文档需不需要

@Inherited：父类存在 子类是否继承

-读取

发射

Method.getDeclareAnnotations



###### 步骤

1.写annotion 规定权限（method）

2.写拦截器从handler拿到method，如果这个method是我们规定的注解 &&此时用户未登录 我们从response里面直接重定向

3.在webmvc注册拦截器





# *过滤敏感词

###### 前缀树

数据结构

-字符串检索、词频统计、字符串排序

-根结点无数据，根结点到叶子节点就是一个敏感词。

###### 敏感词过滤器

-定义前缀树

-根据敏感词，初始化前缀树

-编写过滤敏感词的方法

# 发布帖子

###### AJAX

异步的js和xml

-具有固定的模板，和Controller交互进行操作。

# 显示评论



# 添加评论

数据层：

-增加评论数量

-修改帖子的评论数量

业务层

-处理添加帖子的业务

-先添加评论，再更新帖子的评论数量

表现层：

-处理添加帖子评论的请求。

-设置添加评论的表单



# 私信列表

-私信列表

​	-当前用户的对话列表

​	-每个对话只显示一条最新的私信

​	-支持分页显示

-私信详情

​	-查询某个会话所包含的私信

​	-支持分页查询

# 发送私信

-发送私信

​	-采用异步的方式发送私信

​	-发送成功后刷新私信列表

-设置已读

​	-访问私信详情时将显示的私信设置为已读状态

# 统一异常处理

@ControllerAdvice



# 统一记录日志

AOP



aspect详细代码：

```
@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger= LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.social.community.community.service.*.*(..))")
    public void pointcut(){
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        //用户【1，2，3，4】在【ip】访问了 com.social
        ServletRequestAttributes attributes=(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request=attributes.getRequest();
        String ip=request.getRemoteHost();
        String now=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        String target=joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].",ip,now,target));

    }
}
```

# Redis

基于键值对的NoSQL数据库。

strings、hashes、lists、sets、sorted sets

Redis将数据都存放在内存里。将内存中的数据以快照RDB或日志AOF的形式保存到硬盘，以保证数据的安全性。

应用场景：缓存、排行榜、计数器、社交网络、消息队列。

## 语法

select 0-15

```
flushdb(清空）
```

String类型：

```
set key value 
例：set test:count 1
```

```
get key
例：get test:count
```

```
incr test:count
```

```
decr test:count
```

hash类型![hset](C:\Users\linxiaolu\Desktop\markdown_source\hset.png)

list类型

![image-20210210210131862](C:\Users\linxiaolu\Desktop\markdown_source\list.png)

list有序 可以左右各自push pop来模仿队列和栈。

![image-20210210210456753](C:\Users\linxiaolu\AppData\Roaming\Typora\typora-user-images\image-20210210210456753.png)

sort 可以随机弹出指定个数的元素/查看元素个数。

![image-20210210212927943](C:\Users\linxiaolu\AppData\Roaming\Typora\typora-user-images\image-20210210212927943.png)

给了score以此为排名。

![aaa](C:\Users\linxiaolu\Desktop\markdown_source\aaa.png)

# Spring整合Redis

1.dependency引入

2.redis基本数据结构

3.在项目中主要是创建RedisUtil创建统一的数据结构名，使用RedisTemplate或operations运用Redis

**部分笔记因更新丢失**

# Spring优化登录模块

-存储验证码

​	-频繁访问

​	-不需要永久保存（一段时间后失效）

​	-分布式存储Session共享的问题

-存储登录凭证

​	-访问频率高

-缓存用户信息

​	-访问频率高

# Kafka

#### 阻塞队列

-BlockingQueue

​	-解决线程通信的问题

​	-put、take

-生产者消费者模式

​	-生产者：产生数据的线程

​	-消费者：使用数据的线程

-实现类

​	-ArrayBlockingQueue

​	-LinkBlockingQueue

​	-priorityBlockingQueue、SynchronousQueue、DelayQueue

#### Kafka

-分布式的流媒体平台

-消息系统、日志收集、用户行为追踪；

特点

-高吞吐、消息持久化、高可靠性、高扩张性。

术语

-Broker（服务器）、Zookeeper（管理集群）

-Topic（发布订阅的地点）、Partition（topic的分区）、Offset（消息存放的索引）

-Leader Replica（主副本）、Follower Replica（从副本，从主副本赋值数据）

#### Spring整合Kafka

-引入依赖

-配置server、consumer

-访问kafka

-生产者

-消费者 @KafkaListener=（topics=（“”））

# 发布系统通知

触发事件

-评论

-点赞

-关注

处理事件

-封装事件对象

-开发生产者

-开发消费者

# 显示系统通知

-通知列表

​	-评论

​	-点赞

​	-关注
-通知详情

-未读消息

# Elasticsearch

-分布式、restful风格的搜索引擎

-支持各种类型的数据的索引

-速度快、实时的搜索服务

-便于水平扩展

术语

​	-索引（表）、类型（逐步废弃）、文档（行）、字段（列）

​	-集群、节点、分片、副本

# 社区搜索功能

-搜索服务

​	-帖子保存到es服务器

​	-从es服务器删除帖子

​	-从es服务器搜索帖子

-发布事件

​	-发布时，异步提交到es服务器

​	-增加评论，异步提交到es服务器

​	-在消费组件增加方法、消费帖子发布事件

-显示结果

​	-控制器处理搜索请求、在HTML显示搜索内容

# Security

由FILTER管理

# 置顶、加精、删除



# Redis高级数据类型

HyperLogLog

-基数算法用于完成独立总数的统计

-占据空间小

-不精确

Bitmap

-字符串

-按位存取，看成时byte数组

-适合存储大量的连续的布尔值

​                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         

# 网站数据统计

UV-

-独立访客、通过用户IP排重统计数据

-每次访问都要统计

-HyperLogLog

DAU

-日活跃用户、通过ID统计数据

-访问过一次，则认为其活跃

-Bitmap、、且可以统计精确的结果

# 任务执行和调度

-JDK线程池

​	-ExecutorService

​	-SchedueExecutorService（能执行定时任务）

-Spring线程池

​	-ThreadPoolTaskExecutor

​	-ThreadPoolTaskSchedule

-分布式定时任务

​	-Spring Quartz

生成长图

wkhtmltopdf

# 优化网站性能

Caffine

Jmeter

# 单元测试

-Spring boot testing

-Test Case

​	-保证测试方法的独立性

​	-初始化数据、执行测试代码、验证测试结果、清理测试数据。

​	-常用注解：

@BeforeClass @AfterClass 需用静态方法，每个方法调用前后都会调用一次 

@Before @After

# 项目监控

###### spring boot Actuator

Endpoints：监控应用的入口。内置/自定义

监控方式：HTTP/JMX

访问路径：/actuator/health

注意事项：按需配置暴露的端点，并对所有端点进行权限控制
