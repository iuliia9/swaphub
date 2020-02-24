package brighton.ac.uk.ic257.swaphub;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;

public class CreateFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    EditText editTextName;
    EditText editTextDescription;
    EditText editTextSwapFor;
    EditText editTextUserName;
    EditText editTextUserPhone;
    Spinner spinnerCategory;
    Button buttonAdd;


    Button buttonChoosePhoto;
    ImageView imageView;
    ProgressBar progressBar;
    Uri mImageUri;
    DatabaseReference databaseItems;
    StorageReference storageRef;

    private StorageTask mUploadTask;
    public CreateFragment(){
        // empty constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_create, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextItemDesc);
        editTextSwapFor = view.findViewById(R.id.editTextItemSwapFor);
        editTextUserName = view.findViewById(R.id.editTextUserName);
        editTextUserPhone = view.findViewById(R.id.editTextUserPhone);
        spinnerCategory = view.findViewById(R.id.spinner);
        buttonAdd = view.findViewById(R.id.buttonAdd);

        buttonChoosePhoto = view.findViewById(R.id.buttonChoosePhoto);
        imageView = view.findViewById(R.id.imageView);
        progressBar = view.findViewById(R.id.progressBar);

        buttonChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        storageRef = FirebaseStorage.getInstance().getReference("Items");
        databaseItems = FirebaseDatabase.getInstance().getReference("Items");
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        return view;
    }

    private void addItem(){
        if (mImageUri != null)
        {
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();


            final StorageReference fileRef = storageRef.child("images/"+ UUID.randomUUID().toString());
            UploadTask uploadTask =  fileRef.putBytes(data);

           uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();

                    }
                    Toast.makeText(getActivity(), "success: ", Toast.LENGTH_SHORT).show();
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        Log.e(TAG, "then: " + downloadUri.toString());
                        String id = databaseItems.push().getKey();
                        String name = editTextName.getText().toString().trim();
                        String category = spinnerCategory.getSelectedItem().toString();
                        String description = editTextDescription.getText().toString().trim();
                        String swapfor = editTextSwapFor.getText().toString().trim();
                        String username = editTextUserName.getText().toString().trim();
                        String userphone = editTextUserPhone.getText().toString().trim();
//id
                        Item item = new Item(id, name, category, description,
                                swapfor, username, userphone, downloadUri.toString());

                        databaseItems.child(id).setValue(item);

                        editTextName.setText("");
                        Toast.makeText(getActivity(), "Item added", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        Toast.makeText(getActivity(), "upload failed!!!: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(imageView);
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}
