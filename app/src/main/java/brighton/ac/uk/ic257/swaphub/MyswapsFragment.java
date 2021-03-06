package brighton.ac.uk.ic257.swaphub;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyswapsFragment extends Fragment {
    private DatabaseReference databaseCurrentUser;
    RecyclerView mItems;
    Query query2;
    private FirebaseStorage firebaseStorage;
    Button inquiry;
    LinearLayoutManager mLayoutManager;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myswaps, null);
        // display progress dialog until all items are loaded
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Please Wait...");
        progressDialog.show();
        // get a reference to items
        databaseCurrentUser = FirebaseDatabase.getInstance().getReference("Items");
        // get current user
        firebaseAuth = FirebaseAuth.getInstance();
        String currentUser = firebaseAuth.getCurrentUser().getUid();
        // display items posted by the current user
        query2 = databaseCurrentUser.orderByChild("uid").equalTo(currentUser);
        mItems = view.findViewById(R.id.myrecycleview);
        mItems.setHasFixedSize(true);
        // ensure most recent items are displayed first
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mItems.setLayoutManager(mLayoutManager);

        // firebase recycler adapter
        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(query2, Item.class)
                        .build();
        Log.d("Options"," data : "+options);

        FirebaseRecyclerAdapter<Item, ItemViewHolder>firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>
                (options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {
                holder.setName(model.getItemName());
                holder.setCategory(model.getItemCategory());
                holder.setDescription(model.getItemDescription());
                holder.setSwapFor(model.getItemSwapFor());
                holder.setUserName(model.getSellerName());
                holder.setUserPhone(model.getSellerPhone());
                holder.setUserCity(model.getSellerCity());
                Picasso.get()
                        .load(model.getImageUrl())
                        .fit()
                        .centerCrop()
                        .into(holder.imageView);
                // get avatar photo of the item author
                firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = firebaseStorage.getReference("Avatars");
                storageReference.child(model.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).fit().centerInside().into(holder.userImage);
                    }
                });
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_row, parent, false);
                // remove progress dialog since items have been loaded
                progressDialog.dismiss();
                // remove make an inquiry button
                inquiry = view.findViewById(R.id.button_make_inquiry);
                inquiry.setVisibility(View.GONE);
                return new ItemViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        mItems.setAdapter(firebaseRecyclerAdapter);

    query2.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            // remove progress dialog even when no items in database yet
            progressDialog.dismiss();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
        return view;
    }

    
    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ImageView imageView;
        private CircleImageView userImage;
        // constructor
        public ItemViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.image_view_upload);
            userImage = (CircleImageView) itemView.findViewById(R.id.custom_profile_image);
        }
        public void setName(String name){
            TextView itemName = mView.findViewById(R.id.Name);
            itemName.setText(name);
        }
        public void setCategory(String category){
            TextView itemCategory = mView.findViewById(R.id.Category);
            itemCategory.setText(category);
        }
        public void setDescription(String description){
            TextView itemDescription = mView.findViewById(R.id.Description);
            itemDescription.setText(description);
        }
        public void setSwapFor(String swapfor){
            TextView itemSwapFor = mView.findViewById(R.id.SwapFor);
            itemSwapFor.setText(swapfor);
        }
        public void setUserName(String username){
            TextView itemSwapFor = mView.findViewById(R.id.UserName);
            itemSwapFor.setText(username);
        }
        public void setUserPhone(String userPhone){
            TextView itemSwapFor = mView.findViewById(R.id.UserPhone);
            itemSwapFor.setText(userPhone);
        }
        public void setUserCity(String userCity){
            TextView itemSwapFor = mView.findViewById(R.id.UserCity);
            itemSwapFor.setText(userCity);
        }
    }
}
