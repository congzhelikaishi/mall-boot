package com.testweb.mall.controller;

import com.testweb.mall.api.CommonPage;
import com.testweb.mall.api.CommonResult;
import com.testweb.mall.dto.OmsOrderReturnApplyResult;
import com.testweb.mall.dto.OmsReturnApplyQueryParam;
import com.testweb.mall.dto.OmsUpdateStatusParam;
import com.testweb.mall.model.OmsOrderReturnApply;
import com.testweb.mall.service.OmsOrderReturnApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单退货申请管理Controller
 */
@RestController
@Api(tags = "OmsOrderReturnApplyController", description = "订单退货申请管理")
@RequestMapping("/returnApply")
public class OmsOrderReturnApplyController {
    @Autowired
    private OmsOrderReturnApplyService returnApplyService;

    @ApiOperation("分页查询退货申请")
    @GetMapping("/list")
    public CommonResult<CommonPage<OmsOrderReturnApply>> list(OmsReturnApplyQueryParam queryParam,
                                                              @RequestParam(value = "/pageSize", defaultValue = "5") Integer pageSize,
                                                              @RequestParam(value = "pageNum" , defaultValue = "1") Integer pageNum){
        List<OmsOrderReturnApply> returnApplyList = returnApplyService.list(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(returnApplyList));
    }

    @ApiOperation("批量删除退货申请")
    @PostMapping("/delete")
    public CommonResult delete(@RequestParam("ids") List<Long> ids){
        int count = returnApplyService.delete(ids);
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取退货申请详情")
    @GetMapping("/{id}")
    public CommonResult getItem(@PathVariable Long id){
        OmsOrderReturnApplyResult result = returnApplyService.getItem(id);
        return CommonResult.success(result);
    }

    @ApiOperation("修改退货申请状态")
    @PostMapping("/update/status/{id}")
    public CommonResult updateStatus(@PathVariable Long id, @RequestBody OmsUpdateStatusParam statusParam){
        int count = returnApplyService.updateStatus(id, statusParam);
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
