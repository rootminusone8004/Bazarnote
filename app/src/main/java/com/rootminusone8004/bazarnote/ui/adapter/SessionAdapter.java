package com.rootminusone8004.bazarnote.ui.adapter;

import static com.rootminusone8004.bazarnote.util.FormatUtils.formatNumber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.rootminusone8004.bazarnote.data.Session;
import com.rootminusone8004.bazarnote.databinding.SessionItemBinding;
import com.rootminusone8004.bazarnote.guide.TapSessionAdapter;

public class SessionAdapter extends ListAdapter<Session, SessionAdapter.SessionHolder> {
    public interface OnItemClickListener {
        void onItemClick(Session session);
    }

    private static final DiffUtil.ItemCallback<Session> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Session oldItem, @NonNull Session newItem) {
            return oldItem.getSessionId() == newItem.getSessionId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Session oldItem, @NonNull Session newItem) {
            return oldItem.getName().equals(newItem.getName())
                    && oldItem.getPrice() == newItem.getPrice();
        }
    };

    private OnItemClickListener listener;

    public SessionAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public Session getSessionAt(int position) {
        return getItem(position);
    }

    /** Shows a ticked checkbox on every session so the user can choose which ones to export. */
    public void showAllCheckboxesWithTick() {
        for (Session session : getCurrentList()) {
            session.setCheckboxVisible(true);
            session.setCheckboxChecked(true);
        }
        notifyDataSetChanged();
    }

    /** Hides the checkboxes; the checked state is kept so the selection can still be exported. */
    public void hideAllCheckboxes() {
        for (Session session : getCurrentList()) {
            session.setCheckboxVisible(false);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SessionHolder(SessionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SessionHolder holder, int position) {
        Session session = getItem(position);
        SessionItemBinding binding = holder.binding;
        binding.textViewSessionItem.setText(session.getName());
        binding.textViewSessionSum.setText(formatNumber(session.getPrice()));

        // Detach the old listener first: a recycled holder must not write into the previous session.
        binding.sessionChkBtn.setOnCheckedChangeListener(null);
        if (session.isCheckboxVisible()) {
            binding.sessionChkBtn.setVisibility(View.VISIBLE);
            binding.sessionChkBtn.setChecked(session.isCheckboxChecked());
        } else {
            binding.sessionChkBtn.setVisibility(View.GONE);
        }
        binding.sessionChkBtn.setOnCheckedChangeListener((buttonView, isChecked) -> session.setCheckboxChecked(isChecked));

        if (position == 0) {
            new TapSessionAdapter(holder.itemView).startGuide();
        }
    }

    public class SessionHolder extends RecyclerView.ViewHolder {
        private final SessionItemBinding binding;

        private SessionHolder(SessionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }
}
