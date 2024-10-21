package com.poekafka.api_rest_kafka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterEventService {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public <T> void addEvent(String topic, T data) {
        kafkaTemplate.send(topic, data);
    }
}
