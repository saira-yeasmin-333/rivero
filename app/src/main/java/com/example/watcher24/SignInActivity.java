package com.example.watcher24;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.watcher24.model.User;
import com.example.watcher24.util.Constants;
import com.example.watcher24.util.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    public static final String TAG="SignInActivity";
    private EditText editText1,editText2;
    private MaterialButton button;
    private TextView textView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        preferenceManager=new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        init();
        setListeners();
    }

    private void init() {
        editText1=findViewById(R.id.inputEmail_in);
        editText2=findViewById(R.id.inputPassword_in);
        button=findViewById(R.id.buttonSignIn);
        textView=findViewById(R.id.textCreateNewAccount);
        progressBar=findViewById(R.id.progressBar_in);
    }

    private void setListeners(){
        textView.setOnClickListener(v->startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
        button.setOnClickListener(v->{
            if(isValidSignInDetails()){
                signIn();
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signIn(){
        loading(true);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo(Constants.KEY_EMAIL,editText1.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,editText2.getText().toString()).get()
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()&&task.getResult()!=null&& task.getResult().getDocuments().size()>0){
                        Log.d(TAG, "signIn: task successful");
                        DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                        User user=documentSnapshot.toObject(User.class);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME,user.getName());
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        Log.d(TAG, "signIn: unsuccessful"+task.getException());
                        loading(false);

                        showToast("Unable to sign in");
                    }
                });

    }

    private void loading(Boolean isLoading){
        if(isLoading){
            button.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            button.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private Boolean isValidSignInDetails(){
        if(editText1.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(editText1.getText().toString()).matches()){
            showToast("Enter valid email");
            return false;
        }
        else if(editText2.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        }else{
            return true;
        }
    }
}