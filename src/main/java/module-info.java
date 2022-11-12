module com.example.grapheditor {
    requires javafx.controls;
    requires javafx.fxml;


    opens grapheditor to javafx.fxml;
    exports grapheditor;
    exports grapheditor.controller;
    opens grapheditor.controller to javafx.fxml;
}