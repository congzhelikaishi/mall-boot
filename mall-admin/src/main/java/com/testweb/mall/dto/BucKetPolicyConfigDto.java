package com.testweb.mall.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Minio Bucket访问策略配置
 */
@Data
@EqualsAndHashCode(callSuper = false) //表达为在对象比较时不会考虑父类中的成员,仅仅比较子类中的属性就判断是否相同
@Builder
public class BucKetPolicyConfigDto {
    private String Version;
    private List<Statement> Statement;

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    public static class Statement{
        private String Effect;
        private String Principal;
        private String Action;
        private String Resource;
    }
}
