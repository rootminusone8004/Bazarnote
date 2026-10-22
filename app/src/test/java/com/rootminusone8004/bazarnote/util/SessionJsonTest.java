package com.rootminusone8004.bazarnote.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.rootminusone8004.bazarnote.data.Note;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SessionJsonTest {

    @Test
    public void keepsTheStoredFormat() {
        String json = SessionJson.fromNotes(Collections.singletonList(new Note("Rice", 2, 10.5f)));

        assertEquals("[{\"Item\":\"Rice\",\"Quantity\":2.0,\"Price\":10.5}]", json);
    }

    @Test
    public void roundTripsNotes() {
        String json = SessionJson.fromNotes(Arrays.asList(new Note("Rice", 2, 10.5f), new Note("Oil", 1, 0)));

        List<SessionJson.Line> lines = SessionJson.parse(json);

        assertEquals(2, lines.size());
        assertEquals("Rice", lines.get(0).item);
        assertEquals(2f, lines.get(0).quantity, 0f);
        assertEquals(10.5f, lines.get(0).price, 0f);
        assertEquals("Oil", lines.get(1).item);
    }

    @Test
    public void missingSnapshotIsEmpty() {
        assertTrue(SessionJson.parse(null).isEmpty());
        assertTrue(SessionJson.parse("").isEmpty());
        assertTrue(SessionJson.parse("[]").isEmpty());
    }

    @Test
    public void excludesUncheckedNotesFromSnapshot() {
        Note included = new Note("Rice", 2, 10.5f);
        Note excluded = new Note("Oil", 1, 20f);
        excluded.setIncluded(false);

        String json = SessionJson.fromNotes(Arrays.asList(included, excluded));

        assertEquals("[{\"Item\":\"Rice\",\"Quantity\":2.0,\"Price\":10.5}]", json);
    }
}
