package saintindustries.cs408.lab3a;

import android.widget.Toast;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Model {


    private StringBuilder leftHand = new StringBuilder().append("0");
    private StringBuilder rightHand = new StringBuilder();
    private String originalNumber = "";
    private String output = "0";
    private char operator = ' ';
    private States currentState = States.CLEAR;

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
        firePropertyChange(Controller.DISPLAYTAG, output, input);
        this.output = input;
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
        this.updateDisplay(String.valueOf(leftHand));
    }

    public void addToRightHand(char rightHand) {
        this.rightHand.append(rightHand);
        this.updateDisplay(String.valueOf(rightHand));
    }

    public void setOperator(char operator) {
        this.operator = operator;
        this.updateDisplay(String.valueOf(operator));
    }

    public void setCurrentState(States currentState) {
        this.currentState = currentState;
    }

    public void clear() {
        this.leftHand = new StringBuilder().append("0");
        this.rightHand = new StringBuilder();
        this.operator = ' ';
        this.currentState = States.CLEAR;
        this.originalNumber = "";
        this.updateDisplay("0");

    }

    public void clearAndSetLeftHand(String input) {
        this.leftHand = new StringBuilder().append(input);
        this.updateDisplay(input);

    }

    public void clearAndSetRightHand(String input) {
        this.rightHand = new StringBuilder().append(input);
        this.updateDisplay(input);

    }

    public String getOriginalNumber() {
        return this.originalNumber;
    }

    public void setOriginalNumber(String originalNumber) {
        if (originalNumber == null) {
            this.originalNumber = this.getLeftHand();
        } else
            this.originalNumber = originalNumber;

    }


    public String sqrt(String input) {
        try {
            MathContext mc = new MathContext(9, RoundingMode.HALF_UP);
            BigDecimal num = BigDecimal.valueOf(Double.parseDouble(input));
            num = num.pow(new BigDecimal(0.5).intValue());
            return num.toString();
        } catch (Exception e) {
            this.setCurrentState(States.ERROR);
            return "Error hit C to clear";
        }
    }

    public String percent() {
        try {
            MathContext mc = new MathContext(9, RoundingMode.HALF_UP);
            BigDecimal LHS = new BigDecimal(this.getLeftHand());
            BigDecimal RHS = new BigDecimal(this.getRightHand());
            LHS = LHS.multiply(RHS);
            LHS = LHS.divide(new BigDecimal("100"), mc);
            return LHS.toString();

        } catch (Exception e) {
            this.setCurrentState(States.ERROR);
            return "Error hit c to clear";
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
            return false;
        }

        result = resultBig.toString();
        this.clearAndSetLeftHand(result);
        this.setCurrentState(States.RESULT);
        this.updateDisplay(result);

        return true;

    }

    public void parseInput(Character input) {

        if ((CheckIfOperand(input) && ChecktoAppendLH())) {
            if (this.getCurrentState().equals(States.CLEAR) || this.getCurrentState().equals(States.ERROR))
                this.clearAndSetLeftHand(String.valueOf(input));
            else
                this.addToLeftHand(input);
            this.setCurrentState(States.LHS);
        } else if (CheckIfOperand(input) && ChecktoAppendRH()) {
            this.addToRightHand(input);
            this.setCurrentState(States.RHS);
        } else if (input == '%' && this.getCurrentState().equals(States.RHS)) {
            this.clearAndSetLeftHand(percent());

        } else if (input == '%' && !this.getCurrentState().equals(States.RHS)) {
            this.setCurrentState(States.CLEAR);
            this.clear();
        } else if (input == '√') {
            if (this.getCurrentState().equals(States.LHS)) {
                this.clearAndSetLeftHand(sqrt(this.getLeftHand()));
            } else if (this.getCurrentState().equals(States.RHS)) {
                this.clearAndSetRightHand(sqrt(this.getRightHand()));
            }
        } else if (input == '±') {
            if (this.getCurrentState().equals(States.LHS)) {
                this.clearAndSetLeftHand(sign(this.getLeftHand()));
            } else if (this.getCurrentState().equals(States.RHS)) {
                this.clearAndSetRightHand(sign(this.getRightHand()));
            }
        } else if (input == 'C') {
            this.clear();
            this.setCurrentState(States.CLEAR);
        } else if (input == '=') {
            boolean doubleLeft = (this.getCurrentState().equals(States.OP_SCHEDULED) || this.getCurrentState().equals(States.RESULT));
            if (doubleLeft && this.getOriginalNumber().isEmpty()) {
                this.setOriginalNumber(null);
            }
            if (this.getCurrentState().equals(States.LHS)) {
                equals(new BigDecimal(this.getOriginalNumber()));
            } else
                equals(new BigDecimal(this.getRightHand()));

        } else {
            //when inputing new operator, set LHS to Original.
            this.setOperator(input);
            this.setCurrentState(States.OP_SCHEDULED);
            setOriginalNumber(null);
        }


    }
}
