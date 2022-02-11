package com.testweb.mall.controller;

import com.testweb.mall.api.CommonPage;
import com.testweb.mall.api.CommonResult;
import com.testweb.mall.model.PmsBrand;
import com.testweb.mall.model.PmsProduct;
import com.testweb.mall.service.PortalBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页品牌推荐管理Controller
 */
@RestController
@Api(tags = "PortalBrandController", description = "前台品牌管理")
@RequestMapping("/brand")
public class PortalBrandController {

    @Autowired
    private PortalBrandService homeBrandService;

    @ApiOperation("分页获取推荐品牌")
    @GetMapping("/recommendList")
    public CommonResult<List<PmsBrand>> recommendList(@RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsBrand> brandList = homeBrandService.recommendList(pageNum, pageSize);
        return CommonResult.success(brandList);
    }

    @ApiOperation("获取品牌详情")
    @GetMapping("/detail/{brandId}")
    public CommonResult<PmsBrand> detail(@PathVariable Long brandId) {
        PmsBrand brand = homeBrandService.detail(brandId);
        return CommonResult.success(brand);
    }

    @ApiOperation("分页获取品牌相关商品")
    @GetMapping("/productList")
    @ResponseBody
    public CommonResult<CommonPage<PmsProduct>> productList(@RequestParam Long brandId,
                                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize) {
        CommonPage<PmsProduct> result = homeBrandService.productList(brandId,pageNum, pageSize);
        return CommonResult.success(result);
    }
}
