package org.example.task7;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.task7.model.Item;
import org.example.task7.service.ItemService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ItemTableView extends Application {
    private TableView table = new TableView();
    final HBox hb = new HBox();
    private final ObservableList<Item> data = FXCollections.observableArrayList();
    ItemService itemService = new ItemService();
    SaveService saveService = new SaveService();
    Text noteForAddField = new Text("Поля со звёздочкой (*) обязательны для заполнения");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        noteForAddField.setStyle("-fx-base: red ");
        Scene scene = new Scene(new Group());
        stage.setTitle("Table View Sample");
        stage.setWidth(1450);
        stage.setHeight(700);

        final Label label = new Label("Address Book");
        label.setFont(new Font("Arial", 20));

        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(null);
        errorAlert.setTitle("Ошибка!");

        Alert comfirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        comfirmAlert.setTitle("Требуется подтверждение");
        comfirmAlert.setContentText("Нажмите ОК для удаления и Cancel для отмены");

        data.addAll(itemService.getAll());
        table.setItems(data);
        table.setEditable(true);

        TableColumn<Item, Boolean> activeColumn = new TableColumn<>("Active");
        activeColumn.setCellValueFactory(cd -> cd.getValue().activeProperty());
        activeColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activeColumn));

        TableColumn idColumn = createColumn("№", "id", 50);

        TableColumn nameColumn = createColumn("Наименование", "name", 300);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Item, String>>) t -> {
                    Long id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
                    String updatedValue = itemService.updateName(id, t.getNewValue());
                    ((Item) t.getTableView().getItems().get(t.getTablePosition().getRow())).setName(updatedValue);
                }
        );

        TableColumn dateColumn = createColumn("Дата регистрации", "registrationDate", 200);
        dateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        dateColumn.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Item, String>>) t -> {
                    Long id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
                    String oldValue = t.getTableView().getItems().get(t.getTablePosition().getRow()).getRegistrationDate();
                    if (isDate(t.getNewValue())) {
                        String updatedValue = itemService.updateRegistrationDate(id, t.getNewValue());
                        ((Item) t.getTableView().getItems().get(t.getTablePosition().getRow())).setRegistrationDate(updatedValue);
                    } else {
                        ((Item) t.getTableView().getItems().get(t.getTablePosition().getRow())).setRegistrationDate(oldValue);
                        errorAlert.setContentText("Допустимый формат даты: ДД.ММ.ГГГГ");
                        errorAlert.showAndWait();
                    }
                    reloadDataList();
                }
        );

        TableColumn amountColumn = createColumn("Количество", "amount", 100);
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountColumn.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Item, String>>) t -> {
                    Long id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
                    String oldValue = t.getTableView().getItems().get(t.getTablePosition().getRow()).getAmount();
                    if (isDouble(t.getNewValue())) {
                        Double updatedValue = itemService.updateAmount(id, t.getNewValue());
                        ((Item) t.getTableView().getItems().get(t.getTablePosition().getRow())).setAmount(String.valueOf(updatedValue));
                    } else {
                        ((Item) t.getTableView().getItems().get(t.getTablePosition().getRow())).setAmount(oldValue);
                        errorAlert.setContentText("В поле \"Количество\" допустимо только положительное число");
                        errorAlert.showAndWait();
                    }
                    reloadDataList();
                }
        );

        TableColumn descriptionColumn = createColumn("Описание", "description", 400);
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Item, String>>) t -> {
                    Long id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
                    String updatedValue = itemService.updateDescription(id, t.getNewValue());
                    ((Item) t.getTableView().getItems().get(t.getTablePosition().getRow())).setDescription(updatedValue);
                }
        );

        TableColumn<Item, String> imageColumn = new TableColumn<>("Изображение");
        imageColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getPathToImage()).getReadOnlyProperty());
        imageColumn.setCellFactory(p -> new ImageCell<>());
        imageColumn.setPrefWidth(100);

        TableColumn<Item, Item> imageEditColumn = getImageEditColumn(stage, comfirmAlert);

        TableColumn<Item, Item> removeColumn = getRemoveColumn(comfirmAlert);

        table.getColumns().addAll(activeColumn, idColumn, nameColumn, dateColumn, amountColumn, descriptionColumn, imageColumn, imageEditColumn, removeColumn);

        final TextField addName = createTextField("*Наименование", nameColumn.getPrefWidth());
        final TextField addAmount = createTextField("*Количество", amountColumn.getPrefWidth());
        final TextField addDescription = createTextField("Описание", descriptionColumn.getPrefWidth());
        Text imagePath = new Text("Нет изображения");
        final Button loadImageButton = new Button("Загрузить картинку");

        loadImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                imagePath.setText(file.getAbsolutePath());
            }
            noteForAddField.setFill(Color.BLACK);
        });

        final Button addNoteButton = new Button("Добавить запись");
        addNoteButton.setOnAction(e -> {
            Item newItem;
            if (addName.getText() == null || addName.getText().isEmpty() || addAmount.getText() == null || addAmount.getText().isEmpty()) {
                noteForAddField.setFill(Color.RED);
                return;
            }

            if (!isDouble(addAmount.getText())) {
                addAmount.clear();
                errorAlert.setContentText("В поле \"Количество\" допустимо только положительное число");
                errorAlert.showAndWait();
                return;
            }

            if (imagePath.getText().equals("Нет изображения")) {
                newItem = itemService.createItem(addName.getText(), addAmount.getText(), addDescription.getText(), null);
                data.add(newItem);
                addName.clear();
                addAmount.clear();
                addDescription.clear();
                imagePath.setText("Нет изображения");
                noteForAddField.setFill(Color.BLACK);
            } else {
                newItem = itemService.createItem(addName.getText(), addAmount.getText(), addDescription.getText(), imagePath.getText());
                data.add(newItem);
                addName.clear();
                addAmount.clear();
                addDescription.clear();
                imagePath.setText("Нет изображения");
                noteForAddField.setFill(Color.BLACK);
            }
            reloadDataList();
        });

        final Button saveToXlsxButton = new Button("Сохранить выбранное в .xlsx");
        saveToXlsxButton.setMinWidth(150);
        saveToXlsxButton.setStyle("-fx-base: dodgerblue");
        saveToXlsxButton.setOnAction(event -> {
            List<Item> changedItems = data.stream()
                    .filter(Item::isActive)
                    .toList();
            try {
                saveService.saveToXlsx(changedItems);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        final Button saveToCSV = new Button("Сохранить выбранное в .csv");
        saveToCSV.setMinWidth(150);
        saveToCSV.setStyle("-fx-base: dodgerblue");
        saveToCSV.setOnAction(event -> {
            List<Item> changedItems = data.stream()
                    .filter(Item::isActive)
                    .toList();
            try {
                saveService.saveToCSV(changedItems);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        final Button clearTableButton = new Button("Очистить таблицу");
        clearTableButton.setStyle("-fx-base: red");
        clearTableButton.setMinWidth(150);
        clearTableButton.setOnAction(event -> {
            comfirmAlert.setHeaderText("Вы действительно хотите удалить все данные из таблицы?");
            Optional<ButtonType> option = comfirmAlert.showAndWait();
            if (option.isPresent() && option.get() == ButtonType.OK) {
                itemService.deleteAll();
                reloadDataList();
            }
        });

        hb.getChildren().addAll(addNoteButton, addName, addAmount, addDescription, loadImageButton, imagePath);
        hb.setPrefWidth(1320);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table, hb, noteForAddField, saveToCSV, saveToXlsxButton, clearTableButton);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setScene(new Scene(new Group(vbox)));
        stage.show();
    }

    private TableColumn<Item, Item> getRemoveColumn(Alert comfirmAlert) {
        TableColumn<Item, Item> removeColumn = new TableColumn<>();
        removeColumn.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        removeColumn.setCellFactory(param -> new TableCell<Item, Item>() {
            private final Button deleteButton = new Button("Удалить");

            @Override
            protected void updateItem(Item itemToRemove, boolean empty) {
                super.updateItem(itemToRemove, empty);

                if (itemToRemove == null) {
                    setGraphic(null);
                    return;
                }
                deleteButton.setStyle("-fx-base: red ");
                setGraphic(deleteButton);
                deleteButton.setOnAction(event -> {
                    comfirmAlert.setHeaderText("Вы действительно хотите удалить строку?");
                    Optional<ButtonType> option = comfirmAlert.showAndWait();
                    if (option.isPresent() && option.get() == ButtonType.OK) {
                        itemService.deleteById(itemToRemove.getId());
                        reloadDataList();
                    }
                });
            }
        });
        return removeColumn;
    }

    private TableColumn<Item, Item> getImageEditColumn(Stage stage, Alert comfirmAlert) {
        TableColumn<Item, Item> imageLoadColumn = new TableColumn<>();
        imageLoadColumn.setPrefWidth(150);
        imageLoadColumn.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );

        imageLoadColumn.setCellFactory(param -> new TableCell<Item, Item>() {
            private final Button changeImageButton = new Button("Изменить картинку");
            private final Button deleteImageButton = new Button("Удалить картинку");

            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);

                final VBox vbox2 = new VBox();
                vbox2.getChildren().addAll(changeImageButton, deleteImageButton);
                setGraphic(vbox2);

                changeImageButton.setMinWidth(150);
                changeImageButton.setOnAction(event -> {
                    FileChooser fileChooser = new FileChooser();
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        itemService.updatePathToImage(item.getId(), file.getAbsolutePath());
                        reloadDataList();
                    }
                });

                deleteImageButton.setMinWidth(150);
                deleteImageButton.setOnAction(event -> {
                    comfirmAlert.setHeaderText("Вы действительно хотите удалить изображение?");
                    Optional<ButtonType> option = comfirmAlert.showAndWait();
                    if (option.isPresent() && option.get() == ButtonType.OK) {
                        itemService.deletePathToImage(item.getId());
                        reloadDataList();
                    }
                });
            }
        });
        return imageLoadColumn;
    }

    private TableColumn createColumn(String headerTable, String nameFieldItem, int size) {
        TableColumn column = new TableColumn(headerTable);
        column.setMinWidth(size);
        column.setCellValueFactory(new PropertyValueFactory<Item, String>(nameFieldItem));
        return column;
    }

    private TextField createTextField(String fieldName, Double sizeField) {
        TextField field = new TextField();
        field.setPromptText(fieldName);
        field.setMaxWidth(sizeField);
        return field;
    }

    private void reloadDataList() {
        data.clear();
        data.addAll(itemService.getAll());
        noteForAddField.setFill(Color.BLACK);
    }

    private boolean isDouble(String s) {
        return s.matches("^\\d+.?\\d*$");
    }

    private boolean isDate(String s) {
        return s.matches("^(0?[1-9]|[12]\\d|3[01])\\.(0?[1-9]|1[12])\\.(19\\d\\d|20(0\\d|1\\d|2[0-4]))$");
    }
}