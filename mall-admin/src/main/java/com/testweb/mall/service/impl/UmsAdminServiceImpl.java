package com.testweb.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.testweb.mall.bo.AdminUserDetails;
import com.testweb.mall.exception.Asserts;
import com.testweb.mall.util.RequestUtil;
import com.testweb.mall.dao.UmsAdminRoleRelationDao;
import com.testweb.mall.dto.UmsAdminParam;
import com.testweb.mall.dto.UpdateAdminPasswordParam;
import com.testweb.mall.mapper.UmsAdminLoginLogMapper;
import com.testweb.mall.mapper.UmsAdminMapper;
import com.testweb.mall.mapper.UmsAdminRoleRelationMapper;
import com.testweb.mall.model.*;
import com.testweb.mall.service.UmsAdminCacheService;
import com.testweb.mall.service.UmsAdminService;
import com.testweb.mall.security.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UmsAdminServiceImpl implements UmsAdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsAdminServiceImpl.class);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UmsAdminMapper adminMapper;
    @Autowired
    private UmsAdminRoleRelationMapper adminRoleRelationMapper;
    @Autowired
    private UmsAdminRoleRelationDao adminRoleRelationDao;
    @Autowired
    private UmsAdminLoginLogMapper loginLogMapper;
    @Autowired
    private UmsAdminCacheService adminCacheService;


    /*
    根据用户名获取后台管理员
     */
    @Override
    public UmsAdmin getAdminByUsername(String username) {
        UmsAdmin admin = adminCacheService.getAdmin(username); // 获取后台用户缓存信息
        if (admin != null) return admin; // 在缓存查询到的关于admin直接返回
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(username); // 1.传入值要查找的username 2.createCriteria().andUsernameEqualTo(username):where username = username
        List<UmsAdmin> adminList = adminMapper.selectByExample(example); // 数据库查询操作:select * from ums_admin where username = username（与上面代码连用）
        if (adminList != null && adminList.size() > 0){
            admin = adminList.get(0);
            adminCacheService.setAdmin(admin); // 设置后台用户缓存信息（查询到的信息储存到缓存）
            return admin;
        }
        return null;
    }

    /*
    注册功能
     */
    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) { // 1.在dto自定义统一前端传值为另一个实体类umsAdminParam为前端传入的值的实体类  2.umsAdmin为数据库操作所用的实体类
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam, umsAdmin); // 1.将umsAdminParam的值-->赋值给umsAdmin,通过反射将一个对象的值赋值个另外一个对象（前提是对象中属性的名字相同）  2.也可以直接用umsAdmin。set数据(umsAdminParam。get数据),但是数据比较多，所以直接用赋值的方法比较方便
        /*
        BeanUtils.copyProperties("转换前的类", "转换后的类");
        BeanUtils.copyProperties(a, b);
        b中的存在的属性，a中一定要有，但是a中可以有多余的属性；
        a中与b中相同的属性都会被替换，不管是否有值；
        a、 b中的属性要名字相同，才能被赋值，不然的话需要手动赋值；
        Spring的BeanUtils的CopyProperties方法需要对应的属性有getter和setter方法；
        如果存在属性完全相同的内部类，但是不是同一个内部类，即分别属于各自的内部类，则spring会认为属性不同，不会copy；
        spring和apache的copy属性的方法源和目的参数的位置正好相反，所以导包和调用的时候都要注意一下
         */
        umsAdmin.setCreateTime(new Date()); // 设置注册时间
        umsAdmin.setStatus(1); // 设置用户状态为开启
        // 查询是否有相同用户名的用户
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(umsAdmin.getUsername()); // 条件where为被赋值后所得到前端传出的用户名
        List<UmsAdmin> umsAdminList = adminMapper.selectByExample(example); // 数据库查找操作:查找指定用户名数据保存为List集合
        if (umsAdminList.size() > 0){ // 如果在数据库查找到数据，说明用户名已经存在，返回空值
            return null;
        }
        // 将密码加密操作
        String encodePassword = passwordEncoder.encode(umsAdmin.getPassword()); // 将赋值到的密码进行加密
        umsAdmin.setPassword(encodePassword); // 设置密码为加密后的密码
        adminMapper.insert(umsAdmin); // 数据库添加操作
        return umsAdmin;
    }

    /*
    登录功能
     */
    @Override
    public String login(String username, String password) {
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = loadUserByUsername(username); // 获取用户信息存入UserDetails,后面会进行security安全验证
            if (!passwordEncoder.matches(password,userDetails.getPassword())){
                /*
                matches(CharSequence rawPassword, String encodedPassword)
                第一个参数是原密码
                第二个参数就是用 PasswordEncoder 调用 encode(CharSequence rawPassword) 编码过后保存在数据库的密码
                 */
                Asserts.fail("密码不正确");
            }
            if (!userDetails.isEnabled()){ // 是否被禁用,禁用的用户不能身份验证
                Asserts.fail("账号已被禁用");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null ,userDetails.getAuthorities()); // 从AdminUserDetails传入信息验证,获取认证
            /*
            UsernamePasswordAuthenticationToken继承AbstractAuthenticationToken实现Authentication
            所以当在页面中输入用户名和密码之后首先会进入到UsernamePasswordAuthenticationToken验证(Authentication)，
            然后生成的Authentication会被交由AuthenticationManager来进行管理
            而AuthenticationManager管理一系列的AuthenticationProvider，
            而每一个Provider都会通UserDetailsService和UserDetail来返回一个
            以UsernamePasswordAuthenticationToken实现的带用户名和密码以及权限的Authentication
             */
            SecurityContextHolder.getContext().setAuthentication(authentication); // 传递authentication对象，来建立安全上下文（security context）
            token = jwtTokenUtil.generateToken(userDetails); // 根据保存的用户信息生成token令牌
            insertLoginLog(username); // 添加登录记录
        } catch (AuthenticationException e) { // 抛出登录安全认证异常
            LOGGER.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    /*
    添加登录记录
     */
    private void insertLoginLog(String username){
        UmsAdmin admin = getAdminByUsername(username); // 获取用户名获取后台管理员信息
        if (admin == null) return;
        UmsAdminLoginLog loginLog = new UmsAdminLoginLog(); // 数据库用户登录信息列表
        loginLog.setAdminId(admin.getId());
        loginLog.setCreateTime(new Date());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes(); // 接收到请求，记录请求内容
        HttpServletRequest request = attributes.getRequest(); // 获取接收请求的request
        loginLog.setIp(RequestUtil.getRequestIp(request)); // 设置当前ip地址
        loginLogMapper.insert(loginLog); // 数据库添加操作
     }

    /*
    根据用户名修改登录时间
     */
    private void updateLoginTimeByUsername(String username){
        UmsAdmin record = new UmsAdmin();
        record.setLoginTime(new Date());
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(username);
        adminMapper.updateByExampleSelective(record, example); // updateByExampleSelective():更新想更新的字段
    }

    /*
    刷新token的功能
     */
    @Override
    public String refreshToken(String oldToken) {
        return jwtTokenUtil.refreshHeadToken(oldToken); // 传入之前的oldToken当原来的token没过期时是可以刷新的
    }

    /*
    根据id获取用户名
    */
    @Override
    public UmsAdmin getItem(Long id) {
        return adminMapper.selectByPrimaryKey(id); //select * from ums_admin where id = id
    }

    /*
    根据用户名或者昵称分页查询用户
     */
    @Override
    public List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize); // 1.指定页码pageNum和每页的大小pageSize  2.PageHelper.startPage()从第pageNum页开始，每页显示pageSize条记录
        UmsAdminExample example = new UmsAdminExample();
        UmsAdminExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andUsernameLike("%" + keyword + "%"); // 1.根据用户名模糊查询条件->条件一
            example.or(example.createCriteria().andNickNameLike("%" + keyword + "%")); // 1.or()  2.根据昵称模糊查询->条件二
        }
        return adminMapper.selectByExample(example); // 根据条件查询操作
    }

    /*
    修改指定用户信息
     */
    @Override
    public int update(Long id, UmsAdmin admin) {
        admin.setId(id);
        UmsAdmin rawAdmin = adminMapper.selectByPrimaryKey(id); // 根据id查询数据操作
        if (rawAdmin.getPassword().equals(admin.getPassword())){ // 获取查询到的数据的密码和前端传入的密码进行比较
            // 与原加密密码相同的不需要修改
            admin.setPassword(null);
        }else {
            // 与原加密密码不同的需要加密修改
            if (StrUtil.isEmpty(admin.getPassword())){ // 如果传入的密码为空
                admin.setPassword(null); // 返回空值
            }else {
                admin.setPassword(passwordEncoder.encode(admin.getPassword())); // 加密操作
            }
        }
        int count = adminMapper.updateByPrimaryKeySelective(admin); // 根据id更新数据库操作
        adminCacheService.delAdmin(id); // 删除后台用户缓存
        return count;
    }

    /*
    删除指定用户
     */
    @Override
    public int delete(Long id) {
       adminCacheService.delAdmin(id); // 删除后台用户缓存
       int count = adminMapper.deleteByPrimaryKey(id); // 根据指定id删除用户
       adminCacheService.delResourceList(id); // 删除后台用户资源列表缓存
       return count;
    }

    /*
    修改用户角色关系
     */
    @Override
    public int updateRole(Long adminId, List<Long> roleIds) {
        int count = roleIds == null ? 0 : roleIds.size();
        // 先删除原来的关系
        UmsAdminRoleRelationExample adminRoleRelationExample = new UmsAdminRoleRelationExample();
        adminRoleRelationExample.createCriteria().andAdminIdEqualTo(adminId); // 根据用户id作为条件
        adminRoleRelationMapper.deleteByExample(adminRoleRelationExample); // 用户id作为条件，删除指定用户id值
        // 建立新关系
        if (!CollectionUtils.isEmpty(roleIds)){ // 判断角色id集合是否为空，如果为空则没有角色信息，不做用户角色插入操作
            List<UmsAdminRoleRelation> list = new ArrayList<>();
            for (Long roleId : roleIds) { // 遍历集合
                UmsAdminRoleRelation roleRelation = new UmsAdminRoleRelation();
                roleRelation.setAdminId(adminId); // 设置用户id（原用户id）
                roleRelation.setRoleId(roleId); // 设置角色id
                list.add(roleRelation); // 在列表添加数据
            }
            adminRoleRelationDao.insertList(list); // 批量插入用户角色关系
        }
        adminCacheService.delResourceList(adminId); // 删除后台用户资源列表缓存
        return count;
    }

    /*
    获取用户对象角色
     */
    @Override
    public List<UmsRole> getRoleList(Long adminId) {
        return adminRoleRelationDao.getRoleList(adminId); // 根据用户id查询用户角色列表操作
    }

    /*
    获取指定用户可访问的资源
     */
    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        List<UmsResource> resourceList = adminCacheService.getResourceList(adminId); // 根据用户id获取缓存后台用户资源列表
        if (CollUtil.isNotEmpty(resourceList)){ // 判断集合是否不为空
            return resourceList;
        }
        resourceList = adminRoleRelationDao.getResourceList(adminId); // 根据用户id查询用户角色列表操作
        if (CollUtil.isNotEmpty(resourceList)){ // 判断集合是否不为空
            adminCacheService.setResourceList(adminId, resourceList); // 设置缓存后台用户资源列表
        }
        return resourceList;
    }

    /*
    修改密码
     */
    @Override
    public int updatePassword(UpdateAdminPasswordParam updateAdminPasswordParam) {
        if (StrUtil.isEmpty(updateAdminPasswordParam.getUsername()) // 判断值是否为空
                    || StrUtil.isEmpty(updateAdminPasswordParam.getOldPassword()) //判断旧密码是否为空
                    || StrUtil.isEmpty(updateAdminPasswordParam.getNewPassword())){ // 判断新密码是否为空
            return -1; // 提交参数不合法
        }
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(updateAdminPasswordParam.getUsername()); // 将传入的用户名作为参数
        List<UmsAdmin> adminList = adminMapper.selectByExample(example); // 根据用户名在数据库查询擦操作
        if (CollUtil.isEmpty(adminList)){ // 判断获取的用户名是否为空
            return -2; // 找不到该用户
        }
        UmsAdmin umsAdmin = adminList.get(0); // 在获取的列表中第一个为用户名
        if (!passwordEncoder.matches(updateAdminPasswordParam.getOldPassword(), umsAdmin.getPassword())){ // 判断根据用户名获取的密码，与前端传入的旧密码是否一致
            return -3; // 旧密码错误
        }
        umsAdmin.setPassword(passwordEncoder.encode(updateAdminPasswordParam.getNewPassword())); // 将传入的密码进行加密操作
        adminMapper.updateByPrimaryKey(umsAdmin); // 更新数据
        adminCacheService.delAdmin(umsAdmin.getId()); // 删除后台用户缓存
        return 1; // 用户状态开启
    }

    /*
    获取用户信息
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        // 获取用户信息
        UmsAdmin admin = getAdminByUsername(username); // 根据用户名获取后台管理员
        if (admin != null){
            List<UmsResource> resourceList = getResourceList(admin.getId()); // 获取指定用户可访问的资源
            return new AdminUserDetails(admin,resourceList); // 将信息传入AdminUserDetails里面
        }
        throw new UsernameNotFoundException("用户名或者密码错误");
    }
}
