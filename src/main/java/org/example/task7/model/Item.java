package org.example.task7.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;


public class Item {
    private SimpleBooleanProperty active;
    private SimpleLongProperty id;
    private SimpleStringProperty name;
    private SimpleStringProperty registrationDate;
    private SimpleStringProperty amount;
    private SimpleStringProperty description;
    private SimpleStringProperty pathToImage;

    public Item() {
        this.active = new SimpleBooleanProperty(false);
        this.name = new SimpleStringProperty();
        this.registrationDate = new SimpleStringProperty();
        this.amount = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.pathToImage = new SimpleStringProperty();
    }

    public Item(String name, String registrationDate, String amount, String description, String pathToImage) {
        this.active = new SimpleBooleanProperty(false);
        this.name = new SimpleStringProperty(name);
        this.registrationDate = new SimpleStringProperty(registrationDate);
        this.amount = new SimpleStringProperty(amount);
        this.description = new SimpleStringProperty(description);
        this.pathToImage = new SimpleStringProperty(pathToImage);
    }

    public Item(Long id, String name, String registrationDate, String amount, String description, String pathToImage) {
        this.active = new SimpleBooleanProperty(false);
        this.id = new SimpleLongProperty(id);
        this.name = new SimpleStringProperty(name);
        this.registrationDate = new SimpleStringProperty(registrationDate);
        this.amount = new SimpleStringProperty(amount);
        this.description = new SimpleStringProperty(description);
        this.pathToImage = new SimpleStringProperty(pathToImage);
    }

    public boolean isActive() {
        return active.get();
    }

    public SimpleBooleanProperty activeProperty() {
        return active;
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public long getId() {
        return id.get();
    }

    public void setId(long id) {
        this.id = new SimpleLongProperty(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getRegistrationDate() {
        return registrationDate.get();
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate.set(registrationDate);
    }

    public String getAmount() {
        return amount.get();
    }

    public void setAmount(String amount) {
        this.amount.set(String.valueOf(amount));
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getPathToImage() {
        return pathToImage.get();
    }

    public void setPathToImage(String pathToImage) {
        this.pathToImage.set(pathToImage);
    }

}
