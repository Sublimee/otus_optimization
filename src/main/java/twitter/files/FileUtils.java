package twitter.files;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static File createFile(String path) {
        File resultFile = new File(path);
        try {
            resultFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultFile;
    }

}
