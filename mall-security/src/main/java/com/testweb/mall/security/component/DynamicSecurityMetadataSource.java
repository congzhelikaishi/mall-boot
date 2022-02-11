package com.testweb.mall.security.component;

import cn.hutool.core.util.URLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 动态权限数据源，用于获取动态权限规则
 */
public class DynamicSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicSecurityMetadataSource.class);
    private static Map<String, ConfigAttribute> configAttributeMap = null;
    /*
    AbstractSecurityInterceptor的beforeInvocation()方法内部在进行鉴权的时候使用的是注入的AccessDecisionManager的decide()方法进行的。
    如前所述，decide()方法是需要接收一个受保护对象对应的ConfigAttribute集合的。
    一个ConfigAttribute可能只是一个简单的角色名称，具体将视AccessDecisionManager的实现者而定。
    AbstractSecurityInterceptor将使用一个SecurityMetadataSource对象来获取与受保护对象关联的ConfigAttribute集合，
    具体SecurityMetadataSource将由子类实现提供。ConfigAttribute将通过注解的形式定义在受保护的方法上，
    或者通过access属性定义在受保护的URL上。例如我们常见的<intercept-url pattern=”/**” access=”ROLE_USER,ROLE_ADMIN”/>就表示将ConfigAttribute ROLE_USER和ROLE_ADMIN应用在所有的URL请求上。
    对于默认的AccessDecisionManager的实现，上述配置意味着用户所拥有的权限中只要拥有一个GrantedAuthority与这两个ConfigAttribute中的一个进行匹配则允许进行访问。
    当然，严格的来说ConfigAttribute只是一个简单的配置属性而已，具体的解释将由AccessDecisionManager来决定


    在DynamicSecurityFilter中调用super.beforeInvocation(fi)方法时会调用AccessDecisionManager中的decide方法用于鉴权操作,
    而decide方法中的configAttributes参数会通过SecurityMetadataSource中的getAttributes方法来获取，
    configAttributes其实就是配置好的访问当前接口所需要的权限
     */

    @Autowired
    private DynamicSecurityService dynamicSecurityService;
 
    @PostConstruct // 被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器执行一次。PostConstruct在构造函数之后执行，init（）方法之前执行
    public void loadDataSource(){
        configAttributeMap = dynamicSecurityService.loadDataSource(); // 在configAttributeMap传入dynamicSecurityService的资源ANT通配符和资源对应MAP
    }

    public void clearDataSource(){
        configAttributeMap.clear(); // 清空集合
        configAttributeMap = null;
    }
    /*
     后台资源规则被缓存在了一个Map对象之中，所以当后台资源发生变化时，我们需要清空缓存的数据，然后下次查询时就会被重新加载进来。
     这里我们需要修改UmsResourceController类，注入DynamicSecurityMetadataSource，
      当修改后台资源时，需要调用clearDataSource方法来清空缓存的数据
      */

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        if (configAttributeMap == null) this.loadDataSource(); // 如果集合为空就去执行loadDataSource()方法
        List<ConfigAttribute> configAttributes = new ArrayList<>();

        // 访问当前路径
        String url = ((FilterInvocation) o).getRequestUrl();
        /*
        1.把doFilter传进来的request,response和FilterChain对象保存起来，供FilterSecurityInterceptor的处理代码调用
        FilterInvocation filterInvocation = (FilterInvocation) object;
        String url = filterInvocation.getRequestUrl();
         */

        String path = URLUtil.getPath(url); // 将访问路径整理 比如:访问https://blog.csdn.net/?id=13  URLUtil.getPath(url):将路径转化为https://blog.csdn.net/

        PathMatcher pathMatcher = new AntPathMatcher(); // 用于URL的匹配

        // 主要获取configAttributeMap集合的key值并转化为迭代器，并且便于访问遍历
        Iterator<String> iterator = configAttributeMap.keySet().iterator();  // 1.Iterator迭代器集合 2.keySet获得的只是key值的集合 3.iterator()去获得这个集合的迭代器，保存在iter里面,以便后面遍历

        // 获取访问该路径所在资源
        while (iterator.hasNext()) {
            String pattern = iterator.next(); // 所有的资源路径
            if (pathMatcher.match(pattern, path)){ // 根据当前 PathMatcher 的匹配策略，检查指定的径 path 和指定的模式 pattern 是否之间是否为前缀匹配
                /*
                pathMatcher.match("/user/001","/user/001");// 返回 true
                pathMatcher.match("/user/*","/user/001");// 返回 true
                 */
                configAttributes.add(configAttributeMap.get(pattern)); // configAttributes集合里添加路径
                // LOGGER.info("资源:{}", pattern);
            }
        }
        // 未设置操作请求权限， 返回空集合
        return configAttributes;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
