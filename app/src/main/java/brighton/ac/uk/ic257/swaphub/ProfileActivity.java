package brighton.ac.uk.ic257.swaphub;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity{
    private DatabaseReference databaseReference;
    private TextView profileNameTextView, profileSurnameTextView, profilePhonenoTextView;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ImageView profilePicImageView;
    private FirebaseStorage firebaseStorage;
    private TextView textViewemailname;
    private Button btnSave;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        profilePicImageView = findViewById(R.id.profile);
        profileNameTextView = findViewById(R.id.profile_name_textView);
        profileSurnameTextView = findViewById(R.id.profile_surname_textView);
        profilePhonenoTextView = findViewById(R.id.profile_city_textView);
        btnSave = findViewById(R.id.button);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());
        StorageReference storageReference = firebaseStorage.getReference("Avatars");
        storageReference.child(firebaseAuth.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerInside().into(profilePicImageView);
            }
        });
//        if (firebaseAuth.getCurrentUser() == null){
//            view.finish();
//            startActivity(new Intent(getApplicationContext(),SignInActivity.class));
//        }

        final FirebaseUser user=firebaseAuth.getCurrentUser();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                System.out.println("onChange");
                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                profileNameTextView.setText(userProfile.getUserName());
                profileSurnameTextView.setText(userProfile.getUserSurname());
                profilePhonenoTextView.setText(userProfile.getUserCity());
                textViewemailname=findViewById(R.id.textViewEmailAdress);
                textViewemailname.setText(user.getEmail());
            }
            @Override
            public void onCancelled( DatabaseError databaseError) {
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = profileNameTextView.getText().toString();
                String surname = profileSurnameTextView.getText().toString();
                String phoneno =  profilePhonenoTextView.getText().toString();
                UserProfile userinformation = new UserProfile(name,surname, phoneno);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                databaseReference.setValue(userinformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("Completed FireBase Profile Update");
                        System.out.println("Is successful: " + task.isSuccessful());
                    }
                });
            }
        });
    }


}
