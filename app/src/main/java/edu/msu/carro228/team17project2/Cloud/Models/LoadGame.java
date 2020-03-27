package edu.msu.carro228.team17project2.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "game")
public final class LoadGame {
    @Attribute(required = false)
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LoadGame() {}

    public LoadGame(String state) {
        this.state = state;
    }
}
