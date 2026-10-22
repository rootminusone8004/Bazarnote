package com.rootminusone8004.bazarnote.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rootminusone8004.bazarnote.data.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes the JSON snapshot stored in {@code Session.jsonInfo}.
 * The format is persisted on user devices, so the keys must not change.
 */
public final class SessionJson {
    private static final String KEY_ITEM = "Item";
    private static final String KEY_QUANTITY = "Quantity";
    private static final String KEY_PRICE = "Price";

    private SessionJson() {
    }

    /** A single line of a session snapshot. */
    public static final class Line {
        public final String item;
        public final float quantity;
        public final float price;

        Line(String item, float quantity, float price) {
            this.item = item;
            this.quantity = quantity;
            this.price = price;
        }
    }

    public static String fromNotes(@NonNull List<Note> notes) {
        JsonArray array = new JsonArray();
        for (Note note : notes) {
            if (!note.isIncluded()) {
                continue;
            }
            JsonObject object = new JsonObject();
            object.addProperty(KEY_ITEM, note.getItem());
            object.addProperty(KEY_QUANTITY, note.getQuantity());
            object.addProperty(KEY_PRICE, note.getPrice());
            array.add(object);
        }
        return array.toString();
    }

    /** @return the lines of the snapshot, or an empty list if the session has none yet */
    public static List<Line> parse(@Nullable String json) {
        List<Line> lines = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return lines;
        }
        for (JsonElement element : JsonParser.parseString(json).getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();
            lines.add(new Line(
                    object.get(KEY_ITEM).getAsString(),
                    object.get(KEY_QUANTITY).getAsFloat(),
                    object.get(KEY_PRICE).getAsFloat()));
        }
        return lines;
    }
}
