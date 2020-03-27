package edu.msu.carro228.team17project2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.msu.carro228.team17project2.Cloud.Cloud;

public class OpeningActivity extends AppCompatActivity {

    /**
     * User pref keys
     */
    private final String REMEMBER = "Steampunked.remember";
    private final String NAME = "Steampunked.name";
    private final String PASSWORD = "Steampunked.password";

    /*
     * The ID values for each of the board sizes. The values must
     * match the index into the array size_spinner in arrays.xml.
     */
    public static final int SIZE_5 = 0;
    public static final int SIZE_10 = 1;
    public static final int SIZE_20 = 2;

    /**
     * Cloud object for checking if user exists in the database on login
     */
    private Cloud cloud = new Cloud();

    /**
     * User's username
     */
    private String username;

    /**
     * User's password
     */
    private String password;

    /**
     * Status info for joining a game
     */
    private int[] joinStatus = {-1, -1};

    /**
     * Reference to self for use
     */
    private OpeningActivity self = this;

    /**
     * Handler for post login behavior
     */
    private Handler postLogin = new Handler();

    /**
     * Size of the game board
     */
    private int size;


    /**
     * Builds NewUser activity and sets appropriate messages
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        readPrefs();

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.size_spinner, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.spinnerSize);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

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
     * Write user prefs
     */
    public void writePrefs(boolean remember){
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putBoolean(REMEMBER, remember);
        if (remember){
            editor.putString(NAME, username);
            editor.putString(PASSWORD, password);
        }
        editor.commit();
    }

    /**
     * Read from prefs and update accordingly
     */
    public void readPrefs(){
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        boolean remember = preferences.getBoolean(REMEMBER, false);
        if (remember){
            username = preferences.getString(NAME, "");
            password = preferences.getString(PASSWORD, "");
            ((EditText) findViewById(R.id.edit_ID)).setText(username);
            ((EditText) findViewById(R.id.edit_pass)).setText(password);
            ((CheckBox) findViewById(R.id.rememberCheckBox)).setChecked(remember);
        }
    }

    /**
     * Button handler for New User button
     * @param view
     */
    public void onNewUser(View view){
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivity(intent);
    }

    /**
     * Button handler for login button
     * @param view
     */
    public void onLogin(final View view){

        //capture text from EditText views
        username = ((EditText) findViewById(R.id.edit_ID)).getText().toString().trim();
        password = ((EditText) findViewById(R.id.edit_pass)).getText().toString().trim();

        // Store user prefs
        writePrefs(((CheckBox)findViewById(R.id.rememberCheckBox)).isChecked());

        // Attempt to login and join a game
        new Thread(new Runnable() {
            boolean success;
            @Override
            public void run() {
                success = cloud.login(username, password);
                postLogin.post(new Runnable() {
                    @Override
                    public void run() {
                        //if password and username combo in database
                        if(success){
                            TryJoinGame();
                        }
                        //else incorrect password
                        else{
                            Toast.makeText(view.getContext(), R.string.cant_login, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Try to login and then act according to result of login
     */
    private void TryJoinGame(){
        new Thread(new Runnable() {
            View view = findViewById(R.id.openingLayout);
            @Override
            public void run() {
                int tryCount = 0;
                while(joinStatus[0] < 0) {
                    joinStatus = cloud.joinGame(username);
                    switch (joinStatus[0]){

                        // Issue with server / can't connect
                        case -1:
                            try {
                                Thread.sleep(Utility.Timeout.CYCLE_SLEEP_TIME);
                            }catch (InterruptedException e){
                                // Do Nothing
                            }
                            tryCount ++;
                            if (tryCount > Utility.Timeout.SERVER_COMMUNICATION_ATTEMPT_THRESHOLD){
                                view.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(view.getContext(), R.string.server_join_game_fail, Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }

                        // No game available
                        case 0:
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(view.getContext(), R.string.server_lobby_full, Toast.LENGTH_LONG).show();
                                }
                            });
                            return;

                        // This is player one they have to wait in the lobby
                        case 1:
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(self, LobbyActivity.class);
                                    intent.putExtra(Utility.Intent.NAME, username);
                                    intent.putExtra(Utility.Intent.ID, joinStatus[1]);
                                    intent.putExtra(Utility.Intent.SIZE, size);
                                    startActivity(intent);
                                }
                            });
                            return;

                        // This is player two they go to the waiting room to wait for player one's move
                        case 2:
                            Intent intent = new Intent(self, WaitingActivity.class);
                            intent.putExtra(Utility.Intent.TURN, 0);
                            intent.putExtra(Utility.Intent.NAME, username);
                            intent.putExtra(Utility.Intent.ID, getIntent().getIntExtra(Utility.Intent.ID, joinStatus[1]));
                            intent.putExtra(Utility.Intent.FROM_OPENING, true);
                            startActivity(intent);
                    }
                }
            }
        }).start();
    }

}
