package com.neo.config;

import com.neo.sdk.chatgpt.session.OpenAiSession;
import com.neo.sdk.chatgpt.session.OpenAiSessionFactory;
import com.neo.sdk.chatgpt.session.defaults.DefaultOpenAiSessionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(ChatGPTSDKConfigProperties.class)
public class ChatGPTSDKConfig {

    @Bean
    public OpenAiSession openAiSession(ChatGPTSDKConfigProperties properties) {
        // 1. 配置文件
        com.neo.sdk.chatgpt.session.Configuration configuration = new com.neo.sdk.chatgpt.session.Configuration();
        configuration.setApiHost(properties.getApiHost());
        configuration.setApiKey(properties.getApiKey());

        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);

        // 3. 开启会话
        return factory.openSession();
    }

}
