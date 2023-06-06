package com.portal.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserTabView {

    Scene scene;
    public UserTabView(Scene scene) {
        this.scene = scene;
        loginLabel = (Label) scene.lookup("#loginLabel");
        usernameField = (TextField) scene.lookup("#usernameField");
        passwordField = (PasswordField) scene.lookup("#passwordField");
        loginBtn = (Button) scene.lookup("#loginBtn");
        userLabel = (Label) scene.lookup("#userLabel");
        logoutBtn = (Button) scene.lookup("#logoutBtn");
    }

    Label loginLabel;
    public TextField usernameField;
    public PasswordField passwordField;
    public Button loginBtn;
    Label userLabel;
    public Button logoutBtn;

    public void onLogin(String username) {
        loginLabel.setText("Успешно");
        userLabel.setText(username);
        loginBtn.setDisable(true);
        logoutBtn.setDisable(false);
        usernameField.setDisable(true);
        passwordField.setDisable(true);
    }

    public void onLogout() {
        loginLabel.setText("Ошибка");
        userLabel.setText("none");
        loginBtn.setDisable(false);
        logoutBtn.setDisable(true);
        usernameField.setDisable(false);
        passwordField.setDisable(false);
    }
}
