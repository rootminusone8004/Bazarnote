package com.rootminusone8004.bazarnote;

import static com.rootminusone8004.bazarnote.Utility.formatDoubleValue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

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
    }

    public Session getSessionAt(int position) {
        return getItem(position);
    }

    class SessionHolder extends RecyclerView.ViewHolder {
        private TextView textViewSession;
        private TextView textViewSessionSum;

        public SessionHolder(@NonNull View itemView) {
            super(itemView);
            textViewSession = itemView.findViewById(R.id.text_view_session_item);
            textViewSessionSum = itemView.findViewById(R.id.text_view_session_sum);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Session session);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
