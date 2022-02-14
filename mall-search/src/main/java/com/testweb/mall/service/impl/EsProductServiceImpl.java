package com.testweb.mall.service.impl;

import com.testweb.mall.dao.EsProductDao;
import com.testweb.mall.domain.EsProduct;
import com.testweb.mall.domain.EsProductRelatedInfo;
import com.testweb.mall.repository.EsProductRepository;
import com.testweb.mall.service.EsProductService;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 搜索商品管理Service实现类
 */
@Service
public class EsProductServiceImpl implements EsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsProductServiceImpl.class);

    @Autowired
    private EsProductDao productDao;

    @Autowired
    private EsProductRepository productRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 从数据库中导入所有商品到ES
     */
    @Override
    public int importAll() {
        List<EsProduct> esProductList = productDao.getAllEsProductList(null);
        Iterable<EsProduct> esProductIterable = productRepository.saveAll(esProductList);
        Iterator<EsProduct> iterator = esProductIterable.iterator();  // 迭代循环
        int result = 0;
        while (iterator.hasNext()) {
            result++;
            iterator.next();
        }
        return result;
    }

    /**
     * 根据id删除商品
     */
    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * 根据id创建商品
     */
    @Override
    public EsProduct create(Long id) {
        EsProduct result = null;
        List<EsProduct> esProductList = productDao.getAllEsProductList(id);
        if (esProductList.size() > 0) {
            EsProduct esProduct = esProductList.get(0);
            result = productRepository.save(esProduct);
        }
        return result;
    }

    /**
     * 批量删除商品
     */
    @Override
    public void delete(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            List<EsProduct> esProductList = new ArrayList<>();
            for (Long id : ids) {
                EsProduct esProduct = new EsProduct();
                esProduct.setId(id);
                esProductList.add(esProduct);
            }
            productRepository.deleteAll(esProductList);
        }
    }

    /**
     * 根据关键字搜索名称或者副标题
     */
    @Override
    public Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);  // Pageable是包含请求的页面信息的接口。我们可以使用它的实现，它具有各种工厂方法：PageRequest  页面 - 从零开始的页面索引  size - 要返回的页面的大小
        return productRepository.findByNameOrSubTitleOrKeywords(keyword, keyword, keyword, pageable);
    }

    /**
     * 根据关键字搜索名称或者副标题复合查询
     */
    @Override
    public Page<EsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize,Integer sort) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //分页
        nativeSearchQueryBuilder.withPageable(pageable);
        //过滤
        if (brandId != null || productCategoryId != null) {
            // 搜索方式
            // 首先构造多关键字查询条件
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();  // 多个关键字组合查询boolQuery()
            if (brandId != null) {
                // 等值查询
                boolQueryBuilder.must(QueryBuilders.termQuery("brandId", brandId));  // must档必须完全匹配条件，相当于and   term query分词精确查询，关键字不支持分词
            }
            if (productCategoryId != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("productCategoryId", productCategoryId));
            }
            nativeSearchQueryBuilder.withFilter(boolQueryBuilder);  // 使用withFilter，对结果过滤
        }
        //搜索
        if (StringUtils.isEmpty(keyword)) {
            // 如果没有输入关键字，就匹配所有
            nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());  // withQuery查询条件   QueryBuilders.matchAllQuery()匹配所有文件,查询所有数据
        } else {
            // filter打分
            List<FunctionScoreQueryBuilder.FilterFunctionBuilder> filterFunctionBuilders = new ArrayList<>();  // 创建一个打分的动态数组
            filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("name", keyword),  // matchQuery字段匹配查询，关键字支持分词
                    ScoreFunctionBuilders.weightFactorFunction(10)));  // ScoreFunctionBuilders.weightFactorFunction(10):设置权重分值
            filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("subTitle", keyword),
                    ScoreFunctionBuilders.weightFactorFunction(5)));
            filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("keywords", keyword),
                    ScoreFunctionBuilders.weightFactorFunction(2)));
            FunctionScoreQueryBuilder.FilterFunctionBuilder[] builders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[filterFunctionBuilders.size()];  // 创建一个新的的FunctionScoreQueryBuilder.FilterFunctionBuilder数组，数组长度和filterFunctionBuilders一样
            filterFunctionBuilders.toArray(builders);  // 将filterFunctionBuilders对象转换为builders数组
            // 有查询，在查询结果后，对分数filter
            FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(builders)
                    .scoreMode(FunctionScoreQuery.ScoreMode.SUM)  // 设置权重分 求和模式   scoreMode:function的分值合并成一个综合的分值   FunctionScoreQuery.ScoreMode.SUM在DSL中代表SUM
                    .setMinScore(2);  // 设置权重分最低分，取出Score大于2的数据
            nativeSearchQueryBuilder.withQuery(functionScoreQueryBuilder);
        }
        //排序
        if(sort==1){
            //按新品从新到旧
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC));  // withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC)按照id字段降序
        }else if(sort==2){
            //按销量从高到低
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sale").order(SortOrder.DESC));
        }else if(sort==3){
            //按价格从低到高
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));
        }else if(sort==4){
            //按价格从高到低
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        }else{
            //按相关度
            nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));  // 按照打分默认排序
        }
        nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();  // 查询条件构造
        LOGGER.info("DSL:{}", searchQuery.getQuery().toString());  // 输出控制台日志，DSL构造的语句
        SearchHits<EsProduct> searchHits = elasticsearchRestTemplate.search(searchQuery, EsProduct.class);  // searchQuery构建的语句   EsProduct.class索引的文档
        if(searchHits.getTotalHits()<=0){  // getTotalHits()获得总条数
            return new PageImpl<>(null,pageable,0);
        }
        List<EsProduct> searchProductList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());  // SearchHit::getContent获取集合，在此用stream流循环生成list集合
        return new PageImpl<>(searchProductList,pageable,searchHits.getTotalHits());  // 返回分页信息   pageable分页格式信息  searchHits.getTotalHits()条目数
    }

    /**
     * 根据商品id推荐相关商品
     */
    @Override
    public Page<EsProduct> recommend(Long id, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        List<EsProduct> esProductList = productDao.getAllEsProductList(id);
        if (esProductList.size() > 0) {
            EsProduct esProduct = esProductList.get(0);
            String keyword = esProduct.getName();
            Long brandId = esProduct.getBrandId();
            Long productCategoryId = esProduct.getProductCategoryId();
            //根据商品标题、品牌、分类进行搜索
            List<FunctionScoreQueryBuilder.FilterFunctionBuilder> filterFunctionBuilders = new ArrayList<>();
            filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("name", keyword),
                    ScoreFunctionBuilders.weightFactorFunction(8)));
            filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("subTitle", keyword),
                    ScoreFunctionBuilders.weightFactorFunction(2)));
            filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("keywords", keyword),
                    ScoreFunctionBuilders.weightFactorFunction(2)));
            filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("brandId", brandId),
                    ScoreFunctionBuilders.weightFactorFunction(5)));
            filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("productCategoryId", productCategoryId),
                    ScoreFunctionBuilders.weightFactorFunction(3)));
            FunctionScoreQueryBuilder.FilterFunctionBuilder[] builders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[filterFunctionBuilders.size()];  // 创建一个新的的FunctionScoreQueryBuilder.FilterFunctionBuilder数组，数组长度和filterFunctionBuilders一样
            filterFunctionBuilders.toArray(builders);
            FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(builders)
                    .scoreMode(FunctionScoreQuery.ScoreMode.SUM)
                    .setMinScore(2);
            //用于过滤掉相同的商品
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id",id));
            //构建查询条件
            NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
            builder.withQuery(functionScoreQueryBuilder);  // 添加查询
            builder.withFilter(boolQueryBuilder);  // 添加过滤条件
            builder.withPageable(pageable);  // 添加分页
            NativeSearchQuery searchQuery = builder.build();
            LOGGER.info("DSL:{}", searchQuery.getQuery().toString());
            SearchHits<EsProduct> searchHits = elasticsearchRestTemplate.search(searchQuery, EsProduct.class);  // ElasticsearchTemplate 该对象是负责索引库操作的,可以对索引库进行增删改查操作
            if(searchHits.getTotalHits()<=0){
                return new PageImpl<>(null,pageable,0);
            }
            List<EsProduct> searchProductList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
            return new PageImpl<>(searchProductList,pageable,searchHits.getTotalHits());
        }
        return new PageImpl<>(null);
    }

    /**
     * 获取搜索词相关品牌、分类、属性
     */
    @Override
    public EsProductRelatedInfo searchRelatedInfo(String keyword) {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();  // nativeSearchQueryBuilder该参数用于构建各种搜索条件，主要用于封装参数使用
        //搜索条件
        if(StringUtils.isEmpty(keyword)){
            builder.withQuery(QueryBuilders.matchAllQuery());  // withQuery:构建一个关键字查询
        }else{
            builder.withQuery(QueryBuilders.multiMatchQuery(keyword,"name","subTitle","keywords"));  // multiMatchQuery()多字段匹配
        }
        //聚合搜索品牌名称
        builder.addAggregation(AggregationBuilders.terms("brandNames").field("brandName"));  //  按某个字段分组（设置一个叫 brandNames 聚合，聚合字段是brandName）  1.addAggregation:添加一个聚合擦操作比如mysql中的group by, count 等函数   2.AggregationBuilders.terms("brandNames").field("brandName")根据brandName进行分组统计，统计出的列别名叫brandNames   3.terms 取别名 等价于mysql product_name as 产品名称的概念(给聚合查询取得名   4.field 表示根据哪个域进行分组 es分类名称:brandName   5.AggregationBuilders.field 相当于sql中的group by
        //集合搜索分类名称
        builder.addAggregation(AggregationBuilders.terms("productCategoryNames").field("productCategoryName"));
        //聚合搜索商品属性，去除type=1的属性
        AbstractAggregationBuilder aggregationBuilder = AggregationBuilders.nested("allAttrValues","attrValueList")  // nested嵌套的聚合
                .subAggregation(AggregationBuilders.filter("productAttrs",QueryBuilders.termQuery("attrValueList.type",1))  // 1.filter聚合过滤   2.termQuery：不会对搜索词进行分词处理，而是作为一个整体与目标字段进行匹配，若完全匹配，则可查询到   3.subAggregation添加子查询到节点里面
                        .subAggregation(AggregationBuilders.terms("attrIds")
                                .field("attrValueList.productAttributeId")
                                    .subAggregation(AggregationBuilders.terms("attrValues")
                                        .field("attrValueList.value"))
                                            .subAggregation(AggregationBuilders.terms("attrNames")
                                                .field("attrValueList.name"))));
        builder.addAggregation(aggregationBuilder);
        NativeSearchQuery searchQuery = builder.build();
        SearchHits<EsProduct> searchHits = elasticsearchRestTemplate.search(searchQuery, EsProduct.class);
        return convertProductRelatedInfo(searchHits);
    }

    /**
     * 将返回结果转换为对象
     */
    private EsProductRelatedInfo convertProductRelatedInfo(SearchHits<EsProduct> response) {
        EsProductRelatedInfo productRelatedInfo = new EsProductRelatedInfo();
        //将返回的结果放入到一个map中
        Map<String, Aggregation> aggregationMap = response.getAggregations().getAsMap();
        //设置品牌
        Aggregation brandNames = aggregationMap.get("brandNames");  // 取出聚合属性
        List<String> brandNameList = new ArrayList<>();
        for(int i = 0; i<((Terms) brandNames).getBuckets().size(); i++){  // getBuckets().size()获取桶的数
            brandNameList.add(((Terms) brandNames).getBuckets().get(i).getKeyAsString());  // 获取当前桶的数据   getAsString()--转换成字符串
        }
        productRelatedInfo.setBrandNames(brandNameList);
        //设置分类
        Aggregation productCategoryNames = aggregationMap.get("productCategoryNames");
        List<String> productCategoryNameList = new ArrayList<>();
        for(int i=0;i<((Terms) productCategoryNames).getBuckets().size();i++){
            productCategoryNameList.add(((Terms) productCategoryNames).getBuckets().get(i).getKeyAsString());
        }
        productRelatedInfo.setProductCategoryNames(productCategoryNameList);
        //设置参数
        Aggregation productAttrs = aggregationMap.get("allAttrValues");
        List<? extends Terms.Bucket> attrIds = ((ParsedLongTerms) ((ParsedFilter) ((ParsedNested) productAttrs).getAggregations().get("productAttrs")).getAggregations().get("attrIds")).getBuckets();  // getAggregations().get("xxx")使用聚合请求 返回类型可以转换为Terms
        List<EsProductRelatedInfo.ProductAttr> attrList = new ArrayList<>();
        for (Terms.Bucket attrId : attrIds) {
            EsProductRelatedInfo.ProductAttr attr = new EsProductRelatedInfo.ProductAttr();
            attr.setAttrId((Long) attrId.getKey());
            List<String> attrValueList = new ArrayList<>();
            List<? extends Terms.Bucket> attrValues = ((ParsedStringTerms) attrId.getAggregations().get("attrValues")).getBuckets();  // jackson在序列化的时候，当terms聚合的key为字符串类型时，则会调用ParsedStringTerms类来转换处理字段
            List<? extends Terms.Bucket> attrNames = ((ParsedStringTerms) attrId.getAggregations().get("attrNames")).getBuckets();
            for (Terms.Bucket attrValue : attrValues) {
                attrValueList.add(attrValue.getKeyAsString());
            }
            attr.setAttrValues(attrValueList);
            if(!CollectionUtils.isEmpty(attrNames)){
                String attrName = attrNames.get(0).getKeyAsString();
                attr.setAttrName(attrName);
            }
            attrList.add(attr);
        }
        productRelatedInfo.setProductAttrs(attrList);
        return productRelatedInfo;
    }
}
/*
elasticsearchRestTemplate.queryForList是查询一个列表，用的就是ElasticsearchRestTemplate的一个对象实例；
NativeSearchQuery ：是springdata中的查询条件；
NativeSearchQueryBuilder ：用于建造一个NativeSearchQuery查询对象；
QueryBuilders ：设置查询条件，是ES中的类；
SortBuilders ：设置排序条件；
HighlightBuilder ：设置高亮显示；

QueryBuilders是ES中的查询条件构造器

QueryBuilders.boolQuery().should();//至少满足一个条件，这个文档就符合should，相当于or

聚合查询
一个桶就是满足特定条件的一个文档集合
分桶是达到最终目的的手段：提供了对文档进行划分的方法，从而让你能够计算需要的指标

 Aggregation集合
聚合可以是父子（嵌套）关系聚合，buckets 聚合作为父，metric 聚合作为子。
聚合也可以是兄弟关系聚合，buckets 聚合在前，pipeline 聚合在后
 */