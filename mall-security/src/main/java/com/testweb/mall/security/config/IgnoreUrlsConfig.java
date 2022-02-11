package com.testweb.mall.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于配置白名单资源路径
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "secure.ignored") // @ConfigurationProperties自定义配置文件中的前缀
public class IgnoreUrlsConfig {
    private List<String> urls = new ArrayList<>(); // urls为配置内容
}
