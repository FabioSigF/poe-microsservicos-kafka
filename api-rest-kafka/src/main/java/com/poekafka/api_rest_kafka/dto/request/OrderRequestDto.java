package com.poekafka.api_rest_kafka.dto.request;

import java.math.BigDecimal;

public record OrderRequestDto(String code, String productName, BigDecimal price) {
}
