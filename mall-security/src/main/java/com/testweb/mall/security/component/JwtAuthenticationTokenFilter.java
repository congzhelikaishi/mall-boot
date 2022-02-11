package com.testweb.mall.security.component;

import com.testweb.mall.security.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
JWT登录授权过滤器
request -> header -> token -> username -> userDetails(getAuthentication()) -> authentication
SecurityContextHolder.getContext().setAuthentication(authentication  建立上下文
 */
public class  JwtAuthenticationTokenFilter extends OncePerRequestFilter { // OncePerRequestFilter是在一次外部请求中只过滤一次。对于服务器内部之间的forward等请求，不会再次执行过滤方法

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //request 中获取去 header
        String authHeader = httpServletRequest.getHeader(this.tokenHeader);  // JWT存储的请求头tokenHeader: Authorization

        //对header做判断
        // 是否存在token
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) { // 1.startsWith() 方法用于检测字符串是否以指定的前缀开始  2.JWT负载中开头tokenHead: 'Bearer '
            //取出header
            //此处注意token之前有一个7字符长度的“Bearer “
            String authToken = authHeader.substring(this.tokenHead.length());// "Bearer "         substring() 方法返回字符串的子字符串，从指定位置之后开始索引
            //token中获取username
            String username = jwtTokenUtil.getUserNameFromToken(authToken); // 从token令牌中获取用户名
            LOGGER.info("checking username:{}", username);  // 输出日志用户名

            //判断username
            // token存在但是没有登录
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // 1.用户名不为空  2.储存的认证为空
                //拿到userDetails
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); //loadUserByUsername():用于通过用户名获取用户数据对象. 返回 UserDetails 对象, 表示用户的核心信息 (用户名, 用户密码, 权限等信息)
                /*
                1.可以通过查询数据库（或者是缓存、或者是其他的存储形式）来获取用户信息，然后组装成一个UserDetails,(通常是一个org.springframework.security.core.UserDetails.User，它继承自UserDetails) 并返回
                2.在实现loadUserByUsername方法的时候，如果我们通过查库没有查到相关记录，需要抛出一个异常来告诉spring security来“善后”。这个异常是org.springframework.security.core.UserDetails.UsernameNotFoundException
                 */
                //验证token
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    //完整填充的 authentication（其中包含了权限集 getAuthorities()）
                    //token中的用户信息和数据库中的用户信息对比成功后将用户信息加入SecurityContextHolder相当于登陆
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // 1.将权限信息封装进去  2.getAuthorities():获取用户权限，一般情况下获取到的是用户的角色信息
                    /*
                    1.在前台输入完用户名密码之后，会进入UsernamePasswordAuthenticationFilter类中去获取用户名和密码，然后去构建一个UsernamePasswordAuthenticationToken对象
                    2.这个对象实现了Authentication接口，Authentication接口封装了验证信息，在调用UsernamePasswordAuthenticationToken的构造函数的时候先调用父类AbstractAuthenticationToken的构造方法，传递一个authorities(userDetails.getAuthorities()包含用户的权限)，之后去给用户名密码赋值(此处设置密码为了null)，最后有一个setAuthenticated（true）方法，代表存进去的信息是否经过了身份认证
                     */

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    /*
                    1.实例化UsernamePasswordAuthenticationToken之后调用了setDetails(request,authRequest)将请求的信息设到UsernamePasswordAuthenticationToken中去，包括ip、session等内容
                    2.登录时对用户信息进行详细补充，发送到前端
                     */

                    LOGGER.info("authenticated user:{}", username);

                    //建立安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication); // 1.SecurityContextHolder.getContext().setAuthentication(authentication):整体是为了储存认证信息  2.authentication.setAuthentication():设置当前 Authentication 是否已认证（true or false）
                    /*
                    1.安全上下文，用户通过Spring Security 的校验之后，验证信息存储在SecurityContext中，SecurityContext接口只定义了两个方法，实际上其主要作用就是获取Authentication对象
                    2.SecurityContextHolder.getContext()
                      获取安全上下文SecurityContext。
                      安全上下文 SecurityContext
                      SecurityContext 可以理解成一个 存储 Authentication 的容器。Authentication 是一个用户凭证接口用来作为用户认证的凭证使用，通常常用的实现有 认证用户 UsernamePasswordAuthenticationToken 和 匿名用户AnonymousAuthenticationToken。其中 UsernamePasswordAuthenticationToken 包含了 UserDetails , AnonymousAuthenticationToken 只包含了一个字符串 anonymousUser 作为匿名用户的标识。
                      比如我们接受前端发起来的请求，我们在自己定义spring security 过滤器链中对每个请求进行处理。
                      获取用户信息和对应权限并将其封装成UsernamePasswordAuthenticationToken

                    3.SecurityContextHolder看名知义，是一个holder,用来hold住SecurityContext实例的。在典型的web应用程序中，用户登录一次，然后由其会话ID标识。服务器缓存持续时间会话的主体信息。
                      在Spring Security中，在请求之间存储SecurityContext的责任落在SecurityContextPersistenceFilter上，默认情况下，该上下文将上下文存储为HTTP请求之间的HttpSession属性。它会为每个请求恢复上下文SecurityContextHolder，
                      并且最重要的是，在请求完成时清除SecurityContextHolder。SecurityContextHolder是一个类，他的功能方法都是静态的（static）

                    4.SecurityContextHolder可以设置指定JVM策略（SecurityContext的存储策略），这个策略有三种：
                      MODE_THREADLOCAL：SecurityContext 存储在线程中。
                      MODE_INHERITABLETHREADLOCAL：SecurityContext 存储在线程中，但子线程可以获取到父线程中的 SecurityContext。
                      MODE_GLOBAL：SecurityContext 在所有线程中都相同
                      SecurityContextHolder默认使用MODE_THREADLOCAL模式，即存储在当前线程中。在spring security应用中，我们通常能看到类似如下的代码：
                      SecurityContextHolder.getContext().setAuthentication(token)
                     */
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse); // 继续执行放行转到真正目标(或者有另一个拦截器转到下一个拦截器)
    }
}
/*
从数据库加载使用使用细节并不是必需的, 也可以存储信息在令牌中读取它并从中读取它.
 */