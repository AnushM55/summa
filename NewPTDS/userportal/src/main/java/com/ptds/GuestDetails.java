package com.ptds;
import javafx.scene.control.TextField;




class GuestDetails {
    TextField Name;
    TextField PhoneNumber;
    static String PNR;

    static float amount = 20;
    
    GuestDetails(TextField name , TextField PhNo){
        
        Name= name;
        PhoneNumber = PhNo;
    }
    static void SetPNR(String pnr){
        PNR = pnr;
    }
    String returnName(){
        return Name.getText();
    }
    String returnPhoneNumber(){
        return PhoneNumber.getText();
    }
}
