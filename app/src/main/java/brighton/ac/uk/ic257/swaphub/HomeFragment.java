package brighton.ac.uk.ic257.swaphub;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class HomeFragment extends Fragment {
    private DatabaseReference databaseItems;
    RecyclerView mItems;

    Query query1;
    private ProgressDialog progressDialog;
    FirebaseRecyclerAdapter<Item, ItemViewHolder> firebaseRecyclerAdapter;
    LinearLayoutManager mLayoutManager;


//    List<Item> items;
//    ListView listViewItems;
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
        //list to store items
//        items = new ArrayList<>();
//        listViewItems = (ListView) view.findViewById(R.id.listViewItems);
        //get reference for the database root items
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Please Wait...");
        progressDialog.show();


        databaseItems = FirebaseDatabase.getInstance().getReference("Items");
//        databaseItems.keepSynced(true);
    query1 = FirebaseDatabase.getInstance().getReference().child("Items");

        mItems = (RecyclerView)view.findViewById(R.id.myrecycleview);
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
                public ItemViewHolder(View itemView){
                    super(itemView);
                    mView = itemView;
                }
                public void setName(String name){
                    TextView itemName = mView.findViewById(R.id.Name);
                    itemName.setText(name);
                }
                public void setCategory(String category){
                    TextView itemCategory = mView.findViewById(R.id.Category);
                    itemCategory.setText(category);
                }
    }
}
