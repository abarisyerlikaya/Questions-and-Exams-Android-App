package tr.edu.yildiz.ahmetbarisyerlikaya;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentResolver.SCHEME_CONTENT;
import static android.graphics.Bitmap.CompressFormat.PNG;

public class Utils {
    public static final int MAX_ATTACHMENT_SIZE = 52428800; // 50MB
    public static final String COLOR_GREEN = "#5cb85c";
    public static final int DEFAULT_EXAM_LENGTH = 30;
    public static final int DEFAULT_POINTS_PER_QUESTION = 10;
    public static final int DEFAULT_DIFFICULTY = 5;
    private static final String EMAIL_REGEX = "^(.+)@(.+)$";
    private static final String PHONE_REGEX = "^[+]?[0-9]{10,13}$";
    private static final String DATE_REGEX = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};


    public static String encryptPassword(String password) {
        try {
            byte[] plainText = password.getBytes();

            MessageDigest md = MessageDigest.getInstance("SHA");
            md.reset();
            md.update(plainText);

            byte[] encodedPassword = md.digest();
            StringBuilder sb = new StringBuilder();

            for (byte b : encodedPassword) {
                if ((b & 0xff) < 0x10) sb.append("0");
                sb.append(Long.toString(b & 0xff, 16));
            }

            return sb.toString();
        } catch (Exception e) { return null; }
    }

    public static boolean isEmailValid(String email) {
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        return Pattern.compile(PHONE_REGEX).matcher(phoneNumber).matches();
    }

    public static boolean isDateStringValid(String date) {
        return Pattern.compile(DATE_REGEX).matcher(date).matches();
    }

    public static byte[] convertBitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap convertBytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String generateTimestamp() {
        return new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
    }

    public static String getFileExtension(String path) {
        return path.substring(path.lastIndexOf("."));
    }

    public static String getMimeTypeFromUri(Uri uri, Context context) {
        String mimeType = null;
        if (SCHEME_CONTENT.equals(uri.getScheme()))
            mimeType = context.getContentResolver().getType(uri);
        else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static String saveToInternalStorage(Uri uri, String fileName, Context context) {
        try {
            File newFile;
            InputStream in = context.getContentResolver().openInputStream(uri);

            newFile = new File(context.getApplicationContext().getFilesDir(), fileName);
            if (newFile.exists()) newFile.delete();

            OutputStream out = new FileOutputStream(newFile);
            byte[] buf = new byte[MAX_ATTACHMENT_SIZE];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);

            in.close();
            out.close();

            System.out.println("Copy successful!\n" + newFile.getAbsolutePath());
            return newFile.getAbsolutePath();
        } catch (Exception e) {
            System.out.println("An error occurred!");
            e.printStackTrace();
            return null;
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
    }
}
