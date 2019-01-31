# dubbo-service-checker
check dubbo service status

functions

* 检查服务是否运行（是否被注册）
* 检查服务需要调用的服务是否都有注册
* 判断关键服务：检查服务被最多引用的，这个可能导致性能或者功能瓶颈
* 判断是否有应用引用环：假如出现环，可能会出现循环调用的问题，也可能代表逻辑拆分不够合理。
