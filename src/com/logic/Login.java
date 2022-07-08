package com.logic;

import com.dao.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login {
    //检测是否有该账户,有则返回真，没有则返回假
    public static boolean checkUser(String username) {
        try {
            Connection conn = Dao.getConnection();              //连接数据库
            //构造sql语句
            PreparedStatement p = conn.prepareStatement("select * from user_data where open_id=?;");       //查询
            //将？替代成具体的值
            p.setString(1, username);
            //执行sql语句
            ResultSet rs = p.executeQuery();
            //读取sql的值
            if(rs.next()){
                String user_name = rs.getString("open_id");
                Dao.close(rs, p, conn);
                return true;
            }
            Dao.close(rs, p, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    //检测是否有该账户,有则返回真，没有则返回假
    public static boolean checkskey(String skey) {
        try {
            Connection conn = Dao.getConnection();              //连接数据库
            PreparedStatement p = conn.prepareStatement("select * from user_data where skey=?;");       //查询
            p.setString(1, skey);
            ResultSet rs = p.executeQuery();
            if(rs.next()){
                String user_name = rs.getString("skey");
                Dao.close(rs, p, conn);
                return true;
            }
            Dao.close(rs, p, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    //注册
    public static boolean register(String open_id,String skey){
        //ticket就是准考证号，准考证号就是登录名
        if(Login.checkUser(open_id) == false ){          //已经注册的就不能再注册
            try {
                Connection conn = Dao.getConnection();
                /* 对账户密码的数据库进行补充 */
                PreparedStatement p = conn.prepareStatement("insert into user_data(open_id,skey) VALUES (?,?);");
                //对占位符进行补充
                p.setString(1, open_id);
                p.setString(2, skey);
                p.executeUpdate();
                System.out.println("注册成功");
                Dao.close(p, conn);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("该账户已经注册过，请不要重复注册！");
            return false;
        }
        return false;
    }

    //更新skey
    public static boolean updateskey(String open_id, String newskey) {
        try {
            Connection conn = Dao.getConnection();              //连接数据库
            //
            PreparedStatement p = conn.prepareStatement("update user_data set skey=? where open_id=?;");
            p.setString(1,newskey);
            p.setString(2,open_id);
            p.executeUpdate();            //执行SQL语句(注意这里是execute，因为进行的是修改操作)
            Dao.close(p, conn);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
