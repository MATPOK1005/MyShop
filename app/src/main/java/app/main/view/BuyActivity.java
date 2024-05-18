package app.main.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import app.main.Global;
import app.main.R;
import app.main.controller.Controller;
import app.main.database.structure.Structure;

public class BuyActivity extends AppCompatActivity {

    EditText editTextName, editTextAddress, editTextEmail, editTextPhone;
    Button button;
    JSONArray cartJSON;
    float total = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        editTextAddress = findViewById(R.id.buy_address);
        editTextName = findViewById(R.id.buy_name);
        editTextEmail = findViewById(R.id.buy_email);
        editTextPhone = findViewById(R.id.buy_phone);
        button = findViewById(R.id.buy_button);

        Cursor cursor = Controller.select(Structure.USERS, new String[]{"cart"}, "id = ?", new String[]{Global.CURRENT_ID}, null,null,null);
        cursor.moveToFirst();

        try {
            cartJSON = new JSONArray(cursor.getString(0));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

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

        button.setOnClickListener(view -> buy());

    }

    private void buy() {
        if (formValid()) {

            Controller.updateInto(Structure.USERS, new String[]{"cart"}, new String[]{"[]"}, "id = ?", new String[]{Global.CURRENT_ID});

            Controller.insert(Structure.ORDERS, Global.CURRENT_ID, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()), cartJSON, editTextAddress.getText(), editTextName.getText(), total);

            if (!String.valueOf(editTextPhone.getText()).equals("")) {
                Log.i(Global.TAG, "buy phones");
                sendOrderSms();
            }
            if (!String.valueOf(editTextEmail.getText()).equals("")) {
                sendOrderEmail();
            }

            Toast.makeText(this, getResources().getText(R.string.purchase_success), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void sendOrderEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{String.valueOf(editTextEmail.getText())});
        intent.putExtra(Intent.EXTRA_SUBJECT, "MyShop order");
        intent.putExtra(Intent.EXTRA_TEXT, String.format(getResources().getString(R.string.message), new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()),editTextName.getText(),editTextAddress.getText(),total));
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Email:"));
    }

    private void sendOrderSms() {
        try {
            String message = String.format(getResources().getString(R.string.message), new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()),editTextName.getText(),editTextAddress.getText(),total);
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);

            smsManager.sendMultipartTextMessage(String.valueOf(editTextPhone.getText()), null, parts, null, null);
        } catch (Exception e) {
            Log.e(Global.TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    private boolean formValid() {
        if (!String.valueOf(editTextName.getText()).equals("") && !String.valueOf(editTextAddress.getText()).equals("") && (!String.valueOf(editTextEmail.getText()).equals("") || !String.valueOf(editTextPhone.getText()).equals(""))) {
            return true;
        }
        return false;
    }
}