package com.rootminusone8004.bazarnote.data;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * A single shopping item belonging to a {@link Session}.
 * <p>
 * Do not change the field names/types without bumping the database version: they define the Room schema.
 */
@Entity(tableName = "note_table", indices = {@Index(value = {"item", "sessionId"}, unique = true)})
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int sessionId;
    private boolean isIncluded;
    private float quantity, price;
    private double multiple;
    private String item;

    public Note(String item, float quantity, float price) {
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.multiple = quantity * price;
        this.isIncluded = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isIncluded() {
        return isIncluded;
    }

    public void setIncluded(boolean included) {
        this.isIncluded = included;
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

    public void setMultiple(double multiple) {
        this.multiple = multiple;
    }
}
