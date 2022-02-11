package com.testweb.mall.security.component;

import cn.hutool.core.collection.CollUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Iterator;

/**
 * 动态权限决策管理器，用于判断用户是否有访问权限
 * authentication 当前正在请求受包含对象的Authentication
 * o 受保护对象，其可以是一个MethodInvocation、JoinPoint或FilterInvocation。
 * collection 与正在请求的受保护对象相关联的配置属性
 * AuthenticationManager这个接口方法非常奇特，入参和返回值的类型都是。该接口的作用是对用户的未授信凭据进行认证，认证通过则返回授信状态的凭据，否则将抛出认证异常
 */
public class DynamicAccessDecisionManager implements AccessDecisionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicAccessDecisionManager.class);

    // decide方法中的configAttributes参数会通过SecurityMetadataSource中的getAttributes方法来获取，configAttributes其实就是配置好的访问当前接口所需要的权限
    // Authentication为SecurityContextHolder.getContext().setAuthentication(authentication); 传递authentication对象，来建立安全上下文（security context）
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException { // authentication是当前用户的对应权限列表
        // 当接口未被配置资源时直接放行
        if (CollUtil.isEmpty(configAttributes)){// 判断集合是否为空
            return;
        }
        // 所请求的资源拥有的权限(一个资源对多个权限)
        Iterator<ConfigAttribute> iterable = configAttributes.iterator(); // 迭代器，遍历容器的所有元素，Iterator 则主要用于遍历（即迭代访问）Collection 集合中的元素
        while (iterable.hasNext()){ // boolean hasNext()：如果被迭代的集合元素还没有被遍历完，则返回 true
            ConfigAttribute configAttribute = iterable.next(); // Object next()：返回集合里的下一个元素
            // 将访问所需资源和用户拥有资源进行比对
            String needAuthority = configAttribute.getAttribute(); // 访问所请求资源所需要的权限
            // grantedAuthority 为用户所被赋予的权限。 needAuthority 为访问相应的资源应该具有的权限
            for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {// 1.遍历列表  2.用户所拥有的权限authentication列表  3.getAuthorities()用户权限信息列表，默认是GrantedAuthority接口的一些实现类，通常是代表权限信息的一系列字符串
                if (needAuthority.trim().equals(grantedAuthority.getAuthority())){ // 1.trim() 方法用于删除字符串的头尾空白符  2.getAuthority()获取已授予的权限
                    return;
                }
            }
        }
        throw new AccessDeniedException("抱歉，您没有访问权限");
    }
    /*
    需要实现AccessDecisionManager接口来实现权限校验，
    对于没有配置资源的接口我们直接允许访问，对于配置了资源的接口，我们把访问所需资源和用户拥有的资源进行比对，如果匹配则允许访问
     */
    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
