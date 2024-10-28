package org.example.task7.service;

import org.example.task7.model.Item;
import org.example.task7.repository.ItemRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.example.task7.utility.Constants.FORMATTER;

public class ItemService {
    private ItemRepository itemRepository = new ItemRepository();

    public Item createItem(String name, String amount, String description, String pathToImage) throws SQLException {
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

    public List<Item> getAll() throws SQLException {
        return itemRepository.getAll();
    }

    public Item getById(Long id) throws SQLException {
        return itemRepository.getById(id);
    }

    public void deleteAll() throws SQLException {
        itemRepository.deleteAll();
    }

    public void deleteById(Long id) throws SQLException {
        itemRepository.deleteById(id);
    }

    public String updateName(Long id, String name) throws SQLException {
        return itemRepository.updateName(id, name);
    }

    public String updateRegistrationDate(Long id, String date) throws SQLException {
        return itemRepository.updateRegistrationDate(id, date);
    }

    public Double updateAmount(Long id, String amount) throws SQLException {
        return itemRepository.updateAmount(id, amount);
    }

    public String updateDescription(Long id, String description) throws SQLException {
        return itemRepository.updateDescription(id, description);
    }

    public String updatePathToImage(Long id, String path) throws SQLException {
        return itemRepository.updatePathToImage(id, path);
    }

    public void deletePathToImage(Long id) throws SQLException {
        itemRepository.deletePathToImage(id);
    }

}
