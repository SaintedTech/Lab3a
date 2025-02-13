package saintindustries.cs408.lab3a;

import android.widget.Toast;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Contoller {

    private Model model = new Model();
    private String display = "display";

    public String sqrt(String input){
        try {
            MathContext mc = new MathContext(9, RoundingMode.HALF_UP);
            BigDecimal num = BigDecimal.valueOf(Double.parseDouble(input));
            num = num.pow(new BigDecimal(0.5).intValue());
            return num.toString();
        }catch(Exception e){
            model.setCurrentState(States.ERROR);
            return "Error hit C to clear";
        }
    }
    public String percent(){
        try{
            MathContext mc = new MathContext(9, RoundingMode.HALF_UP);
            BigDecimal LHS = new BigDecimal(model.getLeftHand());
            BigDecimal RHS = new BigDecimal(model.getRightHand());
            LHS = LHS.multiply(RHS);
            LHS = LHS.divide(new BigDecimal("100"), mc);
            return LHS.toString();

        } catch (Exception e) {
            model.setCurrentState(States.ERROR);
            return "Error hit c to clear";
        }

    }
    public String sign(String input){
        try {
            BigDecimal num = new BigDecimal(input);
            num = num.multiply(new BigDecimal("-1"));
            return num.toString();
        }
        catch(Exception e){
            model.setCurrentState(States.ERROR);
            return "Error hit C to clear";
        }

    }

    public boolean CheckIfOperand(char input){
        if((Character.isDigit(input) || input == '.' )  ){
            return true;
        }
        else {
            return false;
        }
    }
    public boolean ChecktoAppendLH(){
        if(model.getRightHand().isEmpty() && (model.getCurrentState().equals(States.CLEAR) || model.getCurrentState().equals(States.LHS) || model.getCurrentState().equals(States.RESULT))){
            return true;
        }
        else{
            return false;
        }

    }
    public boolean ChecktoAppendRH(){
        if(!model.getLeftHand().isEmpty() && (model.getCurrentState().equals(States.OP_SCHEDULED) || model.getCurrentState().equals(States.RHS))){
            return true;
        }
        else{
            return false;
        }

    }
    public Boolean equals(BigDecimal RHS){
        String result = "";
        BigDecimal LHS = new BigDecimal(model.getLeftHand());

        BigDecimal resultBig = new BigDecimal(0);
        MathContext mc = new MathContext(9, RoundingMode.HALF_UP);
        if(model.getCurrentState().equals(States.CLEAR)){
            result = "0";
            return true;
        }


        try{
            switch(model.getOperator()){
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

        }catch(Exception e){
            model.setCurrentState(States.ERROR);
            return false;
        }
        result = resultBig.toString();
        model.clearAndSetLeftHand(result);
        model.setCurrentState(States.RESULT);
        return true;

    }
    private void parseInput(Character input){

        if((CheckIfOperand(input) && ChecktoAppendLH())){
            if(model.getCurrentState().equals(States.CLEAR) || model.getCurrentState().equals(States.ERROR))
                model.clearAndSetLeftHand(String.valueOf(input));
            else
                model.addToLeftHand(input);
            model.setCurrentState(States.LHS);
        }
        else if(CheckIfOperand(input) && ChecktoAppendRH()){
            model.addToRightHand(input);
            model.setCurrentState(States.RHS);
        }
        else if(input == '%' && model.getCurrentState().equals(States.RHS)){
            model.clearAndSetLeftHand(percent());

        }
        else if(input == '%' && !model.getCurrentState().equals(States.RHS)){
            model.setCurrentState(States.CLEAR);
            model.clear();
        }
        else if(input == '√'){
            if(model.getCurrentState().equals(States.LHS)){
                model.clearAndSetLeftHand(sqrt(model.getLeftHand()));
            }
            else if(model.getCurrentState().equals(States.RHS)){
                model.clearAndSetRightHand(sqrt(model.getRightHand()));
            }
        }
        else if(input == '±') {
            if (model.getCurrentState().equals(States.LHS)) {
                model.clearAndSetLeftHand(sign(model.getLeftHand()));
            } else if (model.getCurrentState().equals(States.RHS)) {

                model.clearAndSetRightHand(sign(model.getRightHand()));
            }
        }
        else if(input == 'C'){
            model.clear();
            model.setCurrentState(States.CLEAR);
        }
        else if(input == '='){
            boolean doubleLeft = (model.getCurrentState().equals(States.OP_SCHEDULED) || model.getCurrentState().equals(States.RESULT) );
            if(doubleLeft && model.getOriginalNumber().isEmpty()){
                model.setOriginalNumber(model.getLeftHand());
            }
            if(model.getCurrentState().equals(States.LHS)){
                equals(new BigDecimal(model.getOriginalNumber()));
            }
            else
                equals(new BigDecimal(model.getRightHand()));
        }
        else
        {
            //when inputing new operator, set LHS to Original.
            model.setOperator(input);
            model.setCurrentState(States.OP_SCHEDULED);
            model.setOriginalNumber(model.getLeftHand());
        }

    }

}
