package com.s19mobility.spotbuy.Others;

import com.google.firebase.firestore.DocumentSnapshot;

public interface OnImageUploadListener   {

    void onSuccess();

    void onStart();

    void onFailure();
}
