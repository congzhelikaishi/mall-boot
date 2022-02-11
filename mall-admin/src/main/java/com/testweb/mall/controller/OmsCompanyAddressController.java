package com.testweb.mall.controller;

import com.testweb.mall.api.CommonResult;
import com.testweb.mall.model.OmsCompanyAddress;
import com.testweb.mall.service.OmsCompanyAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 收货地址管理Controller
 */
@RestController
@Api(tags = "OmsCompanyAddressController", description = "收货地址管理")
@RequestMapping("/companyAddress")
public class OmsCompanyAddressController {
    @Autowired
    private OmsCompanyAddressService companyAddressService;

    @ApiOperation("获取所有收货地址")
    @GetMapping("/list")
    public CommonResult<List<OmsCompanyAddress>> list(){
        List<OmsCompanyAddress> companyAddressesList = companyAddressService.list();
        return CommonResult.success(companyAddressesList);
    }

}
