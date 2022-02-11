package com.testweb.mall.service;

import com.testweb.mall.dto.OssCallbackResult;
import com.testweb.mall.dto.OssPolicyResult;

import javax.servlet.http.HttpServletRequest;

/**
 * Oss对象存储管理Service
 */
public interface OssService {
    /*
    Oss上传策略生成
     */
    OssPolicyResult policy();

    /*
    Oss上传成功回调
     */
    OssCallbackResult callback(HttpServletRequest request);
}
