module com.ptds {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    requires java.desktop;
    requires org.apache.pdfbox;

    requires com.google.zxing;
    
        opens com.ptds to javafx.fxml;

    exports com.ptds;
}
