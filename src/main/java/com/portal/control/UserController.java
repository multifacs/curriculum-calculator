package com.portal.control;

import com.portal.service.UserService;
import com.portal.view.UserTabView;
import javafx.application.Platform;
import javafx.scene.Scene;

public class UserController {

    Scene scene;
    public UserController(Scene scene) {
        this.scene = scene;
        userTabView = new UserTabView(scene);

        userTabView.loginBtn.setOnAction(e -> {
            String username = userTabView.usernameField.getText();
            String password = userTabView.passwordField.getText();

            Platform.runLater(() -> {
                if (login(username, password)) {
                    userTabView.onLogin(username);
                } else {
                    userTabView.onLogout();
                }
            });
        });
    }

    UserService userService = new UserService();
    String loggedInUser = null;
    UserTabView userTabView;

    public boolean login(String username, String password) {
        boolean res = userService.login(username, password);
        if (res) {
            loggedInUser = username;
            return true;
        }
        return false;
    }

    public void logout() {
        loggedInUser = null;
    }
}
