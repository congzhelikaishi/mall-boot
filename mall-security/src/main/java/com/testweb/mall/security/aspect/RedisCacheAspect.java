package com.testweb.mall.security.aspect;

import com.testweb.mall.security.annotation.CacheException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Redis缓存切面，防止Redis宕机影响正常业务逻辑
 */
@Aspect
@Component
@Order(2)
public class RedisCacheAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheAspect.class);

    @Pointcut("execution(public * com.testweb.mall.portal.service.*CacheService.*(..)) || execution(public * com.testweb.mall.service.*CacheService.*(..))")
    public void cacheAspect(){}

    @Around("cacheAspect()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable { // JoinPoint类，用来获取代理类和被代理类的信息
        //获取连接点签名
        Signature signature = joinPoint.getSignature(); // 获取到:修饰符+ 包名+组件名(类名) +方法名
        //将其转换为方法签名
        MethodSignature methodSignature = (MethodSignature) signature;
        // 1.通过方法签名获取被调用的目标方法  2.//获取方法 如：controller的login
        Method method = methodSignature.getMethod();
        Object result = null;
        try {
            result = joinPoint.proceed(); // 执行当前类所切入的目标方法
        } catch (Throwable throwable) {
            // 对于有@CacheException注解的方法，如果发生异常直接抛出
            // @CacheException注解应用到存储和获取验证码的方法上去，这里需要注意的是要应用在实现类上而不是接口上，因为isAnnotationPresent方法只能获取到当前方法上的注解，而不能获取到它实现接口方法上的注解
            if (method.isAnnotationPresent(CacheException.class)){ // isAnnotationPresent()如果指定元素注解在里面，就返回true
                throw throwable;
            }else {
                LOGGER.error(throwable.getMessage());
            }
        }
        return result;
    }
}
