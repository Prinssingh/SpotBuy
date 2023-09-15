package com.s19mobility.spotbuy.Activity;

import static com.s19mobility.spotbuy.Others.Constants.CALL_REQUEST;
import static com.s19mobility.spotbuy.Others.Constants.UserCollection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.s19mobility.spotbuy.Adapters.FireBase.ChatRecyclerAdapter;
import com.s19mobility.spotbuy.Models.ChatMessageModel;
import com.s19mobility.spotbuy.Models.ChatRoomModel;
import com.s19mobility.spotbuy.Models.User;
import com.s19mobility.spotbuy.Others.DownloadImage;
import com.s19mobility.spotbuy.Dialogs.LoadingDialog;
import com.s19mobility.spotbuy.Others.Utils;
import com.s19mobility.spotbuy.Widgets.WrapContentLinearLayoutManager;
import com.s19mobility.spotbuy.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    User otherUser;
    String chatroomId;
    ChatRoomModel chatroomModel;
    ChatRecyclerAdapter adapter;

    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton back;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;
    ImageView call;
    ProgressBar imageProgressIndicator;

    LoadingDialog loadingDialog;
    String[] callPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        loadingDialog = new LoadingDialog(this);
        otherUser = (User) getIntent().getSerializableExtra("User");
        callPermissions = new String[]{Manifest.permission.CALL_PHONE};
        initView();

        if (otherUser.getName() == null) {
            loadingDialog.show();
            FirebaseFirestore.getInstance().collection(UserCollection).document(otherUser.getId()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            otherUser = (User) documentSnapshot.toObject(User.class);
                            if (otherUser == null)
                                return;
                            if (otherUser.getImage() != null)
                                new DownloadImage(imageView, imageProgressIndicator).execute(otherUser.getImage());

                            if (otherUser.getName() != null)
                                otherUsername.setText(otherUser.getName());
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            loadingDialog.dismiss();
                        }
                    });
        }
    }

    private void initView() {
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        call = findViewById(R.id.call);
        call.setOnClickListener(this);

        chatroomId = Utils.getChatroomId(Utils.currentUserId(), otherUser.getId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);

        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.userImage);
        imageProgressIndicator = findViewById(R.id.imageProgressIndicator);

        if (otherUser.getImage() != null)
            new DownloadImage(imageView, imageProgressIndicator).execute(otherUser.getImage());

        if (otherUser.getName() != null)
            otherUsername.setText(otherUser.getName());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

        getOrCreateChatroomModel();
        setupChatRecyclerView();


    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onClick(View view) {
        if (view == back)
            onBackPressed();
        if (view == call)
            if (otherUser != null && otherUser.getMobile() != "") {
                makeCall();
            }
        else
                Toast.makeText(this, "Sellers information is not available right now", Toast.LENGTH_LONG).show();



    }

    private void makeCall() {
        if (!checkCallPermission()) {
            requestCallPermission();
        } else {
            callSeller();
        }

    }
    // checking storage permissions
    private Boolean checkCallPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == (PackageManager.PERMISSION_GRANTED);
    }

    // Requesting  gallery permission
    private void requestCallPermission() {
        requestPermissions(callPermissions, CALL_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_REQUEST) {
            if (grantResults.length > 0) {
                boolean call_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (call_accepted) {
                    callSeller();

                } else {
                    Toast.makeText(this, "Please Enable Call Permissions", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private void callSeller() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + otherUser.getMobile()));
        startActivity(intent);

        //TODO Make a dialog and let the user choose the calling number
    }



    void setupChatRecyclerView() {
        Query query = Utils.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        WrapContentLinearLayoutManager manager = new WrapContentLinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message) {

        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(Utils.currentUserId());
        chatroomModel.setLastMessage(message);
        Utils.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, Utils.currentUserId(), Timestamp.now());
        Utils.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            messageInput.setText("");
                            // sendNotification(message);
                        }
                    }
                });
    }

    void getOrCreateChatroomModel() {
        Utils.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatRoomModel.class);
                if (chatroomModel == null) {
                    //first time chat
                    chatroomModel = new ChatRoomModel(chatroomId,
                            Arrays.asList(Utils.currentUserId(), otherUser.getId()),
                            Timestamp.now(), "");
                    Utils.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    void sendNotification(String message) {

        Utils.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User currentUser = task.getResult().toObject(User.class);
                try {
                    JSONObject jsonObject = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", currentUser.getName());
                    notificationObj.put("body", message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId", currentUser.getId());

                    jsonObject.put("notification", notificationObj);
                    jsonObject.put("data", dataObj);
                    //  jsonObject.put("to", otherUser.getFcmToken());

                    callApi(jsonObject);


                } catch (Exception e) {

                }

            }
        });

    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer YOUR_API_KEY")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

    }

}