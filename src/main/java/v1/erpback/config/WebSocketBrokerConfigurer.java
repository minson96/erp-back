package v1.erpback.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfigurer implements WebSocketMessageBrokerConfigurer {

    @Bean
    public WebSocketHandler customWebSocketHandler() {
        return new CustomWebSocketHandler();
    }
    @Autowired
    private HttpHandshakeInterceptor httpHandshakeInterceptor;

    @Autowired
    private WebsocketBrokerInterceptor websocketBrokerInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(websocketBrokerInterceptor);
//    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                //.addInterceptors(httpHandshakeInterceptor)
                .setAllowedOriginPatterns("http://192.168.0.121:3000","http://192.168.50.252:3000","http://192.168.0.101:3000", "http://localhost:3000", "http://192.168.50.5:3000");
//                .setHandshakeHandler(new CustomHandshakeHandler()) // 모든 도메인 허용 (필요에 따라 제한)
    }
}
