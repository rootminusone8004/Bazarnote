package com.rootminusone8004.bazarnote.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.rootminusone8004.bazarnote.data.Note;
import com.rootminusone8004.bazarnote.data.Session;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CsvExporterTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private static Session session(String name, Note... notes) {
        Session session = new Session(name);
        session.setJsonInfo(SessionJson.fromNotes(Arrays.asList(notes)));
        return session;
    }

    @Test
    public void buildsTableWithTotalsPerSessionAndOverall() {
        List<Session> sessions = Arrays.asList(
                session("Weekly", new Note("Rice", 2, 50), new Note("Oil", 1.5f, 10)),
                session("Party", new Note("Cake", 1, 20.25f)));

        List<String[]> rows = CsvExporter.buildRows(sessions);

        assertEquals(12, rows.size());
        assertArrayEquals(new String[]{"#####", "Weekly", "#####"}, rows.get(0));
        assertArrayEquals(new String[]{"Item", "Quantity", "Unit Price", "Real Price"}, rows.get(1));
        assertArrayEquals(new String[]{"Rice", "2", "50", "100"}, rows.get(2));
        assertArrayEquals(new String[]{"Oil", "1.50", "10", "15"}, rows.get(3));
        assertArrayEquals(new String[]{"", "", "Total Price", "115"}, rows.get(4));
        assertArrayEquals(new String[]{""}, rows.get(5));
        assertArrayEquals(new String[]{"Cake", "1", "20.25", "20.25"}, rows.get(8));
        assertArrayEquals(new String[]{"", "", "Total Sum", "135.25"}, rows.get(11));
    }

    @Test
    public void sessionNeverOpenedExportsAsEmptyTable() {
        List<String[]> rows = CsvExporter.buildRows(Collections.singletonList(new Session("Empty")));

        assertArrayEquals(new String[]{"", "", "Total Price", "0"}, rows.get(2));
        assertArrayEquals(new String[]{"", "", "Total Sum", "0"}, rows.get(rows.size() - 1));
    }

    @Test
    public void noSessionsStillProducesTotal() {
        List<String[]> rows = CsvExporter.buildRows(Collections.emptyList());

        assertEquals(1, rows.size());
        assertArrayEquals(new String[]{"", "", "Total Sum", "0"}, rows.get(0));
    }

    @Test
    public void uniqueFileKeepsFreeNames() throws Exception {
        File wanted = new File(folder.getRoot(), "list.csv");

        assertEquals(wanted, CsvExporter.uniqueFile(wanted));
    }

    @Test
    public void uniqueFileAppendsCounterBeforeExtension() throws Exception {
        folder.newFile("list.csv");
        folder.newFile("list_1.csv");

        File result = CsvExporter.uniqueFile(new File(folder.getRoot(), "list.csv"));

        assertEquals("list_2.csv", result.getName());
    }
}
