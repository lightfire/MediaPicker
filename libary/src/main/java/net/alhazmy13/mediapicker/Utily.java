package net.alhazmy13.mediapicker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Alhazmy13 on 11/10/15.
 */
public class Utily {

    public static String compressImage(String path) throws IOException {
        File file = new File(path);
        Bitmap bitmap=BitmapFactory.decodeFile(path);
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        os.close();
        return path;
    }

}
