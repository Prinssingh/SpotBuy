package com.s19.spotbuy.Others;

import com.google.firebase.firestore.DocumentSnapshot;

public interface OnGetDataListener {

    void onSuccess(DocumentSnapshot documentSnapshot);

    void onStart();

    void onFailure();

}
