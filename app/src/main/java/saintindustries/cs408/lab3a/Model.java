package saintindustries.cs408.lab3a;

import android.util.Log;
import android.widget.Toast;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Model {



    private StringBuilder leftHand = new StringBuilder();
    private StringBuilder rightHand = new StringBuilder();
    private String originalNumber = "";
    private String output = "0";
    private char operator = ' ';
    private States currentState = States.CLEAR;

    private DecimalFormat df = new DecimalFormat("0.#####E0");

    protected PropertyChangeSupport propertyChangeSupport;



    public Model() {
        propertyChangeSupport = new PropertyChangeSupport(this);

    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }


    // Getters

    public void updateDisplay(String input){

        if(this.getCurrentState().equals(States.ERROR)){
            firePropertyChange(Controller.DISPLAYTAG, output, "Error hit C to clear");
            this.output = input;
        }

        else if(input.length() >= 8) {
            String sendIt = "";
            try {
                BigDecimal value = new BigDecimal(input);

                sendIt = df.format(value);

            } catch (Exception e) {
                Log.i("MyTag", "Failed to update display");

            }
            firePropertyChange(Controller.DISPLAYTAG, output, sendIt);
            this.output = sendIt;
        }else{
            firePropertyChange(Controller.DISPLAYTAG, output, input);
            this.output = input;
        }
    }
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
        this.leftHand = new StringBuilder().append(new BigDecimal(this.getLeftHand()).toString());
        this.updateDisplay(String.valueOf(this.getLeftHand()));
    }

    public void addToRightHand(char rightHand) {
        this.rightHand.append(rightHand);
        this.rightHand = new StringBuilder().append(new BigDecimal(this.getRightHand()).toString());
        this.updateDisplay(String.valueOf(this.getRightHand()));
    }

    public void setOperator(char operator) {
        this.operator = operator;

    }

    public void setCurrentState(States currentState) {
        this.currentState = currentState;
    }

    public void clear() {
        this.leftHand = new StringBuilder();
        this.rightHand = new StringBuilder();
        this.operator = ' ';
        this.currentState = States.CLEAR;
        this.originalNumber = "";
        this.updateDisplay("0");

    }

    public void clearAndSetLeftHand(String input) {
        this.leftHand = new StringBuilder().append(input);
        this.updateDisplay(this.getLeftHand());

    }
    public void clearAndSetRightHand(){
        this.rightHand = new StringBuilder();
    }

    public void clearAndSetRightHand(String input) {
        this.rightHand = new StringBuilder().append(input);
        this.updateDisplay(this.getRightHand());

    }

    public String getOriginalNumber() {
        return this.originalNumber;
    }

    public void setOriginalNumber(String originalNumber) {
        this.originalNumber = originalNumber;

    }



    public String sqrt(String input) {
        try {
            MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
            Double num = Double.parseDouble(input);
            num = Math.pow(num, 0.5);
            return num.toString();
        } catch (Exception e) {
            this.setCurrentState(States.ERROR);
            return "Error hit C to clear";
        }
    }

    public void percent() {
        try {
            MathContext mc = new MathContext(9, RoundingMode.HALF_UP);
            BigDecimal LHS = new BigDecimal(this.getLeftHand());
            BigDecimal RHS = new BigDecimal(this.getRightHand());
            RHS = LHS.multiply(RHS);
            RHS = RHS.divide(new BigDecimal("100"), mc);
            this.clearAndSetRightHand(RHS.toString());
            this.updateDisplay(this.getRightHand());


        } catch (Exception e) {
            this.setCurrentState(States.ERROR);
        }

    }

    public String sign(String input) {
        try {
            BigDecimal num = new BigDecimal(input);
            num = num.multiply(new BigDecimal("-1"));
            return num.toString();
        } catch (Exception e) {
            this.setCurrentState(States.ERROR);
            return "Error hit C to clear";
        }

    }

    public boolean CheckIfOperand(char input) {
        if ((Character.isDigit(input) || input == '.')) {
            return true;
        } else {
            return false;
        }
    }
    public boolean CheckIfOperator(char input){
        if(input=='+' || input=='-' || input=='÷' || input=='X'){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean ChecktoAppendLH() {
        if (this.getRightHand().isEmpty() && (this.getCurrentState().equals(States.CLEAR) || this.getCurrentState().equals(States.LHS) || this.getCurrentState().equals(States.RESULT))) {
            return true;
        } else {
            return false;
        }

    }

    public boolean ChecktoAppendRH() {
        if (!this.getLeftHand().isEmpty() && (this.getCurrentState().equals(States.OP_SCHEDULED) || this.getCurrentState().equals(States.RHS))) {
            return true;
        } else {
            return false;
        }

    }

    public Boolean equals(BigDecimal RHS) {

        String previousResult = this.output;
        String result = "";
        BigDecimal LHS = new BigDecimal(this.getLeftHand());

        BigDecimal resultBig = new BigDecimal(0);
        MathContext mc = new MathContext(9, RoundingMode.HALF_UP);
        if (this.getCurrentState().equals(States.CLEAR)) {
            result = "0";
            return true;
        }


        try {
            switch (this.getOperator()) {
                case '+':
                    resultBig = LHS.add(RHS, mc);
                    break;
                case '-':
                    resultBig = LHS.subtract(RHS, mc);
                    break;
                case 'X':
                    resultBig = LHS.multiply(RHS, mc);
                    break;
                case '÷':
                    resultBig = LHS.divide(RHS, mc);
                    break;
            }

        } catch (Exception e) {
            this.setCurrentState(States.ERROR);
            this.updateDisplay("Error hit c to clear");

            return false;
        }

        result = resultBig.toString();
        this.clearAndSetLeftHand(result);
        this.setCurrentState(States.RESULT);
        this.clearAndSetRightHand();
        Log.i("Result", result);


        this.updateDisplay(result);

        return true;

    }

    public void setInput(Character input) {
        Log.i("MyTag", "It is attempting to parse input (Inside Model)");

        if ((CheckIfOperand(input) && ChecktoAppendLH())) {
            if (this.getCurrentState().equals(States.CLEAR) || this.getCurrentState().equals(States.ERROR) || this.getCurrentState().equals(States.RESULT))
                this.clearAndSetLeftHand(String.valueOf(input));
            else
                this.addToLeftHand(input);
            this.setCurrentState(States.LHS);
            this.setOriginalNumber(String.valueOf(input));
        } else if (CheckIfOperand(input) && ChecktoAppendRH()) {
            this.addToRightHand(input);
            this.setCurrentState(States.RHS);
            this.setOriginalNumber(String.valueOf(input));

        }
        else if(CheckIfOperand(input) && this.getCurrentState().equals(States.UNI) ){
            this.clearAndSetRightHand(String.valueOf(input));
            this.setCurrentState(States.RHS);
        }

        else if (input == '%' && this.getCurrentState().equals(States.RHS)) {
            percent();
            this.setCurrentState(States.UNI);

        } else if (input == '%' && !this.getCurrentState().equals(States.RHS)) {
            this.setCurrentState(States.CLEAR);
            this.clear();
        } else if (input == '√') {
            if (this.getCurrentState().equals(States.LHS) || this.getCurrentState().equals(States.RESULT)) {
                this.clearAndSetLeftHand(sqrt(this.getLeftHand()));
            } else if (this.getCurrentState().equals(States.RHS)) {
                this.clearAndSetRightHand(sqrt(this.getRightHand()));
            }
        } else if (input == '±') {
            if (this.getCurrentState().equals(States.LHS) || this.getCurrentState().equals(States.RESULT)) {
                this.clearAndSetLeftHand(sign(this.getLeftHand()));
                setCurrentState(States.LHS);
            } else if (this.getCurrentState().equals(States.RHS) ) {
                this.clearAndSetRightHand(sign(this.getRightHand()));
                setCurrentState(States.RHS);
            }
        } else if (input == 'C') {
            this.clear();
            this.setCurrentState(States.CLEAR);
        } else if (input == '=') {

            boolean doubleLeft = (this.getCurrentState().equals(States.OP_SCHEDULED) || this.getCurrentState().equals(States.RESULT));
            if(doubleLeft && this.getLeftHand().isEmpty()){
                updateDisplay("0");
            }
            else if(this.getCurrentState().equals(States.RHS) || this.getCurrentState().equals(States.UNI)){
                equals(new BigDecimal(this.getRightHand()));

            }
            /*
            else if (doubleLeft && this.getOriginalNumber().isEmpty()) {
                this.setOriginalNumber(this.getLeftHand());
            }

             */

            else if (doubleLeft) {
                equals(new BigDecimal(this.getOriginalNumber()));
            }



        } else {
            //when inputing new operator, set LHS to Original.


            if(this.getCurrentState().equals(States.RHS) && this.CheckIfOperator(input)){
                equals(new BigDecimal(this.getRightHand()));
                this.setOperator(input);
                this.setCurrentState(States.OP_SCHEDULED);
                setOriginalNumber(this.getLeftHand());
                this.clearAndSetRightHand();

            }
            else if(this.getCurrentState().equals(States.LHS) && this.CheckIfOperator(input)){
                this.setOperator(input);
                this.updateDisplay(String.valueOf(input));
                this.setCurrentState(States.OP_SCHEDULED);
                setOriginalNumber(this.getLeftHand());
            }
            else if (this.CheckIfOperator(input) && (this.getCurrentState().equals(States.RESULT) || this.getCurrentState().equals(States.LHS))){
                this.setOperator(input);
                this.updateDisplay(String.valueOf(input));
                this.setCurrentState(States.OP_SCHEDULED);
                setOriginalNumber(this.getLeftHand());
            }
            else if(this.CheckIfOperator(input) && this.getCurrentState().equals(States.OP_SCHEDULED)){
                this.setOperator(input);
                this.setCurrentState(States.OP_SCHEDULED);
                this.updateDisplay(String.valueOf(input));
            }

        }



        Log.i("Operator", String.valueOf(this.getOperator()));
        Log.i("MyState", String.valueOf(this.getCurrentState().ordinal()));

    }
}
