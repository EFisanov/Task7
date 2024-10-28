package org.example.task7.repository;

import org.example.task7.model.Item;
import org.example.task7.utility.DBConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.example.task7.utility.Constants.FORMATTER;

public class ItemRepository {
    public Long create(String name, LocalDateTime date, double amount, String description, String pathToImage) throws SQLException {
        String sqlQuery = "INSERT INTO items (name, registration_date, amount, description, path_to_image) VALUES (?,?,?,?,?);";
        long id = -1L;
        try (Connection connection = DBConnectionManager.connect();
             PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setTimestamp(2, Timestamp.valueOf(date));
            System.out.println(date);
            System.out.println(Timestamp.valueOf(date));
            statement.setDouble(3, amount);
            statement.setString(4, description);
            statement.setString(5, pathToImage);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys != null && keys.next()) {
                    id = keys.getLong(1);
                }
            }
        }
        return id;
    }

    public String updateName(Long id, String name) throws SQLException {
        String sqlQuery = "UPDATE items SET name = ? WHERE id = ? RETURNING name;";
        String updatedValue = "";
        try (Connection connection = DBConnectionManager.connect();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, name);
            statement.setLong(2, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                updatedValue = result.getString(1);
            }
        }
        return updatedValue;
    }

    public String updateRegistrationDate(Long id, String date) throws SQLException {
        String sqlQuery = "UPDATE items SET registration_date = ? WHERE id = ? RETURNING registration_date;";
        String updatedValue = "";
        try (Connection connection = DBConnectionManager.connect();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            LocalDate dateFormat = LocalDate.parse(date, FORMATTER);
            statement.setTimestamp(1, Timestamp.valueOf(dateFormat.atStartOfDay()));
            statement.setLong(2, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                updatedValue = result.getString(1);
            }
        }
        return updatedValue;
    }

    public Double updateAmount(Long id, String amount) throws SQLException {
        String sqlQuery = "UPDATE items SET amount = ? WHERE id = ? RETURNING amount;";
        double updatedValue = 0.0;
        try (Connection connection = DBConnectionManager.connect();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setDouble(1, Double.parseDouble(amount));
            statement.setLong(2, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                updatedValue = result.getDouble(1);
            }
        }
        return updatedValue;
    }

    public String updateDescription(Long id, String description) throws SQLException {
        String sqlQuery = "UPDATE items SET description = ? WHERE id = ? RETURNING description;";
        String updatedValue = "";
        try (Connection connection = DBConnectionManager.connect();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, description);
            statement.setLong(2, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                updatedValue = result.getString(1);
            }
        }
        return updatedValue;
    }

    public String updatePathToImage(Long id, String pathToImage) throws SQLException {
        String sqlQuery = "UPDATE items SET path_to_image = ? WHERE id = ? RETURNING path_to_image;";
        String updatedValue = "";
        try (Connection connection = DBConnectionManager.connect();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, pathToImage);
            statement.setLong(2, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                updatedValue = result.getString(1);
            }
        }
        return updatedValue;
    }

    public void deletePathToImage(Long id) throws SQLException {
        String sqlQuery = "UPDATE items SET path_to_image = NULL WHERE id = ?;";
        try (Connection connection = DBConnectionManager.connect();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public List<Item> getAll() throws SQLException {
        String sqlQuery = "SELECT * FROM items order by id;";
        List<Item> items = new ArrayList<>();
        try (Connection connection = DBConnectionManager.connect();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sqlQuery);

            while (result.next()) {
                Item item = new Item(result.getLong("id"),
                        result.getString("name"),
                        result.getTimestamp("registration_date").toLocalDateTime().format(FORMATTER),
                        result.getString("amount"),
                        result.getString("description"),
                        result.getString("path_to_image"));
                items.add(item);
            }
        }
        return items;
    }

    public Item getById(Long id) throws SQLException {
        String sqlQuery = "SELECT * FROM items WHERE id = ?;";
        Item item = null;
        try (Connection connection = DBConnectionManager.connect();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                item = new Item(result.getLong("id"), result.getString("name"),
                        result.getTimestamp("registration_date").toLocalDateTime().format(FORMATTER), result.getString("amount"),
                        result.getString("description"), result.getString("path_to_image"));

            }
        }
        return item;
    }

    public void deleteAll() throws SQLException {
        String sqlQuery = "TRUNCATE items;";
        try (Connection connection = DBConnectionManager.connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlQuery);
        }
    }

    public void deleteById(Long id) throws SQLException {
        String sqlQuery = "DELETE FROM items WHERE id = ?;";
        try (Connection connection = DBConnectionManager.connect();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }
}
