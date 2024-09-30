package org.example.task7;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageCell<S> extends TableCell<S, String> {
    private final ImageView imageView = new ImageView();

    @Override
    protected void updateItem(String fileName, boolean empty) {
        super.updateItem(fileName, empty);

        if (fileName == null || empty) {
            imageView.setImage(null);
            setGraphic(null);
        } else {
            imageView.setImage(createImage(fileName));
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            setGraphic(imageView);
        }
    }

    private Image createImage(String fileName) {
        return new Image(fileName);
    }
}
