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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import saintindustries.cs408.lab3a.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    

    Model model = new Model();
    TextView display;
    private Controller controller;
    class CalculatorClickHandler implements View.OnClickListener {



        public void onClick(View view) {

            String tag = view.getTag().toString();
            char input = ' ';
            input = tag.charAt(tag.length()-1);
            Log.i("Btn",String.valueOf(input));

            Toast toast = Toast.makeText(binding.getRoot().getContext(), tag, Toast.LENGTH_SHORT);
            toast.show();
            // INSERT EVENT HANDLING CODE HERE

            //parsesInput
            Controller.parseInput(input);


            Log.i("State", String.valueOf(model.getCurrentState().ordinal()));
            Log.i("Operator", String.valueOf(model.getOperator()));
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