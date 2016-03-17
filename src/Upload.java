import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by joey on 16-3-16.
 */
public class Upload {
    /** The userID of the administrator */
    //protected static String adminID = "567347999932437";
    protected static String adminID = "567347999933166";

    /** The directory storing the original pictures */
    protected static String PICTURE_ORIGINAL_DIR = "/app-data/ljy/pictures/";
    //protected static String PICTURE_ORIGINAL_DIR = "/home/joey/pictures/";

    protected static S3Transfer s3Transfer = new S3Transfer();

    public static void main(String[] args) throws IOException, InterruptedException {
        long startTime=System.currentTimeMillis();

        java.security.Security.setProperty("networkaddress.cache.ttl", "60");

        File pictureFile = new File(PICTURE_ORIGINAL_DIR);
        File[] files = pictureFile.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                ArrayList<Picture> imageList = new ArrayList<>();
                imageList.add(new Picture(file));
                post(file.getName(), imageList);
            }
            else {
                File[] innerFiles = file.listFiles();
                ArrayList<Picture> imageList = new ArrayList<>();
                for (File innerFile : innerFiles) {
                    imageList.add(new Picture(innerFile));
                }
                post(file.getName(), imageList);
            }
        }

        FileHelper.deleteDir(new File(Picture.PICTURE_DATA_DIR));
        s3Transfer.shutdownClient();

        long endTime=System.currentTimeMillis();
        long useTime = endTime - startTime;
        double minutes = (double)useTime / 60_000;
        System.out.printf("Program run time： %.2f minutes\n\n", minutes);
    }

    public static void post(String name, ArrayList<Picture> imageList) throws IOException, InterruptedException {
        int isPrivate = 1;
        int topic = 0;

        String boardID = MySQL.addBoard(adminID, name, isPrivate, "", topic);

        MySQL.addPrivateMember(boardID, adminID);

        if (imageList.size() != 0) {
            for (Picture p : imageList) {
                String filename = p.getOriginalPictureName();
                String realName = p.getRealName();
                boolean imageFlag = p.uploadPicture();

                if (imageFlag) {
                    String imageId = MySQL.addImage(adminID, realName, filename, p.getWidth(), p.getHeight(), 1);
                    MySQL.addBoardImage(boardID, imageId);
                } else {
                    return;
                }
            }

            File jpgDir = new File(Picture.PICTURE_DATA_DIR + "jpg/" + adminID);
            if (jpgDir.exists() && jpgDir.isDirectory()) {
                s3Transfer.S3UploadDir("data/jpg/", jpgDir);
            }
        }
    }

}
