package brighton.ac.uk.ic257.swaphub;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.google.firebase.auth.FirebaseAuth;
import static android.app.Activity.RESULT_OK;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;

public class CreateFragment extends Fragment {

    EditText editTextName;
    EditText editTextDescription;
    EditText editTextSwapFor;
    EditText editTextUserName;
    EditText editTextUserPhone;
    Spinner spinnerCategory;
    Button buttonAdd;
    private FirebaseAuth firebaseAuth;

    Button buttonChoosePhoto;
    Button buttonTakePhoto;
    ImageView imageView;
    ProgressBar progressBar;
    Uri mImageUri;
    DatabaseReference databaseItems;
    StorageReference storageRef;
    Uri image;
    String CameraFile;
    static final int PICK_IMAGE_REQUEST = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
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
        buttonTakePhoto = view.findViewById(R.id.buttonTakePhoto);
        // if the user does not grant permission to use camera then disable the button
//        if (ContextCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            buttonTakePhoto.setEnabled(false);
//            ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.CAMERA,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
//        }
        imageView = view.findViewById(R.id.imageView);
        progressBar = view.findViewById(R.id.progressBar);
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
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
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//          File where the photo goes
            File outFile = null;
            try {
             outFile= createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            CameraFile = outFile.toString();
            Uri outuri = FileProvider.getUriForFile(
                getContext(),
                getContext().getApplicationContext()
                        .getPackageName() + ".provider", outFile);
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(getActivity(),
//                        BuildConfig.APPLICATION_ID + ".provider",
//                        photoFile);
//                mPhotoFile = photoFile;
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
            if (requestCode == PICK_IMAGE_REQUEST){
                // && resultCode == RESULT_OK && data != null && data.getData() != null) {
                mImageUri = data.getData();
                Picasso.get().load(mImageUri).into(imageView);
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (data != null) {
                    image = data.getData();
//                    imageView.setImageURI(image);
//                    imageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(image).into(imageView);
                }
                if (image == null && CameraFile != null) {
                    image = Uri.fromFile(new File(CameraFile));
//                    imageView.setImageURI(image);
//                    imageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(image).into(imageView);
                }
//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                imageView.setImageBitmap(imageBitmap);
                File file = new File(CameraFile);
                if (!file.exists()) {
                    file.mkdir();
                }
            }
        }
    }


    /**
     * Create file with current timestamp name
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

    /**
     * Get real file path from URI
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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
                        String uid = firebaseAuth.getInstance().getCurrentUser().getUid();
                        Item item = new Item(id, name, uid, category, description,
                                swapfor, username, userphone, downloadUri.toString());

                        databaseItems.child(id).setValue(item);
                        // clear all fields
                        editTextName.setText("");
                        editTextDescription.setText("");
                        editTextSwapFor.setText("");
                        editTextUserName.setText("");
                        editTextUserPhone.setText("");
                        imageView.setImageBitmap(null);
                        Toast.makeText(getActivity(), "Item added", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        Toast.makeText(getActivity(), "upload failed!!!: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
