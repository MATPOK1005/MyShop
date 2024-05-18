package app.main.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import app.main.Global;
import app.main.R;
import app.main.controller.Controller;
import app.main.database.structure.Structure;
import app.main.view.adapter.OrdersListAdapter;

public class OrdersActivity extends AppCompatActivity {

    ListView listView;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        listView = findViewById(R.id.orders_listview);
        
        setListData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setListData() {
        Cursor data;
        data = Controller.select(Structure.ORDERS, new String[]{"id","date","items","delivery_address","delivery_name","price"}, "buyer_id = ?", new String[]{Global.CURRENT_ID},null, null,null);
        data.moveToFirst();
        List<String> orsersData = new ArrayList<>();

        List<String> ids = new ArrayList<>();
        for (int i = 0; i < data.getCount(); i++) {
            data.moveToPosition(i);
            ids.add(data.getString(0));
            orsersData.add("#: " + data.getString(0) + " date: " + data.getString(1) + " items: " + data.getString(2) + " delivery address: " + data.getString(3) + " delivery name: " + data.getString(4) + " price: " + data.getString(5) + " zł");
        }
        data.close();

        listAdapter = new OrdersListAdapter(this, ids);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, orsersData.get(i));
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);

        });
    }


}