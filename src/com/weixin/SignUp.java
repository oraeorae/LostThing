package com.weixin;

import com.dao.Dao;
import com.dao.SqlArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

public class SignUp {

    /*
        category_value       失物类别
        LostTime       丢失时间
        LostPosition   丢失地点
        LostPerson     联系人
        LostTelNumber    联系方式
        LostThingsPicture    失物照片
        LostMessage       备注
    */
    //create table data_enrol(category_value char(50),LostTime char(100),LostPosition char(100),LostPerson char(50),LostTelNumber char(50),LostThingsPicture longtext,LostMessage longtext,model char(10));
    //进行报名
    public static boolean Enrol(String category_value, String LostTime, String LostPosition, String LostPerson, String LostTelNumber, String LostThingsPicture, String LostMessage, String model) {
        //ticket就是准考证号，准考证号就是登录名
        try {
            Connection conn = Dao.getConnection();      //连接数据库
            /* 对报名的数据库进行补充 */
            //sql语句预编译
            PreparedStatement p = conn.prepareStatement("insert into data_enrol(category_value,LostTime,LostPosition,LostPerson,LostTelNumber,LostThingsPicture,LostMessage,model) VALUES (?,?,?,?,?,?,?,?);");
            //对占位符进行补充
            p.setString(1, category_value);
            p.setString(2, LostTime);
            p.setString(3, LostPosition);
            p.setString(4, LostPerson);
            p.setString(5, LostTelNumber);
            p.setString(6, LostThingsPicture);
            p.setString(7, LostMessage);
            p.setString(8, model);
            System.out.println(p);
            p.executeUpdate();          //运行sql语句
            System.out.println("登记成功");
            Dao.close(p, conn);         //关闭数据库连接
            return true;
        } catch (Exception e) {
            System.out.println("登记失败");
            e.printStackTrace();
            return false;
        }
    }

    //查询总的数目
    public static int queryNum(String which) {
        int res = 0;
        try {
            Connection conn = Dao.getConnection();              //连接数据库
            PreparedStatement p = conn.prepareStatement("select count(*) from data_enrol where model=?;");       //查看报名的人数
            p.setString(1, which);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                res = rs.getInt(1);
                Dao.close(rs, p, conn);
                return res;
            }
            Dao.close(rs, p, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //返回全部报名人及信息
    public static List signupList(String which) {
        int sign = 0;           //标记一下当前页数是否有数据
        //参考链接：https://blog.csdn.net/weixin_28779183/article/details/117810091
        List list = new ArrayList();
        int res = 0;
        try {
            Connection conn = Dao.getConnection();              //连接数据库
            PreparedStatement p = conn.prepareStatement("select * from data_enrol where model=?;");       //查看报名信息
            p.setString(1, which);
            ResultSet rs = p.executeQuery();

            while (rs.next()) {           //不断获取读到的数据
                sign = 1;
                //category_value char(50),LostTime char(100),LostPosition char(100),LostPerson char(50),LostTelNumber char(50),LostThingsPicture longtext,LostMessage longtext,model char(10));
                Map<String, String> data = new HashMap();
                //获取到对应列的数据保存的map中
                data.put("category_value", rs.getString("category_value"));
                data.put("LostTime", rs.getString("LostTime"));
                data.put("LostPosition", rs.getString("LostPosition"));
                data.put("LostPerson", rs.getString("LostPerson"));
                data.put("LostTelNumber", rs.getString("LostTelNumber"));

                String s = rs.getString("LostThingsPicture");
                if (s == null) {
                    data.put("LostThingsPicture", null);
                    data.put("FirstPicture", null);
                } else {              //返回一个全部图片的地址和第一个图片的地址
                    s = s.replaceAll("\\\\", "/");
                    data.put("LostThingsPicture", s);
                    String[] tmpa = SqlArray.SeparationArray(s);
                    data.put("FirstPicture", tmpa[0]);
                }

                data.put("LostMessage", rs.getString("LostMessage"));
                //返回类型
                if ("picker".equals(rs.getString("model"))) {  //说明是拾物
                    data.put("model", "拾物");
                } else {
                    data.put("model", "失物");
                }
                list.add(data);         //加本次的map进入到链表中
            }
            Dao.close(rs, p, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sign == 1) {
            return list;
        } else {
            return null;
        }

    }

    //返回部分报名人及信息
    public static List signupList(int page, int limit, String which) {
        int sign = 0;           //标记一下当前页数是否有数据
        //参考链接：https://blog.csdn.net/weixin_28779183/article/details/117810091
        List list = new ArrayList();
        try {
            Connection conn = Dao.getConnection();              //连接数据库
            // 进行分页
            // page 页码 limit 每页数量
            // select * from TABLE_NAME where ... order by ... limit (page -1) * limit, limit
            PreparedStatement p = conn.prepareStatement("select * from data_enrol where model=? limit ?,?;");       //查看报名信息
            p.setString(1, which);
            p.setInt(2, (page - 1) * limit);
            p.setInt(3, limit);
            ResultSet rs = p.executeQuery();

            while (rs.next()) {
                sign = 1;
                Map<String, String> data = new HashMap();

                data.put("category_value", rs.getString("category_value"));
                data.put("LostTime", rs.getString("LostTime"));
                data.put("LostPosition", rs.getString("LostPosition"));
                data.put("LostPerson", rs.getString("LostPerson"));
                data.put("LostTelNumber", rs.getString("LostTelNumber"));
                String s = rs.getString("LostThingsPicture");
                if (s == null) {
                    data.put("LostThingsPicture", null);
                    data.put("FirstPicture", null);

                } else {
                    s = s.replaceAll("\\\\", "/");
                    data.put("LostThingsPicture", s);
                    String[] tmpa = SqlArray.SeparationArray(s);
                    data.put("FirstPicture", tmpa[0]);
                }

                data.put("LostMessage", rs.getString("LostMessage"));
                if ("picker".equals(rs.getString("model"))) {  //说明是拾物
                    data.put("model", "拾物");
                } else {
                    data.put("model", "失物");
                }
                list.add(data);
            }
            Dao.close(rs, p, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(list);
        if (sign == 1) {
            return list;
        } else {
            return null;
        }

    }

    //进行查询返回部分报名人及信息
    public static List searchList(int page, int limit, String category_value, String begintime, String endtime, String find, String which) {
        int sign = 0;           //标记一下当前页数是否有数据

        //limit ?,?;
        //进行拼接
        String sql = "select * from data_enrol where model='" + which + "'";
        if (category_value != null && !"".equals(category_value)) {               //说明有筛选类别
            sql += (" and category_value='" + category_value + "'");
            if (find != null && !"".equals(find)) {
                sql += (" and (LostPosition like '%" + find + "%' or LostMessage like '%" + find + "%')");
            }
        } else {
            if (find != null && !"".equals(find)) {
                sql += (" and LostPosition like '%" + find + "%' or LostMessage like '%" + find + "%'");
            }
        }

        sql += " limit ?,?;";

        //参考链接：https://blog.csdn.net/weixin_28779183/article/details/117810091
        List list = new ArrayList();
        int res = 0;
        try {
            Connection conn = Dao.getConnection();              //连接数据库
            // page 页码 limit 每页数量
            // select * from TABLE_NAME where ... order by ... limit (page -1) * limit, limit
            PreparedStatement p = conn.prepareStatement(sql);       //查看报名信息
            p.setInt(1, (page - 1) * limit);
            p.setInt(2, limit);
            System.out.println(p);
            ResultSet rs = p.executeQuery();
            //从前端或者自己模拟一个日期格式，转为String即可
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //将字符串日期转为日期
            Date datebegin = null, dateend = null;
            int time = 0;
            if (begintime != null && !"".equals(begintime) && endtime != null && !"".equals(endtime)) {
                datebegin = format.parse(begintime);
                dateend = format.parse(endtime);
                time = 1;
            }
            while (rs.next()) {
                Map<String, String> data = new HashMap();
                Date when = format.parse(rs.getString("LostTime"));
                if (time == 1) {             //判断现在时间是否在选择的时间范围内
                    if (!(when.after(datebegin) && when.before(dateend))) {
                        continue;
                    }
                }

                sign = 1;
                data.put("category_value", rs.getString("category_value"));
                data.put("LostTime", rs.getString("LostTime"));
                data.put("LostPosition", rs.getString("LostPosition"));
                data.put("LostPerson", rs.getString("LostPerson"));
                data.put("LostTelNumber", rs.getString("LostTelNumber"));

                String s = rs.getString("LostThingsPicture");
                if (s == null) {
                    data.put("LostThingsPicture", null);
                    data.put("FirstPicture", null);

                } else {
                    s = s.replaceAll("\\\\", "/");       //字符串替代
                    data.put("LostThingsPicture", s);
                    String[] tmpa = SqlArray.SeparationArray(s);
                    data.put("FirstPicture", tmpa[0]);
                }

                data.put("LostMessage", rs.getString("LostMessage"));
                if ("picker".equals(rs.getString("model"))) {  //说明是拾物
                    data.put("model", "拾物");
                } else {
                    data.put("model", "失物");
                }
                list.add(data);
            }
            Dao.close(rs, p, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(list);
        if (sign == 1) {
            return list;
        } else {
            return null;
        }

    }

}