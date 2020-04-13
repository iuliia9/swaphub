package brighton.ac.uk.ic257.swaphub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;

public class EditUserProfile extends AppCompatActivity implements View.OnClickListener {
    Button btnsave;
    private FirebaseAuth firebaseAuth;
    private TextView textViewemailname;
    private DatabaseReference databaseReference;
    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextCity;
    private ImageView profileImageView;
    private FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE = 123;
    Uri imagePath;
    private StorageReference storageReference;

    public EditUserProfile() {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        // add a toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar3);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Create Profile");
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), HomeFragment.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextCity = findViewById(R.id.editTextCity);
        btnsave = findViewById(R.id.buttonSubmit);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        btnsave.setOnClickListener(this);
        textViewemailname = findViewById(R.id.TextViewEmailAddress);
        textViewemailname.setText(user.getEmail());
        profileImageView = findViewById(R.id.profile);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Avatars");

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent();
                profileIntent.setType("image/*");
                profileIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(profileIntent, "Select Image."), PICK_IMAGE);
            }
        });
    }

    private void userProfile() {
        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        UserProfile userinformation = new UserProfile(name, surname, city);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(userinformation);
        Toast.makeText(getApplicationContext(), "User information updated", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if (view == btnsave) {
            if (fieldsCompleted() == true) {
                userProfile();
                if (imagePath != null) {
                    sendUserData();
                }
                finish();
                startActivity(new Intent(EditUserProfile.this, HomeActivity.class));
            }
            else{
                Toast.makeText(this, "Please Complete All Fields", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean fieldsCompleted(){
        if (!editTextName.getText().toString().trim().equals("")&&
                !editTextSurname.getText().toString().trim().equals("")&&
                !editTextCity.getText().toString().trim().equals("")){
            return true;
        } else{
            return false;
        }

    }
    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // Get "User UID" from Firebase > Authentification > Users.
        StorageReference imageReference = storageReference.child(firebaseAuth.getUid());
            UploadTask uploadTask = imageReference.putFile(imagePath);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditUserProfile.this, "Error: Uploading profile picture", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditUserProfile.this, "Profile picture uploaded", Toast.LENGTH_SHORT).show();
                }
            });
    }
}

