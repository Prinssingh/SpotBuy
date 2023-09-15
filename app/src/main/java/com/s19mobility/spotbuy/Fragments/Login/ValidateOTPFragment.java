package com.s19mobility.spotbuy.Fragments.Login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.s19mobility.spotbuy.Activity.ProfileActivity;
import com.s19mobility.spotbuy.DataBase.SharedPrefs;
import com.s19mobility.spotbuy.Others.Constants;
import com.s19mobility.spotbuy.Dialogs.LoadingDialog;
import com.s19mobility.spotbuy.Others.ReadBasicFireBaseData;
import com.s19mobility.spotbuy.R;

import java.util.concurrent.TimeUnit;


public class ValidateOTPFragment extends Fragment implements View.OnClickListener {

    public static final String SMS_BUNDLE = "pdus";
    private final String TAG = "testing";
    View Root;
    EditText otp;
    Button verify;
    PhoneAuthProvider.ForceResendingToken token;
    String verificationId;
    LoadingDialog loadingDialog;
    SharedPrefs sharedPrefs;
    Activity activity;
    TextView resendOTP;
    IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    private FirebaseAuth mAuth;
    private BroadcastReceiver smsBroadcastReceiver;
    String firebaseVerificationCode;

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
        activity = requireActivity();
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(activity);
        sharedPrefs = new SharedPrefs(activity);

        initView();
        return Root;
    }

    private void initView() {
        otp = Root.findViewById(R.id.otp);
        resendOTP = Root.findViewById(R.id.resendOTP);
        resendOTP.setOnClickListener(this);
        resendOTP.setActivated(false);
        verify = Root.findViewById(R.id.verify);
        verify.setOnClickListener(this);

        new CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                resendOTP.setText("Resend OTP in  0:" + l / 1000);

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                resendOTP.setText("Resend OTP");
                resendOTP.setActivated(true);
            }
        }.start();

    }

    @Override
    public void onClick(View view) {
        if (view == verify) {
            loadingDialog.show();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp.getText().toString());
            signInWithPhoneAuthCredential(credential);
        }

        if (view == resendOTP) {
            resendOTPToUser();
        }

    }

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
                            sharedPrefs.setLogin(true);
                            // Update UI

                            Toast.makeText(activity, "Success!!", Toast.LENGTH_LONG).show();


                            new ReadBasicFireBaseData(requireContext());
                            //GOTO Next ACTIVITY
                            Intent profileIntent = new Intent(requireActivity(), ProfileActivity.class);
                            profileIntent.putExtra(Constants.ProfileMode, "edit");
                            startActivity(profileIntent);
                            activity.finish();

                        } else {
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

    private void resendOTPToUser() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                sharedPrefs.getSharedMobile(),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                requireActivity(),               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }
                },         // OnVerificationStateChangedCallbacks
                token);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        smsBroadcastReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("smsBroadcastReceiver", "onReceive");
                Bundle pudsBundle = intent.getExtras();
                Object[] pdus = (Object[]) pudsBundle.get(SMS_BUNDLE);
                SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[0]);
                Log.i(TAG, messages.getMessageBody());

                firebaseVerificationCode = messages.getMessageBody().trim().split(" ")[0];//only a number code
                Toast.makeText(getContext(), firebaseVerificationCode, Toast.LENGTH_SHORT).show();
                if(firebaseVerificationCode.length() == 6)
                    otp.setText(""+firebaseVerificationCode);
            }
        };
    }

}
