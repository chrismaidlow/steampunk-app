package edu.msu.carro228.team17project2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Activity for end screen
 * Handles end game status message and navigation back to title
 */
public class EndActivity extends AppCompatActivity {

    /**
     * Builds end activity and sets appropriate messages
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int status = getIntent().getIntExtra(GameActivity.GAME_END_STATUS, 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        String player;
        String formatted;

        // Display text appropriate to end status
        switch (status){
            case GameActivity.QUIT:
                ((TextView)findViewById(R.id.win_text)).setText(R.string.winless);
                ((TextView)findViewById(R.id.congrats)).setText(R.string.notext);
                break;
            case GameActivity.WON:
                player = getIntent().getStringExtra("NAME");
                //formatted = getString(R.string.congrats_string, player);
                ((TextView)findViewById(R.id.win_text)).setText(R.string.p1_win);
                //((TextView)findViewById(R.id.congrats)).setText(formatted);
                break;
                /*
            case GameActivity.WON:
                player = getIntent().getStringExtra(GameActivity.PLAYER_NAME2);
                formatted = getString(R.string.congrats_string, player);
                ((TextView)findViewById(R.id.win_text)).setText(R.string.p2_win);
                ((TextView)findViewById(R.id.congrats)).setText(formatted);
                break;
                 */
            case GameActivity.LOST:
                ((TextView)findViewById(R.id.win_text)).setText(R.string.lose);
                ((TextView)findViewById(R.id.congrats)).setText(R.string.notext);
        }
    }

    /**
     * Prevent back button from being used
     * @param keyCode code of key press
     * @param event key press event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return false;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * Button handler for the return to title button on the end screen
     * @param view
     */
    public void onReturnToTitle(View view){
        Intent intent = new Intent(this, OpeningActivity.class);
        startActivity(intent);
    }
}
