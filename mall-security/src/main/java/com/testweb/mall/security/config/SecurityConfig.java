package com.testweb.mall.security.config;

import com.testweb.mall.security.component.*;
import com.testweb.mall.security.util.JwtTokenUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 对SpringSecurity的配置的扩展，支持自定义白名单资源路径和查询用户逻辑
 */
//@Configuration
//@EnableWebSecurity  // EnableWebSecurity注解有两个作用,1: 加载了WebSecurityConfiguration配置类, 配置安全认证策略。2: 加载了AuthenticationConfiguration, 配置了认证信息
//@EnableGlobalMethodSecurity(prePostEnabled=true)  // 1.开启基于方法的安全认证机制，也就是说在web层的controller启用注解机制的安全确认  2.只有加了@EnableGlobalMethodSecurity(prePostEnabled=true) 那么在上面使用的 @PreAuthorize(“hasAuthority(‘admin’)”)才会生效
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired(required = false)
    private DynamicSecurityService dynamicSecurityService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        // 不需要保护的资源允许访问
        for (String url : ignoreUrlsConfig().getUrls()){
            registry.antMatchers(url).permitAll();  // 除配置文件忽略路径其它所有请求都需经过认证和授权
        }

        // 允许跨域请求的OPTIONS请求
        registry.antMatchers(HttpMethod.OPTIONS)
                .permitAll();

        // 任何请求需要身份法
        registry.and()
                .authorizeRequests()
                .anyRequest()  // 任何请求
                .authenticated()  // 需要身份认证
                // 关闭跨站请求防护及不使用session
                .and()
                // 关闭跨站请求防护
                .csrf().disable()
                // 前后端分离采用JWT 不需要sessio
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 自定义权限拒绝处理类
                .and()
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler())  // 自定义返回结果：没有权限访问时
                .authenticationEntryPoint(restAuthenticationEntryPoint())  // 自定义返回结果：未登录或登录过期
                // 自定义权限拦截器JWT过滤器
                .and()
                .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        // 有动态权限配置时添加动态权限校验过滤器
        if (dynamicSecurityService != null){  // 动态权限不为空
            registry.and().addFilterBefore(dynamicSecurityFilter(), FilterSecurityInterceptor.class);
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())  // 在MallSecurityConfig中userDetailsService()获取用户信息
                .passwordEncoder(passwordEncoder());  // 自定义userDetailsService加密
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 官方内置加密接口
    }

    // JWT登录授权过滤器
    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {  //TODO 目前只知道有这个才可以正常拦截
        return super.authenticationManagerBean();
    }

    // 自定义返回结果：没有权限访问时
    @Bean
    public RestfulAccessDeniedHandler restfulAccessDeniedHandler() {
        return new RestfulAccessDeniedHandler();
    }

    // 自定义返回结果：未登录或登录过期
    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    // 用于配置白名单资源路径
    @Bean
    public IgnoreUrlsConfig ignoreUrlsConfig() {
        return new IgnoreUrlsConfig();
    }

    // JwtToken生成的工具类
    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }

    /*
    只有当dynamicSecurityService这个Bean 存在时，才会创建bean: `DynamicAccessDecisionManager`
     */
    // 动态权限决策管理器
    @ConditionalOnBean(name = "dynamicSecurityService")
    @Bean
    public DynamicAccessDecisionManager dynamicAccessDecisionManager() {
        return new DynamicAccessDecisionManager();
    }

    // 动态权限过滤器
    @ConditionalOnBean(name = "dynamicSecurityService")
    @Bean
    public DynamicSecurityFilter dynamicSecurityFilter() {
        return new DynamicSecurityFilter();
    }

    // 动态权限数据源，用于获取动态权限规则
    @ConditionalOnBean(name = "dynamicSecurityService")
    @Bean
    public DynamicSecurityMetadataSource dynamicSecurityMetadataSource() {
        return new DynamicSecurityMetadataSource();
    }
}
/*
I. Bean的存在与否作为条件
当Bean不存在时，创建一个默认的Bean

1. @ConditionalOnBean
要求bean存在时，才会创建这个bean；如我提供了一个bean名为，用于封装相关的操作；但是我这个bean需要依赖这个bean，只有当应用引入了redis的相关依赖，并存在这个bean的时候，我这个bean才会生效

@ConditionalOnMissingClass
class不存在时，才会加载bean
 */