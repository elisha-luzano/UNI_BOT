package ai.api.uni.Model;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by Lenovo-G4030 on 4/15/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class ImageFileFilter implements FileFilter {

    public ImageFileFilter() {}

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        else if (isImageFile(file.getAbsolutePath())) {
            return true;
        }
        return false;
    }

    private boolean isImageFile(String filePath) {
        if (filePath.endsWith(".jpg") || filePath.endsWith(".png"))
        // Add other formats as desired
        {
            return true;
        }
        return false;
    }
}
