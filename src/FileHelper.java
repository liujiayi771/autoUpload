import java.io.File;
import java.io.IOException;

/**
 * Created by joey on 16-3-17.
 */
public class FileHelper {
    public static boolean deleteFile(File file) throws IOException {
        boolean flag = false;
        if (file.exists() && file.isFile()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public static boolean deleteDir(File file) throws IOException {
        boolean flag = true;
        if (!file.exists() || !file.isDirectory()) {
            return false;
        }

        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i]);
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteDir(files[i]);
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) return false;
        if (file.delete()) {
            return true;
        } else {
            return false;
        }
    }
}
