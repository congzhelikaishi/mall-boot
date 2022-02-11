package com.testweb.mall.repository;

import com.testweb.mall.domain.MemberBrandAttention;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * 会员关注Repository
 * 可以根据需要通过方法名选择自己需要的查询方法，方法无需实现，直接使用即可
 * MongoRepository<行对应的对象类型, 主键列类型> 原生态提供了增删改查
 */
public interface MemberBrandAttentionRepository extends MongoRepository<MemberBrandAttention,String> {
    MemberBrandAttention findByMemberIdAndBrandId(Long memberId, Long brandId);

    int deleteByMemberIdAndBrandId(Long memberId,Long brandId);

    Page<MemberBrandAttention> findByMemberId(Long memberId, Pageable pageable);

    void deleteAllByMemberId(Long memberId);
}
/*
 可根据需求自己定义方法, 无需对方法进行实现
 */