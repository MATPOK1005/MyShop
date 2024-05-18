package app.main.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.List;

import app.main.Global;
import app.main.R;
import app.main.controller.Controller;
import app.main.database.structure.Structure;

public class OrdersListAdapter extends BaseAdapter {
    Context context;
    List<String> itemIds;

    public OrdersListAdapter(Context context, List<String> itemIds) {
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
        return Long.parseLong(itemIds.get(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.orders_adapter_row, viewGroup, false);
        }

        Cursor data = Controller.select(Structure.ORDERS, new String[]{"id","date","items","delivery_address","delivery_name","price"}, "id = ?", new String[]{itemIds.get(i)},null, null,null);
        data.moveToFirst();

        StringBuilder itemsString = new StringBuilder(context.getString(R.string.items));

        JSONArray items;
        try {
            items = new JSONArray(data.getString(2));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        for (int j = 0; j < items.length(); j++) {
            Cursor itemData;
            try {
                itemData = Controller.select(Structure.ITEMS, new String[]{"name"}, "id = ?", new String[]{items.getJSONObject(j).getString("id")},null, null,null);
                itemData.moveToFirst();
                itemsString.append("\n").append(items.getJSONObject(j).getString("amount")).append("x ").append(itemData.getString(0));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            itemData.close();
        }


        TextView textViewId = view.findViewById(R.id.ordersadapter_id);
        TextView textViewDate = view.findViewById(R.id.ordersadapter_date);
        TextView textViewItems = view.findViewById(R.id.ordersadapter_items);
        TextView textViewAddress = view.findViewById(R.id.ordersadapter_address);
        TextView textViewName = view.findViewById(R.id.ordersadapter_name);
        TextView textViewPrice = view.findViewById(R.id.ordersadapter_price);

        textViewId.setText(String.format("#: %s", data.getString(0)));
        textViewDate.setText(String.format("%s %s", context.getText(R.string.date), data.getString(1)));
        textViewAddress.setText(String.format("%s %s", context.getText(R.string.address2), data.getString(3)));
        textViewName.setText(String.format("%s %s", context.getText(R.string.name2), data.getString(4)));
        textViewPrice.setText(String.format("%s %s %s", context.getText(R.string.price), data.getString(5), "zł"));
        textViewItems.setText(itemsString);

        return view;
    }
}
