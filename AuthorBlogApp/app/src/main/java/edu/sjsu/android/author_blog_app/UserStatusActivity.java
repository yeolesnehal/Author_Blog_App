package edu.sjsu.android.author_blog_app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserStatusActivity extends AppCompatActivity {
    TextInputLayout inputLayout;
    Button submit;
    DatabaseReference databaseReference;
    FirebaseUser currUser;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);

        //CHANGE TITLE AND CODE FOR BACK BUTTON
        this.setTitle("Change the Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        String uID = currUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uID);
        inputLayout = findViewById(R.id.inputStatus);
        submit = findViewById(R.id.Submit);

        //RETAIN PREVIOUS STATUS AS DEFAULT STATUS
        String currStatus = getIntent().getStringExtra("user_current_status");
        inputLayout.getEditText().setText(currStatus);
    }

    public void onClicked(View view){
        String userStatus = inputLayout.getEditText().getText().toString();
        if(TextUtils.isEmpty(userStatus)){
            Toast.makeText(UserStatusActivity.this, "Please enter the status...", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setTitle("Status Update in progress...");
        progressDialog.setMessage("Please wait while your status is updating...");
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        //UPDATING THE STATUS IN DATABASE
        databaseReference.child("status").setValue(userStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(UserStatusActivity.this, "Your Status updated successfully...", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(UserStatusActivity.this, "Failed to update the status", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
    }


}
