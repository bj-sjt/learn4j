## session-redis  整合 spring-security 序列化时遇到的坑



### 添加依赖

```xml
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.session</groupId>
            <artifactId>spring-session-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>io.lettuce</groupId>
            <artifactId>lettuce-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
```



### Spring Security 配置类

```java
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        super.configure(http);
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> {
            List<GrantedAuthority> authorities =
                    AuthorityUtils.commaSeparatedStringToAuthorityList("admin");
            return new User("user", passwordEncoder().encode("123"), authorities);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
```

### redis 配置类

```java
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
```

### controller

```java
@RestController
@Slf4j
public class SessionController {

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}
```

### 浏览器输入localhost:8080/hello 抛出异常

```txt
org.springframework.data.redis.serializer.SerializationException: Could not read JSON: Cannot construct instance of `org.springframework.security.authentication.UsernamePasswordAuthenticationToken` (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
 at [Source: (byte[])"{"@class":"org.springframework.security.core.context.SecurityContextImpl","authentication":{"@class":"org.springframework.security.authentication.UsernamePasswordAuthenticationToken","authorities":["java.util.Collections$UnmodifiableRandomAccessList",[{"@class":"org.springframework.security.core.authority.SimpleGrantedAuthority","authority":"admin"}]],"details":{"@class":"org.springframework.security.web.authentication.WebAuthenticationDetails","remoteAddress":"0:0:0:0:0:0:0:1","sessionId":"8a0c"[truncated 436 bytes]; line: 1, column: 184] (through reference chain: org.springframework.security.core.context.SecurityContextImpl["authentication"]); nested exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `org.springframework.security.authentication.UsernamePasswordAuthenticationToken` (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
 at [Source: (byte[])"{"@class":"org.springframework.security.core.context.SecurityContextImpl","authentication":{"@class":"org.springframework.security.authentication.UsernamePasswordAuthenticationToken","authorities":["java.util.Collections$UnmodifiableRandomAccessList",[{"@class":"org.springframework.security.core.authority.SimpleGrantedAuthority","authority":"admin"}]],"details":{"@class":"org.springframework.security.web.authentication.WebAuthenticationDetails","remoteAddress":"0:0:0:0:0:0:0:1","sessionId":"8a0c"[truncated 436 bytes]; line: 1, column: 184] (through reference chain: org.springframework.security.core.context.SecurityContextImpl["authentication"])
	at org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer.deserialize(GenericJackson2JsonRedisSerializer.java:152) ~[spring-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer.deserialize(GenericJackson2JsonRedisSerializer.java:130) ~[spring-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.data.redis.core.AbstractOperations.deserializeHashValue(AbstractOperations.java:380) ~[spring-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.data.redis.core.AbstractOperations.deserializeHashMap(AbstractOperations.java:324) ~[spring-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.data.redis.core.DefaultHashOperations.entries(DefaultHashOperations.java:309) ~[spring-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.data.redis.core.DefaultBoundHashOperations.entries(DefaultBoundHashOperations.java:223) ~[spring-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.session.data.redis.RedisIndexedSessionRepository.getSession(RedisIndexedSessionRepository.java:457) ~[spring-session-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.session.data.redis.RedisIndexedSessionRepository.findById(RedisIndexedSessionRepository.java:429) ~[spring-session-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.session.data.redis.RedisIndexedSessionRepository.findById(RedisIndexedSessionRepository.java:251) ~[spring-session-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.session.web.http.SessionRepositoryFilter$SessionRepositoryRequestWrapper.getRequestedSession(SessionRepositoryFilter.java:356) ~[spring-session-core-2.6.0.jar:2.6.0]
	at org.springframework.session.web.http.SessionRepositoryFilter$SessionRepositoryRequestWrapper.getSession(SessionRepositoryFilter.java:290) ~[spring-session-core-2.6.0.jar:2.6.0]
	at org.springframework.session.web.http.SessionRepositoryFilter$SessionRepositoryRequestWrapper.getSession(SessionRepositoryFilter.java:193) ~[spring-session-core-2.6.0.jar:2.6.0]
	at javax.servlet.http.HttpServletRequestWrapper.getSession(HttpServletRequestWrapper.java:244) ~[tomcat-embed-core-9.0.55.jar:4.0.FR]
	at org.springframework.security.web.context.HttpSessionSecurityContextRepository.loadContext(HttpSessionSecurityContextRepository.java:118) ~[spring-security-web-5.6.0.jar:5.6.0]
	at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:98) ~[spring-security-web-5.6.0.jar:5.6.0]
	at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:80) ~[spring-security-web-5.6.0.jar:5.6.0]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-5.6.0.jar:5.6.0]
	at org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:55) ~[spring-security-web-5.6.0.jar:5.6.0]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.3.13.jar:5.3.13]
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:336) ~[spring-security-web-5.6.0.jar:5.6.0]
	at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:211) ~[spring-security-web-5.6.0.jar:5.6.0]
	at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:183) ~[spring-security-web-5.6.0.jar:5.6.0]
	at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:358) ~[spring-web-5.3.13.jar:5.3.13]
	at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:271) ~[spring-web-5.3.13.jar:5.3.13]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-5.3.13.jar:5.3.13]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.3.13.jar:5.3.13]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-5.3.13.jar:5.3.13]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.3.13.jar:5.3.13]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.springframework.session.web.http.SessionRepositoryFilter.doFilterInternal(SessionRepositoryFilter.java:142) ~[spring-session-core-2.6.0.jar:2.6.0]
	at org.springframework.session.web.http.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:82) ~[spring-session-core-2.6.0.jar:2.6.0]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-5.3.13.jar:5.3.13]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.3.13.jar:5.3.13]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:197) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:97) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:540) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:135) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:357) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:382) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:895) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1722) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at java.base/java.lang.Thread.run(Thread.java:833) ~[na:na]
```

### 错误原因

有一些类在反序列化时找不到无参构造方法

### 解决办法

spring 为解决这个错误 ，添加了许多Mixin 类，需要讲这些Mixin类添加到 ObjectMapper中

修改redis配置类

```java
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        // 为 ObjectMapper 注册 SecurityJackson2Modules 可以解决 反序列化错误
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(this.getClass().getClassLoader()));
        return new GenericJackson2JsonRedisSerializer(mapper);
    }
}
```



### 扩展

#### 如何我们需要将自己定义的bean也通过session - redis 保存，则会报错

#### 修改controller

```java
@RestController
@Slf4j
public class SessionController {

    @GetMapping("/session")
    public User user(HttpServletRequest request){
        User user = new User("user", "123456");
        request.getSession().setAttribute("user",user);
        log.info("{}", user);
        return user;
    }

    @GetMapping("/getUser")
    public User getUser(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        log.info("{}", user);
        return user;
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}
```

#### 添加bean

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private String name;
    private String password;
}

```

#### 浏览器输入localhost:8080/hello 跳到登录页面

输入正确的账号后，浏览器显示 hello

然后浏览器输入localhost:8080/session报错

```txt
org.springframework.data.redis.serializer.SerializationException: Could not read JSON: The class with com.itao.security.bean.User and name of com.itao.security.bean.User is not in the allowlist. If you believe this class is safe to deserialize, please provide an explicit mapping using Jackson annotations or by providing a Mixin. If the serialization is only done by a trusted source, you can also enable default typing. See https://github.com/spring-projects/spring-security/issues/4370 for details; nested exception is java.lang.IllegalArgumentException: The class with com.itao.security.bean.User and name of com.itao.security.bean.User is not in the allowlist. If you believe this class is safe to deserialize, please provide an explicit mapping using Jackson annotations or by providing a Mixin. If the serialization is only done by a trusted source, you can also enable default typing. See https://github.com/spring-projects/spring-security/issues/4370 for details
	at org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer.deserialize(GenericJackson2JsonRedisSerializer.java:152) ~[spring-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer.deserialize(GenericJackson2JsonRedisSerializer.java:130) ~[spring-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.data.redis.core.AbstractOperations.deserializeHashValue(AbstractOperations.java:380) ~[spring-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.data.redis.core.AbstractOperations.deserializeHashMap(AbstractOperations.java:324) ~[spring-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.data.redis.core.DefaultHashOperations.entries(DefaultHashOperations.java:309) ~[spring-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.data.redis.core.DefaultBoundHashOperations.entries(DefaultBoundHashOperations.java:223) ~[spring-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.session.data.redis.RedisIndexedSessionRepository.getSession(RedisIndexedSessionRepository.java:457) ~[spring-session-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.session.data.redis.RedisIndexedSessionRepository.findById(RedisIndexedSessionRepository.java:429) ~[spring-session-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.session.data.redis.RedisIndexedSessionRepository.findById(RedisIndexedSessionRepository.java:251) ~[spring-session-data-redis-2.6.0.jar:2.6.0]
	at org.springframework.session.web.http.SessionRepositoryFilter$SessionRepositoryRequestWrapper.getRequestedSession(SessionRepositoryFilter.java:356) ~[spring-session-core-2.6.0.jar:2.6.0]
	at org.springframework.session.web.http.SessionRepositoryFilter$SessionRepositoryRequestWrapper.getRequestedSessionId(SessionRepositoryFilter.java:338) ~[spring-session-core-2.6.0.jar:2.6.0]
	at org.springframework.session.web.http.SessionRepositoryFilter$SessionRepositoryRequestWrapper.commitSession(SessionRepositoryFilter.java:228) ~[spring-session-core-2.6.0.jar:2.6.0]
	at org.springframework.session.web.http.SessionRepositoryFilter$SessionRepositoryRequestWrapper.access$100(SessionRepositoryFilter.java:193) ~[spring-session-core-2.6.0.jar:2.6.0]
	at org.springframework.session.web.http.SessionRepositoryFilter.doFilterInternal(SessionRepositoryFilter.java:145) ~[spring-session-core-2.6.0.jar:2.6.0]
	at org.springframework.session.web.http.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:82) ~[spring-session-core-2.6.0.jar:2.6.0]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-5.3.13.jar:5.3.13]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.3.13.jar:5.3.13]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:197) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:97) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:540) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:135) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:357) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:382) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:895) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1722) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) ~[tomcat-embed-core-9.0.55.jar:9.0.55]
	at java.base/java.lang.Thread.run(Thread.java:833) ~[na:na]

```

#### 报错原因

在redis序列化时加入了

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModules(SecurityJackson2Modules.getModules(this.getClass().getClassLoader()));
```

导致Jacksjson 认为普通bean 是不安全的

#### 解决办法

可以根据仿照spring提供的Mixin类，编写自己的Mixin类

或者在bean的类上添加注解@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)

```java
/**
 * If you believe this class is safe to deserialize,
 * please provide an explicit mapping using Jackson annotations or by providing a Mixin.
 * If the serialization is only done by a trusted source, you can also enable default typing
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)  // 解决反序列化问题
public class User implements Serializable {

    private String name;
    private String password;
}
```

## 问题解决