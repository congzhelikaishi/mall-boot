package com.testweb.mall.security.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtToken生成的工具类
 * JWT token的格式：header.payload.signature
 * headers: {
 *          'Authorization': 'Bearer ' + token
 *          }
 * header的格式（算法、token的类型）：
 * {"alg": "HS512","typ": "JWT"}
 * payload的格式（用户名、创建时间、生成时间）：
 * {"sub":"wang","created":1489079981393,"exp":1489684781}
 * signature的生成算法：
 * HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
 *
 *
 * Value的值有两类：
 * ① ${ property : default_value }
 * ② #{ obj.property? :default_value }
 * 第一个注入的是外部配置文件对应的property，第二个则是SpEL表达式对应的内容。 那个
 * default_value，就是前面的值为空时的默认值。注意二者的不同，#{}里面那个obj代表对象。
 *
 */

public class JwtTokenUtil {//TODO
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    //用户名的key
    private static final String CLAIM_KEY_USERNAME = "sub";  // 用户
    //签名创建的时间
    private static final String CLAIM_KEY_CREATED = "created";  // 创建时间

    /*
    载荷部分存在两个属性：payload和claims。两个属性均可作为载荷，jjwt中二者只能设置其一

    iss: 签发者
    sub: 面向用户
    aud: 接收者
    iat(issued at): 签发时间
    exp(expires): 过期时间
    nbf(not before)：不能被接收处理时间，在此之前不能被接收处理
    jti：JWT ID为web token提供唯一标识

    去application.yml拿jwt密钥和jwt失效时间和JWT负载中拿到开头，前缀
    jwt:
        tokenHeader: Authorization #JWT存储的请求头
        secret: mall-portal-secret #JWT加解密使用的密钥
        expiration: 604800 #JWT的超期限时间(60*60*24*7)
        tokenHead: 'Bearer '  #JWT负载中拿到开头
     */
    //JWT密钥
    @Value("${jwt.secret}")
    private String secret;

    //JWT失效时间
    @Value("${jwt.expiration}")
    private Long expiration;

    //JWT负载中拿到开头，前缀
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /*
    根据荷载生成JWT的token
     */
    private String generateToken(Map<String, Object> claims){
        return Jwts.builder()  //创建对象
                .setClaims(claims)  // 1.设置claims，以参数创建一个新Claims对象，直接赋值  2.意使用setClaims这个方法的时候要将它移动到上面，避免覆盖
                .setExpiration(generateExpirationDate())//添加失效时间
                .signWith(SignatureAlgorithm.HS512, secret)//添加密钥以及加密方式
                .compact();  //生成token 1.编码 Header 和 Payload 2.生成签名 3.拼接字符串
    }

    /*
    从token中获取JWT中的荷载（payload）
     */
    private Claims getClaimsFromToken(String token){
        Claims claims = null;
        try {
            claims = Jwts.parser()  // 1.创建解析对象  2.Jwts.parser() 返回了DefaultJwtParser对象
                    .setSigningKey(secret)  // 设置安全密钥（生成签名所需的密钥和算法）
                    .parseClaimsJws(token)  // 解析token
                    .getBody();  // 获取payload部分内容
        } catch (Exception e){
            LOGGER.info("JWT格式验证失败:{}", token);
        }
        return claims;
    }

    /*
    生成token的过期时间
     */
    private Date generateExpirationDate(){
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /*
    从token中获取登录用户名
     */
    public String getUserNameFromToken(String token){
        String username;
        //根据Token去拿荷载
        try {
            Claims claims = getClaimsFromToken(token);  // 获取token里面的荷载
            username = claims.getSubject();//获取用户名
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /*
    自义定方法验证token是否还有效
     */
    public boolean validateToken(String token, UserDetails userDetails){
        String username = getUserNameFromToken(token);  // 从令牌中获取用户名
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);  // 1.判断用户名是否一致和时间是否过期  2.自义定方法isTokenExpired(token)验证令牌时间是否过期返回true或者false
    }

    /*
    判断token是否已经失效
     */
    private boolean isTokenExpired(String token){
        //获取Token的失效时间
        Date expiredDate = getExpiredDateFromToken(token);
        //在当前时间之前，则失效
        return expiredDate.before(new Date());
    }

    /*
    从token中获取过期时间
     */
    private Date getExpiredDateFromToken(String token){
        Claims claims = getClaimsFromToken(token);  // 获取荷载
        return claims.getExpiration();  // 获取时间
    }

    /*
    根据用户信息生效token
     */
    public String generateToken(UserDetails userDetails){
        //荷载
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());  // 在AdminUserDetails中已经实现了UserDetails，并且放入了用户信息
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /*
    当原来的token没过期时是可以刷新的
     */
    public String refreshHeadToken(String oldToken) {
        if(StrUtil.isEmpty(oldToken)){
            return null;
        }
        String token = oldToken.substring(tokenHead.length());  // 索引请求头后的所有数据
        if(StrUtil.isEmpty(token)){
            return null;
        }
        //token校验不通过
        Claims claims = getClaimsFromToken(token);  // 获取荷载
        if(claims == null){
            return null;
        }
        //如果token已经过期，不支持刷新
        if(isTokenExpired(token)){  // 判断过期时间，是否过期
            return null;
        }
        //如果token在30分钟之内刚刷新过，返回原token
        if(tokenRefreshJustBefore(token,30*60)){
            return token;
        }else{
            claims.put(CLAIM_KEY_CREATED, new Date());  // 集合中存在用户名，只需要放入新的时间即可
            return generateToken(claims);  // 生成token
        }
    }

    /**
     * 判断token在指定时间内是否刚刚刷新过
     */
    private boolean tokenRefreshJustBefore(String token, int time) {
        Claims claims = getClaimsFromToken(token);  // 获取荷载
        Date created = claims.get(CLAIM_KEY_CREATED, Date.class);  // 从荷载中获取创建时间
        Date refreshDate = new Date();
        //刷新时间在创建时间的指定时间内
        if(refreshDate.after(created)&&refreshDate.before(DateUtil.offsetSecond(created,time))){
            return true;
        }
        return false;
    }
}
