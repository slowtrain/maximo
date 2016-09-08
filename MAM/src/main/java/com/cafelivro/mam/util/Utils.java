package com.cafelivro.mam.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Fast campus 안드로이드 앱 개발 프로젝트 CAMP
 * MyGallery (2/3) (by mindwing)
 */
public class Utils {
    public static final int REQUEST_CODE_EXTERNAL_STORAGE = 251;

    // TODO Transition 에 쓰일 nama 상수를 정의해주세요.
    public static final String TRANSITION_NAME = "BG_TRANSITION";

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static final String[] PROJECTION_GALLERY = new String[]{MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE};

    public static void collectPicturesInfo(Context ctx, ArrayList<String> imagePaths,
                                           ArrayList<String> imageNames) {
        Cursor imageCursor = ctx
                .getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        PROJECTION_GALLERY, null, null, null);

        if (imageCursor == null) {
            return;
        }

        if (!imageCursor.moveToFirst()) {
            return;
        }

        String imageName;
        String imagePath;

        int imagePathCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        int imageNameCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

        while (imageCursor.moveToNext()) {
            imagePath = imageCursor.getString(imagePathCol);
            imageName = imageCursor.getString(imageNameCol);

            if (imagePath != null) {
                imagePaths.add(imagePath);
                imageNames.add(imageName);
            }
        }

        imageCursor.close();
    }

    public static Bitmap getBitmap(String path, boolean thumbnail) {
        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inSampleSize = thumbnail ? 4 : 2;

        return BitmapFactory.decodeFile(path, opt);
    }

    public static boolean requestStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean checkVal = permission == PackageManager.PERMISSION_GRANTED;

        if (!checkVal) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(activity, "'저장소' 권한은 사진파일을 읽는데 필요하니 꼭 승인해주세요.", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(
                    activity, PERMISSIONS_STORAGE, REQUEST_CODE_EXTERNAL_STORAGE);
        }

        return checkVal;
    }
}
