package com.neo.trigger.http.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayOrderDTO {
    private String payUrl;

    private Integer payType;
}
