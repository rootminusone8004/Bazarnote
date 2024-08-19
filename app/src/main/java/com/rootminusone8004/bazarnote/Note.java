package com.rootminusone8004.bazarnote;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table", indices = {@Index(value = {"item", "sessionId"}, unique = true)})
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int sessionId;
    private float quantity, price;
    private double multiple;
    private String item;

    public Note(String item, float quantity, float price) {
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        multiply();
    }

    // setters
    
    public void setId(int id) {
        this.id = id;
    }

    public void setMultiple(double multiple) {
        this.multiple = multiple;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    // getters
    
    public int getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public float getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public double getMultiple() {
        return multiple;
    }

    public int getSessionId() {
        return sessionId;
    }

    // other functions
    
    private void multiply(){
        this.multiple = this.quantity * this.price;
    }
}
