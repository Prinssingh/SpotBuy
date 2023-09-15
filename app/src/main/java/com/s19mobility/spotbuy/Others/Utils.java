package com.s19mobility.spotbuy.Others;

import static com.s19mobility.spotbuy.Others.Constants.ChatRoomCollection;
import static com.s19mobility.spotbuy.Others.Constants.UserCollection;

import android.content.Context;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
    String jsonString;
    JSONObject json;

    public Utils(Context context) {
        try {
            InputStream is = context.getAssets().open("Indian_State_City.json");

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
            json = new JSONObject(jsonString);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection(UserCollection).document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection(UserCollection);
    }

    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection(ChatRoomCollection).document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(Utils.currentUserId())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfilePicStorageRef() {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(Utils.currentUserId());
    }

    public static StorageReference getOtherProfilePicStorageRef(String otherUserId) {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }

    public List<String> getStateList() {
        List<String> states = new ArrayList<String>() {
        };

        for (int i = 0; i < json.names().length(); i++) {
            try {
                states.add((String) json.names().get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(states);

        return states;
    }

    public List<String> getCityList(String state) {
        List<String> cities = new ArrayList<String>() {
        };

        JSONArray tem = null;
        try {
            tem = (JSONArray) json.get(state);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < tem.length(); i++) {
            try {
                cities.add(tem.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(cities);
        return cities;
    }

    public List<String> getCountrylist() {
        List<String> country = new ArrayList<String>() {
        };

        country.add("India");

        return country;
    }


    public static  void deleteImageFromFirebase(String imageUrl){
        FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl).delete();
    }

}
