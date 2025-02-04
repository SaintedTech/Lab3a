package saintindustries.cs408.lab3a;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import saintindustries.cs408.lab3a.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int CHAIN_LENGTH_ROW = 4;
    private int  CHAIN_LENGTH_COL = 5;
    private void initLayout(){
        ConstraintLayout layout = binding.main;
        int[][] btnIdsHort = new int[CHAIN_LENGTH_ROW][CHAIN_LENGTH_COL]; //
        int[][] btnIdVert = new int[CHAIN_LENGTH_ROW][CHAIN_LENGTH_COL];

        String[][] names = new String[][]
        {{"7", "8", "9", "√", "C"},
         {"4", "5", "6", "÷", "%"},
         {"1", "2", "3", "x", "-"},
         {"±", "0", ".", "+", "="}};

        for(int row = 0; row < CHAIN_LENGTH_ROW; ++row) {
            for (int col = 0; col < CHAIN_LENGTH_COL; ++col) {
                int id = View.generateViewId(); // generate new ID
                Button btn = new Button(this); // create new TextView
                btn.setId(id); // assign ID
                btn.setTag("btn" + names[row][col]); // assign tag (for acquiring references later)
                btn.setText(names[row][col]); // set text (using a string resource)
                btn.setTextSize(24); // set size
                btnIdsHort[row][col] = id; // store ID to collection
                btnIdVert[col][row] = id;
                layout.addView(btn); // add to layout
                //set chains


            }
        }

        //set chains

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initLayout();
    }

}