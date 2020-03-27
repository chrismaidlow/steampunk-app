package edu.msu.carro228.team17project2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import androidx.appcompat.app.AlertDialog;

import java.util.Calendar;

import edu.msu.carro228.team17project2.Cloud.Cloud;
import edu.msu.carro228.team17project2.Data.CloudData;

/**
 * Activity for waiting for next turn
 */
public class WaitingActivity extends Activity {

    /**
     * Time, in milliseconds, to wait before timeout
     */
    private int TRY_UPDATE_THRESHOLD = 75 * 1000;

    /**
     * The current turn
     */
    private int turn;

    /**
     * The game id
     */
    private int id;

    /**
     * Data from cloud
     */
    private CloudData obj = new CloudData();

    /**
     * For cloud communication
     */
    private Cloud cloud = new Cloud();

    /**
     * Handles post load behavior
     */
    private Handler postLoad = new Handler();

    /**
     * Self ref
     */
    private WaitingActivity self = this;

    /**
     * Data from server in string form
     */
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        // Get data from intent
        id = getIntent().getIntExtra(Utility.Intent.ID, -1);
        turn = getIntent().getIntExtra(Utility.Intent.TURN, 0);

        // Try to get next update from server
        TryUpdate();
    }

    /**
     * Try to update from server
     */
    private void TryUpdate(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Loop until turn updates or timeout
                long time = Calendar.getInstance().getTimeInMillis();
                while (turn >= obj.turn || obj.turn == 0) {
                    if (Calendar.getInstance().getTimeInMillis() - time < TRY_UPDATE_THRESHOLD){

                        // Load data
                        data = cloud.load(id);
                        obj = CloudData.Deserialize(data);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            // Do Nothing
                        }
                    }else{

                        // Deal with timeout
                        postLoad.post(new Runnable() {
                            @Override
                            public void run() {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            cloud.delete(id);
                                        }catch (Exception e){
                                            // Do Nothing
                                        }
                                    }
                                });
                                AlertDialog.Builder builder = new AlertDialog.Builder(self);
                                builder.setTitle(R.string.timeout_detected);
                                builder.setMessage(R.string.timeout_Msg);
                                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(self, OpeningActivity.class);
                                        self.startActivity(intent);
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                        return;
                    }
                }

                // Success, go to game activity
                postLoad.post(new Runnable() {
                    @Override
                    public void run() {
                        if (turn > -2) {
                            Intent intent = new Intent(self, GameActivity.class);
                            intent.putExtra(Utility.Intent.ACTION, 1);
                            intent.putExtra(Utility.Intent.DATA, data);
                            intent.putExtra(Utility.Intent.ID, id);
                            intent.putExtra(Utility.Intent.SIZE, obj.board.length);
                            intent.putExtra(Utility.Intent.MISSES, getIntent().getIntExtra(Utility.Intent.MISSES, 0));
                            startActivity(intent);
                        }
                    }
                });

            }
        }).start();
    }

    /**
     * Prevent use of back button in nav bar
     * @param keyCode key pressed
     * @param event key event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
