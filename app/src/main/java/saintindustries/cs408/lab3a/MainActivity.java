package saintindustries.cs408.lab3a;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import saintindustries.cs408.lab3a.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    

    Model model = new Model();
    TextView display;
    class CalculatorClickHandler implements View.OnClickListener {



        States currentState = model.getCurrentState();

        private boolean CheckIfOperand(char input){
            if((Character.isDigit(input) || input == '.' )  ){
                return true;
            }
            else {
                return false;
            }
        }
        private boolean ChecktoAppendLH(){
            if(model.getRightHand().isEmpty() && (currentState.equals(States.CLEAR) || currentState.equals(States.LHS) || currentState.equals(States.RESULT))){
                return true;
            }
            else{
                return false;
            }

        }
        private boolean ChecktoAppendRH(){
            if(!model.getLeftHand().isEmpty() && (currentState.equals(States.OP_SCHEDULED) || currentState.equals(States.RHS))){
                return true;
            }
            else{
                return false;
            }

        }
        public Boolean equals(){
            String result = "";

            boolean doubleLeft = (model.getCurrentState().equals(States.OP_SCHEDULED));
            if(doubleLeft && model.getOriginalNumber().isEmpty()){
                model.setOriginalNumber(model.getLeftHand());
            }
            if(model.getCurrentState().equals(States.CLEAR)){
                result = "0";
                return true;
            }


            try{
                switch(model.getOperator()){
                    case '+':
                        if(doubleLeft)
                            result = String.valueOf(Integer.parseInt(model.getLeftHand()) + Integer.parseInt(model.getOriginalNumber()));
                        else
                            result = String.valueOf(Integer.parseInt(model.getLeftHand()) + Integer.parseInt(model.getRightHand()));
                        break;
                    case '-':
                        if(doubleLeft)
                            result = String.valueOf(Integer.parseInt(model.getLeftHand()) - Integer.parseInt(model.getOriginalNumber()));
                        else
                            result = String.valueOf(Integer.parseInt(model.getLeftHand()) - Integer.parseInt(model.getRightHand()));
                        break;
                    case 'X':
                        if(doubleLeft)
                            result = String.valueOf(Integer.parseInt(model.getLeftHand()) * Integer.parseInt(model.getOriginalNumber()));
                        else
                            result = String.valueOf(Integer.parseInt(model.getLeftHand()) * Integer.parseInt(model.getRightHand()));
                        break;
                    case '÷':
                        if(doubleLeft)
                            result = String.valueOf(Integer.parseInt(model.getLeftHand()) / Integer.parseInt(model.getOriginalNumber()));
                        else
                            result = String.valueOf(Integer.parseInt(model.getLeftHand()) / Integer.parseInt(model.getRightHand()));
                        break;

                }

            }catch(Exception e){
                model.setCurrentState(States.ERROR);
                Toast toast = Toast.makeText(binding.getRoot().getContext(), "I'm Mortally WOUNDED!", Toast.LENGTH_SHORT);

                return false;
            }

            model.clearAndSetLeftHand(result);
            model.setCurrentState(States.RESULT);
            return true;

        }


        public void onClick(View view) {
            currentState = model.getCurrentState();

            String tag = view.getTag().toString();
            char input = ' ';
            input = tag.charAt(tag.length()-1);
            Log.i("Btn",String.valueOf(input));

            Toast toast = Toast.makeText(binding.getRoot().getContext(), tag, Toast.LENGTH_SHORT);
            toast.show();
            // INSERT EVENT HANDLING CODE HERE

            //check if operator, and check which state
            if((CheckIfOperand(input) && ChecktoAppendLH())){
                model.addToLeftHand(input);
                model.setCurrentState(States.LHS);
            }
            else if(CheckIfOperand(input) && ChecktoAppendRH()){
                model.addToRightHand(input);
                model.setCurrentState(States.RHS);
            }
            else if(input == 'C'){
                model.clear();
                model.setCurrentState(States.CLEAR);
            }
            else if(input == '='){
                equals();
            }
            else
            {
                model.setOperator(input);
                model.setCurrentState(States.OP_SCHEDULED);
            }
            if(model.getCurrentState().equals(States.RESULT))
                display.setText(model.getLeftHand());
            else
                display.setText(String.valueOf(input));
            Log.i("State", String.valueOf(model.getCurrentState().ordinal()));




        }
    }
    private ActivityMainBinding binding;
    private int CHAIN_LENGTH_ROW = 4;
    private int  CHAIN_LENGTH_COL = 5;

    CalculatorClickHandler click = new CalculatorClickHandler();

    private void initLayout(){

        ConstraintSet set = new ConstraintSet();
        ConstraintLayout layout = binding.main;
        int[][] btnIdsHort = new int[CHAIN_LENGTH_ROW][CHAIN_LENGTH_COL];
        int[][] btnIdVert = new int[CHAIN_LENGTH_COL][CHAIN_LENGTH_ROW];

        //save ID for later use
        int displayViewID = View.generateViewId();

        display = new TextView(this);
        display.setId(displayViewID);
        display.setText("0");
        display.setTextSize(48);
        display.setTag("display");

        layout.addView(display);



        set.clone(layout);
        set.connect(display.getId(), ConstraintSet.TOP, binding.northGuide.getId(), ConstraintSet.BOTTOM, 0);
        set.connect(display.getId(), ConstraintSet.LEFT, binding.westGuide.getId(), ConstraintSet.RIGHT, 0);
        set.connect(display.getId(), ConstraintSet.RIGHT, binding.eastGuide.getId(), ConstraintSet.LEFT, 0);
        set.connect(display.getId(), ConstraintSet.BOTTOM, binding.extraGuide.getId(), ConstraintSet.TOP, 0);

        set.applyTo(layout);

        display.setGravity(Gravity.CENTER_VERTICAL);
        display.setGravity(Gravity.END);

        LayoutParams params = display.getLayoutParams();
        params.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;

        display.setLayoutParams(params);

        String[] btnGrid = getResources().getStringArray(R.array.buttonGrid);
        for(int row = 0; row < CHAIN_LENGTH_ROW; row++) {

            for (int col = 0; col < CHAIN_LENGTH_COL; col++) {


                set.clone(layout);
                int id = View.generateViewId(); // generate new ID
                Button btn = new Button(this); // create new TextView
                btn.setId(id); // assign ID
                btn.setTag("btn" + btnGrid[row].charAt(col)); // assign tag (for acquiring references later)
                btn.setText(""+btnGrid[row].charAt(col)); // set text (using a string resource)
                btn.setTextSize(24); // set size
                btnIdsHort[row][col] = id; // store ID to collection
                btnIdVert[col][row] = id;
                btn.setOnClickListener(click);

                layout.addView(btn); // add to layout

                LayoutParams btnParams = btn.getLayoutParams();
                btnParams.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                btnParams.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                btn.setLayoutParams(btnParams);


                set.setMargin(id, ConstraintSet.TOP, 8);
                set.setMargin(id, ConstraintSet.LEFT, 8);
                set.setMargin(id, ConstraintSet.RIGHT, 8);
                set.setMargin(id, ConstraintSet.BOTTOM, 8);
                set.applyTo(layout);




            }
        }

        set.clone(layout);
        //set chains
        for( int row[] : btnIdsHort){
            set.createHorizontalChain(binding.westGuide.getId(), ConstraintSet.LEFT, binding.eastGuide.getId(), ConstraintSet.RIGHT, row, null, ConstraintSet.CHAIN_SPREAD);
        }
        for(int col[]: btnIdVert){
            set.createVerticalChain(binding.extraGuide.getId(), ConstraintSet.BOTTOM, binding.southGuide.getId(), ConstraintSet.TOP, col, null, ConstraintSet.CHAIN_SPREAD);
        }

        set.applyTo(layout);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        CalculatorClickHandler click = new CalculatorClickHandler();


        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initLayout();
    }

}