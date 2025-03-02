package com.chuwa.itemservice.service.impl;

import com.chuwa.itemservice.dao.ItemRepository;
import com.chuwa.itemservice.entity.Item;
import com.chuwa.itemservice.exception.ResourceNotFoundException;
import com.chuwa.itemservice.payload.ItemDTO;
import com.chuwa.itemservice.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public ItemDTO createItem(ItemDTO itemDTO) {
        Item item = new Item();
        mapToItem(item, itemDTO);

        Item savedItem = itemRepository.save(item);
        return convertToDTO(savedItem);
    }

    public ItemDTO getItemById(String itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        return convertToDTO(item);

    }


    public ItemDTO updateItem(String id, ItemDTO itemDTO) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        mapToItem(existingItem, itemDTO);
        Item updatedItem = itemRepository.save(existingItem);

        return convertToDTO(updatedItem);
    }

    public void deleteItem(String id) {
        itemRepository.deleteById(id);
    }

    public Page<ItemDTO> getAllItems(Pageable pageable) {
        Page<Item> items = itemRepository.findAll(pageable);
        return items.map(this::convertToDTO);

    }

    public Map<String, Integer> getAvailableUnits(List<String> itemIds) {
        return itemRepository.findItemsByItemIdIn(itemIds).stream()
                .collect(Collectors.toMap(Item::getItemId, Item::getAvailableUnits));
    }

    private void mapToItem(Item item, ItemDTO itemDTO) {
        item.setItemName(itemDTO.getItemName());
        item.setUpc(itemDTO.getUpc());
        item.setUnitPrice(itemDTO.getUnitPrice());
        item.setPictureUrls(itemDTO.getPictureUrls());
        item.setAvailableUnits(itemDTO.getAvailableUnits());
    }

    private ItemDTO convertToDTO(Item item) {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setItemId(item.getItemId());
        itemDTO.setItemName(item.getItemName());
        itemDTO.setUpc(item.getUpc());
        itemDTO.setUnitPrice(item.getUnitPrice());
        itemDTO.setPictureUrls(item.getPictureUrls());
        itemDTO.setAvailableUnits(item.getAvailableUnits());
        return itemDTO;
    }




}
