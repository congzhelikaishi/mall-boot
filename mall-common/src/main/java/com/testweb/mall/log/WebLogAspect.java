package com.testweb.mall.log;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.testweb.mall.domain.WebLog;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import net.logstash.logback.marker.Markers;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一日志处理切面
 */
@Aspect // 把当前类标识为一个切面供容器读取
@Component
@Order(1) //Order是顺序，此注解可操作于类、方法、字段，当作用在类时，值越小，则加载的优先级越高
public class WebLogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebLogAspect.class);
    /*
      execution：一般用于指定方法的执行，用的最多。
      within：指定某些类型的全部方法执行，也可用来指定一个包。
      this：Spring Aop是基于动态代理的，生成的bean也是一个代理对象，this就是这个代理对象，当这个对象可以转换为指定的类型时，对应的切入点就是它了，Spring Aop将生效。
      target：当被代理的对象可以转换为指定的类型时，对应的切入点就是它了，Spring Aop将生效。
      args：当执行的方法的参数是指定类型时生效。
      @target：当代理的目标对象上拥有指定的注解时生效。
      @args：当执行的方法参数类型上拥有指定的注解时生效。
      @within：与@target类似，看官方文档和网上的说法都是@within只需要目标对象的类或者父类上有指定的注解，则@within会生效，而@target则是必须是目标对象的类上有指定的注解。而根据笔者的测试这两者都是只要目标类或父类上有指定的注解即可。
      @annotation：当执行的方法上拥有指定的注解时生效。
      reference pointcut：(经常使用)表示引用其他命名切入点，只有@ApectJ风格支持，Schema风格不支持
      bean：当调用的方法是指定的bean的方法时生效。(Spring AOP自己扩展支持的)

      Pointcut定义时，还可以使用&&、||、! 这三个运算。进行逻辑运算。可以把各种条件组合起来使用
     */
    // Controller层切点
    @Pointcut("execution(public * com.testweb.mall.*.controller.*.*(..))||execution(public * com.testweb.mall.controller.*.*(..))")// Pointcut切入点表达式类型标准的AspectJ Aop的pointcut的表达式类型是很丰富的，但是Spring Aop只支持其中的9种，外加Spring Aop自己扩充的一种一共是11(10+1)种类型的表达式
    public void webLog(){}

    //切入点之前执行
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable{}

    /*
     * 声明rvt时指定的类型会限制目标方法必须返回指定类型的值或没有返回值
     * 此处将rvt的类型声明为Object，意味着对目标方法的返回值不加限制
     * 在目标方法成功执行之后调用通知功能
     */
    @AfterReturning(value = "webLog()", returning = "ret")
    public void doAfterReturning(Object ret) throws Throwable{}

    // 环绕通知，调用webLog()的@pointcut指定包下的任意类的任意方法时均会调用此方法
    @Around("webLog()") // 通知方法会将目标方法封装起来 前置+目标方法执行+后置通知
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable{
        long startTime = System.currentTimeMillis(); // 操作时间
        //获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes(); // 接收到前端所有请求，记录请求内容
        HttpServletRequest request = attributes.getRequest(); // 获取请求的Request
        //记录请求信息（通过Logstash传入Elasticsearch）
        WebLog webLog = new WebLog();
        // 执行当前类所切入的目标方法
        Object result = joinPoint.proceed(); // 执行方法
        //获取连接点签名
        Signature signature = joinPoint.getSignature(); // 获取到:修饰符+ 包名+组件名(类名) +方法名， 即获取所执行方法所访问的所有信息
        //将其转换为方法签名
        MethodSignature  methodSignature = (MethodSignature) signature; // 主要作用是为了使用getMethod方法
        //获取方法 如：controller的login
        Method method = methodSignature.getMethod(); // 获取当前方法信息
        if (method.isAnnotationPresent(ApiOperation.class)){ // isAnnotationPresent()如果指定元素注解在里面，就返回true
            ApiOperation log = method.getAnnotation(ApiOperation.class); // getAnnotation()返回该元素的指定类型的注释 比如@ApiOperation(value = "用户注册") 会返回:用户注册
            webLog.setDescription(log.value()); // 操作描述
        }
        long endTime = System.currentTimeMillis(); //操作结束时间
        String urlStr = request.getRequestURL().toString(); // 获取访问路径
        webLog.setBasePath(StrUtil.removeSuffix(urlStr, URLUtil.url(urlStr).getPath())); // 根路径  1.StrUtil.removeSuffix(字符串，指定需要去掉的字符)去掉字符串的后缀  2.URLUtil.url()通过一个字符串形式的URL地址创建对象  3.URLUtil.getPath() 字符串形式地址(http://www.aaa.bbb/search?scope=ccc&q=ddd) 获取PATH -> /search  4.URLUtil.getFile() 字符串形式地址(http://www.aaa.bbb/search?scope=ccc&q=ddd) 获取PATH -> /search?scope=ccc&q=ddd
        webLog.setUsername(request.getRemoteUser());//操作用户  获取缓存的用户:比如Spring Security做权限控制后就会将用户登录名缓存到这里
        webLog.setIp(request.getRemoteAddr()); // 获取客户端IP，需要注意的是获取到的是直接面向的IP地址，并不是经过代理等处理的原始地址
        webLog.setMethod(request.getMethod()); // 请求类型GET或者POST
        webLog.setParameter(getParameter(method, joinPoint.getArgs())); // 1.getArgs():获取目标方法的参数对象数组(获取参数并创建为一个数组对象)  2.设置请求参数  3.获取结果例如:Parameter:[{},{"pageSize":5},{"pageNum":1}]
        webLog.setSpendTime((int) (endTime - startTime));
        webLog.setResult(result);
        webLog.setStartTime(startTime);
        webLog.setUri(request.getRequestURI()); // 访问网址全部内容
        webLog.setUrl(request.getRequestURL().toString());
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("url", webLog.getUrl());
        logMap.put("method", webLog.getMethod());
        logMap.put("parameter", webLog.getParameter());
        logMap.put("spendTime", webLog.getSpendTime());
        logMap.put("description", webLog.getDescription());
        //在这里可以add到数据库，可以不同返回值
        LOGGER.info(Markers.appendEntries(logMap), JSONUtil.parse(webLog).toString()); // 传入logstash，发送日志
        return result;
    }

    /*
    根据方法和传入的参数获取请求参数,设置getParameter请求参数的值
     */
    private Object getParameter(Method method, Object[] args){
        List<Object> argList = new ArrayList<>();
        Parameter[] parameters = method.getParameters(); // 反射的方法获取到接口的参数名称
        for (int i = 0; i < parameters.length; i++){
            //将RequestBody注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class); // 1.getAnnotation()返回该元素的指定类型的注释 比如@RequestBody  2.获取requestBody发送的参数值
            // 以下第一个if作用不是太大(可以忽略)
            if (requestBody != null){
                argList.add(args[i]);  // 例如:value为"10"
            }
            //将RequestParam注解修饰的参数作为请求参数
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null){
                Map<String, Object> map = new HashMap<>();
                String key = parameters[i].getName();  // 获取参数名称例如:public Show(int firstPlaceHolderWithALongName) 中的 firstPlaceHolderWithALongName
                if (!StringUtils.isEmpty(requestParam.value())){ // requestParam.value()获取@RequestParam中value的值
                    key = requestParam.value(); //例如:key为"pagSize"
                }
                map.put(key, args[i]);
                argList.add(map);
            }
        }
        // 判断列表元素个数
        if (argList.size() == 0){
            return null;
        }else if (argList.size() == 1){
            return argList.get(0);
        }else {
            return argList;
        }
    }
}