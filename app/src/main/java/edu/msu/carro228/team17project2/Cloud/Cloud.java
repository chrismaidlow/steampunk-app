package edu.msu.carro228.team17project2.Cloud;

import android.util.Log;

import java.io.IOException;

import edu.msu.carro228.team17project2.Cloud.Models.Count;
import edu.msu.carro228.team17project2.Cloud.Models.DeleteGame;
import edu.msu.carro228.team17project2.Cloud.Models.JoinGame;
import edu.msu.carro228.team17project2.Cloud.Models.LoadGame;
import edu.msu.carro228.team17project2.Cloud.Models.Login;
import edu.msu.carro228.team17project2.Cloud.Models.SaveGame;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@SuppressWarnings("deprecation")
public class Cloud {
    private static final String BASE_URL = "http://webdev.cse.msu.edu/~creceli2/cse476/proj2/";
    public static final String LOGIN_PATH = "Login.php";
    public static final String NEW_USER_PATH = "addUser.php";
    public static final String GAME_PATH = "manageGame.php";
    public static final String JOIN_GAME_PATH = "joinGame.php";
    public static final String GET_COUNT_PATH = "getCount.php";

    private int [] ret = new int[2];

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();

    /**
     * Create a new user on the server
     * @param user username
     * @param password password
     * @return true on success
     */
    public boolean new_user(String user, String password) {
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            Login result = service.new_user(user, password).execute().body();
            if (result.getStatus() != null && result.getStatus().equals("yes")) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Log a user into the system
     * @param user username
     * @param password password
     * @return true on success
     */
    public boolean login(String user, String password) {
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            Login result = service.login(user, password).execute().body();
            if (result.getStatus() != null && result.getStatus().equals("yes")) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Attempt to join the current game
     * @param user username
     * @return integer state code
     */
    public int[] joinGame(String user) {
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            JoinGame result = service.join_game(user).execute().body();
            if (result.getMessage() != null){
                Log.e("Join Game Message", result.getMessage());
            }
            Log.e("Join Game Status", "" + result.getStatus());
            ret[0] = result.getStatus();
            ret[1] = result.getId();
            return ret;
        } catch (IOException e) {
            Log.e("Join Failed", e.toString());
            ret [0] = -1;
            return ret;
        }
    }

    public boolean getCount(int id) {
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            Count result = service.get_count(id).execute().body();
            if (result.getStatus() == 1) {
                return true;
            }
            else if (result.getStatus() == 2) {
                return false;
            }
            return true;
        } catch (IOException e) {
            return true;
        }
    }

    public String load(int id){
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            LoadGame result = service.load(id,"receive").execute().body();
            if (result.getState() != null) {
                return result.getState();
            }
            return "";
        } catch (IOException e) {
            return "";
        }
    }

    public boolean save(String state, int id){
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            SaveGame result = service.save(id, "update", state).execute().body();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean delete(int id){
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try{
            DeleteGame result = service.delete(id, "clear").execute().body();
            return true;
        } catch (IOException e){
            return false;
        }
    }
}
