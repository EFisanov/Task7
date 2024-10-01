package org.example.task7;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.util.Duration;
import org.example.task7.model.Item;
import org.example.task7.service.ItemService;
import org.example.task7.service.SaveService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ItemTableView extends Application {
    private final TableView<Item> tableView = new TableView<>();
    private final HBox addRowsHBox = new HBox();
    private final HBox saveToFileHBox = new HBox();
    private final ObservableList<Item> data = FXCollections.observableArrayList();
    private final ItemService itemService = new ItemService();
    private final SaveService saveService = new SaveService();
    private final Text noteForAddField = new Text("Поля со звёздочкой (*) обязательны для заполнения");
    private final Text resultOfSavingText = new Text("");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Table View Sample");
        stage.setWidth(1350);
        stage.setHeight(700);

        Scene scene = new Scene(new Group());
        tableView.getStylesheets().add("myStyles.css");

        final Label label = new Label("Опись имущества");
        label.setFont(new Font("Arial", 20));

        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(null);
        errorAlert.setTitle("Ошибка!");

        Alert comfirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        comfirmAlert.setTitle("Требуется подтверждение");
        comfirmAlert.setContentText("Нажмите ОК для удаления и Cancel для отмены");

        data.addAll(itemService.getAll());
        tableView.setItems(data);
        tableView.setEditable(true);

        TableColumn<Item, Boolean> activeColumn = new TableColumn<>();
        activeColumn.setCellValueFactory(cd -> cd.getValue().activeProperty());
        activeColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activeColumn));

        TableColumn<Item, String> idColumn = createColumn("№", "id", 50);
        idColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Item, String> nameColumn = createColumn("Наименование", "name", 180);
        nameColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                t -> {
                    Long id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
                    String updatedValue = itemService.updateName(id, t.getNewValue());
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setName(updatedValue);
                }
        );

        TableColumn<Item, String> dateColumn = createColumn("Дата регистрации", "registrationDate", 110);
        dateColumn.setStyle("-fx-alignment: CENTER;");
        dateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        dateColumn.setOnEditCommit(
                t -> {
                    Long id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
                    String oldValue = t.getTableView().getItems().get(t.getTablePosition().getRow()).getRegistrationDate();
                    if (isDate(t.getNewValue())) {
                        String updatedValue = itemService.updateRegistrationDate(id, t.getNewValue());
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setRegistrationDate(updatedValue);
                    } else {
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setRegistrationDate(oldValue);
                        errorAlert.setContentText("Допустимый формат даты: ДД.ММ.ГГГГ");
                        errorAlert.showAndWait();
                    }
                    reloadDataList();
                }
        );

        TableColumn<Item, String> amountColumn = createColumn("Количество", "amount", 80);
        amountColumn.setStyle("-fx-alignment: CENTER;");
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountColumn.setOnEditCommit(
                t -> {
                    Long id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
                    String oldValue = t.getTableView().getItems().get(t.getTablePosition().getRow()).getAmount();
                    if (isDouble(t.getNewValue())) {
                        Double updatedValue = itemService.updateAmount(id, t.getNewValue());
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setAmount(String.valueOf(updatedValue));
                    } else {
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setAmount(oldValue);
                        errorAlert.setContentText("В поле \"Количество\" допустимо только положительное число в формате: \"0\", \"0.\" \"0.0\"");
                        errorAlert.showAndWait();
                    }
                    reloadDataList();
                }
        );

        TableColumn<Item, String> descriptionColumn = createColumn("Описание", "description", 550);
        descriptionColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(
                t -> {
                    Long id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
                    String updatedValue = itemService.updateDescription(id, t.getNewValue());
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setDescription(updatedValue);
                }
        );

        TableColumn<Item, String> imageColumn = new TableColumn<>("Изображение");
        imageColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getPathToImage()).getReadOnlyProperty());
        imageColumn.setCellFactory(p -> new ImageCell<>());
        imageColumn.setPrefWidth(100);

        TableColumn<Item, Item> buttonsColumn = getButtonsColumn(stage, comfirmAlert);

        tableView.getColumns().addAll(activeColumn, idColumn, nameColumn, dateColumn, amountColumn, descriptionColumn, imageColumn, buttonsColumn);

        final TextField addName = createTextField("*Наименование", nameColumn.getPrefWidth());
        final TextField addAmount = createTextField("*Количество", amountColumn.getPrefWidth());
        final TextField addDescription = createTextField("Описание", descriptionColumn.getPrefWidth());

        addName.setMinWidth(120);
        addAmount.setMinWidth(100);
        addDescription.setMinWidth(400);

        Text imagePath = new Text("Нет изображения");
        final Button loadImageButton = new Button("Загрузить картинку");

        loadImageButton.setOnAction(event -> {
            File file = getImageFile(stage);
            if (file != null) {
                imagePath.setText(file.getAbsolutePath());
            }
        });

        final Button addNoteButton = new Button("Добавить запись");
        addNoteButton.setOnAction(e -> {
            Item newItem;
            if (addName.getText() == null || addName.getText().isEmpty() || addAmount.getText() == null || addAmount.getText().isEmpty()) {
                noteForAddField.setFill(Color.RED);
                PauseTransition visiblePause = new PauseTransition(
                        Duration.seconds(4)
                );
                visiblePause.setOnFinished(
                        event -> noteForAddField.setFill(Color.BLACK)
                );
                visiblePause.play();
                return;
            }

            if (!isDouble(addAmount.getText())) {
                addAmount.clear();
                errorAlert.setContentText("В поле \"Количество\" допустимо только положительное число в формате: \"0\", \"0.\" \"0.0\"");
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
            } else {
                newItem = itemService.createItem(addName.getText(), addAmount.getText(), addDescription.getText(), imagePath.getText());
                data.add(newItem);
                addName.clear();
                addAmount.clear();
                addDescription.clear();
                imagePath.setText("Нет изображения");
            }
            reloadDataList();
        });

        final Button saveToFileButton = getSaveButton(stage, resultOfSavingText);
        final Button clearTableButton = getClearTableButton(comfirmAlert);

        addRowsHBox.getChildren().addAll(addNoteButton, addName, addAmount, addDescription, loadImageButton, imagePath);
        addRowsHBox.setPrefWidth(1320);
        addRowsHBox.setSpacing(10);

        saveToFileHBox.getChildren().addAll(saveToFileButton, resultOfSavingText);
        saveToFileHBox.setSpacing(10);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, tableView, addRowsHBox, noteForAddField, saveToFileHBox, clearTableButton);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setScene(new Scene(new Group(vbox)));
        stage.show();
    }

    private Button getClearTableButton(Alert comfirmAlert) {
        final Button clearTableButton = new Button("Очистить таблицу");
        clearTableButton.setStyle("-fx-base: red");
        clearTableButton.setMinWidth(180);
        clearTableButton.setOnAction(event -> {
            comfirmAlert.setHeaderText("Вы действительно хотите удалить все данные из таблицы?");
            Optional<ButtonType> option = comfirmAlert.showAndWait();
            if (option.isPresent() && option.get() == ButtonType.OK) {
                itemService.deleteAll();
                reloadDataList();
            }
        });
        return clearTableButton;
    }

    private Button getSaveButton(Stage stage, Text text) {
        final Button saveToFileButton = new Button("Сохранить выбранное в файл");
        saveToFileButton.setMinWidth(180);
        saveToFileButton.setStyle("-fx-base: dodgerblue");
        saveToFileButton.setOnAction(event -> {
            List<Item> changedItems = data.stream()
                    .filter(Item::isActive)
                    .toList();
            if (changedItems.isEmpty()) {
                setSaveToFileMessage(text, 4, Color.RED, "(Внимание!) Не выбраны строки");
            } else {
                try {
                    saveService.saveToFile(stage, changedItems);
                    setSaveToFileMessage(text, 4, Color.BLACK, "Файл сохранён");
                } catch (IOException e) {
                    setSaveToFileMessage(text, 7, Color.RED, "(Ошибка!) Возникли проблемы при сохранении файла");
                    e.printStackTrace();
                }
            }
        });
        return saveToFileButton;
    }


    private TableColumn<Item, Item> getButtonsColumn(Stage stage, Alert comfirmAlert) {
        TableColumn<Item, Item> imageLoadColumn = new TableColumn<>();
        imageLoadColumn.setPrefWidth(150);
        imageLoadColumn.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );

        imageLoadColumn.setCellFactory(param -> new TableCell<>() {
            private final Button changeImageButton = new Button("Изменить картинку");
            private final Button deleteImageButton = new Button("Удалить картинку");
            private final Button deleteRowButton = new Button("Удалить строку");

            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null) {
                    setGraphic(null);
                    return;
                }

                final VBox editRowButtonHBox = new VBox();
                editRowButtonHBox.getChildren().addAll(changeImageButton, deleteImageButton, deleteRowButton);
                setGraphic(editRowButtonHBox);

                changeImageButton.setMinWidth(145);
                changeImageButton.setOnAction(event -> {
                    File file = getImageFile(stage);
                    if (file != null) {
                        itemService.updatePathToImage(item.getId(), file.getAbsolutePath());
                        reloadDataList();
                    }
                });

                deleteImageButton.setMinWidth(145);
                deleteImageButton.setOnAction(event -> {
                    comfirmAlert.setHeaderText("Вы действительно хотите удалить изображение?");
                    Optional<ButtonType> option = comfirmAlert.showAndWait();
                    if (option.isPresent() && option.get() == ButtonType.OK) {
                        itemService.deletePathToImage(item.getId());
                        reloadDataList();
                    }
                });

                deleteRowButton.setStyle("-fx-base: red ");
                deleteRowButton.setMinWidth(145);
                deleteRowButton.setOnAction(event -> {
                    comfirmAlert.setHeaderText("Вы действительно хотите удалить строку?");
                    Optional<ButtonType> option = comfirmAlert.showAndWait();
                    if (option.isPresent() && option.get() == ButtonType.OK) {
                        itemService.deleteById(item.getId());
                        reloadDataList();
                    }
                });
            }
        });
        return imageLoadColumn;
    }

    private TableColumn<Item, String> createColumn(String headerTable, String nameFieldItem, int size) {
        TableColumn<Item, String> column = new TableColumn<>(headerTable);
        column.setMinWidth(size);
        column.setCellValueFactory(new PropertyValueFactory<>(nameFieldItem));
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
    }

    private boolean isDouble(String s) {
        return s.matches("^\\d+\\.?(\\d+)?$");
    }

    private boolean isDate(String s) {
        return s.matches("^(0?[1-9]|[12]\\d|3[01])\\.(0?[1-9]|1[12])\\.(19\\d\\d|20(0\\d|1\\d|2[0-4]))$");
    }

    private void setSaveToFileMessage(Text text, int durationInSecond, Color color, String message) {
        PauseTransition pause = new PauseTransition(Duration.seconds(durationInSecond));
        text.setText(message);
        text.setFill(color);
        pause.setOnFinished(event ->
                text.setText("")
        );
        pause.play();
    }

    private File getImageFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Файл изображения", "*.jpg")
        );
        return fileChooser.showOpenDialog(stage);
    }
}