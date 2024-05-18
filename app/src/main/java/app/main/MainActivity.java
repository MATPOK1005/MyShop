package app.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.Objects;

import app.main.controller.Controller;
import app.main.controller.QueryBuilder;
import app.main.database.structure.Structure;
import app.main.view.ShopActivity;

public class MainActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    CheckBox checkBoxRemember;
    Button buttonLogin, buttonRegister;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Controller.initDatabase(getApplicationContext());

        sharedPreferences = getSharedPreferences(Global.SAVE_TAG, MODE_PRIVATE);
        sharedEditor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean(Global.SAVE_TAG_KEEP_LOGGED_IN, false)) {
            Global.CURRENT_ID = sharedPreferences.getString(Global.SAVE_TAG_ID, "-1");
            startActivity(new Intent(this, ShopActivity.class));
        }

        editTextUsername = findViewById(R.id.login_username);
        editTextPassword = findViewById(R.id.login_password);
        checkBoxRemember = findViewById(R.id.login_check);
        buttonLogin = findViewById(R.id.login_login);
        buttonRegister = findViewById(R.id.login_register);

        buttonLogin.setOnClickListener(view -> login());
        buttonRegister.setOnClickListener(view -> register());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Controller.closeDatabase();
    }

    private void register() {
        try {
            Cursor data = Controller.select(Structure.USERS, new String[]{"id", "username", "password"}, "username = ?", new String[]{editTextUsername.getText().toString()},null,null,null);
            if (data.getCount() > 0){
                data.close();
                throw new Exception("User already exists");
            }
            data.close();
            Controller.insert(Structure.USERS, editTextUsername.getText().toString(), editTextPassword.getText().toString(), "[]");
            login();
        } catch (Exception e) {
            Log.e(Global.TAG, Objects.requireNonNull(e.toString()));
            Toast.makeText(this, getResources().getText(R.string.unsuccesfull_register), Toast.LENGTH_SHORT).show();
        }
    }

    private void login() {
        Cursor data = null;
        try {
            data = Controller.select(Structure.USERS, new String[]{"id", "username", "password"}, "username = ?", new String[]{editTextUsername.getText().toString()},null,null,null);
            data.moveToFirst();

            if (data.getString(2).equals(editTextPassword.getText().toString())) {
                Global.CURRENT_ID = data.getString(0);
                if (checkBoxRemember.isChecked()) {
                    sharedEditor.putBoolean(Global.SAVE_TAG_KEEP_LOGGED_IN, true);
                    sharedEditor.putString(Global.SAVE_TAG_ID, Global.CURRENT_ID);
                } else {
                    editTextPassword.setText("");
                    sharedEditor.putBoolean(Global.SAVE_TAG_KEEP_LOGGED_IN, false);
                }
                sharedEditor.apply();
                startActivity(new Intent(this, ShopActivity.class));
            } else {
                throw new Exception("Incorrect password");
            }
        } catch (Exception e) {
            Log.e(Global.TAG, Objects.requireNonNull(e.toString()));
            Toast.makeText(this, getResources().getText(R.string.unsuccesfull_login), Toast.LENGTH_SHORT).show();
        }
        if(data != null) {
            data.close();
        }
    }
}