package com.ptds;

import java.util.Random;

public class RandomNumber {
   
    int digits;
    
    public RandomNumber(int digitNo){
      
      digits = digitNo;
     

    }
   

    public String GenerateNumber(){
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digits; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
           
        }

    }
    
  
