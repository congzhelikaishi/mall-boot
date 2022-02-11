package com.testweb.mall.service;

import com.testweb.mall.model.UmsMemberLevel;
import java.util.List;
/**
 * 会员等级管理Service

 */
public interface UmsMemberLevelService {
    /*
     * 获取所有会员等级
     */
    List<UmsMemberLevel> list(Integer defaultStatus);
}
