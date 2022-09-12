# 项目介绍
【SpringMVC】——微信小程序“失物招领”后端

项目简介：该系统用于校内的失物招领，有效得帮助师生及时找到失物；项目分为失物模块和拾物模块，都基于登记，查看两大功能。

技术栈 ：SpringMVC，JWT

项目亮点 ：

( 1 ) 对图片进行了上传时压缩，减轻前端显示压力；

( 2 ) 接口使用了拦截器来进行请求的拦截 ，将需要先登陆才能请求的接口进行拦截判断 ，大大提高了接口的安全性；
	
功能模块：
（1）失物模块——登记、查看、筛选

（2）拾物模块——登记、查看、筛选

# 版本

## 终版1.0（2022.4.15）
与小程序前端进行了测试，修改了部分错误


## 完善版2.0(2022.4.4)
完善:
1.简化部分接口参数名称
2.解决数据库中文乱码			【web.xml加代码和数据库加链接】
3.部分接口一个改成两个

## 基本功能版1.0（2022.3.28）

## 测试接口版0.1（2022.3.27）
当前功能：
1.返回两张图片
2.得到用户手机号码
