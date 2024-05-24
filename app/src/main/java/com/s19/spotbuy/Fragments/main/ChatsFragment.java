package com.s19.spotbuy.Fragments.main;

import static com.s19.spotbuy.Others.Constants.ChatRoomCollection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.s19.spotbuy.Activity.ChatActivity;
import com.s19.spotbuy.Activity.HomeActivity;
import com.s19.spotbuy.Adapters.FireBase.RecentChatRecyclerAdapter;
import com.s19.spotbuy.DataBase.SharedPrefs;
import com.s19.spotbuy.Models.Chat;
import com.s19.spotbuy.Models.ChatRoomModel;
import com.s19.spotbuy.Widgets.WrapContentLinearLayoutManager;
import com.s19.spotbuy.R;


public class ChatsFragment extends Fragment {

    View Root;
    RecyclerView chatsList;
    RecentChatRecyclerAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPrefs sharedPrefs;
    LinearLayout emptyIndicator;

    public ChatsFragment() {
        // Required empty public constructor
    }


    public static ChatsFragment newInstance() {


        return new ChatsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Root = inflater.inflate(R.layout.fragment_messages, container, false);
        sharedPrefs = new SharedPrefs(requireContext());

        initView();
        return Root;
    }

    public void initView() {
        emptyIndicator = Root.findViewById(R.id.emptyIndicator);
        chatsList = Root.findViewById(R.id.chats);
        Query query = db.collection(ChatRoomCollection)
                .whereNotEqualTo("lastMessage",null)
                .orderBy("lastMessage")
                .whereArrayContains("userIds", sharedPrefs.getSharedUID())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
               ;

        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query, ChatRoomModel.class).build();

        adapter = new RecentChatRecyclerAdapter(options, requireContext(),this);
        chatsList.setLayoutManager(new WrapContentLinearLayoutManager(requireContext()));
        chatsList.setAdapter(adapter);

    }

    public void showEmptyIndicator() {

        try {
            emptyIndicator.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideEmptyIndicator() {

        try {
            emptyIndicator.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onItemClickListener(Chat chat) {
        Intent intent = new Intent(requireActivity(), ChatActivity.class);
        intent.putExtra("User", chat);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((HomeActivity)context).setSelectedChip(1);
    }
}
