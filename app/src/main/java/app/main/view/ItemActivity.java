package app.main.view;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import app.main.Global;
import app.main.R;
import app.main.controller.Controller;
import app.main.controller.Converter;
import app.main.database.base.Table;
import app.main.database.structure.Structure;

public class ItemActivity extends AppCompatActivity {

    String id;
    Integer maxAmount;

    ImageView imageView;
    TextView textViewName, textViewPrice, textViewSeller, textViewDescription, textViewAmount, textViewInCart;
    Button buttonCart, buttonAdd, buttonSubtract;
    EditText editText;

    CountDownTimer refreshTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        getSystemService(LAYOUT_INFLATER_SERVICE);

        id = Objects.requireNonNull(getIntent().getExtras()).getString("id");

        imageView = findViewById(R.id.item_image);
        textViewName = findViewById(R.id.item_name);
        textViewPrice = findViewById(R.id.item_price);
        textViewSeller = findViewById(R.id.item_seller);
        textViewDescription = findViewById(R.id.item_description);
        textViewAmount = findViewById(R.id.item_amount);
        buttonCart = findViewById(R.id.item_addtocart);
        buttonAdd = findViewById(R.id.item_amount_add);
        buttonSubtract = findViewById(R.id.item_amount_subtract);
        editText = findViewById(R.id.item_amount_edittext);
        textViewInCart = findViewById(R.id.item_already_in_cart);

        setItemData();
        checkForItemInCart();


        buttonAdd.setOnClickListener(view -> changeAmount(1));
        buttonSubtract.setOnClickListener(view -> changeAmount(-1));
        editText.setOnKeyListener((view, i, keyEvent) -> checkAmountBounds());

        refreshTimer = new CountDownTimer(1000,500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                checkForItemInCart();
                setItemData();
                refreshTimer.start();
            }
        };
        refreshTimer.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        setItemData();
        checkForItemInCart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refreshTimer.cancel();
    }

    private void goToCart() {
        startActivity(new Intent(this, CartActivity.class));
    }

    private void checkForItemInCart() {
        Cursor cursor = Controller.select(Structure.USERS, new String[]{"cart"}, "id = ?", new String[]{Global.CURRENT_ID}, null,null,null);
        cursor.moveToFirst();

        JSONArray cart = new JSONArray();

        try {
            cart = new JSONArray(cursor.getString(0));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        boolean found = false;
        for (int i = 0; i < cart.length(); i++) {
            try {
                if (id.equals(cart.getJSONObject(i).getString("id"))) {
                    found = true;
                    break;
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        if (found) {
            buttonAdd.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);
            buttonSubtract.setVisibility(View.GONE);
            textViewInCart.setVisibility(View.VISIBLE);
            buttonCart.setText(R.string.go_to_cart);
            buttonCart.setOnClickListener(view -> goToCart());
        } else {
            buttonAdd.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);
            buttonSubtract.setVisibility(View.VISIBLE);
            textViewInCart.setVisibility(View.GONE);
            buttonCart.setText(R.string.add_to_cart);
            buttonCart.setOnClickListener(view -> addToCart());
        }
    }

    private void setItemData() {
        Cursor data = Controller.select(Structure.ITEMS,null, "id = ?", new String[]{id}, null,null,null);
        data.moveToFirst();

        maxAmount = Integer.valueOf(data.getString(4));

        imageView.setImageBitmap(Converter.base64ToBitmap(data.getString(5)));
        textViewName.setText(data.getString(1));
        textViewPrice.setText(String.format("%s zł", data.getString(3)));
        textViewSeller.setText(data.getString(6));
        textViewDescription.setText(data.getString(2));
        textViewAmount.setText(String.format("%s %s", getResources().getText(R.string.in_stock), data.getString(4)));

        data.close();
    }

    private void addToCart() {
        checkAmountBounds();

        Cursor cursor = Controller.select(Structure.USERS, new String[]{"cart"}, "id = ?", new String[]{Global.CURRENT_ID}, null,null,null);
        cursor.moveToFirst();

        JSONArray cart = new JSONArray();

        try {
            cart = new JSONArray(cursor.getString(0));
            cart.put(new JSONObject().put("id", id).put("amount", editText.getText()));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        cursor.close();

        Controller.updateInto(Structure.USERS, new String[]{"cart"}, new Object[]{cart.toString()}, "id = ?", new String[]{Global.CURRENT_ID});

        Cursor cursor1 = Controller.select(Structure.ITEMS, new String[]{"amount"}, "id = ?", new String[]{id},null,null,null);
        cursor1.moveToFirst();

        Integer amount = cursor1.getInt(0);
        amount -= Integer.parseInt(String.valueOf(editText.getText()));

        Controller.updateInto(Structure.ITEMS, new String[]{"amount"}, new Object[]{amount}, "id = ?", new String[]{id});

        cursor1.close();

        goToCart();

    }

    private void changeAmount(Integer diff) {
        Log.i(Global.TAG, editText.toString());
        editText.setText(String.valueOf(Integer.parseInt(editText.getText().toString()) + diff));
        checkAmountBounds();
    }

    private boolean checkAmountBounds() {
        int tmp;
        try {
            tmp = Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException e) {
            tmp = 1;
        }

        if (tmp < 1) {
            editText.setText("1");
        } else if (tmp > maxAmount) {
            editText.setText(String.valueOf(maxAmount));
        }
        return false;
    }
}