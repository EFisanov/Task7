package org.example.task7.service;

import org.example.task7.model.Item;
import org.example.task7.repository.ItemRepository;

import java.time.LocalDate;
import java.util.List;

import static org.example.task7.utility.Constants.FORMATTER;

public class ItemService {
    private ItemRepository itemRepository = new ItemRepository();

    public Item createItem(String name, String amount, String description, String pathToImage) {
        Item item = new Item();
        item.setName(name);
        item.setRegistrationDate(LocalDate.now().format(FORMATTER));
        item.setAmount(amount);
        item.setDescription(description);
        if (pathToImage != null) {
            item.setPathToImage(pathToImage);
        }

        Long id = itemRepository.create(item.getName(), LocalDate.parse(item.getRegistrationDate(), FORMATTER).atStartOfDay(), Double.parseDouble(item.getAmount()),
                item.getDescription(), item.getPathToImage());

        item.setId(id);
        return item;
    }

    public List<Item> getAll() {
        return itemRepository.getAll();
    }

    public Item getById(Long id) {
        return itemRepository.getById(id);
    }

    public void deleteAll() {
        itemRepository.deleteAll();
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    public String updateName(Long id, String name) {
        return itemRepository.updateName(id, name);
    }

    public String updateRegistrationDate(Long id, String date) {
        return itemRepository.updateRegistrationDate(id, date);
    }

    public Double updateAmount(Long id, String amount) {
        return itemRepository.updateAmount(id, amount);
    }

    public String updateDescription(Long id, String description) {
        return itemRepository.updateDescription(id, description);
    }

    public String updatePathToImage(Long id, String path) {
        return itemRepository.updatePathToImage(id, path);
    }

    public void deletePathToImage(Long id) {
        itemRepository.deletePathToImage(id);
    }

}
