package com.testweb.mall.config;

import com.testweb.mall.model.UmsResource;
import com.testweb.mall.security.component.DynamicSecurityService;
import com.testweb.mall.security.config.SecurityConfig;
import com.testweb.mall.service.UmsAdminService;
import com.testweb.mall.service.UmsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * mall-security模块相关配置
 */
@Configuration
@EnableWebSecurity  // EnableWebSecurity注解有两个作用,1: 加载了WebSecurityConfiguration配置类, 配置安全认证策略。2: 加载了AuthenticationConfiguration, 配置了认证信息
@EnableGlobalMethodSecurity(prePostEnabled=true)  // 1.开启基于方法的安全认证机制，也就是说在web层的controller启用注解机制的安全确认  2.只有加了@EnableGlobalMethodSecurity(prePostEnabled=true) 那么在上面使用的 @PreAuthorize(“hasAuthority(‘admin’)”)才会生效
public class MallSecurityConfig extends SecurityConfig {

    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsResourceService resourceService;

    @Bean
    public UserDetailsService userDetailsService() {
        //获取登录用户信息
        return username -> adminService.loadUserByUsername(username);
    }

    @Bean
    public DynamicSecurityService dynamicSecurityService() {  // 传入相关权限
        return new DynamicSecurityService() {
            @Override
            public Map<String, ConfigAttribute> loadDataSource() {
                Map<String, ConfigAttribute> map = new ConcurrentHashMap<>();
                List<UmsResource> resourceList = resourceService.listAll();  //查询全部资源
                for (UmsResource resource : resourceList) {
                    map.put(resource.getUrl(), new org.springframework.security.access.SecurityConfig(resource.getId() + ":" + resource.getName()));  // 1.资源url  2.资源名称
                }
                return map;  // 返回所有资源列表
            }
        };
    }
}
