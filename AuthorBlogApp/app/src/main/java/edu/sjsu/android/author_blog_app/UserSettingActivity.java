package edu.sjsu.android.author_blog_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UserSettingActivity extends AppCompatActivity {
    CircleImageView circleImageView;
    TextView userName;
    TextView status;
    Button changePhoto, changeStatus;
    ProgressDialog progressDialog;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    String userStatus = "";

    private static final int IMAGE_REQUEST = 1;
    String uID;
    byte[] bytes=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        circleImageView = findViewById(R.id.displayImage);
        userName = findViewById(R.id.viewDisplayName);
        status = findViewById(R.id.viewStatus);
        changePhoto = findViewById(R.id.changeImageButton);
        changeStatus = findViewById(R.id.changeStatusButton);
        progressDialog = new ProgressDialog(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uID = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uID);
        databaseReference.keepSynced(true);

        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String profileUserName = (String)dataSnapshot.child("name").getValue();
                String userStatus = (String)dataSnapshot.child("status").getValue();
                final String userImage = (String)dataSnapshot.child("image").getValue();
                String thumb = (String)dataSnapshot.child("thumb_image").getValue();

                userName.setText(profileUserName);
                status.setText(userStatus);

                if(!userImage.equals("default"))
                    Picasso.with(UserSettingActivity.this).load(userImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user_img).into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(UserSettingActivity.this).load(userImage).placeholder(R.drawable.user_img).into(circleImageView);
                        }
                    });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void buttonIsClicked(View view){
        switch (view.getId()){
            case R.id.changeImageButton:
                openImage();
                break;

            case R.id.changeStatusButton:
                Intent intent = new Intent(UserSettingActivity.this,UserStatusActivity.class);
                intent.putExtra("user_current_status", userStatus);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    public void openImage(){
        Log.d("Message", "Inside openImage method...");
        Intent userGalleryIntent = new Intent();
        userGalleryIntent.setType("image/*");
        userGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(userGalleryIntent,"Select Image"),IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data){
        super.onActivityResult(reqCode, resCode, data);
        Log.d("***Activity Result***", "Inside onActivityResult method...");

        //OPEN GALLERY
        if(reqCode == IMAGE_REQUEST && resCode == RESULT_OK){
            Uri srcUri = data.getData();

            //IMAGE CROPPING
            CropImage.activity(srcUri).setAspectRatio(1,1)
                    .setMinCropWindowSize(500, 500)
                    .start(UserSettingActivity.this);
        }

        //START CROPPING IMAGE
        if(reqCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult cropResult = CropImage.getActivityResult(data);

            if(resCode == RESULT_OK){
                progressDialog.setTitle("Uploading the Image...");
                progressDialog.setMessage("Please wait while the image is getting uploaded !!");
                progressDialog.setCancelable(false);
                progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                final Uri resUri = cropResult.getUri();
                File filePath = new File(resUri.getPath());
                circleImageView.setImageURI(resUri);

                try{
                    Bitmap bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(filePath);
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
                    bytes = b.toByteArray();

                }catch (IOException exception){
                    exception.printStackTrace();
                }

                StorageReference pathFile = storageReference.child("profile_picture").child(uID+".jpg");
                final StorageReference thumbFilePath = storageReference.child("profile_picture").child("thumb").child(uID+".jpg");


                //CODE TO STORE THE IMAGE IN FIREBASE STORAGE
                pathFile.putFile(resUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful() && task != null){
                            //@SuppressWarnings("VisibleForTests")
                            final String downloadImageURL = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                            final UploadTask uploadTask = thumbFilePath.putBytes(bytes);

                            //STORE THUMB IMAGE INTO STORAGE REFERENCE
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task2) {
                                    @SuppressWarnings("VisibleForTests")

                                    String thumbImageDownloadURL = task2.getResult().getMetadata().getReference().getDownloadUrl().toString();
                                    if(task2.isSuccessful()){
                                        Map userMap = new HashMap();
                                        userMap.put("image", downloadImageURL);
                                        userMap.put("thumb_image", thumbImageDownloadURL);

                                        //UPDATING URL TO DATABASE
                                        databaseReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    progressDialog.dismiss();
                                                    Toast.makeText(UserSettingActivity.this, "Image uploaded successfully...", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Cannot upload the Image", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });

                                    }else{
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Cannot upload thumbnail image...", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error in uploading the image...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}
