package com.ivansolutions.gcmandroidnights.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivansolutions.gcmandroidnights.AppContext;
import com.ivansolutions.gcmandroidnights.R;
import com.ivansolutions.gcmandroidnights.javabeans.NotifItem;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> implements View.OnClickListener{

    private View.OnClickListener listener;

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list, parent, false);
        itemView.setOnClickListener(this);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        NotifItem item = AppContext.getList().get(position);
        holder.bindMessage(item);
    }

    @Override
    public int getItemCount() {
        return AppContext.getList().size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener != null)
            listener.onClick(v);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView txtMessageList;

        public MessageViewHolder(View itemView) {
            super(itemView);

            txtMessageList = (TextView) itemView.findViewById(R.id.txt_message_list);
        }

        public void bindMessage(NotifItem message) {
            txtMessageList.setText(message.getContenido());
        }
    }


}
