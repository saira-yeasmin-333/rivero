package com.example.watcher24;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {


    private PreferenceManager preferenceManager;
    public static final String TAG="SignUpActivity";
    private EditText editText1,editText2,editText3,editText4;
    private MaterialButton button;
    private TextView textView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        preferenceManager=new PreferenceManager(getApplicationContext());
        init();
        setListeners();
    }

    private void init() {
        editText1=findViewById(R.id.inputName);
        editText2=findViewById(R.id.inputEmail);
        editText3=findViewById(R.id.inputPassword);
        editText4=findViewById(R.id.inputConfirmPassword);
        button=findViewById(R.id.buttonSignUp);
        textView=findViewById(R.id.textSignIn);
        progressBar=findViewById(R.id.progressbar_up);
    }

    private void setListeners(){
        textView.setOnClickListener(v->onBackPressed());
        button.setOnClickListener(v->{
            if(isValidSignUpDetails()){
                signup();
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signup(){
       loading(true);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        User user=new User(editText1.getText().toString(),
                editText2.getText().toString(),
                editText3.getText().toString());
        database.collection(Constants.KEY_COLLECTION_USERS).add(user).addOnSuccessListener(documentReference -> {
            loading(false);
            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
            preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
            preferenceManager.putString(Constants.KEY_NAME,editText1.getText().toString());
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }).addOnFailureListener(eception->{
            loading(false);
            showToast(eception.getMessage());
        });

    }

    private Boolean isValidSignUpDetails(){
        if(editText1.getText().toString().trim().isEmpty()){
            showToast("Enter Name");
            return false;
        }else if(editText2.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(editText2.getText().toString()).matches()){
            showToast("Enter valid email");
            return false;
        }
        else if(editText3.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        }else if(editText4.getText().toString().trim().isEmpty()){
            showToast("Confirm your password");
            return false;
        }else if(!editText3.getText().toString().equals(editText4.getText().toString())){
            showToast("Password and confirm password must be same");
            return false;
        }else{
            return true;
        }
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            button.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            button.setVisibility(View.VISIBLE);
        }
    }
}