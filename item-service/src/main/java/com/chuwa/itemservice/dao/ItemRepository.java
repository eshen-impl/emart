package com.chuwa.itemservice.dao;

import com.chuwa.itemservice.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;




public interface ItemRepository extends MongoRepository<Item, String> {

}