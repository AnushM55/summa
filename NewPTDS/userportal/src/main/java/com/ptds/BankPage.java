package com.ptds;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;

 public class BankPage implements Initializable{
    static String TransacID;

    @FXML Label AmountLabel;
    @FXML Button CancelButton;
    @FXML Button MakePayment;
    @FXML TextField AccNumberTextField;
    @FXML TextField AccHolderTextField;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AmountLabel.setText("Rs. "+PassengerInformation.getPassengerInformation().size()*GuestDetails.amount);
        CancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            try{
                PassengerInformation.setPassengerInfo(null);
                GuestDetails.SetPNR("");
                App.setRoot("welcomepage");
            }catch(IOException e){
                System.err.println(e.getLocalizedMessage());
            }
        }
            
        
        });
        MakePayment.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            String accountNumber = AccNumberTextField.getText();
                double paymentAmount = Double.parseDouble(AmountLabel.getText().replace("Rs.","").strip());
                Connection conn = null;
                try{
                     conn = DBConn.connect();
                    conn.setAutoCommit(false); // Disable auto-commit to start transaction
                    
                    // ] Check if the account exists
                    String query = "SELECT balance FROM account WHERE account_no = ? AND name = ?";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1,  accountNumber);
                    statement.setString(2,AccHolderTextField.getText());
                    ResultSet rset = statement.executeQuery();
                    if(rset.next()){
                        
                        double currentBalance = rset.getDouble("balance");
                        System.out.println(currentBalance);
                        //  Deduct the payment amount from the current balance
                        if (currentBalance >= paymentAmount) {
                            double updatedBalanceForUser = currentBalance - paymentAmount;
                            
                            // Update the account balance
                            String updateQuery = "UPDATE account SET balance = balance + ? WHERE name = ? AND account_no = ?";
                            PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
                            updateStatement.setDouble(1, paymentAmount);
                            updateStatement.setString(2, "SASA RAILWAYS");
                            updateStatement.setString(3, "98766987651");
                            updateStatement.executeUpdate();
                            
                            updateQuery = "UPDATE account SET balance = ? WHERE name = ? AND account_no = ?";
                            updateStatement = conn.prepareStatement(updateQuery);
                            updateStatement.setDouble(1,updatedBalanceForUser);
                            updateStatement.setString(2, AccHolderTextField.getText());
                            updateStatement.setString(3, AccNumberTextField.getText());
                            updateStatement.executeUpdate();
                            
                            

                            /* 
                            // Insert transaction record into another database table
                            */
                            String insertQuery = "INSERT INTO banking_info(account_no,amount,transaction_id,name) VALUES(?, ?, ?, ?)";
                            PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                            insertStatement.setString(1, accountNumber);
                            insertStatement.setDouble(2, paymentAmount);
                            SetTransacID(new RandomNumber(10).GenerateNumber());
                            insertStatement.setString(3,ReturnTransacID());
                            insertStatement.setString(4,AccHolderTextField.getText());
                            
                            insertStatement.executeUpdate();
                            conn.commit();
                            Alert  alert = new Alert(AlertType.INFORMATION, "Payment Success!");
                            alert.initStyle(StageStyle.UTILITY);
                            alert.setResizable(true);
                            alert.onShownProperty().addListener(e -> { 
                                Platform.runLater(() -> alert.getDialogPane().getScene().getWindow().sizeToScene()); 
                            });

                    
                          alert.showAndWait();
                          try{

                          App.setRoot("TicketPage");
                          }catch(IOException e){
                            
                            System.err.println("Caught IO Exception while switching to ticket page");
                            for( StackTraceElement x : e.getStackTrace()){
                                System.out.println(x.toString());
                            }
                          }
                        } else {
                           RaiseAlertBox("Insufficient Balance. Please Retry.");
                        }
                    } else {
                        RaiseAlertBox("Invalid Account Number or name. Please Retry.");
                    }
                    
                    rset.close();
                    statement.close();
                } catch (SQLException ex){
                    ex.printStackTrace();
                    try {
                        if (conn != null) {
                            conn.rollback(); // Rollback the transaction if an error occurs
                        }
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                } finally {
                    try {
                        if (conn != null) {
                            conn.close(); // Close the connection in the finally block
                        }
                    } catch (SQLException closeEx) {
                        closeEx.printStackTrace();
                    }
                }
            }           
        });
            
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

    public static void SetTransacID(String trID){
        TransacID = trID;
    }
    public static String ReturnTransacID(){
        return TransacID;
    }
    
     
}
