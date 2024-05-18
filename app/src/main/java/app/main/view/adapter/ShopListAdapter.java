package app.main.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.main.R;
import app.main.controller.Controller;
import app.main.controller.Converter;
import app.main.database.structure.Structure;

public class ShopListAdapter extends BaseAdapter {

    Context context;
    List<String> itemIds;

    public ShopListAdapter(Context context, List<String> itemIds) {
        this.context = context;
        this.itemIds = itemIds;
    }

    @Override
    public int getCount() {
        return itemIds.size();
    }

    @Override
    public Object getItem(int i) {
        return itemIds.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.shop_adapter_row, viewGroup, false);
        }

        Cursor data = Controller.select(Structure.ITEMS, new String[]{"id", "name", "price", "picture"}, "id = ?", new String[]{itemIds.get(i)}, null, null,null);
        data.moveToFirst();

        TextView textViewName = view.findViewById(R.id.shopadapter_name);
        TextView textViewPrice = view.findViewById(R.id.shopadapter_price);
        ImageView imageView = view.findViewById(R.id.shopadapter_image);

        textViewName.setText(data.getString(1));
        textViewPrice.setText(data.getString(2) + " zł");
        imageView.setImageBitmap(Converter.base64ToBitmap(data.getString(3)));

        data.close();

        return view;
    }
}
