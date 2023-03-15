package com.example.demodatabase;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demodatabase.metadata.Role;
import com.example.demodatabase.model.User;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    EditText editText_gmail;
    EditText editText_password;
    Button btn_login;
    SignInButton btn_googleLogin;
    TextView textView_signUpLink;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    TextView tv_forgotPassword;
    FirebaseUser currentUser;
    FirebaseFirestore database;
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

    private void bindingEvents() {

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
        database = FirebaseFirestore.getInstance();
    }


    void login() {
        String gmail = editText_gmail.getText().toString().trim();
        String password = editText_password.getText().toString().trim();

        boolean validGmail = true,
                validPassword = true;
        if (gmail.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(gmail).matches()) {
            editText_gmail.setError("Please enter valid email");
            validGmail = false;
        } else if (password.isEmpty()) {
            editText_password.setError("Please enter valid password");
            validPassword = false;
        }
        if (!validGmail || !validPassword)
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
                            if (firebaseUser.isEmailVerified()) {
                                database.collection("users")
                                        .document(firebaseUser.getEmail())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                                                User user = task.getResult().toObject(User.class);
                                                if(user.getRole() == Role.ADMIN_ROLE){
                                                    Intent intent = new Intent(LoginActivity.this, HomeForAdminActivity.class);
                                                    finishAffinity();
                                                    startActivity(intent);
                                                }else if(user.getRole() == Role.USER_ROLE){
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    finishAffinity();
                                                    startActivity(intent);
                                                }
                                            }
                                        });



                            } else {
                                FancyToast.makeText(getApplicationContext(), "Your account has not been verified !", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                            }
                        } else {
                            FancyToast.makeText(getApplicationContext(), "Wrong gmail or password", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();

        if (result.getResultCode() == RESULT_OK) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
           database.collection("users")
                    .document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                       @Override
                       public void onComplete( Task<DocumentSnapshot> task) {
                           if (task.getResult().getData() == null) {
                               User userFireStorage = new User();
                               userFireStorage.setEmail(currentUser.getEmail());
                               userFireStorage.setGoogleAccount(true);
                               userFireStorage.setDisplayName(currentUser.getEmail());
                               userFireStorage.setPassword("");
                               userFireStorage.setCreatedDate(new Date());
                               database.collection("users").document(currentUser.getEmail()).set(userFireStorage);
                           }else {
                               User user = task.getResult().toObject(User.class);
                               if (user.getBan()){
                                   new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                           .setTitleText("Banning notification")
                                           .setContentText("You have been banned from the system, please contact our admin through: toiyeutaixiuvobeben@gmail.com")
                                           .show();
                                   return;
                               }
                               if(user.getRole() == Role.USER_ROLE){
                                   Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                   finishAffinity();
                                   startActivity(intent);
                               }else if (user.getRole() == Role.ADMIN_ROLE){
                                   Intent intent = new Intent(LoginActivity.this, HomeForAdminActivity.class);
                                   finishAffinity();
                                   startActivity(intent);
                               }
                           }
                       }
                   });



        } else {
            FancyToast.makeText(this, "Can't sign in", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }

    }

    private void signInWithGoogle() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }
}