package org.example.task7.repository;

import org.example.task7.model.Item;
import org.example.task7.utility.DBConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.example.task7.utility.Constants.FORMATTER;

public class ItemRepository {
    public Long create(String name, LocalDateTime date, double amount, String description, String pathToImage) {
        String sqlQuery = "INSERT INTO items (name, registration_date, amount, description, path_to_image) VALUES (?,?,?,?,?);";
        long id = -1L;
        try (Connection connection = DBConnectionManager.connect();
             PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setTimestamp(2, Timestamp.valueOf(date));
            statement.setDouble(3, amount);
            statement.setString(4, description);
            statement.setString(5, pathToImage);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys != null && keys.next()) {
                    id = keys.getLong(1);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return id;
    }

    public String updateName(Long id, String name) {
        String sqlQuery = "UPDATE items SET name = '" + name + "' WHERE id = " + id + " RETURNING name;";
        String updatedValue = "";
        try (Connection connection = DBConnectionManager.connect();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sqlQuery);
            while (result.next()) {
                updatedValue = result.getString(1);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return updatedValue;
    }

    public String updateRegistrationDate(Long id, String date) {
        String sqlQuery = "UPDATE items SET registration_date = '" + date + "' WHERE id = " + id + " RETURNING registration_date;";
        String updatedValue = "";
        try (Connection connection = DBConnectionManager.connect();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sqlQuery);
            while (result.next()) {
                updatedValue = result.getString(1);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return updatedValue;
    }

    public Double updateAmount(Long id, String amount) {
        String sqlQuery = "UPDATE items SET amount = '" + amount + "' WHERE id = " + id + " RETURNING amount;";
        double updatedValue = 0.0;
        try (Connection connection = DBConnectionManager.connect();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sqlQuery);
            while (result.next()) {
                updatedValue = result.getDouble(1);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return updatedValue;
    }

    public String updateDescription(Long id, String description) {
        String sqlQuery = "UPDATE items SET description = '" + description + "' WHERE id = " + id + " RETURNING description;";
        String updatedValue = "";
        try (Connection connection = DBConnectionManager.connect();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sqlQuery);
            while (result.next()) {
                updatedValue = result.getString(1);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return updatedValue;
    }

    public String updatePathToImage(Long id, String pathToImage) {
        String sqlQuery = "UPDATE items SET path_to_image = '" + pathToImage + "' WHERE id = " + id + " RETURNING path_to_image;";
        String updatedValue = "";
        try (Connection connection = DBConnectionManager.connect();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sqlQuery);
            while (result.next()) {
                updatedValue = result.getString(1);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return updatedValue;
    }

    public void deletePathToImage(Long id) {
        String sqlQuery = "UPDATE items SET path_to_image = NULL WHERE id = " + id + ";";
        try (Connection connection = DBConnectionManager.connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlQuery);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public List<Item> getAll() {
        String sqlQuery = "SELECT * FROM items order by id;";
        List<Item> items = new ArrayList<>();
        try (Connection connection = DBConnectionManager.connect();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sqlQuery);

            while (result.next()) {
                Item item = new Item(result.getLong("id"), result.getString("name"),
                        result.getTimestamp("registration_date").toLocalDateTime().format(FORMATTER), result.getString("amount"),
                        result.getString("description"), result.getString("path_to_image"));
                items.add(item);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return items;
    }

    public Item getById(Long id) {
        String sqlQuery = "SELECT * FROM items WHERE id = " + id + ";";
        Item item = null;
        try (Connection connection = DBConnectionManager.connect();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sqlQuery);

            while (result.next()) {
                item = new Item(result.getLong("id"), result.getString("name"),
                        result.getTimestamp("registration_date").toLocalDateTime().format(FORMATTER), result.getString("amount"),
                        result.getString("description"), result.getString("path_to_image"));

            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return item;
    }

    public void deleteAll() {
        String sqlQuery = "TRUNCATE items;";
        try (Connection connection = DBConnectionManager.connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlQuery);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteById(Long id) {
        String sqlQuery = "DELETE FROM items WHERE id = " + id + ";";
        try (Connection connection = DBConnectionManager.connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlQuery);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}
