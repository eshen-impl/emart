package com.chuwa.itemservice.service;

import com.chuwa.itemservice.payload.ItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {
    ItemDTO createItem(ItemDTO itemDTO);

    ItemDTO getItemById(String itemId);
    ItemDTO updateItem(String id, ItemDTO itemDTO);
    void deleteItem(String id);
    Page<ItemDTO> getAllItems(Pageable pageable);


}
