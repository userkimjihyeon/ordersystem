package com.example.ordersystem.common.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    // RabbitMQ 접속 정보 (application.yml에서 주입)
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    // ✅ RabbitMQ 연결 팩토리 생성
    // RabbitMQ에 연결할 수 있는 ConnectionFactory 객체를 생성
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter()); //객체를 자동json직렬화
        return rabbitTemplate;
    }

    // ✅ 메시지를 보낼 큐 등록
    // 해당 이름의 큐가 없으면 RabbitMQ 서버에 자동 생성됨
//    스프링빈 생성을 통해 rabbitMQ에 자동으로 아래 변수명으로 큐가 생성
    @Bean
    public Queue stockQueue() {
        return new Queue("stockDecreaseQueue", true);
    }
}
