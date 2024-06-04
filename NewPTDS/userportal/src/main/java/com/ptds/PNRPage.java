package com.ptds;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Popup;

public class PNRPage implements Initializable{
  @FXML Button BackButton;
  @FXML Label Invalid;
    @Override
   public void initialize(URL location, ResourceBundle resources) {
    BackButton.setOnAction( new EventHandler<ActionEvent>() {
        public void handle(ActionEvent event) {
            try{
            Invalid.setVisible(false);
            PNRField.clear();
            App.setRoot("welcomepage");
            }catch(IOException e){
                System.err.println("IO Exception occured while going back one page");
            }
        };
    } ); 

        PNRField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.length()<10 ){
                PNRField.setStyle(PNRField.getStyle().replace("-fx-border-color: #fdf8e1","-fx-border-color : #ff6f00"));
            }else{
                PNRField.setStyle(PNRField.getStyle().replace("-fx-border-color : #ff6f00","-fx-border-color: #fdf8e1"));


            }
            if (newValue.matches("\\d*")&&(newValue.length()<=10)) { 
                return;
            }else if(newValue.length()>10){
                newValue = newValue.substring(0, 10);
            }
            PNRField.setText(newValue.replaceAll("[^\\d]", ""));    

        });
    
   }
    @FXML Button SubmitButton;
    @FXML TextField PNRField;
    @FXML
    private void switchToPrimary() throws IOException {

        App.setRoot("welcomepage");
    }
    @FXML 
    public void ValidatePNRNumber()throws  IOException{
        if(!VerifyPNRNUmber(PNRField.getText())){
            Invalid.setVisible(true);
            
        }else{
            GuestDetails.SetPNR(PNRField.getText());
            Invalid.setVisible(false);

            App.setRoot("GuestPage");
            

        }
    }
   private  boolean VerifyPNRNUmber(String textboxContent) {
       
        try (
            Connection newConn = DBConn.connect(); 
            PreparedStatement newStat = newConn.prepareStatement("select PNR_NO from train_ticket_info where PNR_NO = ? and dateofbooking::date = (now()::date) and departure_time > current_time and departure_time <= (current_time + interval '30 minutes');");
            ){
            newStat.setString(1, textboxContent);
            ResultSet recs = newStat.executeQuery();
            
            if(recs.next()){
                return true;
            }
                return false;
            
        }catch (SQLException e) {
            // TODO: handle exception
            System.err.println(e.getLocalizedMessage());
            return false;
        }

        
    }
    private void showPopup(Button showPopupButton, String message) {
        Popup popup = new Popup();
        Label msgLabel = new javafx.scene.control.Label(message);
        msgLabel.setStyle(" -fx-text-fill: white;");
        popup.getContent().add(msgLabel);
        popup.show(showPopupButton.getScene().getWindow());
      }
}