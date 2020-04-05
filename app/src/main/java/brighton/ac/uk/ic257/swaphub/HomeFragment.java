package brighton.ac.uk.ic257.swaphub;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;



public class HomeFragment extends Fragment {
    private DatabaseReference databaseItems;
    private DatabaseReference databaseGroups;
    RecyclerView mItems;
    Query query1;
    private FirebaseAuth firebaseAuth;
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
        databaseGroups = FirebaseDatabase.getInstance().getReference("Groups");
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
                holder.setUserCity(model.getSellerCity());
                holder.setButton();
                holder.makeInquiry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Please write Chat Name...",
                                Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Enter Chat Name :");
                        final EditText groupNameField = new EditText(getActivity());
                        groupNameField.setHint("e.g Fresh Vegetables");
                        groupNameField.setText(model.getItemName());
                        builder.setView(groupNameField);

                        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                String groupName = groupNameField.getText().toString();

                                if (TextUtils.isEmpty(groupName))
                                {
                                    Toast.makeText(getActivity(), "Please write Chat Name...",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    databaseGroups.child(groupName).setValue("")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(getActivity(), " Chat is Created Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        builder.show();
                    }
                });
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

    public  class ItemViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Button makeInquiry;

        public ImageView imageView;
                public ItemViewHolder(View itemView){
                    super(itemView);
                    mView = itemView;
                    imageView = itemView.findViewById(R.id.image_view_upload);
                }

        public void setButton() {
           makeInquiry = mView.findViewById(R.id.button_make_inquiry);
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
