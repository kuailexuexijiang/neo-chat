package com.neo.domain.order.model.entity;

import com.neo.types.enums.ProductEnableModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {

    /**
     * 商品ID
     */
    private Integer productId;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品描述
     */
    private String productDesc;

    /**
     * 可用模型
     */
    private String productModelTypes;
    /**
     * 额度次数
     */
    private Integer quota;
    /**
     * 商品价格
     */
    private BigDecimal price;
    /**
     * 商品状态；0无效、1有效
     */
    private ProductEnableModel enable;

    /**
     * 是否有效；true = 有效，false = 无效
     */
    public boolean isAvailable() {
        return ProductEnableModel.OPEN.equals(enable);
    }

}