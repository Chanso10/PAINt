module com.example.pain {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.swing;
    requires org.testng;
    requires org.slf4j;
    requires java.logging;


    opens com.example.pain to javafx.fxml;
    exports com.example.pain;
}