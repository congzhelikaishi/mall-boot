package com.testweb.mall.controller;

import cn.hutool.core.collection.CollUtil;
import com.testweb.mall.api.CommonPage;
import com.testweb.mall.api.CommonResult;
import com.testweb.mall.dto.UmsAdminLoginParam;
import com.testweb.mall.dto.UmsAdminParam;
import com.testweb.mall.dto.UpdateAdminPasswordParam;
import com.testweb.mall.model.UmsAdmin;
import com.testweb.mall.model.UmsRole;
import com.testweb.mall.service.UmsAdminService;
import com.testweb.mall.service.UmsRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
后台用户管理Controller
 */
@RestController
@Api(tags = "UmsAdminController", description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsRoleService roleService;

    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public CommonResult<UmsAdmin> register(@Validated @RequestBody UmsAdminParam umsAdminParam){ // 1.@Validated:对传输参数进行较验的注解,例如:@Email,@NotEmpty，配合着自义定全局异常的参数异常处理(handleValidException)使用，否则为默认抛出异常    2.@RequestBody直接以String接收前端传过来的json数据
        UmsAdmin umsAdmin = adminService.register(umsAdminParam); // 调用adminServer的注册功能,传入前端获取的值
        if (umsAdmin == null){ // 如果返回的集合为空
            return CommonResult.failed(); // 返回操作失败
        }
        return CommonResult.success(umsAdmin); // 返回操作成功
    }

    @ApiOperation(value = "登录以后返回token")
    @PostMapping("/login")
    public CommonResult login(@Validated @RequestBody UmsAdminLoginParam umsAdminLoginParam){
        String token = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword()); // 登录并且返回token令牌
        if (token == null){
            return CommonResult.validateFailed("用户名或密码错误"); // 参数验证失败返回失败结果
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap); // 成功并且返回成功结果
    }

    @ApiOperation(value = "刷新token")
    @GetMapping("/refreshToken")
    public CommonResult refreshToken(HttpServletRequest request){
        String token = request.getHeader(tokenHeader); //获取单个请求头name对应的value值
        String refreshToken = adminService.refreshToken(token); // 刷新token，如果没有过期则可以返回
        if (refreshToken == null){
            return CommonResult.failed("token已经过期!"); // 返回错误结果
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @GetMapping("/info")
    public CommonResult getAdminInfo(Principal principal){ // Principal是一个包含用户的标识和用户的所属角色的对象，Spring 会将 Username、Password、Authentication、Token 注入到 Principal 接口中
        if(principal == null){
            return CommonResult.unauthorized(null); // 未登录结果返回
        }
        String username = principal.getName(); // 获取当前用户名
        UmsAdmin umsAdmin = adminService.getAdminByUsername(username); // 根据用户名获取后台用户管理员
        Map<String, Object> data = new HashMap<>();
        data.put("username", umsAdmin.getUsername());
        data.put("menus", roleService.getMenuList(umsAdmin.getId()));
        data.put("icon", umsAdmin.getIcon());
        List<UmsRole> roleList = adminService.getRoleList(umsAdmin.getId()); // 获取用户对应角色列表
        if (CollUtil.isNotEmpty(roleList)){ // 判断聚合是否为空
            List<String> roles = roleList.stream().map(UmsRole::getName).collect(Collectors.toList()); // 获取列表中所有用户的用户名集合 list.stream().map().collect(Collectors.toList())
            data.put("roles", roles); // 放入角色对应列表
        }
        return CommonResult.success(data); // 返回成功结果并且返回数据
    }

    @ApiOperation(value = "登出功能")
    @PostMapping("/logout")
    public CommonResult logout(){
        return CommonResult.success(null);
    }

    @ApiOperation(value = "根据用户名或姓名分页获取用户列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsAdmin>> list (@RequestParam(value = "keyword", required = false) String keyword, // 模糊查询用的用户名
                                                    @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize, // 提前设置每页数量
                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNu){ // 提前设置当前页码
        List<UmsAdmin> adminList = adminService.list(keyword, pageSize, pageNu); // 根据用户名或昵称分页查询用户
        return CommonResult.success(CommonPage.restPage(adminList)); // 将PageHelper分页后的list转为分页信息
    }

    @ApiOperation(value = "获取指定用户信息")
    @GetMapping("/{id}")
    public CommonResult<UmsAdmin> getItem(@PathVariable Long id){
        UmsAdmin admin = adminService.getItem(id); // 根据id获取用户
        return CommonResult.success(admin);
    }

    @ApiOperation("修改指定用户信息")
    @PostMapping("/update/{id}")
    public CommonResult update(@PathVariable Long id, @RequestBody UmsAdmin admin){
        int count = adminService.update(id, admin); // 修改指定用户信息
        if (count > 0){ // 数据库操作成功返回1
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改指定用户密码")
    @PostMapping("/updatePassword")
    public CommonResult updatePassword(@Validated @RequestBody UpdateAdminPasswordParam updateAdminPasswordParam){
        int status = adminService.updatePassword(updateAdminPasswordParam); // 修改密码
        if (status > 0){
            return CommonResult.success(status);
        }else if (status == -1){
            return CommonResult.failed("提交参数不合法");
        }else if (status == -2){
            return CommonResult.failed("找不到该用户");
        }else if (status == -3){
            return CommonResult.failed("就密码错误");
        }else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("删除指定用户信息")
    @PostMapping("/delete/{id}")
    public CommonResult delete(@PathVariable Long id){
        int count = adminService.delete(id); // 删除指定用户信息
        if (count > 0){ // 数据库操作成功返回1
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改账号状态")
    @PostMapping("/updateStatus/{id}")
    public CommonResult updateStatus(@PathVariable Long id, @RequestParam(value = "status") Integer status){
        UmsAdmin umsAdmin = new UmsAdmin();
        umsAdmin.setStatus(status);
        int count = adminService.update(id, umsAdmin); // 修改用户状态并且返回数据库操作结果
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("给用户分配角色")
    @PostMapping("/role/update")
    public CommonResult updateRole(@RequestParam("adminId") Long adminId,
                                   @RequestParam("roleIds") List<Long> roleIds){
        int count = adminService.updateRole(adminId, roleIds); // 修改用户角色关系
        if (count >= 0){ // 没有具体操作或者已经操作成功
            return CommonResult.success(count);
        }
        return  CommonResult.failed();
    }

    @ApiOperation("获取指定用户的角色名")
    @GetMapping("/role/{adminId}")
    public CommonResult<List<UmsRole>> getRoleList(@PathVariable Long adminId){
        List<UmsRole> roleList = adminService.getRoleList(adminId); // 获取用户对应角色
        return  CommonResult.success(roleList);
    }
}

