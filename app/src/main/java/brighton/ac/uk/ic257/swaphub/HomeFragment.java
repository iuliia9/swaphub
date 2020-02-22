package brighton.ac.uk.ic257.swaphub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    DatabaseReference databaseItems;
    List<Item> items;
    ListView listViewItems;
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
        items = new ArrayList<>();
        listViewItems = (ListView) view.findViewById(R.id.listViewItems);
        //get reference for the database root items
        databaseItems = FirebaseDatabase.getInstance().getReference("Items");
        return view;}

    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        databaseItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                items.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting item
                    Item item = postSnapshot.getValue(Item.class);
                    //adding artist to the list
                    items.add(item);
                }

                //creating adapter
                ItemList itemAdapter = new ItemList(getActivity(), items);
                //attaching adapter to the listview
                listViewItems.setAdapter(itemAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
