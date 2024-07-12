package com.rootminusone8004.bazarnote;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int price;
    private float quantity;
    private float multiple;
    private String item;
    private boolean checked = false;

    public Note(String item, float quantity, int price) {
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        multiply();
    }

    // setters
    
    public void setId(int id) {
        this.id = id;
    }

    public void setMultiple(float multiple) {
        this.multiple = multiple;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public int getPrice() {
        return price;
    }

    public float getMultiple() {
        return multiple;
    }

    public boolean getChecked(){
        return checked;
    }

    // other functions
    
    private void multiply(){
        this.multiple = this.quantity * this.price;
    }
}
