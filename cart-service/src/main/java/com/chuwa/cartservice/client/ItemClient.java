package com.chuwa.cartservice.client;


import com.chuwa.cartservice.payload.ItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item-service", path = "/api/v1/items")
public interface ItemClient {

    @GetMapping("/{id}")
    ItemDTO getItemById(@PathVariable("id") String id);
}
