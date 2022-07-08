package com.filter;

import com.alibaba.fastjson.JSONObject;
import com.logic.Login;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// https://blog.csdn.net/qq_32363305/article/details/82469451
// 过滤器，可以保证每个部分请求提交必须是已经登陆的状态
// 不过这里加在头部貌似没用，后期可以考虑参数直接传递
public class CrossFilter implements Filter{
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Max-Age", "2700000");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept, Cookie");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        //获取请求时头部的sessionId
        String skey = request.getHeader("skey");
        if( skey == null || "".equals(skey) ){      //如果没有说明不需要登录的权限
            //该请求不需要验证session,直接通过
            System.out.println("该请求不需要过滤，通过");
            chain.doFilter(request,response);
            return;
        }else{
            //只有在缓存中存在该sessionId才能进行请求
            if ( Login.checkskey(skey) == false ) {
                // 登录信息已过期，请重新登录
                System.out.println("登录信息失效，请重新登录");
                //response.getWriter().write("登录信息失效，请重新登录");
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter out = response.getWriter();
                JSONObject tmp = new JSONObject();
                tmp.put("msg", "fail");
                tmp.put("error", "登录信息失效，请重新登录");
                out.append(tmp.toString());
                return;
            }
            System.out.println("session验证成功");
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //system.out.println("开始初始化");
    }

    @Override
    public void destroy() {
        // system.out.println("销毁完成");
    }
}
