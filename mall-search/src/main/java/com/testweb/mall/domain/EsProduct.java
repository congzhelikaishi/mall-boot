package com.testweb.mall.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 搜索商品信息
 */
@Data  // @Data相当于@Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode
@EqualsAndHashCode(callSuper = false)
/*
@equalsandhashcode(callsuper=false)表达为在对象比较时不会考虑父类中的成员,仅仅比较子类中的属性就判断是否相同
@equalsandhashcode(callsuper=true)在比较时会考虑父类中的成员,通过父类和子类中的属性一起判断是否相同
 */
@Document(indexName = "pms", shards = 1, replicas = 0)  // indexName()索引库的名称  shards()默认分区数   replicas()每个分区默认的 备份数
public class EsProduct implements Serializable {
    private static final long serialVersionUID = -1L;
    @Id
    private Long id;
    @Field(type = FieldType.Keyword)  // type()自动检测属性的类型，可以根据实际情况自己设置   FieldType.Keyword表示字段格式在es中字段格式为keyword,不能使用分词,只能完全匹配,这个是我们自定义的要生成到ElasticSearch中的
    private String productSn;
    private Long brandId;
    @Field(type = FieldType.Keyword)
    private String brandName;
    private Long productCategoryId;
    @Field(type = FieldType.Keyword)
    private String productCategoryName;
    private String pic;
    @Field(analyzer = "ik_max_word",type = FieldType.Text)  // analyzer分词器名称   analyzer = "ik_max_word",type = FieldType.Text表示在es中,字段为text格式使用ik_max_word分词方式进行查询
    private String name;
    @Field(analyzer = "ik_max_word",type = FieldType.Text)
    private String subTitle;
    @Field(analyzer = "ik_max_word",type = FieldType.Text)
    private String keywords;
    private BigDecimal price;
    private Integer sale;
    private Integer newStatus;
    private Integer recommandStatus;
    private Integer stock;
    private Integer promotionType;
    private Integer sort;
    @Field(type =FieldType.Nested)
    private List<EsProductAttributeValue> attrValueList;
}
/*
@Field
FieldType type() default FieldType.Auto; //自动检测属性的类型，可以根据实际情况自己设置
FieldIndex index() default FieldIndex.analyzed; //默认情况下分词，一般默认分词就好，除非这个字段你确定查询时不会用到
DateFormat format() default DateFormat.none; //时间类型的格式化
String pattern() default "";
boolean store() default false; //默认情况下不存储原文
String searchAnalyzer() default ""; //指定字段搜索时使用的分词器
String indexAnalyzer() default ""; //指定字段建立索引时指定的分词器
String[] ignoreFields() default {}; //如果某个字段需要被忽略
boolean includeInParent() default false;

@Document
String indexName(); //索引库的名称，个人建议以项目的名称命名
String type() default ""; //类型，个人建议以实体的名称命名
short shards() default 5; //默认分区数
short replicas() default 1; //每个分区默认的备份数
String refreshInterval() default "1s"; //刷新间隔
String indexStoreType() default "fs"; //索引文件存储类型

为文档自动指定元数据类型
public enum FieldType {
    Text,//会进行分词并建了索引的字符类型
    Integer,
    Long,
    Date,
    Float,
    Double,
    Boolean,
    Object,
    Auto,//自动判断字段类型
    Nested,//嵌套对象类型
    Ip,
    Attachment,
    Keyword//不会进行分词建立索引的类型

ik_smart：智能切分 最少切分 粗粒度 分出的词较少
ik_max_word：最细切分 细粒度 分出的词较多 内存消耗高
}
 */