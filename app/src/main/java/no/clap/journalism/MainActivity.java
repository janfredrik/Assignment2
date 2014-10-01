package no.clap.journalism;

import no.clap.journalism.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

// Camera Intent Main Source:
// http://developer.android.com/training/camera/photobasics.html
// From source: Start a Camera intent, handle the File and save to gallery
// External libaries used: Crouton (https://github.com/keyboardsurfer/Crouton)

public class MainActivity extends Activity {
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    boolean welcomeMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Displays a welcome message if the user has just opened the app.
        if (!welcomeMessage) {
            Crouton.makeText(this, this.getString(R.string.welcome), Style.INFO).show();
            welcomeMessage = true;
        }
    }

    public void takePhoto(View v) {
        // New intent for the camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there is a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Crouton.makeText(this, this.getString(R.string.camera_error), Style.ALERT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name in this ex. form: "20141001_150000"
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        // Put together the filename/info
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Photo path:
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    // Gives a Crouton (Toast) about the result: Saved or not saved.
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Crouton.makeText(this, this.getString(R.string.photo_saved), Style.CONFIRM).show();
                    }

                else if (resultCode == Activity.RESULT_CANCELED) {
                    Crouton.makeText(this, this.getString(R.string.photo_not_saved), Style.ALERT).show();
                }
        }
    }

    // Starts the audio recording activity
    public void recordAudio(View v) {
        Intent intent = new Intent(this, AudioRecord.class);
        startActivity(intent);
    }
}