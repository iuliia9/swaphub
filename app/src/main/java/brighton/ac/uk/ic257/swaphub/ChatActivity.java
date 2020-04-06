package brighton.ac.uk.ic257.swaphub;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private String currentGroupName;
    private DatabaseReference user, user2;
    private String messageReceiverID, messageSenderID;
    private ImageButton SendMessageButton;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef, RootRef0;
    private String saveCurrentTime, saveCurrentDate;
    private final List<Messages> messagesList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;
    private LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        // add a toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar3);
        setSupportActionBar(myToolbar);
        // enable the back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // set title for toolbar
        getSupportActionBar().setTitle(currentGroupName);
        user = FirebaseDatabase.getInstance().getReference("Groups").child(currentGroupName);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("uid")) {
                    messageReceiverID = dataSnapshot.child("uid").getValue().toString();
                    Toast.makeText(ChatActivity.this, messageReceiverID, Toast.LENGTH_SHORT).show();

                    RootRef = FirebaseDatabase.getInstance().getReference().child("Groups");

            if (dataSnapshot.hasChild("Messages")) {
                Toast.makeText(ChatActivity.this, "heello", Toast.LENGTH_SHORT).show();
                user2 = RootRef.child(currentGroupName).child("Messages").child(messageSenderID).child(messageReceiverID);
//        user2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild("Messages")) {
            //}
                ///////////////////////////////// this is not working for receiving messages!
                    user2.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s)
                        {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            Toast.makeText(ChatActivity.this, "hello", Toast.LENGTH_SHORT).show();
                            messagesList.add(messages);

                            messageAdapter.notifyDataSetChanged();

                            userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                        }
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



            }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });
    }

    private void SendMessage() {
        String messageText = MessageInputText.getText().toString();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        } else {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;
            RootRef = FirebaseDatabase.getInstance().getReference().child("Groups");
            DatabaseReference userMessageKeyRef = RootRef.child(currentGroupName)
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.child(currentGroupName).updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });
        }
    }
    @Override
    protected void onStart()
    {
        super.onStart();
//        RootRef = FirebaseDatabase.getInstance().getReference().child("Groups");
//        user2 = RootRef.child(currentGroupName).child("Messages").child(messageSenderID);
//
////        user2.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                if (dataSnapshot.hasChild("Messages")) {
//        user2 = user2.child("LFx4RiAR3KX80aYF4yZSuLMqTcJ3");
//                    Toast.makeText(ChatActivity.this, "Heey", Toast.LENGTH_SHORT).show();
//                    user2.addChildEventListener(new ChildEventListener() {
//                        @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
//                    {
//                        Toast.makeText(ChatActivity.this, "Hey", Toast.LENGTH_SHORT).show();
//                        Messages messages = dataSnapshot.getValue(Messages.class);
//
//                        messagesList.add(messages);
//
//                        messageAdapter.notifyDataSetChanged();
//
//                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
//                    }
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                    });
                    }

            }

//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//                }
//    }
//}

