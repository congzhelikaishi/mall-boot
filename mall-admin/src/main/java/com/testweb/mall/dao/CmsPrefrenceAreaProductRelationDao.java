package com.testweb.mall.dao;

import com.testweb.mall.model.CmsPrefrenceAreaProductRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 优选和商品关系自定义Dao
 */
@Repository
@Mapper
public interface CmsPrefrenceAreaProductRelationDao {
    /*
    批量创建
     */
    int insertList(@Param("list") List<CmsPrefrenceAreaProductRelation> prefrenceAreaProductRelationList);
}
