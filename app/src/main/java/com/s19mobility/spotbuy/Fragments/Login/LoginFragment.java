package com.s19mobility.spotbuy.Fragments.Login;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.s19mobility.spotbuy.Activity.LoginActivity;
import com.s19mobility.spotbuy.DataBase.SharedPrefs;
import com.s19mobility.spotbuy.Others.LoadingDialog;
import com.s19mobility.spotbuy.Others.NetworkUtil;
import com.s19mobility.spotbuy.R;

import java.util.concurrent.TimeUnit;

public class LoginFragment extends Fragment implements View.OnClickListener {
    View Root;
    private final String TAG="Login Fragment";

    EditText userMobile, password;

    Button loginButton;
    LinearLayout linearBG;

    ImageView passwordVisibility;
    boolean isPasswordVisible = false;

    private FirebaseAuth mAuth;
    LoadingDialog loadingDialog;
    SharedPrefs sharedPrefs;

    public LoginFragment() {

    }


    public static LoginFragment newInstance() {

        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Root = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(requireActivity());
        sharedPrefs = new SharedPrefs(requireActivity());
        initView();
        return Root;
    }

    private void initView() {

        userMobile = Root.findViewById(R.id.userMobile);
        userMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isDigitsOnly(charSequence))
                    password.setVisibility(View.VISIBLE);
                else
                    userMobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        password = Root.findViewById(R.id.password);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


        linearBG = Root.findViewById(R.id.linearBG);
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    linearBG.setBackgroundResource(R.drawable.custom_active_background);
                else
                    linearBG.setBackgroundResource(R.drawable.custom_input_design);
            }
        });

        loginButton = Root.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        passwordVisibility = Root.findViewById(R.id.passwordVisibility);
        passwordVisibility.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == loginButton) {
            if(NetworkUtil.getConnectivityStatusString(requireContext())!=0) Login();
            else
                Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        if (view == passwordVisibility) {
            setPasswordVisibility();
        }
    }

    private void setPasswordVisibility() {

        if (isPasswordVisible) {
            isPasswordVisible = false;
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordVisibility.setImageResource(R.drawable.icon_visibility_on);

        } else {
            isPasswordVisible = true;
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordVisibility.setImageResource(R.drawable.icon_visibility_off);
        }

    }

    private void Login() {
        //Mobile Number Login
        if (TextUtils.isDigitsOnly(userMobile.getText().toString()) && isValidMobile()){
            MobileLogin();
        }
        //Email Login
        else if(isValidEmail()){
            EmailLogin();
        }

    }

    private boolean isValidEmail() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String passwordPattern = "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}";

        if (userMobile.getText().toString().length() == 0 || userMobile.getText().toString().trim().equals("")) {
            userMobile.setError("Empty");
            return false;
        }
        if (password.getText().toString().length() == 0 || password.getText().toString().trim().equals("")) {
            password.setError("Empty");
            return false;
        }
        if (userMobile.getText().toString().matches(emailPattern)) {
            if (password.getText().toString().matches(passwordPattern))
                return true;
            else{
                password.setError("in valid password");
                return false;
            }

        }
        else{
            userMobile.setError("in valid email");
            return false;
        }

    }

    private boolean isValidMobile(){
        return android.util.Patterns.PHONE.matcher(userMobile.getText().toString()).matches();
    }

    private void EmailLogin(){
        String email = userMobile.getText().toString().trim();
        String password = this.password.getText().toString().trim();
    }
    private void MobileLogin(){
        loadingDialog.show();
        String mobile = "+91 "+userMobile.getText().toString().trim();
        sharedPrefs.setSharedMobile(mobile);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mobile)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(requireActivity())                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
          //  Log.d(TAG, "onVerificationCompleted:" + credential);

           // signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.e(TAG, "onVerificationFailed", e);
            loadingDialog.setMessage("OTP send Failed");
            loadingDialog.dismiss();
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota  for the project has been exceeded
            } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }

            // Show a message and update the UI
            Toast.makeText(requireContext(), "Error:"+e, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {

            Log.d(TAG, "onCodeSent:" + verificationId);
            loadingDialog.setMessage("OTP send success");
            loadingDialog.dismiss();

            // Save verification ID and resending token so we can use them later
            // mVerificationId = verificationId;
            //mResendToken = token;
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ValidateOTPFragment.newInstance(verificationId,token))
                    .commitNow();
        }
    };
}