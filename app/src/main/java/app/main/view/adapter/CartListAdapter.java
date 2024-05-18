package app.main.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import app.main.Global;
import app.main.R;
import app.main.controller.Controller;
import app.main.controller.Converter;
import app.main.database.structure.Structure;

public class CartListAdapter extends BaseAdapter {

    Context context;
    JSONArray cartItems;

    public CartListAdapter(Context context, JSONArray cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public int getCount() {
        return cartItems.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return cartItems.getJSONObject(i);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.cart_adapter_row, viewGroup, false);
        }

        Cursor data = null;
        try {
            data = Controller.select(Structure.ITEMS, new String[]{"id", "name", "price", "picture"}, "id = ?", new String[]{cartItems.getJSONObject(i).getString("id")}, null, null,null);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        data.moveToFirst();

        TextView textViewName = view.findViewById(R.id.cartadapter_name);
        TextView textViewPrice = view.findViewById(R.id.cartadapter_price);
        TextView textViewAmount = view.findViewById(R.id.cartadapter_amount);
        TextView textViewTotal = view.findViewById(R.id.cartadapter_total);
        ImageView imageView = view.findViewById(R.id.cartadapter_image);
        ImageButton button = view.findViewById(R.id.cartadapter_delete);

        textViewName.setText(data.getString(1));
        textViewPrice.setText(data.getString(2) + " zł");
        imageView.setImageBitmap(Converter.base64ToBitmap(data.getString(3)));

        Float total;
        Integer amount;

        try {
            amount = Integer.valueOf(cartItems.getJSONObject(i).getString("amount"));
            total = amount * Float.parseFloat(data.getString(2).replace(",","."));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        textViewAmount.setText(context.getResources().getText(R.string.amount) + " " + String.valueOf(amount));
        textViewTotal.setText(context.getResources().getText(R.string.total) + " " + String.valueOf(total).replace(".",",") + " zł");

        button.setOnClickListener(view1 -> {
            Cursor cursor = Controller.select(Structure.USERS, new String[]{"cart"}, "id = ?", new String[]{Global.CURRENT_ID}, null,null,null);
            cursor.moveToFirst();


            JSONArray oldCart, newCart;
            try {
                oldCart = new JSONArray(cursor.getString(0));
                newCart = new JSONArray();

                for (int j = 0; j < oldCart.length(); j++) {
                    if (!oldCart.getJSONObject(j).getString("id").equals(cartItems.getJSONObject(i).getString("id"))) {
                        newCart.put(oldCart.getJSONObject(j));
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            Controller.updateInto(Structure.USERS, new String[]{"cart"}, new Object[]{newCart.toString()}, "id = ?", new String[]{Global.CURRENT_ID});
            cursor.close();

            try {
                Cursor cursor1 = Controller.select(Structure.ITEMS, new String[]{"amount"}, "id = ?", new String[]{cartItems.getJSONObject(i).getString("id")}, null,null,null);
                cursor1.moveToFirst();
                Controller.updateInto(Structure.ITEMS, new String[]{"amount"}, new Object[]{String.valueOf(Integer.parseInt(cartItems.getJSONObject(i).getString("amount")) + cursor1.getInt(0))}, "id = ?", new String[]{cartItems.getJSONObject(i).getString("id")});
                cursor1.close();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });


        return view;
    }
}
