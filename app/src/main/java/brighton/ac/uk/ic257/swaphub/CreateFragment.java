package brighton.ac.uk.ic257.swaphub;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.google.firebase.auth.FirebaseAuth;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import static android.app.Activity.RESULT_OK;
import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;

public class CreateFragment extends Fragment implements  EasyPermissions.PermissionCallbacks{

    EditText editTextName;
    EditText editTextDescription;
    EditText editTextSwapFor;
    EditText editTextUserName;
    EditText editTextUserPhone;
    EditText editTextUserCity;
    Spinner spinnerCategory;
    Button buttonAdd;
    private FirebaseAuth firebaseAuth;
    Button buttonChoosePhoto;
    Button buttonTakePhoto;
    ImageView imageView;
    Uri mImageUri;
    DatabaseReference databaseItems;
    StorageReference storageRef;
    Uri image;
    String CameraFile;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    static final int PICK_IMAGE_REQUEST = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    public CreateFragment() {
        // required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        // initialise fields
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());
        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextItemDesc);
        editTextSwapFor = view.findViewById(R.id.editTextItemSwapFor);
        editTextUserName = view.findViewById(R.id.editTextUserName);
        editTextUserPhone = view.findViewById(R.id.editTextUserPhone);
        editTextUserCity = view.findViewById(R.id.editTextUserCity);
        spinnerCategory = view.findViewById(R.id.spinner);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        buttonChoosePhoto = view.findViewById(R.id.buttonChoosePhoto);
        buttonTakePhoto = view.findViewById(R.id.buttonTakePhoto);
        imageView = view.findViewById(R.id.imageView);

        // pre-fill name and city fields
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                editTextUserName.setText(userProfile.getUserName());
                editTextUserCity.setText(userProfile.getUserCity());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    openCamera();
                }
                else{
                    dispatchTakePictureIntent();
                }
            }
        });

        buttonChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check API version because of different ways permissions work
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    openGallery();
                }
                else {
                    openFileChooser();
                }
            }
        });

        storageRef = FirebaseStorage.getInstance().getReference("Items");
        databaseItems = FirebaseDatabase.getInstance().getReference("Items");

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allFieldsComplete() == true) {
                    // display progress dialog while the item is being added
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Loading Please Wait...");
                    progressDialog.show();
                    addItem();
                }
                else{
                    Toast.makeText(getActivity(), "Please Complete All Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    // check all fields are completed
    public boolean allFieldsComplete(){
        if (!editTextName.getText().toString().trim().equals("") &&
                !editTextDescription.getText().toString().trim().equals("") &&
                !editTextSwapFor.getText().toString().trim().equals("") &&
                !editTextUserName.getText().toString().trim().equals("")&&
                !editTextUserPhone.getText().toString().trim().equals("")&&
                !editTextUserCity.getText().toString().trim().equals("")&&
                (mImageUri != null || image != null)){
            return true;
        }
        else{
            return false;
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//          File where the photo goes
        File outFile = null;
        try {
            outFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            // Error occurred while creating the File
        }
        CameraFile = outFile.toString();
        Uri outuri = FileProvider.getUriForFile(
                getContext(),
                getContext().getApplicationContext()
                        .getPackageName() + ".provider", outFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                mImageUri = data.getData();
                Picasso.get().load(mImageUri).into(imageView);
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (data != null) {
                    image = data.getData();
                    Picasso.get().load(image).into(imageView);
                }
                if (image == null && CameraFile != null) {
                    image = Uri.fromFile(new File(CameraFile));
                    Picasso.get().load(image).into(imageView);
                }
                File file = new File(CameraFile);
                if (!file.exists()) {
                    file.mkdir();
                }
            }
        }
    }


    /**
     * Create file with current timestamp name
     *
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }


    private void addItem() {
        if (mImageUri != null || image != null) {
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            final StorageReference fileRef = storageRef.child("images/" + UUID.randomUUID().toString());
            UploadTask uploadTask = fileRef.putBytes(data);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e(TAG, "then: " + downloadUri.toString());
                        String id = databaseItems.push().getKey();
                        String name = editTextName.getText().toString().trim();
                        String category = spinnerCategory.getSelectedItem().toString();
                        String description = editTextDescription.getText().toString().trim();
                        String swapfor = editTextSwapFor.getText().toString().trim();
                        String username = editTextUserName.getText().toString().trim();
                        String userphone = editTextUserPhone.getText().toString().trim();
                        String usercity = editTextUserCity.getText().toString().trim();
                        String uid = firebaseAuth.getInstance().getCurrentUser().getUid();
                        Item item = new Item(id, name, uid, category, description,
                                swapfor, username, userphone, usercity, downloadUri.toString());

                        databaseItems.child(id).setValue(item);

                        // clear all fields
                        editTextName.setText("");
                        editTextDescription.setText("");
                        editTextSwapFor.setText("");
                        editTextUserPhone.setText("");
                        editTextUserCity.setText("");
                        imageView.setImageBitmap(null);
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Item added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "upload failed!!!: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(123)
    private void openCamera() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            dispatchTakePictureIntent();
            Toast.makeText(getActivity(), "Opening camera", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "We need permissions to take a photo",
                    123, perms);
        }
    }
    @AfterPermissionGranted(124)
    private void openGallery() {
        String perms = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            openFileChooser();
            Toast.makeText(getActivity(), "Opening gallery", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "We need permissions to pick a " +
                            "photo from your gallery",
                    124, perms);
        }
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

}
