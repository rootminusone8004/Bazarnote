package com.rootminusone8004.bazarnote;

import static com.rootminusone8004.bazarnote.Utility.formatDoubleValue;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.rootminusone8004.bazarnote.Utilities.TapSessionAdapter;
import com.rootminusone8004.bazarnote.Utilities.TapTargetUtil;

public class SessionAdapter extends ListAdapter<Session, SessionAdapter.SessionHolder> {
    private OnItemClickListener listener;

    public SessionAdapter() {
        super(DIFF_CALLBACK);
    }

    public static final DiffUtil.ItemCallback<Session> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Session oldItem, @NonNull Session newItem) {
            return oldItem.getSessionId() == newItem.getSessionId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Session oldItem, @NonNull Session newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                oldItem.getPrice() == newItem.getPrice();
        }
    };

    @NonNull
    @Override
    public SessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View sessionItemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.session_item, parent, false);
        return new SessionHolder(sessionItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionHolder holder, int position) {
        Session currentSession = getItem(position);
        holder.textViewSession.setText(currentSession.getName());
        holder.textViewSessionSum.setText(formatDoubleValue(currentSession.getPrice()));

        if (currentSession.isCheckboxVisible()) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(currentSession.isCheckboxChecked());
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }

        if (position == 0) {
            TapSessionAdapter tapSessionAdapter = new TapSessionAdapter(holder);
            tapSessionAdapter.startGuide();
        }

        // Update session state when checkbox is clicked
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> currentSession.setCheckboxChecked(isChecked));
    }

    public Session getSessionAt(int position) {
        return getItem(position);
    }

    public class SessionHolder extends RecyclerView.ViewHolder {
        private TextView textViewSession;
        private TextView textViewSessionSum;
        private CheckBox checkBox;

        public SessionHolder(@NonNull View itemView) {
            super(itemView);
            textViewSession = itemView.findViewById(R.id.text_view_session_item);
            textViewSessionSum = itemView.findViewById(R.id.text_view_session_sum);
            checkBox = itemView.findViewById(R.id.session_chk_btn);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public void showAllCheckboxesWithTick() {
        for (int i = 0; i < getItemCount(); i++) {
            Session session = getItem(i);
            session.setCheckboxVisible(true);
            session.setCheckboxChecked(true);
        }
        // Notify the adapter to refresh the RecyclerView
        notifyDataSetChanged();
    }

    public String hideAllCheckboxesWithTick() {
        StringBuilder compactJson = new StringBuilder();
        for (int i = 0; i < getItemCount(); i++) {
            Session session = getItem(i);
            if (session.isCheckboxChecked()) {
                compactJson.append(session.getJsonInfo());
            }
            session.setCheckboxVisible(false);
        }
        // Notify the adapter to refresh the RecyclerView
        notifyDataSetChanged();
        return compactJson.toString();
    }

    public boolean isCheckboxChecked(int position) {
        Session session = getItem(position);
        return session.isCheckboxChecked(); // Return checkbox state for a specific session
    }

    public interface OnItemClickListener {
        void onItemClick(Session session);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
