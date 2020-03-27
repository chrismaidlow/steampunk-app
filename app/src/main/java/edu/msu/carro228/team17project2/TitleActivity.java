package edu.msu.carro228.team17project2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class TitleActivity extends AppCompatActivity {

    public static final String GAME_AREA_SIZE = "edu.msu.cse.cse476.Proj1.GAME_AREA_SIZE";
    public static final String PLAYER_NAME = "edu.msu.cse.cse476.Proj1.PLAYER_NAME";
    public static final String PLAYER_NAME2 = "edu.msu.cse.cse476.Proj1.PLAYER_NAME2";

    /**
     * The hatter view object
     */
    private TitleView getTitleView() {
        return (TitleView) findViewById(R.id.titleView);
    }

    /*
     * The ID values for each of the board sizes. The values must
     * match the index into the array size_spinner in arrays.xml.
     */
    public static final int SIZE_5 = 0;
    public static final int SIZE_10 = 1;
    public static final int SIZE_20 = 2;

    //Default boardSizeInCells 5
    public int size = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.size_spinner, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        getSpinner().setAdapter(adapter);

        getSpinner().setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int pos, long id) {

                //getTitleView().setSize(pos);
                switch(pos) {
                    case SIZE_5:
                        size = 5;
                        break;

                    case SIZE_10:
                        size = 10;
                        break;

                    case SIZE_20:
                        size = 20;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                size = 5;
            }

        });
    }

    /**
     * The hat choice spinner
     */
    private Spinner getSpinner() {
        return (Spinner) findViewById(R.id.spinnerSize);
    }

    public void onStart(View view){

        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra(GAME_AREA_SIZE, size);
        //intent.putExtra(PLAYER_NAME, player_name);
        //intent.putExtra(PLAYER_NAME2, player_name2);
        startActivity(intent);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return false;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }
}
