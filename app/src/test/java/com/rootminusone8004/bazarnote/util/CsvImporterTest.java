package com.rootminusone8004.bazarnote.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.rootminusone8004.bazarnote.data.Note;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvImporterTest {

    private static List<Note> parse(String csv) throws Exception {
        return CsvImporter.parseNotes(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void importsRowsAfterHeader() throws Exception {
        List<Note> notes = parse("Item,Quantity\nRice,2\nOil,1.5\n");

        assertEquals(2, notes.size());
        assertEquals("Rice", notes.get(0).getItem());
        assertEquals(2f, notes.get(0).getQuantity(), 0f);
        assertEquals(0f, notes.get(0).getPrice(), 0f);
        assertEquals(1.5f, notes.get(1).getQuantity(), 0f);
    }

    @Test
    public void headerIsCaseInsensitiveAndRowsBeforeItAreSkipped() throws Exception {
        List<Note> notes = parse("Shopping list,3\nITEM,quantity\nSalt,4\n");

        assertEquals(1, notes.size());
        assertEquals("Salt", notes.get(0).getItem());
    }

    @Test
    public void ignoresRowsWithoutNumericQuantity() throws Exception {
        List<Note> notes = parse("Item,Quantity\nRice,two\nSugar,3\n,\n");

        assertEquals(1, notes.size());
        assertEquals("Sugar", notes.get(0).getItem());
    }

    @Test
    public void fileWithoutHeaderImportsNothing() throws Exception {
        assertTrue(parse("Rice,2\nOil,1\n").isEmpty());
    }

    @Test
    public void canReimportAnExportedFile() throws Exception {
        String exported = "#####,Weekly,#####\n"
                + "Item,Quantity,Unit Price,Real Price\n"
                + "Rice,2,50,100\n"
                + ",,Total Price,100\n"
                + "\n"
                + ",,Total Sum,100\n";

        List<Note> notes = parse(exported);

        assertEquals(1, notes.size());
        assertEquals("Rice", notes.get(0).getItem());
        assertEquals(2f, notes.get(0).getQuantity(), 0f);
        assertEquals(50f, notes.get(0).getPrice(), 0f);
    }
}
