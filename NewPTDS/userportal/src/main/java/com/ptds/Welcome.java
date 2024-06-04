package com.ptds;

import java.io.IOException;
import javafx.fxml.FXML;

public class Welcome {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("PNRPage");
        
    }
}
