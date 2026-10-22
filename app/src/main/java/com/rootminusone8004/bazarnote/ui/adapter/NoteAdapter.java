package com.rootminusone8004.bazarnote.ui.adapter;

import static com.rootminusone8004.bazarnote.util.FormatUtils.formatNumber;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.rootminusone8004.bazarnote.data.Note;
import com.rootminusone8004.bazarnote.databinding.NoteItemBinding;
import com.rootminusone8004.bazarnote.guide.TapNoteAdapter;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {
    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public interface OnItemAddPriceListener {
        void onItemAddPrice(Note note);
    }

    public interface OnItemToggleListener {
        void onItemToggle(Note note, boolean isIncluded);
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getItem().equals(newItem.getItem())
                    && oldItem.getQuantity() == newItem.getQuantity()
                    && oldItem.getPrice() == newItem.getPrice()
                    && oldItem.isIncluded() == newItem.isIncluded();
        }
    };

    private OnItemClickListener clickListener;
    private OnItemAddPriceListener priceListener;
    private OnItemToggleListener toggleListener;

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemAddPriceListener(OnItemAddPriceListener listener) {
        this.priceListener = listener;
    }

    public void setOnItemToggleListener(OnItemToggleListener listener) {
        this.toggleListener = listener;
    }

    public Note getNoteAt(int position) {
        return getItem(position);
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteHolder(NoteItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note note = getItem(position);
        NoteItemBinding binding = holder.binding;
        binding.textViewItem.setText(note.getItem());
        binding.textViewQuantity.setText(formatNumber(note.getQuantity()));
        binding.textViewPrice.setText(formatNumber(note.getPrice()));
        binding.textViewMultiple.setText(formatNumber(note.getMultiple()));

        binding.checkboxInclude.setChecked(note.isIncluded());
        applyIncludedState(binding, note.isIncluded());

        if (position == 0) {
            new TapNoteAdapter(holder.itemView).startGuide();
        }
    }

    private static void applyIncludedState(NoteItemBinding binding, boolean isIncluded) {
        float alpha = isIncluded ? 1.0f : 0.45f;
        binding.textViewItem.setAlpha(alpha);
        binding.textViewQuantity.setAlpha(alpha);
        binding.textViewPrice.setAlpha(alpha);
        binding.textViewMultiple.setAlpha(alpha);

        int flag = Paint.STRIKE_THRU_TEXT_FLAG;
        setStrikeThru(binding.textViewItem, isIncluded, flag);
        setStrikeThru(binding.textViewQuantity, isIncluded, flag);
        setStrikeThru(binding.textViewPrice, isIncluded, flag);
        setStrikeThru(binding.textViewMultiple, isIncluded, flag);
    }

    private static void setStrikeThru(TextView tv, boolean isIncluded, int flag) {
        if (isIncluded) {
            tv.setPaintFlags(tv.getPaintFlags() & ~flag);
        } else {
            tv.setPaintFlags(tv.getPaintFlags() | flag);
        }
    }

    public class NoteHolder extends RecyclerView.ViewHolder {
        private final NoteItemBinding binding;

        private NoteHolder(NoteItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.checkboxInclude.setOnClickListener(v -> {
                Note note = currentNote();
                if (note != null && toggleListener != null) {
                    toggleListener.onItemToggle(note, binding.checkboxInclude.isChecked());
                }
            });

            binding.priceBtn.setOnClickListener(v -> {
                Note note = currentNote();
                if (note != null && priceListener != null) {
                    priceListener.onItemAddPrice(note);
                }
            });

            itemView.setOnClickListener(v -> {
                Note note = currentNote();
                if (note != null && clickListener != null) {
                    clickListener.onItemClick(note);
                }
            });
        }

        private Note currentNote() {
            int position = getAdapterPosition();
            return position == RecyclerView.NO_POSITION ? null : getItem(position);
        }
    }
}
