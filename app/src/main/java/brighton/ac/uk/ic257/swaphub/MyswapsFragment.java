package brighton.ac.uk.ic257.swaphub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyswapsFragment extends Fragment {
    private DatabaseReference databaseCurrentUser;
    RecyclerView mItems;
    Query query2;
    Button inquiry;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    FirebaseRecyclerAdapter<Item,ItemViewHolder> firebaseRecyclerAdapter;
    LinearLayoutManager mLayoutManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myswaps, null);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Please Wait...");
        progressDialog.show();
        databaseCurrentUser = FirebaseDatabase.getInstance().getReference("Items");
//        databaseItems.keepSynced(true);
        firebaseAuth = FirebaseAuth.getInstance();
        String currentUser = firebaseAuth.getCurrentUser().getUid();
        query2 = databaseCurrentUser.orderByChild("uid").equalTo(currentUser);
        mItems = view.findViewById(R.id.myrecycleview);
        mItems.setHasFixedSize(true);
        mItems.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                Picasso.get()
                        .load(model.getImageUrl())
                        .fit()
                        .centerCrop()
                        .into(holder.imageView);
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_row, parent, false);
                progressDialog.dismiss();
                inquiry = view.findViewById(R.id.button_make_inquiry);
                inquiry.setVisibility(View.GONE);
                return new ItemViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        mItems.setAdapter(firebaseRecyclerAdapter);

//        Toolbar myToolbar = view.findViewById(R.id.toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Swaps");
        return view;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;
//
//            case R.id.action_logout:
//                // User chose the "Favorite" action, mark the current item
//                // as a favorite...
//                Toast.makeText(getActivity(),"You are logged out", Toast.LENGTH_SHORT).show();
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getActivity(), MainActivity.class));
//                return true;
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//    }
    
    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ImageView imageView;
        public ItemViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
        public void setName(String name){
            TextView itemName = mView.findViewById(R.id.Name);
            itemName.setText(name);
        }
        public void setCategory(String category){
            TextView itemCategory = mView.findViewById(R.id.Category);
            itemCategory.setText("Category: " + category);
        }
        public void setDescription(String description){
            TextView itemDescription = mView.findViewById(R.id.Description);
            itemDescription.setText("Description: " + description);
        }
        public void setSwapFor(String swapfor){
            TextView itemSwapFor = mView.findViewById(R.id.SwapFor);
            itemSwapFor.setText("Swap For: " + swapfor);
        }
        public void setUserName(String username){
            TextView itemSwapFor = mView.findViewById(R.id.UserName);
            itemSwapFor.setText("Contact Name: " + username);
        }
        public void setUserPhone(String userPhone){
            TextView itemSwapFor = mView.findViewById(R.id.UserPhone);
            itemSwapFor.setText("Phone: " + userPhone);
        }
    }
}
