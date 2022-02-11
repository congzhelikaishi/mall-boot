package com.testweb.mall.controller;

import com.testweb.mall.api.CommonPage;
import com.testweb.mall.api.CommonResult;
import com.testweb.mall.domain.MemberProductCollection;
import com.testweb.mall.service.MemberCollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 会员商品收藏管理Controller
 */
@RestController
@Api(tags = "MemberCollectionController", description = "会员收藏管理")
@RequestMapping("/member/productCollection")
public class MemberProductCollectionController {
    @Autowired
    private MemberCollectionService memberCollectionService;

    @ApiOperation("添加商品收藏")
    @PostMapping("/add")
    public CommonResult add(@RequestBody MemberProductCollection productCollection) {  // @RequestBody
        int count = memberCollectionService.add(productCollection);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("删除商品收藏")
    @PostMapping("/delete")
    public CommonResult delete(Long productId) {
        int count = memberCollectionService.delete(productId);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("显示商品收藏列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<MemberProductCollection>> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        Page<MemberProductCollection> page = memberCollectionService.list(pageNum,pageSize);
        return CommonResult.success(CommonPage.restPage(page));
    }

    @ApiOperation("显示商品收藏详情")
    @GetMapping("/detail")
    public CommonResult<MemberProductCollection> detail(@RequestParam Long productId) {
        MemberProductCollection memberProductCollection = memberCollectionService.detail(productId);
        return CommonResult.success(memberProductCollection);
    }

    @ApiOperation("清空商品收藏列表")
    @PostMapping("/clear")
    public CommonResult clear() {
        memberCollectionService.clear();
        return CommonResult.success(null);
    }
}

