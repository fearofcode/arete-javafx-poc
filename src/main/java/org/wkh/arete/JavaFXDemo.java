package org.wkh.arete;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class JavaFXDemo extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Hello World");
        Group root = new Group();
        Scene scene = new Scene(root, 300, 250);
        Button btn = new Button();
        btn.setLayoutX(100);
        btn.setLayoutY(80);
        btn.setText("Hello World");
        btn.setOnAction( actionEvent ->
                System.out.println("Hello World"));
        root.getChildren().add(btn);
        stage.setScene(scene);
        stage.show();
    }
}
