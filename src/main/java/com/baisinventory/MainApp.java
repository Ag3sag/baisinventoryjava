package com.baisinventory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/baisinventory/ui/login.fxml"));
        Scene scene = new Scene(loader.load(), 400, 400);   // carga Login.fxml con tamaño 400x300
        stage.setScene(scene);
        stage.setTitle("Login - BAIS Inventory");          // título de la ventana
        stage.getIcons().add(new Image(getClass().getResource("/com/baisinventory/images/Logo.png").toExternalForm()));
        stage.show();                                      // muestra la ventana
    }

    public static void main(String[] args) {
        launch(args);   // inicia la app JavaFX
    }
}