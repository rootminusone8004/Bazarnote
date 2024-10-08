package com.rootminusone8004.bazarnote;

import static com.rootminusone8004.bazarnote.Utility.formatDoubleValue;
import static com.rootminusone8004.bazarnote.Utility.formatFloatValue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.rootminusone8004.bazarnote.Utilities.TapNoteAdapter;
import com.rootminusone8004.bazarnote.Utilities.TapSessionAdapter;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {
    private OnItemClickListener listener;
    private OnItemAddPriceListener priceListener;

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getItem().equals(newItem.getItem()) &&
                    oldItem.getQuantity() == newItem.getQuantity() &&
                    oldItem.getPrice() == newItem.getPrice();
        }
    };

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = getItem(position);
        holder.textViewItem.setText(currentNote.getItem());
        holder.textViewQuantity.setText(formatFloatValue(currentNote.getQuantity()));
        holder.textViewPrice.setText(formatFloatValue(currentNote.getPrice()));
        holder.textViewMultiple.setText(formatDoubleValue(currentNote.getMultiple()));

        if (position == 0) {
            TapNoteAdapter tapNoteAdapter = new TapNoteAdapter(holder);
            tapNoteAdapter.startGuide();
        }
    }

    public Note getNoteAt(int position) {
        return getItem(position);
    }

    public class NoteHolder extends RecyclerView.ViewHolder {
        private TextView textViewItem;
        private TextView textViewQuantity;
        private TextView textViewPrice;
        private TextView textViewMultiple;
        private ImageButton priceBtn;

        private NoteHolder(View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.text_view_item);
            textViewQuantity = itemView.findViewById(R.id.text_view_quantity);
            textViewPrice = itemView.findViewById(R.id.text_view_price);
            textViewMultiple = itemView.findViewById(R.id.text_view_multiple);
            priceBtn = itemView.findViewById(R.id.price_btn);

            priceBtn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    priceListener.onItemAddPrice(getItem(position));
                }
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemAddPriceListener {
        void onItemAddPrice(Note note);
    }

    public void setOnItemAddPriceListener(OnItemAddPriceListener priceListener){
        this.priceListener = priceListener;
    }
}
