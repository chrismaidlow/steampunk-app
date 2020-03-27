package edu.msu.carro228.team17project2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.msu.carro228.team17project2.Cloud.Cloud;

public class NewUserActivity extends AppCompatActivity {

    /**
     * Cloud object for creating new users in the database
     */
    private Cloud cloud = new Cloud();

    private String username;

    private String password;

    private boolean result;

    /**
     * Builds NewUser activity and sets appropriate messages
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

    }

    class MyRunnable implements Runnable{
        @Override
        public void run() {
            result = cloud.new_user(username, password);
        }
    }

    /**
     * Button handler for the return to title button on the end screen
     * @param view
     */
    public void onCreateUser(View view){

        //capture text from textboxes on create
        EditText ID = findViewById(R.id.editText_ID_entry);
        EditText pass = findViewById(R.id.editText_password_entry);
        EditText pass_re = findViewById(R.id.editText_password_reentry);

        username =  ID.getText().toString().trim();
        password =  pass.getText().toString().trim();
        String password_re =  pass_re.getText().toString().trim();

        //IF ALL FIELDS ENTERED
        if(!username.matches("") && !password.matches("") && !password_re.matches("")){
            //IF PASSWORDS MATCH

            //MOVE TO OPENING ACTIVTY TO LOGIN

            if(password.equals(password_re)){

                Thread t = new Thread(new MyRunnable(), "t");
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                }
                if(!result) {
                    Toast.makeText(view.getContext(), R.string.cant_create_new_user, Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Intent intent = new Intent(this, OpeningActivity.class);
                    startActivity(intent);
                }
            }
            else{
                //ERROR PASSWORDS DON'T MATCH
                //Toast.makeText(view.getContext(), R.string.InvalidInsert_Msg, Toast.LENGTH_SHORT).show();
            }

        }
        else{
            //ERROR NULL FIELD

        }




    }
}