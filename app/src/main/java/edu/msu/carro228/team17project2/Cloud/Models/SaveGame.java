package edu.msu.carro228.team17project2.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

public class SaveGame {
    @Attribute(required = false)
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public SaveGame(){

    }

    public SaveGame(int state){
        this.state = state;
    }
}
