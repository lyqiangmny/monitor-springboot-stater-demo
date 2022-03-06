# moniter-springboot-stater-demo

**监控接口耗时的 spring-boot-starter demo**

配置项：

```
必需项：
 
rentback.monitor.wechat-robot-url=https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxxx   // 机器人 webHook
rentback.monitor.app-name=paladin-test   //应用名称，可加上环境
 
 
可选项：
 
rentback.monitor.robot-switch-open=true  //报警是否开启，默认 true，如果出现大量报警，可改为 false 临时关闭
rentback.monitor.wechat-tell-to=<@lyqiang><@zhangsan>  // 需要 @ 的成员
rentback.monitor.record-log-gt-mill-second=500   //接口耗时超过该值则会打印日志，默认 500
rentback.monitor.api-cost-alarm-gt-mill-second=5000   //接口耗时超过 打印日志阈值↑ 并且超过该阈值则会发送报警，默认 0 （0 表示不开启此报警）
 
rentback.monitor.api-cost-alarm-by-period=false //是否开启按时间段报警，近 N 时间段内发生 X 次慢请求，则报警，默认不启用
rentback.monitor.api-cost-alarm-count-period=30000 //报警监测时间段 N ，默认 30 秒
rentback.monitor.api-cost-alarm-count-threshold=5 //报警监测阈值 X，默认 5
```