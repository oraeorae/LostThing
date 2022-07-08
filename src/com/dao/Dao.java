package com.dao;

import java.sql.*;

//作为辅助连接数据库的工具
public class Dao {
    // 获取数据库连接
    //scnu_wechat	scnu_wechat	YJ8DhK5miHaYsx44
    public static Connection getConnection(){
        Connection conn = null;
        //注意这里后面加参数避免中文乱码，而且在web.xml加部分内容避免乱码
        String url = "jdbc:mysql://*****:3306/*****?useUnicode=true&characterEncoding=utf8";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Class.forName("com.mysql.jdbc.Driver");
            //数据库名字和密码在这里改！！！！
            conn = DriverManager.getConnection(url, "*****", "*****");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("数据库驱动加载出错");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库出错");
        }
        return conn;
    }
    //关闭相关通道
    public static void close(ResultSet rs,PreparedStatement p,Connection conn)
    {
        try
        {
            if(!rs.isClosed()){
                rs.close();
            }
            if(!p.isClosed()){
                p.close();
            }
            if(!conn.isClosed()){
                conn.close();
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.out.println("数据关闭出错");
        }
    }

    //关闭相关通道
    public static void close(PreparedStatement p,Connection conn)
    {
        try
        {
            if(!p.isClosed()){
                p.close();
            }
            if(!conn.isClosed()){
                conn.close();
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.out.println("数据关闭出错");
        }
    }

}
