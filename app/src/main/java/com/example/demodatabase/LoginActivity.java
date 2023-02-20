package com.example.demodatabase;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    EditText editText_gmail;
    EditText editText_password;
    Button btn_login;
    SignInButton btn_googleLogin;
    TextView textView_signUpLink;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    TextView tv_forgotPassword;
    private static final String TAG = "GoogleActivity";
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    firebaseAuthWithGoogle(result);
                }
            }
    );



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        init();
        bindingEvents();


    }

    private void bindingEvents(){
        btn_login.setOnClickListener(view -> login());
        textView_signUpLink.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        btn_googleLogin.setOnClickListener(view -> signInWithGoogle());
        tv_forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    void init() {
        editText_gmail = findViewById(R.id.edt_gmail);
        editText_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_signUp);
        textView_signUpLink = findViewById(R.id.textView_signup);
        btn_googleLogin = findViewById(R.id.btn_googleLogIn);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        tv_forgotPassword = findViewById(R.id.tv_forgotPassword);
        btn_googleLogin.setSize(SignInButton.SIZE_WIDE);
    }


    void login() {
        String gmail = editText_gmail.getText().toString().trim();
        String password = editText_password.getText().toString().trim();

        boolean validGmail = true,
                validPassword = true;
        if(gmail.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(gmail).matches()){
            editText_gmail.setError("Please enter valid email");
            validGmail = false;
        }else if(password.isEmpty()){
            editText_password.setError("Please enter valid password");
            validPassword = false;
        }
        if (!validGmail || !validPassword )
            return;

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(gmail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if(firebaseUser.isEmailVerified()){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            }else{
                                Toast.makeText(LoginActivity.this, "Your account has not been verified !", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong gmail or password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();

        if (result.getResultCode() == RESULT_OK) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            finishAffinity();
            startActivity(intent);
        } else {
            Toast.makeText(this, "Can't sign in", Toast.LENGTH_SHORT).show();

        }

    }
    private void signInWithGoogle() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(  new AuthUI.IdpConfig.GoogleBuilder().build());
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }
}