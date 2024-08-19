package com.rootminusone8004.bazarnote;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "session_table", indices = {@Index(value = "name", unique = true)})
public class Session {
    @PrimaryKey(autoGenerate = true)
    private int sessionId;
    private final String name;
    private String jsonInfo;
    private double price;

    public Session(String name){
        this.name = name;
    }

    public void setSessionId(int sessionId){
        this.sessionId = sessionId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setJsonInfo(String jsonInfo) {
        this.jsonInfo = jsonInfo;
    }

    public int getSessionId(){
        return this.sessionId;
    }

    public String getName(){
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public String getJsonInfo() {
        return this.jsonInfo;
    }
}
