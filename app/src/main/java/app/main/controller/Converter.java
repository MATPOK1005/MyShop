package app.main.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import app.main.Global;


public class Converter {
    public static Bitmap base64ToBitmap(String encryptedImage) {
        byte[] decodedString = Base64.decode(encryptedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        String baseString = "";
        Log.i(Global.TAG, "bitmapToBase64: " + bitmap);
        if (bitmap != null) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            byte[] byteArray = byteStream.toByteArray();
            baseString = Base64.encodeToString(byteArray,Base64.DEFAULT);
        }
        return baseString;
    }


}
