package edu.sjsu.android.author_blog_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout userName,email,password;
    Button buttonSubmit;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.enterUserName);
        email = findViewById(R.id.enterEmail);
        password = findViewById(R.id.enterPassword);
        buttonSubmit = findViewById(R.id.createAccount);
        progressDialog = new ProgressDialog(RegisterActivity.this);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    //REGISTER BUTTON IS CLICKED
    public void buttonIsClicked(View view){
        if(view.getId()==R.id.createAccount){
            String displayName = userName.getEditText().getText().toString().trim();
            final String emailAddress = email.getEditText().getText().toString().trim();
            String userPassword = password.getEditText().getText().toString().trim();

            //CHECKING IF THE CREDENTIALS FIELDS ARE EMPTY
            if(displayName.equals("") || emailAddress.equals("") || userPassword.equals("")){
                Toast.makeText(RegisterActivity.this, "All the fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if(userPassword.length()<6){
                Toast.makeText(RegisterActivity.this, "Password is too short", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.setTitle("Registering User");
            progressDialog.setMessage("Please wait while we are creating your account... ");
            progressDialog.setCancelable(true);
            progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            register_user(displayName,emailAddress,userPassword);
        }
    }

    //REGISTERING NEW USERS
    private void register_user(final String userName, final String email, String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //------IF USER IS SUCCESSFULLY REGISTERED-----
                if(task.isSuccessful()){

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    final String uid=current_user.getUid();
                    String token_id = FirebaseInstanceId.getInstance().getToken();
                    Log.d("Token", token_id);
                    Map userMap=new HashMap();
                    userMap.put("device_token",token_id);
                    userMap.put("name",userName);
                    userMap.put("status","Hello World");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default");
                    userMap.put("email",email);
                    userMap.put("online","true");

                    databaseReference.child(uid).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if(task1.isSuccessful()){

                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Account is created successfully", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);

                                //----REMOVING THE LOGIN ACTIVITY FROM THE QUEUE----
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();



                            }
                            else{

                                Toast.makeText(RegisterActivity.this, "YOUR NAME IS NOT REGISTERED... MAKE NEW ACCOUNT-- ", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


                }
                //---ERROR IN CREATING ACCOUNT OF NEW USER---
                else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "ERROR REGISTERING USER.... ACCOUNT ALREADY EXISTS !!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
