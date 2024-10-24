package com.poekafka.api_rest_kafka.controller;

import com.poekafka.api_rest_kafka.service.RegisterEventService;
import com.poekafka.api_rest_kafka.dto.request.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrdersController {

    private final RegisterEventService registerEventService;

    @PostMapping("/save")
    public ResponseEntity<String> saveOrder(@RequestBody OrderRequestDto order) {
        registerEventService.addEvent("save_order", order);
        return ResponseEntity.ok("Sucesso");
    }

}
