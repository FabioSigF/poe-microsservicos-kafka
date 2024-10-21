package com.poekafka.microsservico_kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poekafka.microsservico_kafka.dto.request.OrderRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j //adiciona um log na classe com lombok
@Service
public class SaveOrderService {

    @KafkaListener(topics = "save_order", groupId = "microservice_save_order")
    private void execute(ConsumerRecord<String, String> record) {
        log.info("Key = {}", record.key());
        log.info("Headers = {}", record.headers());
        log.info("Partition = {}", record.partition());

        String data = record.value();

        ObjectMapper mapper = new ObjectMapper();
        OrderRequestDto order = null;

        try {
            order = mapper.readValue(data, OrderRequestDto.class);
        } catch (JsonProcessingException e) {
            log.error("Falha ao converter evento [dado={}]", data, e);
            return;
        }

        log.info("Evento recebido = {}", data);
    }

}
