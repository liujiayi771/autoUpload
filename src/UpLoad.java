import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by joey on 16-3-14.
 */
public class UpLoad {
//    protected static String IMAGE_DATA_DIR = "/usr/local/nginx/html/signichat/data/";
    protected static String IMAGE_DATA_DIR = "/app-data/ljy/data/";
//    protected static String adminId = "567347999932437";
    protected static String adminId = "567347999933166";
//    protected static S3Database s3Database = new S3Database();
    protected static S3TransferProgressSample s3Database = null;
    public static void post(String name, ArrayList<Picture> imageList) throws IOException, InterruptedException {
        String title = name;
        int isPrivate = 1;
        int topic = 0;
        String boardId = MySQL.addBoard(adminId, title, isPrivate, "", topic);
        MySQL.addPrivateMember(boardId, adminId);

        if (imageList.size() != 0) {
            for (Picture p : imageList) {
                String filename = p.getOriginalName();
                // Get the name of the picture file without the extension
                String realName = p.getRealName();
                boolean imageFlag = p.uploadImage();
                if (imageFlag) {
                    String imageId = MySQL.uploadImage(adminId, realName, filename, p.getWidth(), p.getHeight(), 1);
                    MySQL.addBoardImage(boardId, imageId);
                } else {
                    return;
                }
            }

            File jpgDir = new File(Picture.IMAGE_DATA_DIR + "jpg/" + adminId);
            if (jpgDir.exists() && jpgDir.isDirectory()) {
                s3Database.S3UploadDir("data/jpg/", jpgDir);
                //Picture.deleteDir(jpgDir);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        long startTime=System.currentTimeMillis();
        java.security.Security.setProperty("networkaddress.cache.ttl", "60");
        s3Database = new S3TransferProgressSample();
//        File imageFile = new File("/home/joey/pictures/");
        File imageFile = new File("/app-data/ljy/pictures/");
        File[] files = imageFile.listFiles();
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
        Picture.deleteDir(new File(Picture.IMAGE_DATA_DIR));
        Picture.deleteDir(new File(Picture.TMP_DIR));
        long endTime=System.currentTimeMillis();
        long useTime = endTime - startTime;
        long minutes = useTime / 60_000;
        s3Database.shutdownClient();
        System.out.println("程序运行时间： " + String.valueOf(minutes) + "分钟");
    }
}
