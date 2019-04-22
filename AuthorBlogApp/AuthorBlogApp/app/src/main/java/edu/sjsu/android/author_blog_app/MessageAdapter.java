package edu.sjsu.android.author_blog_app;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<UserMessages> mMessagesList;
    FirebaseAuth auth;
    DatabaseReference databaseReference ;
    Context context;

    //-----GETTING LIST OF ALL MESSAGES FROM CHAT ACTIVITY ----
    public MessageAdapter(List<UserMessages> mMessagesList) {
        this.mMessagesList = mMessagesList;
    }


    //---CREATING SINGLE HOLDER AND RETURNING ITS VIEW---
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout2,parent,false);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        return new MessageViewHolder(view);
    }

    //----RETURNING VIEW OF SINGLE HOLDER----
    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView msgText;
        public TextView displayUserName;
        public TextView displayUserTime;
        public CircleImageView profilePicture;


        public MessageViewHolder(View itemView) {
            super(itemView);

            msgText = (TextView) itemView.findViewById(R.id.message_text_layout);
            displayUserName = (TextView)itemView.findViewById(R.id.name_text_layout);
            displayUserTime = (TextView) itemView.findViewById(R.id.time_text_layout);
            profilePicture = (CircleImageView)itemView.findViewById(R.id.message_profile_layout);

            context = itemView.getContext();

            //---DELETE FUNCTION---
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    CharSequence options[] = new CharSequence[]{ "Delete","Cancel" };
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete this message....");
                    builder.setItems(options,new AlertDialog.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(which == 0){
                                /*
                                        ....CODE FOR DELETING THE MESSAGE IS YET TO BE WRITTEN HERE...
                                 */
                                long mesPos = getAdapterPosition();
                                String messageId = mMessagesList.get((int)mesPos).toString();
                                Log.e("Message Id is ", messageId);
                                Log.e("User Message is : ",mMessagesList.get((int)mesPos).getMessage());

                            }

                            if(which == 1){

                            }

                        }
                    });
                    builder.show();

                    return true;
                }
            });

        }


    }

    //----SETTING EACH HOLDER WITH DATA----
    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {


        // String current_user_id = mAuth.getCurrentUser().getUid();
        UserMessages mes = mMessagesList.get(position);
        String from_user_id = mes.getFrom();
        String message_type = mes.getType();

        //----CHANGING TIMESTAMP TO TIME-----

        long timeStamp = mes.getTime();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        String cal[] = calendar.getTime().toString().split(" ");
        String time_of_message = cal[1]+","+cal[2]+"  "+cal[3].substring(0,5);
        Log.e("TIME IS : ",calendar.getTime().toString());

        holder.displayUserTime.setText(time_of_message);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user_id);

        //---ADDING NAME THUMB_IMAGE TO THE HOLDER----
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                holder.displayUserName.setText(name);
                Picasso.with(holder.profilePicture.getContext()).load(image).
                        placeholder(R.drawable.user_img).into(holder.profilePicture);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.msgText.setText(mes.getMessage());



    }

    //---NO OF ITEMS TO BE ADDED----
    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }
}