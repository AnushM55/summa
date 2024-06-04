package com.ptds;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.JTextField;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

public class GuestPage implements Initializable{
  
    @FXML Button BackButton;
    @FXML Button AddGuestButton;
    @FXML GridPane AllGuestDisplayPane;
    @FXML FlowPane GuestDetailsPane;
    @FXML TextField GuestNameField;
    @FXML TextField GuestNumberField;
    @FXML TextField NameField;
    @FXML TextField NumberField;
    @FXML Separator seperator ;
    @FXML Separator seperator2 ;
    @FXML Button CancelButton;
    @FXML ScrollPane scrollpane;
    @FXML VBox EntireVBOX;
    @FXML TextField CostField;
    @FXML Button ProceedButton;
    static double XLayout; 
    static double YLayout; 

    static boolean CanProceed;
    static HashMap<Integer, GuestDetails> PassengerInfo;
    JTextField amount;

    void PrintAllValuesInHashmap() {

        for (Integer key : PassengerInfo.keySet()) {
            System.out.println(
                    PassengerInfo.get(key).returnName() + " " + PassengerInfo.get(key).returnPhoneNumber().toString());
        }

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
        XLayout = GuestDetailsPane.getLayoutX();
        YLayout = GuestDetailsPane.getHeight()*-1;
        PassengerInfo = new HashMap<Integer, GuestDetails>();
        AllGuestDisplayPane.setVgap(20);

    BackButton.setOnAction( new EventHandler<ActionEvent>() {
        public void handle(ActionEvent event) {
            try{
             PassengerInfo = null;
             
            App.setRoot("PNRPage");
            }catch(IOException e){
                System.err.println("IO Exception occured while going back one page");
            }
        };
    });

    ProceedButton.setOnAction( new EventHandler<ActionEvent>() {
        public void handle(ActionEvent event) {
            try{
                if( VerifyAllFields()){
                PassengerInformation.setPassengerInfo(PassengerInfo);
                App.setRoot("BankPage");

                }else{
                    RaiseAlertBox("FILL ALL TEXTFIELDS PROPERLY");
                }
            }catch(IOException e){
                System.err.println("IO Exception occured while going to bankpage");
                System.err.println(e.getLocalizedMessage());
            }
        };
    });

    NumberField.textProperty().addListener((observable, oldValue, newValue) -> {
       
        if (newValue.matches("\\d*")&&(newValue.length()<=14)) { 
            return;
        }

        NumberField.setText(newValue.replaceAll("[^\\d+ ]", ""));    

    });
 /*    AllGuestDisplayPane.heightProperty().addListener((new ChangeListener<Number>() {
 //
 //       @Override
 //       public void changed(ObservableValue<? extends Number> observable, Number oldvalue, Number newValue) {
 //           
 //        scrollpane.setVvalue((Double)newValue );  
 //       }
 //   }));
*/

    AddGuestButton.setOnAction(new EventHandler<ActionEvent>() {
     
        public void handle(ActionEvent event) {
           

            if (NameField.getText().isBlank()){
                RaiseAlertBox("Name field is empty ");

            }else if (!phoneNumberFormatValidator(NumberField.getText())){
                RaiseAlertBox("Enter valid phone number");

            }else{
            FlowPane newEntry = GenerateNewGuestEntry(NameField.getText(),NumberField.getText());
            AllGuestDisplayPane.getChildren().add(newEntry);
            CostField.setText("Rs: "+GuestDetails.amount*PassengerInfo.size());

            GridPane.setRowIndex(newEntry, PassengerInfo.size());
            GridPane.setVgrow(newEntry,Priority.ALWAYS);
            GridPane.setHgrow(newEntry,Priority.NEVER);
            if(PassengerInfo.size()>=6){
                scrollpane.setVvalue(0.1*PassengerInfo.size());
            }else{
                scrollpane.setVvalue(0);
            }
            NameField.setText("");
            NumberField.setText("");
            PrintAllValuesInHashmap();
        }
    }
    });
   }
   
   FlowPane GenerateNewGuestEntry(String Name, String PhoneNumber){
   


            FlowPane newpane = new FlowPane();
            TextField name = new TextField();
            
            name.setStyle(GuestNameField.getStyle());
            name.setPrefSize(GuestNameField.getPrefWidth(),GuestNameField.getPrefHeight());
            name.setText(Name);
            name.setFont(GuestNameField.getFont());
            name.setAlignment(GuestNameField.getAlignment());
            TextField number = new TextField();
            number.setPrefSize(GuestNumberField.getPrefWidth(),GuestNumberField.getPrefHeight());

            number.setStyle(GuestNumberField.getStyle());
            number.setText(PhoneNumber);
            number.setFont(GuestNumberField.getFont());
            number.setAlignment(GuestNumberField.getAlignment());

            number.textProperty().addListener((observable, oldValue, newValue) -> {
                
                if (newValue.matches("[\\d+ *]")&&(newValue.length()<=14)) { 
                   
                    return;
                }
                
                number.setText(newValue.replaceAll("[^\\d+ ]", ""));    
        
            });
            Separator sep1 = new Separator(seperator.getOrientation());
            sep1.setPrefSize(seperator.getPrefWidth(),seperator.getPrefHeight());

            Separator sep2 = new Separator(seperator2.getOrientation());
            sep2.setPrefSize(seperator2.getPrefWidth(),seperator2.getPrefHeight());
            sep1.setVisible(false);
            sep2.setVisible(false);
            Button cancelButton = new Button("");
            try {

            
                cancelButton.setGraphic(new ImageView(new Image(getClass().getResource("CancelButton.png").toURI().toString())));

            }catch (URISyntaxException e){
                       System.out.println("Cancelbutton.png does not exist");
            }
            cancelButton.setStyle(CancelButton.getStyle());
            cancelButton.setPrefSize(CancelButton.getPrefWidth(), CancelButton.getPrefHeight());
            cancelButton.setMinSize(CancelButton.getMinWidth(),CancelButton.getMinHeight());
            cancelButton.setOnAction(new EventHandler<ActionEvent>() {
     
                public void handle(ActionEvent event) {

                    if(AllGuestDisplayPane.getChildren().contains(newpane)){
                        AllGuestDisplayPane.getChildren().remove(newpane);
                        ReorderAllGuestPanes();
                    
                    }
                    newpane.setVisible(false);
                    
                    PassengerInfo.remove(cancelButton.hashCode());
                    if(PassengerInfo.size()>=6){
                        scrollpane.setVvalue(0.1*PassengerInfo.size());
                    }else{
                        scrollpane.setVvalue(0);
                    }
                    CostField.setText("Rs: "+GuestDetails.amount*PassengerInfo.size());


                }
            });
            newpane.getChildren().addAll(name,  sep1,number, sep2, cancelButton);
            newpane.setPrefWidth(GuestDetailsPane.getPrefWidth());
            newpane.setPrefHeight(GuestDetailsPane.getPrefHeight());
            newpane.setLayoutX(XLayout);
            if (YLayout <= 0 ) {
                newpane.setLayoutY(20);
            }else{
            newpane.setLayoutY(YLayout+(GuestNameField.getHeight()*2));
            
          }
          YLayout = newpane.getLayoutY();
          PassengerInfo.put(cancelButton.hashCode(), new GuestDetails(name, number));
            return newpane;
            
        }

    boolean phoneNumberFormatValidator(String PhoneNumber){
        // validate indian - format phone numbers using regex
       //using the statement :  (\+91)?(-)?\s*?(91)?\s*?([6-9]{1}\d{2})-?\s*?(\d{3})-?\s*?(\d{4})
       /*
        * (\+91)? // optionally match '+91'
        (91)?    : optionally match '91'
        -?       : optionally match '-'
        [6-9]{1} : compulsory match starting digit in range 6-9
               
        (\d{9})  : compulsory match remaining 9 digits
        \s*?     : optionally match whitespace

        */  


        return PhoneNumber.matches("(\\+91)?(-)?\\s*?(91)?\\s*?([6-9]{1}\\d{9})");
    }
    void RaiseAlertBox(String ErrorMessage){
        Alert  alert = new Alert(AlertType.ERROR, ErrorMessage);
        alert.initStyle(StageStyle.UTILITY);
        alert.setResizable(true);
        alert.onShownProperty().addListener(e -> { 
            Platform.runLater(() -> alert.getDialogPane().getScene().getWindow().sizeToScene()); 
        });

      alert.showAndWait();
    }
    boolean VerifyAllFields() {
        if (PassengerInfo.size()==0){
            return false;
        }
        for (Integer key : PassengerInfo.keySet()) {

            if (PassengerInfo.get(key).returnName().equals("Name")
                    || !phoneNumberFormatValidator(PassengerInfo.get(key).returnPhoneNumber())) {
                return false;
            }
            if (PassengerInfo.get(key).returnName().isBlank()) {
                return false;
            }
        }
        return true;
    }
    
    void ReorderAllGuestPanes(){
        int i = 0;
        for (javafx.scene.Node x : AllGuestDisplayPane.getChildren()){
            GridPane.setRowIndex(x, i);
            i += 1;
        }
    }
   
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("welcomepage");
    }  
}