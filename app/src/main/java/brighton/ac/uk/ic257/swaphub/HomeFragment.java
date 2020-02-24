package brighton.ac.uk.ic257.swaphub;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


public class HomeFragment extends Fragment {
    private DatabaseReference databaseItems;
    RecyclerView mItems;
    Query query1;
    private ProgressDialog progressDialog;
    FirebaseRecyclerAdapter<Item, ItemViewHolder> firebaseRecyclerAdapter;
    LinearLayoutManager mLayoutManager;

    public HomeFragment(){
        // empty constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Please Wait...");
        progressDialog.show();

        databaseItems = FirebaseDatabase.getInstance().getReference("Items");
//        databaseItems.keepSynced(true);
        query1 = FirebaseDatabase.getInstance().getReference().child("Items");

        mItems = view.findViewById(R.id.myrecycleview);
        mItems.setHasFixedSize(true);
        mItems.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(databaseItems, Item.class)
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
                return new ItemViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        mItems.setAdapter(firebaseRecyclerAdapter);
        return view;
    }

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
    }
}
