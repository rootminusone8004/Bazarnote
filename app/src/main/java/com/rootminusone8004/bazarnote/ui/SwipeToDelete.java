package com.rootminusone8004.bazarnote.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.IntConsumer;

/** Swipe-right-to-delete behaviour shared by the list screens. */
final class SwipeToDelete {
    private SwipeToDelete() {
    }

    /** @param onSwiped receives the adapter position of the swiped item */
    static void attach(RecyclerView recyclerView, IntConsumer onSwiped) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onSwiped.accept(position);
                }
            }
        }).attachToRecyclerView(recyclerView);
    }
}
