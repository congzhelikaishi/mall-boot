package com.testweb.mall.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 请求工具类
 */
public class RequestUtil {//TODO

    /*
     获取请求真实IP地址
     */
    public static String getRequestIp(HttpServletRequest request) {
        //通过HTTP代理服务器转发时添加
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            // 从本地访问时根据网卡取本机配置的IP
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) { // 0:0:0:0:0:0:0:1 是 IP v6的形式，其实对应的 IP v4值就是常见的 127. 0. 0. 1
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inetAddress.getHostAddress();
            }
        }
        // 通过多个代理转发的情况，第一个IP为客户端真实IP，多个IP会按照','分割
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
}
/*
	发生的场景：服务器端接收客户端请求的时候，一般需要进行签名验证，客户端IP限定等情况，在进行客户端IP限定的时候，需要首先获取该真实的IP。
	一般分为两种情况：
	方式一、客户端未经过代理，直接访问服务器端(nginx,squid,haproxy)；
	方式二、客户端通过多级代理，最终到达服务器端(nginx,squid,haproxy)；

	客户端请求信息都包含在HttpServletRequest中，可以通过方法getRemoteAddr()获得该客户端IP。
	方式一形式，可以直接获得该客户端真实IP。
	方式二中通过代理的形式，此时经过多级反向的代理，通过方法getRemoteAddr()得不到客户端真实IP，可以通过x-forwarded-for获得转发后请求信息。当客户端请求被转发，IP将会追加在其后并以逗号隔开，例如：10.47.103.13,4.2.2.2,10.96.112.230。

	请求中的参数：
	request.getHeader("x-forwarded-for") : 10.47.103.13,4.2.2.2,10.96.112.230
	request.getHeader("X-Real-IP") : 10.47.103.13
	request.getRemoteAddr():10.96.112.230

	客户端访问经过转发，IP将会追加在其后并以逗号隔开。最终准确的客户端信息为：
		• x-forwarded-for 不为空，则为逗号前第一个IP ；
		• X-Real-IP不为空，则为该IP ；
		• 否则为getRemoteAddr() ；

	相关请求头的解释：
		• X-Forwarded-For ：这是一个 Squid 开发的字段，只有在通过了HTTP代理或者负载均衡服务器时才会添加该项。
	格式为X-Forwarded-For:client1,proxy1,proxy2，一般情况下，第一个ip为客户端真实ip，后面的为经过的代理服务器ip。现在大部分的代理都会加上这个请求头。
		• Proxy-Client-IP/WL- Proxy-Client-IP ：这个一般是经过apache http服务器的请求才会有，用apache http做代理时一般会加上Proxy-Client-IP请求头，而WL-Proxy-Client-IP是他的weblogic插件加上的头。
		• HTTP_CLIENT_IP ：有些代理服务器会加上此请求头。
		• X-Real-IP  ：nginx代理一般会加上此请求头。

 */