package edu.msu.carro228.team17project2.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "game")
public class Count {
    @Attribute
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Count(){

    }

    public Count(int status){
        this.status = status;
    }
}
