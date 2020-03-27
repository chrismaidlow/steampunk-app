package edu.msu.carro228.team17project2.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "game")
public class JoinGame {
    @Attribute
    private int status;

    @Attribute(name = "msg", required = false)
    private String message;

    @Attribute
    private int id;

    public  int getId(){return id;}

    public void setId(){this.id = id;}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public JoinGame() {}

    public JoinGame(int status, String msg, int id) {
        this.status = status;
        this.message = msg;
        this.id = id;
    }
}
