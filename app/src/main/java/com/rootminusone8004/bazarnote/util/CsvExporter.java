package com.rootminusone8004.bazarnote.util;

import android.os.Environment;

import com.opencsv.CSVWriter;
import com.rootminusone8004.bazarnote.data.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Writes sessions (and the notes captured in their JSON snapshots) to a CSV file on shared storage. */
public final class CsvExporter {
    private static final String ROOT_DIRECTORY = "Bazarnote";
    private static final String DATE_DIRECTORY_PATTERN = "dd_MM_yyyy";

    private CsvExporter() {
    }

    /**
     * Exports the given sessions to {@code <storage>/Bazarnote/<dd_MM_yyyy>/<fileName>}, picking a free file name
     * if one already exists.
     *
     * @return the file that was written
     */
    public static File export(List<Session> sessions, String fileName) throws IOException {
        File directory = new File(
                new File(Environment.getExternalStorageDirectory(), ROOT_DIRECTORY),
                new SimpleDateFormat(DATE_DIRECTORY_PATTERN, Locale.getDefault()).format(new Date()));
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Could not create " + directory);
        }

        File file = uniqueFile(new File(directory, fileName));
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.writeAll(buildRows(sessions));
        }
        return file;
    }

    /** Builds the CSV content: one titled table per session followed by the overall total. */
    public static List<String[]> buildRows(List<Session> sessions) {
        List<String[]> rows = new ArrayList<>();
        double totalSum = 0;

        for (Session session : sessions) {
            rows.add(new String[]{"#####", session.getName(), "#####"});
            rows.add(new String[]{"Item", "Quantity", "Unit Price", "Real Price"});

            double sessionTotal = 0;
            for (SessionJson.Line line : SessionJson.parse(session.getJsonInfo())) {
                double lineTotal = line.quantity * line.price;
                rows.add(new String[]{
                        line.item,
                        FormatUtils.formatNumber(line.quantity),
                        FormatUtils.formatNumber(line.price),
                        FormatUtils.formatNumber(lineTotal)
                });
                sessionTotal += lineTotal;
            }

            rows.add(new String[]{"", "", "Total Price", FormatUtils.formatNumber(sessionTotal)});
            rows.add(new String[]{""});
            totalSum += sessionTotal;
        }

        rows.add(new String[]{"", "", "Total Sum", FormatUtils.formatNumber(totalSum)});
        return rows;
    }

    /** Appends {@code _1}, {@code _2}... before the extension until the file name is unused. */
    static File uniqueFile(File file) {
        String name = file.getName();
        String extension = "";
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex != -1) {
            extension = name.substring(dotIndex);
            name = name.substring(0, dotIndex);
        }

        File candidate = file;
        for (int count = 1; candidate.exists(); count++) {
            candidate = new File(file.getParentFile(), name + "_" + count + extension);
        }
        return candidate;
    }
}
