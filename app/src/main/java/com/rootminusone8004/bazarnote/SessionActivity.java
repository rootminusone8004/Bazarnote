package com.rootminusone8004.bazarnote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SessionActivity extends AppCompatActivity {
    public static final int ADD_SESSION_REQUEST = 4;
    public static final int NOTE_TRANSFER_REQUEST = 5;

    private SessionViewModel sessionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        FloatingActionButton buttonAddSession = findViewById(R.id.button_add_session);
        buttonAddSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionActivity.this, AddSessionActivity.class);
                startActivityForResult(intent, ADD_SESSION_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.session_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        SessionAdapter adapter = new SessionAdapter();
        recyclerView.setAdapter(adapter);

        sessionViewModel = new ViewModelProvider(this).get(SessionViewModel.class);
        sessionViewModel.getAllSessions().observe(this, new Observer<List<Session>>() {
            @Override
            public void onChanged(List<Session> sessions) {
                adapter.setSessions(sessions);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                sessionViewModel.delete(adapter.getSessionAt(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new SessionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Session session) {
                Intent intent = new Intent(SessionActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_SESSION_ID, session.getSessionId());
                startActivityForResult(intent, NOTE_TRANSFER_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_SESSION_REQUEST && resultCode == RESULT_OK){
            String sessionName = data.getStringExtra(AddSessionActivity.EXTRA_SESSION);

            Session session = new Session(sessionName);
            sessionViewModel.insert(session);
        }
    }
}