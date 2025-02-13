package saintindustries.cs408.lab3a;

import android.widget.Toast;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Model {


    private StringBuilder leftHand = new StringBuilder().append("0");
    private StringBuilder rightHand = new StringBuilder();
    private String originalNumber = "";
    private char operator = ' ';
    private States currentState = States.CLEAR;

    public Model(){

    }
    // Getters
    public String getLeftHand() {
        return leftHand.toString();
    }

    public String getRightHand() {
        return rightHand.toString();
    }

    public char getOperator() {
        return operator;
    }

    public States getCurrentState() {
        return currentState;
    }

    // Setters
    public void addToLeftHand(char leftHand) {
        this.leftHand.append(leftHand);
    }

    public void addToRightHand(char rightHand) {
        this.rightHand.append(rightHand);
    }

    public void setOperator(char operator) {
        this.operator = operator;
    }

    public void setCurrentState(States currentState) {
        this.currentState = currentState;
    }

    public void clear(){
        this.leftHand = new StringBuilder().append("0");
        this.rightHand = new StringBuilder();
        this.operator = ' ';
        this.currentState = States.CLEAR;
        this.originalNumber = "";

    }

    public void clearAndSetLeftHand(String input){
        this.leftHand = new StringBuilder().append(input);

    }
    public void clearAndSetRightHand(String input){
        this.rightHand = new StringBuilder().append(input);

    }
    public String getOriginalNumber(){
        return this.originalNumber;
    }
    public void setOriginalNumber(String originalNumber){
        this.originalNumber = originalNumber;
    }




}
