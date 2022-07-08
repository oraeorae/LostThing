package com.controller;

import com.dao.SqlArray;
import com.file.FileUpload;
import com.logic.Login;
import com.url.Request;
import com.weixin.SignUp;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
//import net.sf.json.JSONObject;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


//使用Controller来标识它是一个控制器
@Controller
//@CrossOrigin
@RequestMapping(value = "/api")         //使链接还有一个 /api/
public class HelloController {
    //全局变量
    public static String appid = "wxf58558547a7b352a";
    public static String secret = "c2ca1a3ee37256c970a0d6c4f4e475fc" ;

    //测试springmvc是否创建成功
    @RequestMapping(value = "/helloworld")
    public String Hello() {
        return "cs";            //返回测试页面
    }

    /* 测试链接
    * 请求：Get
    * 链接：地址/api/cs
    * 参数：无
    * 返回：json {"msg":"HelloWorld"}
    */
    @RequestMapping(value = "/cs")      //请求链接是cs
    public @ResponseBody Map<String,String> cs() {
        Map<String,String> data = new HashMap<String,String>();     //创建map格式的数据
        data.put("msg","HelloWorld" );
        return data;        //返回后会被前端解析为json格式的数据
    }

    /*
     * 获取两张图片
     * 请求：Get
     * 链接：地址/api/getpicture
     * 参数：无
     * 返回：json
     * {
     *      "msg":"ok",
     *      "picture1":"/img/LostButtonPicture.png",
     *      "picture2":"/img/PickUpButtonPicture.png"
     * }
     */
    @RequestMapping(value = "/getpicture")
    public @ResponseBody
    Map<String,String> getPicture() {
        Map<String,String> data = new HashMap<String,String>();
        data.put("msg","ok" );
        //向前端返回两张图片的地址
        data.put("picture1","/img/LostButtonPicture.png");
        data.put("picture2","/img/PickUpButtonPicture.png");
        return data;
    }

    /* 注册
     * 请求：Get
     * 链接：地址/api/register
     * 参数：无
     * 返回：json {"msg":"HelloWorld"}
     */
    // 没用到
    /*@RequestMapping(value = "/register",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,String> register(HttpServletRequest request) {
        String code = request.getParameter("code");
        Map<String,String> data = new HashMap<String,String>();
        data.put("msg","ok" );
        return data;
    }*/

    /* 获取手机号码
     * 请求：POST
     * 链接：地址/api/getphone
     * 参数：手机号码获取的code
     * 返回：json {"msg":"HelloWorld"}
     */
    @RequestMapping(value = "/getphone",method = RequestMethod.POST)
    public @ResponseBody Map<String,String> getPhone(HttpServletRequest request) {
        Map<String,String> data = new HashMap<String,String>();
        try{
            //获取前端传过来的code
            String code = request.getParameter("code");
            //System.out.println(code);

            //获取获取小程序全局唯一后台接口调用凭据（access_token）
            String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secret;
            String jsonStringa =  Request.doGet(getTokenUrl);       //调用get请求去访问微信小程序自带的链接，将返回结果存储到jsonStringa中
            //.out.println(jsonStringa);

            //String转JSON
            JSONObject jsonObject = JSONObject.parseObject(jsonStringa);
            String access_token = jsonObject.getString("access_token");     //获取JSON数据中的access_token

            //提交参数
            String getPhoneUrl = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token="+access_token;

            Map<String,Object> map =  new HashMap<String,Object>();
            //map.put("access_token",access_token);
            map.put("code",code);               //将code放到map中
            JSONObject json = new JSONObject(map);      //Map格式转化成JSON格式
            //System.out.println(map);
            //System.out.println(json);
            String jsonStringb = Request.doPostForm(getPhoneUrl,json);      //向微信小程序接口提交Post请求得到结果
            //System.out.println(jsonStringb);

            //String转JSON
            JSONObject jsonObject2 = JSONObject.parseObject(jsonStringb);
            HashMap hashMap = JSONObject.parseObject(jsonObject2.toJSONString(), HashMap.class);
            if( 0 == (int)hashMap.get("errcode") ){                //请求成功
                data.put("msg","ok" );
                JSONObject tmp2 = (JSONObject)hashMap.get("phone_info");
                data.put("phone",(String)tmp2.get("phoneNumber"));          //将结果存储下来
            }else{
                data.put("msg","fail" );
                data.put("error",(int)hashMap.get("errcode")+"" );
            }
        }catch (Exception e){
            System.out.println(e);
            data.put("msg","fail" );
        }

        return data;
    }

    /* 登录
     * 请求：POST
     * 链接：地址/api/login
     * 参数：登录获取的code
     * 返回：json {"skey":""}
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public @ResponseBody Map<String,String> login(HttpServletRequest request) {
        Map<String, String> data = new HashMap<String, String>();
        try {
            //获取前端传过来的code
            String code = request.getParameter("code");
            //Get请求（登录凭证校验）
            String getAuthUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
            String jsonString = Request.doGet(getAuthUrl);          //进行get请求
            //System.out.println(jsonString);
            //String转JSON，再json转为map
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            HashMap hashMap = JSONObject.parseObject(jsonObject.toJSONString(), HashMap.class);

            //注意这里要加上 hashMap.get("errcode") == null
            if ( hashMap.get("errcode") == null || 0 == (int) hashMap.get("errcode")) {                //请求成功
                data.put("msg", "ok");
                //得到openid和session_key去生成3rd_session
                //这个生成3rd_session的方式自己决定即可，比如使用SHA或Base64算法都可以。例如：将session_key或openid+session_key作为SHA或Base64算法的输入，输出结果做为3rd_session来使用，同时要将openid，session_key，3rd_session三者关联存储到数据库中，方便下次拿3rd_session获取session_key或openid做其他处理。
                String openid = (String) hashMap.get("openid");
                String session_key = (String) hashMap.get("session_key");

                //uuid生成唯一key(https://blog.csdn.net/weixin_38169886/article/details/99820453?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-5.pc_relevant_default&spm=1001.2101.3001.4242.4&utm_relevant_index=8)
                String skey = UUID.randomUUID().toString();         //用UUID来生成唯一的skey

                //判断是否注册过
                boolean tmp = Login.checkUser(openid);
                if (tmp == false) {                //没有注册过
                    Login.register(openid, skey);
                    data.put("msg", "ok");
                    data.put("skey", skey);
                } else {                          //注册过，更新新的skey
                    Login.updateskey(openid, skey);
                    data.put("skey", skey);
                }
            } else {
                data.put("msg", "fail");
                data.put("error", (int) hashMap.get("errcode") + "");
            }
        }
        catch(Exception e){
            e.printStackTrace();
            data.put("msg", "fail");
            data.put("error", e.toString());
        }
        return data;
    }

    //文件上传
    /*
    * HTTP请求：POST
    * 参数：file   （可以是一个文件，也可以是多个文件）
    * 请求链接：地址/api/upload
    * {"msg":"ok","filename":""}
    * {"msg":"fail","error":""}
    * */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    //@RequestParam(value = "file",required = false) MultipartFile[] file 注意这里的写法，参数名必须和前端提交上来的名字一致
    public @ResponseBody Map<String,String> Upload(@RequestParam(value = "file",required = false) MultipartFile[] file, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Map<String, String> data = new HashMap<String, String>();
        try {
            //System.out.println("总共有"+file.length+"个文件");
            // 文件上传到服务器的位置“/files”
            //System.out.println("正在上传文件");

            List<String> fileList = new ArrayList<>();           //全部的数组
            for( MultipartFile f:file){                 //for each将文件数组一个个取出
                String filelocation = FileUpload.SaveServer(f,request);     //将文件保存下来
                if( filelocation != null ){         //说明文件保存成功
                    fileList.add(filelocation);     //将文件位置加入到链表中
                }else{                              //说明文件保存失败，直接向前端返回错误信息
                    data.put("msg","fail");
                    data.put("error","文件上传失败");
                    return data;
                }
            }
            //合并
            String all_file = SqlArray.Merge(fileList);     //将前面存储起来的文件位置一个个连接起来，变成一个字符串（用|分割）
            //System.out.println(all_file);
            data.put("msg","ok");
            data.put("filepath",all_file);              //向前端返回成功的数据

        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    //失主提交页面
    /*
       HTTP请求：POST
        请求链接：地址/api/ownerEnrol
        参数：     7
            category_value 失物类别
            LostTime       丢失时间
            LostPosition   丢失地点
            LostPerson     联系人
            LostTelNumber  联系方式
            LostThingsPicture    失物照片
            FilePath           图片位置
            LostMessage       备注
        返回JSON
            提交成功
            {
                "msg":"ok"
            }
            提交失败
            {
               "msg":"fail",
               "error":错误原因
            }
    */
    //失主提交和拾主的函数基本一致，只有一些传数据的不同
    @RequestMapping(value = "/ownerEnrol", method = RequestMethod.POST)
    public @ResponseBody Map<String,String> Enrol(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Map<String, String> data = new HashMap<String, String>();
        try {
            //获取前端传过来的数据
            String category_value = request.getParameter("category_value");
            String LostTime = request.getParameter("Time");
            String LostPosition =  request.getParameter("Position");
            String LostPerson =  request.getParameter("Person");
            String LostTelNumber = request.getParameter("TelNumber");
            String LostMessage = request.getParameter("Message");
            String FilePath = request.getParameter("FilePath");

            //如果传文件位置为空的话，说明没有图片，文件位置变成默认“无图片”的图片
            if( FilePath == null || "".equals(FilePath)){
                FilePath = "\\img\\withoutPicture.png";
            }

            //判断关键信息是否存储，若不存在，则直接返回
            if ( category_value == null
                    || LostTime == null || LostTelNumber == null || LostMessage == null
                    || "".equals(category_value) || "".equals(LostTime) || "".equals(LostTelNumber) || "".equals(LostMessage) ) {
                data.put("msg", "fail");
                data.put("error", "失物类型,失物丢失时间,失物情况,联系方式为必填项且不能为空");
                throw new NullPointerException("xx");         //抛出异常，方便直接跳到最后一步
            }

            //进行登记，写入数据库
            if (SignUp.Enrol(category_value,LostTime,LostPosition,LostPerson,LostTelNumber,FilePath,LostMessage,"owner")) {
                data.put("msg", "ok");
            }else{
                data.put("msg","fail");
                data.put("error","写入数据库失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }


    //拾主提交页面
    /*
       HTTP请求：POST
        请求链接：地址/api/pickerEnrol
        参数：     7
            category_value 失物类别
            LostTime       丢失时间
            LostPosition   丢失地点
            LostPerson     联系人
            LostTelNumber  联系方式
            FilePath       图片位置
            LostMessage       备注
        返回JSON
            提交成功
            {
                "msg":"ok"
            }
            提交失败
            {
               "msg":"fail",
               "error":错误原因
            }
    */
    //拾主
    @RequestMapping(value = "/pickerEnrol", method = RequestMethod.POST)
    public @ResponseBody Map<String,String> pickerEnrol(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Map<String, String> data = new HashMap<String, String>();
        try {
            String category_value =  request.getParameter("category_value");
            String LostTime =  request.getParameter("Time");
            String LostPosition =  request.getParameter("Position");
            String LostPerson =  request.getParameter("Person");
            String LostTelNumber =  request.getParameter("TelNumber");
            String LostMessage =  request.getParameter("Message");
            String FilePath = request.getParameter("FilePath");

            if( FilePath == null || "".equals(FilePath)){
                FilePath = "\\img\\withoutPicture.png";
            }

            if ( category_value == null
                    || LostTime == null || LostTelNumber == null || LostMessage == null
                    || "".equals(category_value) || "".equals(LostTime) || "".equals(LostTelNumber) || "".equals(LostMessage) ) {
                data.put("msg", "fail");
                data.put("error", "拾物类型,拾物丢失时间,拾物情况,联系方式为必填项且不能为空");
                throw new NullPointerException("xx");         //抛出异常
            }

            //进行登记（与失主的区别是，这里传参的最后一个参数是“picker”）
            if (SignUp.Enrol(category_value,LostTime,LostPosition,LostPerson,LostTelNumber,FilePath,LostMessage,"picker")) {
                data.put("msg", "ok");
            }else{
                data.put("msg","fail");
                data.put("error","写入数据库失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    //显示失物数据
    /*
       HTTP请求：POST
        请求链接：地址/api/ownershowdata
        参数：     page(字符串)
        返回JSON
            获取成功
            {
                "msg":"ok",
                "num":“总数量”
                "data":{
                    （数组类型的数据）
                }
            }
            获取失败
            {
               "msg":"fail",
               "num":"总数量",
               "error":错误原因
            }
    */
    @RequestMapping(value = "/ownershowdata", method = RequestMethod.POST)
    //嵌套的JSON格式可以用Map<String,Object>，不过需要相关的包，可能是gson吧
    public @ResponseBody Map<String,Object> ownershowData(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            int num = SignUp.queryNum("owner");     //返回失物总数量
            String page =  request.getParameter("page");        //得到前端请求的页数
            List list = SignUp.signupList(Integer.parseInt(page),10,"owner");   //返回数据，每页限制10条数据        //每页显示10条数据
            data.put("num",""+num);         //将数据加入map中
            if( list == null ){         //如果为空，说明没有获取到数据
                data.put("msg","fail");
                data.put("error","当前页面已经无数据");
            }else{
                data.put("msg","ok");
                data.put("data",list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    //显示失物搜索数据
    /*
        HTTP请求：POST
        请求链接：地址/api/ownersearch
        参数：
            page           页数      必填
            search                  必填
            category_value          非必填
            begintime               非必填
            endtime                 非必填
        返回JSON
            获取成功
            {
                "msg":"ok",
                "data":{
                    （数组类型的数据）
                }
            }
            获取失败
            {
               "msg":"fail",
               "error":错误原因
            }
    */
    @RequestMapping(value = "/ownersearch", method = RequestMethod.POST)
    public @ResponseBody Map<String,Object> ownersearch(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            int num = SignUp.queryNum("owner");         //获取数量
            //获取前端得到的值，注意存在一些值为空的情况
            String page =  request.getParameter("page");
            String search =  request.getParameter("search");
            String category_value =  request.getParameter("category_value");
            String begintime =  request.getParameter("begintime");
            String endtime =  request.getParameter("endtime");
            if( page == null || "".equals(page)  ){     //必填项判定
                data.put("msg","fail");
                data.put("error","page为必填项");
                return data;
            }
            //返回搜索的结果
            List list = SignUp.searchList(Integer.parseInt(page),10,category_value,begintime,endtime,search,"owner");        //每页显示10条数据

            if( list == null ){             //同上
                data.put("msg","fail");
                data.put("error","当前页面已经无数据");
            }else{
                data.put("msg","ok");
                System.out.println(list.toString());
                data.put("data",list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }
    //显示拾物数据
    /*
       HTTP请求：POST
        请求链接：地址/api/pickershowdata
        参数：     page(字符串)
        返回JSON
            获取成功
            {
                "msg":"ok",
                "num":“总数量”
                "data":{
                    （数组类型的数据）
                }
            }
            获取失败
            {
               "msg":"fail",
               "num":"总数量",
               "error":错误原因
            }
    */
    //与失物函数基本一致
    @RequestMapping(value = "/pickershowdata", method = RequestMethod.POST)
    public @ResponseBody Map<String,Object> pickershowData(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            int num = SignUp.queryNum("picker");
            String page =  request.getParameter("page");
            List list = SignUp.signupList(Integer.parseInt(page),10,"picker");        //每页显示10条数据
            data.put("num",""+num);
            if( list == null ){
                data.put("msg","fail");
                data.put("error","当前页面已经无数据");
            }else{
                data.put("msg","ok");
                data.put("data",list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    //显示拾物搜索数据
    /*
        HTTP请求：POST
        请求链接：地址/api/pickersearch
        参数：
            page           页数      必填
            search                  必填
            category_value          非必填
            begintime               非必填
            endtime                 非必填
        返回JSON
            获取成功
            {
                "msg":"ok",
                "data":{
                    （数组类型的数据）
                }
            }
            获取失败
            {
               "msg":"fail",
               "error":错误原因
            }
    */
    //与失物函数基本一致
    @RequestMapping(value = "/pickersearch", method = RequestMethod.POST)
    public @ResponseBody Map<String,Object> pickersearch(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            int num = SignUp.queryNum("picker");
            String page =  request.getParameter("page");
            String search =  request.getParameter("search");
            String category_value =  request.getParameter("category_value");
            String begintime =  request.getParameter("begintime");
            String endtime =  request.getParameter("endtime");
            if( page == null || "".equals(page)  ){
                data.put("msg","fail");
                data.put("error","page为必填项");
                return data;
            }
            List list = SignUp.searchList(Integer.parseInt(page),10,category_value,begintime,endtime,search,"picker");        //每页显示10条数据

            if( list == null ){
                data.put("msg","fail");
                data.put("error","当前页面已经无数据");
            }else{
                data.put("msg","ok");
                System.out.println(list.toString());
                data.put("data",list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }
}
