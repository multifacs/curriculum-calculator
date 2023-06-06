package com.portal;

import com.portal.control.UserController;
import com.portal.hikari.DataSource;
import com.portal.repo.UserRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class PortalApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PortalApplication.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 500);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        try(Connection connection = DataSource.getConnection()) {
            System.out.println(connection);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        UserRepository userRepository = new UserRepository();
        UserController userController = new UserController(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}