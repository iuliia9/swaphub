package brighton.ac.uk.ic257.swaphub;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ItemList extends ArrayAdapter<Item> {

    private Activity context;
    private List<Item> itemList;

    public ItemList(Activity context, List<Item> itemList) {
        super(context, R.layout.fragment_home);
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_itemslist, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewCategory = listViewItem.findViewById(R.id.textViewCategory);

        Item item = itemList.get(position);
        textViewName.setText(item.getItemName());
        textViewCategory.setText(item.getItemCategory());

        return listViewItem;
    }
}



