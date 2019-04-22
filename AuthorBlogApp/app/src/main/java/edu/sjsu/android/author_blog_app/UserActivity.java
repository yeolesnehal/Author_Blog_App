package edu.sjsu.android.author_blog_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {
    RecyclerView usersList;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        usersList = findViewById(R.id.recyclerViewUsersList);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);

    }

    protected void onStart(){
        super.onStart();

        //FIREBASE RECYCLE VIEW ADAPTER
        FirebaseRecyclerAdapter<Users , UserViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter <Users, UserViewHolder>(
                Users.class,
                R.layout.recycle_list_single_user,
                UserViewHolder.class,
                databaseReference
        ){

            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, Users users, int position) {
                viewHolder.setName(users.getName());
                viewHolder.setStatus(users.getStatus());
                viewHolder.setImage(users.getThumbImage(),getApplicationContext());
                final String user_id = getRef(position).getKey();

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(UserActivity.this, ChatActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        usersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View view;
        public UserViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name){
            TextView userNameView = view.findViewById(R.id.textViewSingleListName);
            userNameView.setText(name);
        }

        public void setStatus(String status){
            TextView userStatusView = view.findViewById(R.id.textViewSingleListStatus);
            userStatusView.setText(status);
        }

        public void setImage(String thumb_image, Context context){
            CircleImageView userImageView = view.findViewById(R.id.circleImageViewUserImage);
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.user_img).into(userImageView);

        }
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

}
