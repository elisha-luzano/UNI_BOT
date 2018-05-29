package ai.api.uni.Activity;

import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import ai.api.uni.Model.Folder;
import ai.api.uni.Model.Photo;
import ai.api.uni.R;
import uk.co.senab.photoview.PhotoViewAttacher;

import static ai.api.uni.Activity.ChatActivity.database;

/**
 * Created by Lenovo-G4030 on 3/8/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class PhotoLargeActivity extends BaseActivity {
    private Integer cur_id = 0;
    private String current_folder = "";
    private String orig_folder = "";

    private File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "CameraDemo");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);

        ImageView imageView = (ImageView) findViewById(R.id.imageLargeView);
        Button delete_button = (Button) findViewById(R.id.delete);


        final Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            final String filename = bundle.getString("file_name");
            File file = new File(filename);
            Uri uri = Uri.fromFile(file);
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                AssetFileDescriptor fileDescriptor =null;
                fileDescriptor =
                        getContentResolver().openAssetFileDescriptor(uri, "r");
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(
                        fileDescriptor.getFileDescriptor(), null, options);

                ExifInterface exif = new ExifInterface(uri.getPath());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);
                Matrix matrix = new Matrix();
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                Drawable d = new BitmapDrawable(getResources(), bitmap);
                imageView.setImageDrawable(d);

                PhotoViewAttacher pAttacher;
                pAttacher = new PhotoViewAttacher(imageView);
                pAttacher.update();
            } catch(Exception e) {
                Log.i("Error", "ERROR!" + e.getMessage());
            }

            final Integer folder_id = bundle.getInt("folder_id");
            cur_id = folder_id;
            current_folder = Folder.getOne(folder_id, database).getName();

            orig_folder = Folder.getOne(folder_id, database).getName();

            Integer pos = 0;
            Spinner dropdown = (Spinner) findViewById(R.id.update);
            final ArrayList<Folder> folders = Folder.getAll(database);
            ArrayList<String> names = new ArrayList<String>();
            for(int i=0; i<folders.size(); i++){
                if(folders.get(i).getId() == folder_id) pos = i;
                names.add(folders.get(i).getName());
            }
            String[] items = names.toArray(new String[0]);
            //String[] items = new String[]{"English", "Science", "Math"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            dropdown.setAdapter(adapter);
            dropdown.setSelection(pos);
            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                Integer flag = 1;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    //Log.v("item", (String) parent.getItemAtPosition(position));
                    if(folders.get(position).getId() != cur_id) {
                        Integer file_id = bundle.getInt("id");
                        Log.i("NEW POSITION", "NEW POSITION!" + position);
                        //(String) parent.getItemAtPosition(position)

                        File mediaStorageDirSource = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), current_folder);

                        File origSource = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), orig_folder);

                        String time_stamp = filename.replaceAll(origSource.toString(), "");

                        File mediaStorageDirTarget = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), folders.get(position).getName());

                        File sourceLocation = new File (mediaStorageDirSource + File.separator + time_stamp);
                        File targetLocation = new File (mediaStorageDirTarget + File.separator + time_stamp);

                        Log.i("COPY", sourceLocation + "");
                        Log.i("COPY", targetLocation + "");
                        try {
                            if(sourceLocation.exists()){

                                Photo.update(file_id, folders.get(position).getId(), database);
                                Photo.update(file_id, mediaStorageDirTarget + File.separator + time_stamp, database);

                                InputStream in = new FileInputStream(sourceLocation);
                                OutputStream out = new FileOutputStream(targetLocation);

                                byte[] buf = new byte[1024];
                                int len;

                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }

                                in.close();
                                out.close();


                                mediaStorageDirSource = new File(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES), current_folder);
                                time_stamp = filename.replaceAll(mediaStorageDirSource.toString(), "");
                                File file = new File(mediaStorageDirSource, time_stamp);
                                boolean deleted = file.delete();

                                Log.i("COPY", "Copy file successful.");

                            }else{
                                Log.i("COPY", "Copy file failed. Source file missing.");
                            }

                        } catch (NullPointerException e) {
                            Log.i("COPY", e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        current_folder = (String) parent.getItemAtPosition(position);
                        cur_id = Folder.getOne((String) parent.getItemAtPosition(position), database).getId();

                        Toast.makeText(PhotoLargeActivity.this, "Moved photo to " + (String) parent.getItemAtPosition(position) + ".", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });

            delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PhotoLargeActivity.this);
                    builder.setMessage("Are you sure you want delete this photo?")
                            .setCancelable(false)
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // fire an intent go to your next activity
                                    Integer file_id = bundle.getInt("id");
                                    Log.i("TO BE DELETED", "TO BE DELETED!" + file_id);
                                    Photo.delete(file_id, database);


                                    File mediaStorageDirSource = new File(Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_PICTURES), current_folder);
                                    String time_stamp = filename.replaceAll(mediaStorageDirSource.toString(), "");
                                    File file = new File(mediaStorageDirSource, time_stamp);
                                    boolean deleted = file.delete();

                                    //Intent intent = new Intent(PhotoLargeActivity.this, PhotoActivity.class);
                                    Toast.makeText(PhotoLargeActivity.this, "Photo deleted.", Toast.LENGTH_SHORT).show();
                                    //startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }




    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
}