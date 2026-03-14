package com.gautam.batch_processing.config;

import com.gautam.batch_processing.model.Product;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(Product item) {

        if (item.getPrice() == null || item.getPrice().isEmpty()
                || item.getDiscount() == null || item.getDiscount().isEmpty()) {

            return null; // skip this record
        }

        double price = Double.parseDouble(item.getPrice());
        double discount = Double.parseDouble(item.getDiscount());

        double discountAmount = (discount / 100) * price;
        double finalPrice = price - discountAmount;

        item.setDiscountedPrice(String.valueOf(finalPrice));

        return item;
    }
}