package  com.ptds;
import java.util.HashMap;
class PassengerInformation {
    static private HashMap<Integer, GuestDetails> PassengerInfo;
    
    static void setPassengerInfo(HashMap<Integer, GuestDetails> Map){
        PassengerInfo = Map;
    }
    static HashMap<Integer, GuestDetails> getPassengerInformation(){
        return PassengerInfo;
    }
   

}