package app.main.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.function.Consumer;
import java.util.function.Function;

import app.main.Global;
import app.main.R;
import app.main.controller.Controller;
import app.main.database.structure.Structure;
import app.main.view.adapter.CartListAdapter;

public class CartActivity extends AppCompatActivity {

    ListView listView;
    TextView textView;
    Button button;
    JSONArray cartJSON;
    float total = 0f;

    CountDownTimer refreshTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        listView = findViewById(R.id.cart_listview);
        textView = findViewById(R.id.cart_total);
        button = findViewById(R.id.cart_buy);

        setCartData();

        button.setOnClickListener(view -> goToBuy());

        refreshTimer = new CountDownTimer(1000, 500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                setCartData();
                refreshTimer.start();
            }
        };
        refreshTimer.start();
    }

    private void onItemClicked(View view) {
        Log.i(Global.TAG, "onItemClicked: " + view.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCartData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refreshTimer.cancel();
    }

    private void goToBuy() {
        startActivity(new Intent(this, BuyActivity.class));
    }


    private void setCartData() {
        Cursor cursor = Controller.select(Structure.USERS, new String[]{"cart"}, "id = ?", new String[]{Global.CURRENT_ID}, null,null,null);
        cursor.moveToFirst();

        total = 0.0f;

        try {
            cartJSON = new JSONArray(cursor.getString(0));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        CartListAdapter cartListAdapter = new CartListAdapter(this, cartJSON);
        listView.setAdapter(cartListAdapter);

        for (int i = 0; i < cartJSON.length(); i++) {
            try {
                float amount = Integer.parseInt(cartJSON.getJSONObject(i).getString("amount").replace(",", "."));
                Cursor cursorPrice = Controller.select(Structure.ITEMS, new String[]{"price"}, "id = ?", new String[]{cartJSON.getJSONObject(i).getString("id")}, null,null,null);
                cursorPrice.moveToFirst();
                total = (float) (Math.round((total + (amount * Float.parseFloat(cursorPrice.getString(0).replace(",",".")))) * 100.0) / 100.0);
                cursorPrice.close();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        textView.setText(getResources().getText(R.string.total) + " " + String.valueOf(total).replace(".",",") + " zł");
    }
}