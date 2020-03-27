package edu.msu.carro228.team17project2.Cloud;

import edu.msu.carro228.team17project2.Cloud.Models.Count;
import edu.msu.carro228.team17project2.Cloud.Models.DeleteGame;
import edu.msu.carro228.team17project2.Cloud.Models.JoinGame;
import edu.msu.carro228.team17project2.Cloud.Models.LoadGame;
import edu.msu.carro228.team17project2.Cloud.Models.Login;
import edu.msu.carro228.team17project2.Cloud.Models.SaveGame;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static edu.msu.carro228.team17project2.Cloud.Cloud.GET_COUNT_PATH;
import static edu.msu.carro228.team17project2.Cloud.Cloud.JOIN_GAME_PATH;
import static edu.msu.carro228.team17project2.Cloud.Cloud.GAME_PATH;
import static edu.msu.carro228.team17project2.Cloud.Cloud.LOGIN_PATH;
import static edu.msu.carro228.team17project2.Cloud.Cloud.NEW_USER_PATH;

public interface SteampunkedService {

    @GET(LOGIN_PATH)
    Call<Login> login(
            @Query("user") String name,
            @Query("pw") String password
    );

    @GET(NEW_USER_PATH)
    Call<Login> new_user(
            @Query("user") String name,
            @Query("pw") String password
    );

    @GET(JOIN_GAME_PATH)
    Call<JoinGame> join_game(
            @Query("user") String name
    );

    @GET(GET_COUNT_PATH)
    Call<Count> get_count(
            @Query("game") int id
    );

    @GET(GAME_PATH)
    Call<LoadGame> load(
            @Query("game") int id,
            @Query("action") String action
    );

    @GET(GAME_PATH)
    Call<SaveGame> save(
            @Query("game") int id,
            @Query("action") String action,
            @Query("state") String state
    );

    @GET(GAME_PATH)
    Call<DeleteGame> delete(
            @Query("game") int id,
            @Query("action") String action
    );
}