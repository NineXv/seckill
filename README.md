# seckill
基于springcloud netflix的分布式秒杀系统
# 作者的话
作者水平有限，本项目的初衷是为了练手，可能有些地方的设计并不优雅  
如果有问题可以及时联系我：799663897@qq.com

## 开发工具
IntelliJ IDEA 2018.3
## 技术栈
**BootStrap** ： 前端页面设计  
**Thymeleaf** ： 模板语言  
**springcloud netflix** : 主要是使用Eurake  
**mybatis** ： ORM框架  
**mysql** : 数据库，版本8.0.18  
**redis** ： Nosql  
**RabbitMQ** : 消息队列  
## 如何使用
表的设计sql，我放在了consumer模块下的sql包中，还有，我使用mysql存储模块，所以还请先将存储模块语句放在数据库中执行一遍！！！
mysql,redis,rabbitMQ的配置因人而异，只需要修改成自己的地址和username和password就行了。  
小提示（如果你是mysql5的版本，还需要修改mysql的driver）
## 设计
项目设计三个模块
**api模块**提供通用的bean和untils工具类  
**consumer模块**负责接收用户请求  
**server服务模块**提供了数据库的操作服务  
#### 流程图
**画的不好，还请见谅**  
**（具体的代码逻辑和秒杀的优化思路，我都有在项目里详细的使用注释说明了，诸位可以看一看）**
![Alt text](https://github.com/NineXv/seckill/blob/master/myPng/seckill.png)


 



