package com.dk.mensajero.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayOutputStream;


/**
 * Created by juanma on 01/06/15.
 */
public class Utilities {

    /**
     * Convierte un bitmap en string.
     *
     * @param bitmap la imagen a ser convertida.
     * @return String la imagen codificada en base64.
     */
    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * Convierte un string en bitmap.
     *
     * @param encodedString el string a transformar en bitmap.
     *                      (precondition: encodedString debería estar en base64)
     * @return bitmap (convertido desde el string recibido.)
     */
    public static Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            Log.i("WARNING: ", e.getMessage());
            return null;
        }
    }

    public static Bitmap cutCentredSquare(Bitmap bitmap) {
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            bitmap = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );
        } else {
            bitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }
        return bitmap;
    }


    public static Drawable createRoundImage(Bitmap bitmap) {
        return new RoundImage(bitmap);
    }

}
