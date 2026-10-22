package com.rootminusone8004.bazarnote.data;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * A shopping session that groups {@link Note}s.
 * <p>
 * {@code jsonInfo} holds a JSON snapshot of the session's notes (see {@code SessionJson}).
 * The checkbox flags are persisted columns of the existing schema, so they are kept as-is.
 */
@Entity(tableName = "session_table", indices = {@Index(value = "name", unique = true)})
public class Session {
    @PrimaryKey(autoGenerate = true)
    private int sessionId;
    private final String name;
    private String jsonInfo;
    private double price;
    private boolean isCheckboxVisible;
    private boolean isCheckboxChecked;

    public Session(String name) {
        this.name = name;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getJsonInfo() {
        return jsonInfo;
    }

    public void setJsonInfo(String jsonInfo) {
        this.jsonInfo = jsonInfo;
    }

    public boolean isCheckboxVisible() {
        return isCheckboxVisible;
    }

    public void setCheckboxVisible(boolean checkboxVisible) {
        isCheckboxVisible = checkboxVisible;
    }

    public boolean isCheckboxChecked() {
        return isCheckboxChecked;
    }

    public void setCheckboxChecked(boolean checkboxChecked) {
        isCheckboxChecked = checkboxChecked;
    }
}
