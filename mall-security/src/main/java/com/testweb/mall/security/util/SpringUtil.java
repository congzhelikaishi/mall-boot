package com.testweb.mall.security.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring工具类
 * 获取Spring容器中bean工具类
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // Spring容器启动后，会把 applicationContext 给自动注入进来，然后我们把 applicationContext 赋值到静态变量中，方便后续拿到容器对象
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    // 通过name获取Bean
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    // 通过class获取Bean
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    // 通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
/*
ApplicationContextAware 通过它Spring容器会自动把上下文环境对象调用ApplicationContextAware接口中的setApplicationContext方法。
我们在ApplicationContextAware的实现类中，就可以通过这个上下文环境对象得到Spring容器中的Bean。
看到—Aware就知道是干什么的了，就是属性注入的，但是这个ApplicationContextAware的不同地方在于，实现了这个接口的bean，
当spring容器初始化的时候，会自动的将ApplicationContext注入进来
因为我们在做开发的时候，并不是说在每一个地方都能将属性注入到我们想要的地方去的，比如在Utils使用到dao，我们就不能直接注入了，
这个时候就是我们需要封装springContext的时候了，而ApplicationContextAware就起了关键性的作用

SpringMVC中还好，虽然可以自动初始化容器，但是我们依旧可以通过那三个实现类获取ApplicationContext对象，
但是在SpringBoot中，因为没有了ioc配置文件，全都成自动化的了，我们无法通过上述方式拿到ApplicationContext对象，
但有时候遇到的需求是必须要通过Spring容器对象才能实现的，例如将所有三方渠道的代理类加载到ioc容器，然后在代码执行过程中用ioc容器对象的getBean()动态获取某一个三方渠道的代理类对象并执行对应的方法，
所以，简单地说，ApplicationContextAware接口是用来获取框架自动初始化的ioc容器对象的

Spring中管理的对象有两类，一类是用户自定义的对象，一类是Spring容器对象，比如applicationContext、beanFactory等。用户自定义的对象，在注入时可以直接使用@Autowared等方法，但是如果需要使用容器对象，该怎么注入呢？
Spring提供了一个扩展方法，即ApplicationContextAware接口，实现容器对象的注入
 */