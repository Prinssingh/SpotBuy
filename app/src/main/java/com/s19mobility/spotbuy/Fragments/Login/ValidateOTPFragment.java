package com.s19mobility.spotbuy.Fragments.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.s19mobility.spotbuy.Activity.ProfileActivity;
import com.s19mobility.spotbuy.DataBase.SharedPrefs;
import com.s19mobility.spotbuy.Others.Constants;
import com.s19mobility.spotbuy.Others.LoadingDialog;
import com.s19mobility.spotbuy.Others.ReadBasicFireBaseData;
import com.s19mobility.spotbuy.R;


public class ValidateOTPFragment extends Fragment implements View.OnClickListener {

    View Root;
    EditText otp;
    Button verify;
    PhoneAuthProvider.ForceResendingToken token;
    String verificationId;
    LoadingDialog loadingDialog;
    SharedPrefs sharedPrefs;
    Activity activity;
    private FirebaseAuth mAuth;


    public ValidateOTPFragment(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
        // Required empty public constructor
        this.verificationId = verificationId;
        this.token = token;
    }


    public static ValidateOTPFragment newInstance(String verificationId, PhoneAuthProvider.ForceResendingToken token) {

        return new ValidateOTPFragment(verificationId, token);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Root = inflater.inflate(R.layout.fragment_validate_o_t_p, container, false);
        activity =requireActivity();
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(activity);
        sharedPrefs= new SharedPrefs(activity);

        initView();
        return Root;
    }

    private void initView() {
        otp = Root.findViewById(R.id.otp);
        verify = Root.findViewById(R.id.verify);
        verify.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view ==verify){
            loadingDialog.show();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp.getText().toString());
            signInWithPhoneAuthCredential(credential);
        }

    }

    private final String TAG="testing";
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            assert user != null;
                            sharedPrefs.setSharedUID(user.getUid());
                            // Update UI

                            Toast.makeText(activity, "Success!!", Toast.LENGTH_LONG).show();

                            sharedPrefs.setLogin(true);
                            new ReadBasicFireBaseData(requireContext());
                            //GOTO Next ACTIVITY
                            Intent profileIntent = new Intent(requireActivity(), ProfileActivity.class);
                            profileIntent.putExtra(Constants.ProfileMode,"edit");
                            startActivity(profileIntent);
                            activity.finish();

                        }
                        else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(activity, "Failed!!", Toast.LENGTH_LONG).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                otp.setError("incorrect otp");
                            }
                        }

                        loadingDialog.dismiss();
                    }
                });
    }

}
