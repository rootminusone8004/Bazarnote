package com.rootminusone8004.bazarnote;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int price;
    private float quantity;
    private String item;

    public void setId(int id) {
        this.id = id;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

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
}
