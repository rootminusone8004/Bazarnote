package com.rootminusone8004.bazarnote.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.rootminusone8004.bazarnote.data.Note;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Parses notes out of a CSV file exported by (or shaped like an export of) this app. */
public final class CsvImporter {
    private static final String HEADER_ITEM = "Item";
    private static final String HEADER_QUANTITY = "Quantity";

    private CsvImporter() {
    }

    /**
     * Skips everything up to the "Item,Quantity" header, then turns each row with a numeric quantity into a
     * {@link Note}. Rows that cannot be parsed are ignored.
     */
    public static List<Note> parseNotes(InputStream inputStream) throws IOException, CsvValidationException {
        List<Note> notes = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            boolean dataStarted = false;
            String[] row;

            while ((row = reader.readNext()) != null) {
                if (row.length < 2) {
                    continue;
                }
                if (!dataStarted) {
                    dataStarted = row[0].trim().equalsIgnoreCase(HEADER_ITEM)
                            && row[1].trim().equalsIgnoreCase(HEADER_QUANTITY);
                    continue;
                }
                String item = row[0].trim();
                if (item.isEmpty()) {
                    continue;
                }
                float quantity;
                try {
                    quantity = Float.parseFloat(row[1].trim());
                } catch (NumberFormatException e) {
                    // Not a data row (section title, totals, repeated header...)
                    continue;
                }
                float price = 0f;
                if (row.length >= 3) {
                    try {
                        price = Float.parseFloat(row[2].trim());
                    } catch (NumberFormatException ignored) {
                    }
                }
                notes.add(new Note(item, quantity, price));
            }
        }
        return notes;
    }
}
