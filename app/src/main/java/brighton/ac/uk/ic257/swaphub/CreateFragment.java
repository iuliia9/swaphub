package brighton.ac.uk.ic257.swaphub;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateFragment extends Fragment {

    EditText editTextName;
    EditText editTextDescription;
    EditText editTextSwapFor;
    EditText editTextUserName;
    EditText editTextUserPhone;
    Spinner spinnerCategory;
    Button buttonAdd;
    DatabaseReference databaseItems;
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
        String name = editTextName.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String description = editTextDescription.getText().toString().trim();
        String swapfor = editTextSwapFor.getText().toString().trim();
        String username = editTextUserName.getText().toString().trim();
        String userphone = editTextUserPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(name)){
           String id = databaseItems.push().getKey();
           Item item = new Item(id, name, category, description, swapfor, username, userphone);
           databaseItems.child(id).setValue(item);
            //setting edittext to blank again
            editTextName.setText("");
           Toast.makeText(getActivity(), "Item added", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity(), "Enter Name", Toast.LENGTH_SHORT).show();
        }
    }
}