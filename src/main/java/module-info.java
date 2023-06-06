module com.portal {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    requires com.zaxxer.hikari;
    requires java.sql;
    requires org.apache.poi.ooxml;

    opens com.portal to javafx.fxml;
    exports com.portal;
    exports com.portal.hikari;
    opens com.portal.hikari to javafx.fxml;
}