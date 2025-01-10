package v1.erpback.config;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class MyBatisInterceptor implements Interceptor {
    private final SimpMessagingTemplate messagingTemplate;

    public MyBatisInterceptor(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        String sqlCommandType = ms.getSqlCommandType().name(); // INSERT, UPDATE, DELETE

        Object result = invocation.proceed(); // 실제 SQL 실행

        // 특정 작업에 대해 WebSocket 메시지 전송
        if ("INSERT".equals(sqlCommandType) || "UPDATE".equals(sqlCommandType) || "DELETE".equals(sqlCommandType)) {
            messagingTemplate.convertAndSend("/topic/updates", sqlCommandType + " operation occurred");
        }

        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}
}
