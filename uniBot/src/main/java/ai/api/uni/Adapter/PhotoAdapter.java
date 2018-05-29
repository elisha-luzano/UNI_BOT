package ai.api.uni.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ai.api.uni.Activity.PhotoLargeActivity;
import ai.api.uni.Model.Photo;
import ai.api.uni.R;

import static ai.api.uni.Activity.ChatActivity.TAG;

/**
 * Created by Lenovo-G4030 on 3/8/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class PhotoAdapter extends ArrayAdapter<Photo> {
    private Context mContext;
    private List<Photo> photos = new ArrayList<>();
    private File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "CameraDemo");

    public PhotoAdapter(@NonNull Context context, /*@LayoutRes */ArrayList<Photo> list) {
        super(context, 0 , list);
        mContext = context;
        photos = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.photo_gallery_layout,parent,false);

        final Photo currentPhoto = photos.get(position);

        ImageView image = (ImageView)listItem.findViewById(R.id.imageGallery);
        File file = new File(currentPhoto.getName());
        Uri uri = Uri.fromFile(file);
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            AssetFileDescriptor fileDescriptor =null;
            fileDescriptor =
                    mContext.getContentResolver().openAssetFileDescriptor(uri, "r");
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(
                    fileDescriptor.getFileDescriptor(), null, options);

            ExifInterface exif = new ExifInterface(uri.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
            bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
            image.setImageDrawable(d);


            //Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            //image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 60, false));
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PhotoLargeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", currentPhoto.getId());
                    bundle.putInt("folder_id", currentPhoto.getFolderId());
                    bundle.putString("file_name", currentPhoto.getName());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "ERROR!" + e.getMessage());
        }

        return listItem;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

}
