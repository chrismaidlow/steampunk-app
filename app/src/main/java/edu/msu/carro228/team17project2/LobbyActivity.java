package edu.msu.carro228.team17project2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import edu.msu.carro228.team17project2.Cloud.Cloud;

public class LobbyActivity extends AppCompatActivity {

    /**
     * Flag to track if we are still waiting for the server to update
     */
    private boolean waiting = true;

    /**
     * For cloud communication
     */
    private Cloud cloud = new Cloud();

    /**
     * Setup views
     * @param savedInstanceState bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);


        //LOOPS UNTIL SECOND PLAYER LOGS IN
        //THEN CREATES GAME AND ENTERS GAME
        final Handler h = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(waiting) {
                    waiting = cloud.getCount( getIntent().getIntExtra(Utility.Intent.ID, -1));
                    try {
                        if (waiting) {
                            Thread.sleep(Utility.Timeout.CYCLE_SLEEP_TIME);
                        }else {
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
                                    intent.putExtra(Utility.Intent.PRIORITY, true);
                                    intent.putExtra(Utility.Intent.ID,  getIntent().getIntExtra(Utility.Intent.ID, -1));
                                    intent.putExtra(Utility.Intent.SIZE, getIntent().getIntExtra(Utility.Intent.SIZE, 5));
                                    startActivity(intent);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        // Do Nothing
                    }
                }
            }
        }).start();

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
