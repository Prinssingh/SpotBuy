package com.s19.spotbuy.Adapters.FireBase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.s19.spotbuy.Activity.ChatActivity;
import com.s19.spotbuy.Fragments.main.ChatsFragment;
import com.s19.spotbuy.Models.Chat;
import com.s19.spotbuy.Models.ChatRoomModel;
import com.s19.spotbuy.Models.User;
import com.s19.spotbuy.Others.DownloadImage;
import com.s19.spotbuy.Others.Utils;
import com.s19.spotbuy.R;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ViewHolderData> {
    ChatsFragment chatsFragment;
    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context, ChatsFragment chatsFragment) {
        super(options);
        this.context = context;
        this.chatsFragment = chatsFragment;

        if (getItemCount()<1)
            chatsFragment.showEmptyIndicator();
        else
            chatsFragment.hideEmptyIndicator();

    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (getItemCount()<1)
            chatsFragment.showEmptyIndicator();
        else
            chatsFragment.hideEmptyIndicator();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolderData holder, int position, @NonNull ChatRoomModel model) {
        Utils.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(Utils.currentUserId());

                        User otherUserModel = task.getResult().toObject(User.class);

                        new DownloadImage(holder.userImage, holder.imageProgressIndicator).execute(otherUserModel.getImage());

                        holder.userName.setText(otherUserModel.getName());
                        if (lastMessageSentByMe)
                            holder.lastMessage.setText("You : " + model.getLastMessage());
                        else
                            holder.lastMessage.setText(model.getLastMessage());
                        holder.dateTime.setText(Utils.timestampToString(model.getLastMessageTimestamp()));

                        holder.itemView.setOnClickListener(v -> {
                            //navigate to chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("User", otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });

                    }
                });
    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_chat, parent, false);
        return new ViewHolderData(view);
    }

    public static class ViewHolderData extends RecyclerView.ViewHolder {

        ShapeableImageView userImage;
        ProgressBar imageProgressIndicator;
        TextView userName, dateTime, lastMessage;


        public ViewHolderData(@NonNull View itemView) {
            super(itemView);


            userImage = itemView.findViewById(R.id.userImage);
            imageProgressIndicator = itemView.findViewById(R.id.imageProgressIndicator);
            userName = itemView.findViewById(R.id.userName);


            //Messages
            dateTime = itemView.findViewById(R.id.dateTime);
            lastMessage = itemView.findViewById(R.id.lastMessage);


        }

        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
        public void bindData(Chat chat, int i) {
            new DownloadImage(userImage, imageProgressIndicator).execute(chat.getImage());
            userName.setText("" + chat.getName());

            lastMessage.setText("" + chat.getLastMessage());
            dateTime.setText(chat.getTime().toString());


            //get latest messages and count and date time
            itemView.setOnClickListener(view -> {
                //chatsFragment.onItemClickListener(chat);
            });

        }


    }
}