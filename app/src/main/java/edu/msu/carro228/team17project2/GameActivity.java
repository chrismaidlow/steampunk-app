package edu.msu.carro228.team17project2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import edu.msu.carro228.team17project2.Cloud.Cloud;
import edu.msu.carro228.team17project2.Data.CloudData;

/**
 * activity class for game screen and responsible for game control
 */
public class GameActivity extends AppCompatActivity {

    /**
     * Constants for communication to other activities
     */
    public static final String GAME_END_STATUS = "edu.msu.cse.cse476.Proj2.GAME_END_STATUS";

    public static final int LOST = -1;
    public static final int QUIT = 0;
    public static final int WON = 1;

    private Cloud cloud = new Cloud();
    private int id;
    public boolean ended = false;


    private String data;
    private CloudData obj = new CloudData();
    private Handler postLoad = new Handler();
    private GameActivity self = this;

    /**
     * The result of the game
     */
    private boolean result;

    /**
     * TextView that displays the current player's name
     */
    private  TextView currentPlayer;

    /**
     * Hashmap to associate the turn with the player name
     */
    private HashMap<PlayingArea.Turn, String> names = new HashMap<>();

    /**
     * Save Instance State
     * @param outState bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getGameView().putToBundle(outState);
        getTimingView().putToBundle(outState);
    }

    /**
     * What to do on resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        getTimingView().rotate_line();
    }

    /**
     * Setup views
     * @param savedInstanceState bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        id = getIntent().getIntExtra(Utility.Intent.ID, -1);

        int action = getIntent().getIntExtra(Utility.Intent.ACTION, 2);

        if (action == 1){
            obj = CloudData.Deserialize(getIntent().getStringExtra(Utility.Intent.DATA));
            if (obj.turn % 2 != 0){
                getGameView().endTurn();
            }
            ConsumeUpdate();
        }else{
            if (getIntent().getBooleanExtra(Utility.Intent.PRIORITY, false)){
                //TrySave();
            }else{
                getGameView().endTurn();
                obj = CloudData.Deserialize(data);
                if (obj.turn == 0){
                    Intent intent = new Intent(self, WaitingActivity.class);
                    intent.putExtra(Utility.Intent.TURN, 0);
                    intent.putExtra(Utility.Intent.ID, getIntent().getIntExtra(Utility.Intent.ID, id));
                    startActivity(intent);
                }

            }
        }

        currentPlayer = findViewById(R.id.textCurrentPlayer);

        /*
         * Restore Timing view state
         */
        if(savedInstanceState != null && savedInstanceState.getInt(Utility.Intent.ACTION, 2) != 1) {
            getTimingView().getFromBundle(savedInstanceState);
            getGameView().getFromBundle(savedInstanceState);
        }
        else {
            getTimingView().rotate_line();
        }

        currentPlayer.setText(getString(R.string.currentPlayer_Msg) + " " + getIntent().getStringExtra(Utility.Intent.NAME));
    }

    public void incrementMisses(){
        int misses = getIntent().getIntExtra(Utility.Intent.MISSES, 0) + 1;
        Log.e("Misses", "" + misses);
        getIntent().putExtra(Utility.Intent.MISSES, misses);
        if (misses == 2){
            Toast.makeText(this, R.string.warning, Toast.LENGTH_LONG);
        }else if (misses == 3){
            ended = true;
            lose();
        }


    }

    /**
     * Attempt to save to the server
     */
    private void TrySave(final int state){
        new Thread(new Runnable() {
            @Override
            public void run(){
                    obj = getGameView().toData(obj.turn);
                    obj.state = state;
                    boolean saved = false;
                    int tryCount = 0;
                    while(!saved && tryCount < Utility.Timeout.SERVER_COMMUNICATION_ATTEMPT_THRESHOLD){
                        saved = cloud.save(CloudData.Serialize(obj), id);
                        tryCount += 1;
                    }
                    if (tryCount >= Utility.Timeout.SERVER_COMMUNICATION_ATTEMPT_THRESHOLD){
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(findViewById(R.id.waitingLayout).getContext());
                        builder.setTitle(R.string.timeout_detected);
                        builder.setMessage(R.string.timeout_Msg);
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(self, OpeningActivity.class);
                                intent.putExtra(Utility.Intent.MISSES, getIntent().getIntExtra(Utility.Intent.MISSES, 0));
                                self.startActivity(intent);
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
            }
        }).start();
    }

    /**
     * Do something with update data
     */
    private void ConsumeUpdate(){
        int gameState = obj.state;
        switch (gameState){
            case CloudData.LOST:
                EndGame(LOST);
                break;
            case CloudData.PLAYING:
                getGameView().FromData(obj);
                break;
            case CloudData.WON:
                EndGame(WON);
                break;
        }
    }

    private void EndGame(int endCode){
        switch (endCode){
            case LOST:
                lose();
                break;
            case QUIT:
                lose();
                break;
            case WON:
                win();
                break;
        }
    }


    /**
     * Button Handler for return to title button
     * @param view
     */
    public void onReturnToTitle(View view){
        Intent intent = new Intent(this, TitleActivity.class);
        startActivity(intent);
    }

    /**
     * Button handler for quit button
     * @param view
     */
    public void onQuitGame(View view){
        TrySave(CloudData.WON);
        lose();
    }

    /**
     * Button handler for surrender button
     * @param view
     */
    public void onSurrender(View view){
        TrySave(CloudData.WON);
        lose();
    }

    /**
     * Go to end screen and show winner
     */
    private void win(){
        Intent intent = new Intent (this, EndActivity.class);
        intent.putExtra(GAME_END_STATUS, WON);
        intent.putExtra(Utility.Intent.NAME, getIntent().getStringExtra(Utility.Intent.NAME));
        startActivity(intent);
        finish();
    }

    /**
     * Go to end screen and show loss
     */
    private void lose(){
        Intent intent = new Intent (this, EndActivity.class);
        intent.putExtra(GAME_END_STATUS, LOST);
        intent.putExtra(Utility.Intent.NAME, getIntent().getStringExtra(Utility.Intent.NAME));
        startActivity(intent);
        finish();
    }

    /**
     * Open a players valve and determine if they win or lose
     * @param view
     */
    public void onOpenValve(View view) {
        GameView gv = getGameView();

        //Intent intent = new Intent (this, EndActivity.class);
        PlayingArea.Turn current_turn = getGameView().getTurn();

        //Log.e("Valve Opened", current_turn.name() + " opened their valve!");

        //GET RESULT FOR CORRECT START PIPE
        result = gv.checkWin(current_turn);
        gv.invalidate();

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        //CURRENT PLAYER WINS
        if (result) {
            TrySave(CloudData.LOST);
            builder.setTitle(R.string.success_alert);
            builder.setMessage(R.string.success_Msg);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    win();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
        //CURRENT PLAYER LOSES
        else{
            TrySave(CloudData.WON);
            builder.setTitle(R.string.fail_alert);
            builder.setMessage(R.string.fail_Msg);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    lose();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();



        }

    }

    /**
     * Rotate the held pipe clockwise
     * @param view
     */
    public void onRotateClockwise(View view){
        getGameView().rotateSelectedPipe(90);
    }

    /**
     * Rotate the held pipe counter clockwise
     * @param view
     */
    public void onRotateCounterClockwise(View view){
        getGameView().rotateSelectedPipe(-90);
    }

    /**
     * Changes player turn and then starts the animation/timing of next players turn.
     **/
    public void activityEndTurn() {
        if (ended == false){
            ended = true;
            getTimingView().reset_timer();
            TrySave(CloudData.PLAYING);
            Intent intent = new Intent(self, WaitingActivity.class);
            intent.putExtra(Utility.Intent.TURN, obj.turn + 1);
            intent.putExtra(Utility.Intent.ID, getIntent().getIntExtra(Utility.Intent.ID, id));
            intent.putExtra(Utility.Intent.MISSES, getIntent().getIntExtra(Utility.Intent.MISSES, 0));
            startActivity(intent);
            getTimingView().reset_timer();
            finish();
        }

    }


    public GameView getGameView(){
        return (GameView) findViewById(R.id.gameView);
    }

    public TimingView getTimingView(){
        return (TimingView) findViewById(R.id.timingView);
    }


    /**
     * Discard the held pipe
     * @param view
     */
    public void onDiscard(View view){
        getIntent().putExtra(Utility.Intent.MISSES, 0);
        GameView gv = getGameView();
        Pipe dragging = gv.getDragging();
        if(dragging != null){
            gv.setDragging();
            activityEndTurn();
        }

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

    /**
     * Place a pipe in board or toast player on fail
     * @param view
     */
    public void onInsert(View view){
        getIntent().putExtra(Utility.Intent.MISSES, 0);
        GameView gv = getGameView();
        Pipe dragging = gv.getDragging();
        if(dragging != null){
            if (gv.Snap()) {
                gv.setDragging();
                activityEndTurn();
            }else{
                Toast.makeText(view.getContext(), R.string.InvalidInsert_Msg, Toast.LENGTH_SHORT).show();
            }
        }

    }

}