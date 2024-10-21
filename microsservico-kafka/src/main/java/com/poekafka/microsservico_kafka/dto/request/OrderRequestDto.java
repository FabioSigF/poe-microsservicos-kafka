package com.poekafka.microsservico_kafka.dto.request;

import java.math.BigDecimal;

public record OrderRequestDto(String code, String productName, BigDecimal price) {
}
