package app.main.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.main.Global;
import app.main.R;
import app.main.controller.Controller;
import app.main.database.structure.Structure;
import app.main.view.adapter.ShopListAdapter;

public class ShopActivity extends AppCompatActivity {

    Spinner spinner;
    EditText editText;
    Button button;
    ListView listView;
    ShopListAdapter shopListAdapter;
    AlertDialog alertDialog;
    TextView textView;
    List<String> itemIds;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        button = findViewById(R.id.shop_button);
        editText = findViewById(R.id.shop_edittext);
        spinner = findViewById(R.id.shop_spinner);
        listView = findViewById(R.id.shop_list);
        textView = findViewById(R.id.shop_noitems);

        sharedPreferences = getSharedPreferences(Global.SAVE_TAG, MODE_PRIVATE);
        sharedEditor = sharedPreferences.edit();


        String[] spinnerItems = new String[]{getResources().getString(R.string.name), getResources().getString(R.string.description), getResources().getString(R.string.id), getResources().getString(R.string.seller)};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        spinner.setAdapter(spinnerAdapter);

        search();

        button.setOnClickListener(view -> search());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.info);
        alertDialogBuilder.setMessage(getResources().getString(R.string.author) + ": Mateusz Pokorniecki");
        alertDialogBuilder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> dialog.cancel());
        alertDialog = alertDialogBuilder.create();

        textView.setVisibility(View.GONE);

        listView.setOnItemClickListener((adapterView, view, i, l) -> onItemClicked(i));

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menushop_cart) {
            goToCart();
        } else if (item.getItemId() == R.id.menushop_orders) {
            goToMyOrders();
        } else if (item.getItemId() == R.id.menushop_info) {
            alertDialog.show();
        } else if (item.getItemId() == R.id.menushop_logout) {
            sharedEditor.putBoolean(Global.SAVE_TAG_KEEP_LOGGED_IN, false);
            sharedEditor.apply();
            finish();
    }
        return super.onOptionsItemSelected(item);
    }

    private void goToMyOrders() { startActivity(new Intent(this, OrdersActivity.class));}

    private void goToCart() {
        startActivity(new Intent(this, CartActivity.class));
    }


    private void search() {
        itemIds = new ArrayList<>();

        Cursor data = Controller.select(Structure.ITEMS, new String[]{"id"}, translateOption(spinner.getSelectedItem().toString()) + " LIKE ?", new String[]{"%" + editText.getText().toString() + "%"}, null,null,null);
        for (int i = 0; i < data.getCount(); i++) {
            data.moveToPosition(i);
            itemIds.add(data.getString(0));
        }

        checkItemCount(itemIds);

        shopListAdapter = new ShopListAdapter(this, itemIds);
        listView.setAdapter(shopListAdapter);
        data.close();
    }

    private String translateOption(String option) {
        if (option.equals("nazwa")) {
            return "name";
        } else if (option.equals("opis")) {
            return "description";
        } else if (option.equals("sprzedawca")) {
            return "seller";
        }
        return option;
    }

    private void checkItemCount(List<String> list) {
        if (list.size() > 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void onItemClicked(int index) {
        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra("id", itemIds.get(index));
        startActivity(intent);
    }
}