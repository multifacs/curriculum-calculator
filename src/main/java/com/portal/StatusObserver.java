package com.portal;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import java.util.Observable;
import java.util.Observer;

public class StatusObserver implements Observer {
    Label statusLabel;
    ProgressIndicator progressIndicator;
    public StatusObserver(DBConnection o, Label label, ProgressIndicator progressIndicator) {
        observe(o);
        this.statusLabel = label;
        this.progressIndicator = progressIndicator;
    }
    public void observe(Observable o) {
        o.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        String statusText = ((DBConnection) o).getStatusText();
        Platform.runLater(() -> {
            statusLabel.setText(statusText);
            if (statusText == "Connected") {
                progressIndicator.setOpacity(0);
            }
        });
    }
}