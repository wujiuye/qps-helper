# qps-helper
从alibaba sentinel中提取的qps统计代码，并修改为通用的qps统计工具包

### 使用qps-helper统计接口的QPS

```java
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class DemoController {
    
    private QpsHelper qpsHelper = new QpsHelper();

    @GetMapping("/test")
    public ApiResponse testApi() {
        try{
            long startTime = TimeUtil.currentTimeMillis();
            // 业务逻辑
            //    ......
            // 计算耗时
            long rt = TimeUtil.currentTimeMillis() - startTime;
            qpsHelper.incrSuccess(rt);
            return ApiResponse.ok();
        }catch (Exception e){
            qpsHelper.incrException();
            return ApiResponse.error();
        }
    }

}
```