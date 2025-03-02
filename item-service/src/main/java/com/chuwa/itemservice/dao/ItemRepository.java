package com.chuwa.itemservice.dao;

import com.chuwa.itemservice.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;


public interface ItemRepository extends MongoRepository<Item, String> {
    List<Item> findItemsByItemIdIn(List<String> itemId);

}