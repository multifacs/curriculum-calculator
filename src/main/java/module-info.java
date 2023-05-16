module com.portal {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.controlsfx.controls;
    requires org.apache.poi.ooxml;

    opens com.portal to javafx.fxml;
    opens com.portal.model to javafx.base;
    exports com.portal;
    exports com.portal.model;
}