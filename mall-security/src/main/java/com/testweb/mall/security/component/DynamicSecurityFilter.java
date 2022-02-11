package com.testweb.mall.security.component;

import com.testweb.mall.security.config.IgnoreUrlsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
/**
 * 动态权限过滤器，用于实现基于路径的动态权限过滤
 * SpringSecurity实现权限动态管理，第一步需要创建一个过滤器,doFilter方法需要注意,对于OPTIONS直接放行,否则会出现跨域问题。并且对在上篇文章提到的IgnoreUrlsConfig中的白名单也是直接放行，所有的权限操作都会在super.beforeInvocation(fi)中实现
 */
public class DynamicSecurityFilter extends AbstractSecurityInterceptor implements Filter {

    @Autowired
    private DynamicSecurityMetadataSource dynamicSecurityMetadataSource;

    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    @Autowired
    public void setMyAccessDecisionManager(DynamicAccessDecisionManager dynamicAccessDecisionManager){
        super.setAccessDecisionManager(dynamicAccessDecisionManager);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        FilterInvocation fi = new FilterInvocation(servletRequest, servletResponse, filterChain); // FilterInvocation对象你可以当作它封装了request，它的主要工作就是拿请求里面的信息，比如请求的URI
        // OPTIONS请求直接放行
        if (request.getMethod().equals(HttpMethod.OPTIONS.toString())){ // 1.request.getMethod()获取请求方式 2.发送了两次请求，第一次为OPTIONS请求，第二次才GET/POST...请求在OPTIONS请求中，不会携带请求头的参数，所以在拦截器上获取请求头为空，自定义的拦截器拦截成功,第一次请求不能通过，就不能获取第二次的请求了GET/POST...,第一次请求不带参数，第二次请求才带参数 3.OPTIONS请求直接放行，否则前端调用会出现跨域问题
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse()); // 1.访问目标Controller 2.执行下一个拦截器
            return;
        }
        // 白名单请求放行
        PathMatcher pathMatcher = new AntPathMatcher();// 用于URL的匹配
        for (String path : ignoreUrlsConfig.getUrls()){
            if (pathMatcher.match(path, request.getRequestURI())){
                fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
                return;
            }
        }
        // 1.此处会调用AccessDecisionManager中的decide方法进行鉴权操作,父类的方法去拦截权限 2.所有的鉴权操作都会在super.beforeInvocation(fi)中进行
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            // 访问目标Controller
            // 执行下一个拦截器
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } finally {
            // 获取请求后的操作,都会执行这一步
            super.afterInvocation(token, null);  // afterInvocation()方法实现了对返回结果的处理，在注入了AfterInvocationManager的情况下默认会调用其decide()方法
        }
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return dynamicSecurityMetadataSource;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

}
/*
调用super.beforeInvocation(fi)方法时会调用AccessDecisionManager中的decide方法用于鉴权操作,
而decide方法中的configAttributes参数会通过SecurityMetadataSource中的getAttributes方法来获取，
 configAttributes其实就是配置好的访问当前接口所需要的权限
 */