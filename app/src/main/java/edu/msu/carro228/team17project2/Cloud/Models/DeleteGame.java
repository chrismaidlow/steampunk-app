package edu.msu.carro228.team17project2.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "game")
public class DeleteGame {
    @Attribute
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state){
        this.state = state;
    }

    public DeleteGame(){

    }

    public DeleteGame(String state){
        this.state = state;
    }
}
