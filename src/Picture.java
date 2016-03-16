import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;

/**
 * Created by joey on 16-3-14.
 */
public class Picture {
    // The file object of the picture file
    private File pictureFile;
    private File tmpFile;
    // The Image object of the picture file
    private Image pictureImage;
    // The type of the picture file, can usually be gif, jpg, jpeg, png and pdf
    private String type;
    // The original name of the picture file
    private String originalName;
    private String realName;
    // The size of the picture file, the unit is byte
    private long size;
    // The new name we generate randomly for the picture file
    private String generateName;
    // The location of the picture file
    private String filePath;
    // The width of the picture
    private int width = 0;
    // The height of the picture
    private int height = 0;
    // A random key for the picture file
    private long imageKey = 0;
    // The location of the tmp folder
//    protected static final String TMP_DIR = "/usr/local/nginx/html/signichat/tmp/";
    protected static final String TMP_DIR = "/app-data/ljy/tmp/";
//    protected static final String IMAGE_DATA_DIR = "/usr/local/nginx/html/signichat/data/";
    protected static final String IMAGE_DATA_DIR = "/app-data/ljy/data/";
    // The prefix of the preview image
    protected static final String IMAGE_PREVIEW_PREFIX = "preview_";

    private int desBigWidth = 452;
    private int desBigHeight = 300;
    private int desMiddleWidth = 452;
    private int desMiddleHeight = 148;
    private int desSmallWidth = 80;
    private int desSmallHeight = 80;
    private int des120Width = 120;
    private int des120Height = 120;
    private int des150Width = 150;
    private int des150Height = 150;
    private int des1000Width = 1000;
    private int des1000Height = 1000;

    private String pdfTargetUrl = IMAGE_DATA_DIR + "pdf/";
    private String jpgTargetUrl = IMAGE_DATA_DIR + "jpg/";
    private String dziTargetUrl = IMAGE_DATA_DIR + "dzi/";


    // The construct function of the Picture class
    public Picture(File picture) throws IOException{
        pictureFile = picture;
        originalName = pictureFile.getName();
        type = originalName.substring(originalName.lastIndexOf('.') + 1, originalName.length());
        size = pictureFile.getTotalSpace();
        generateFileName();
        filePath = pictureFile.getAbsolutePath();
        pictureImage = ImageIO.read(picture);
        width = pictureImage.getWidth(null);
        height = pictureImage.getHeight(null);
        realName = generateName.substring(0, generateName.lastIndexOf('.') - 1);
        tmpFile = new File(TMP_DIR + generateName);
        copyPicture();
        parseImage();
        image2dzi();
    }

    public String getGenerateName() {
        return this.generateName;
    }

    public String getRealName() { return this.realName; }

    public String getOriginalName() { return this.originalName; }

    public File getPictureFile() {
        return this.pictureFile;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    // We use this function to create a folder named tmp if it does not exist
    private void createTmpFolder() throws IOException {
        File tmpDir = new File(TMP_DIR);
        File commentImageDir = new File(TMP_DIR + "comment_image/");
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        if (!commentImageDir.exists()) {
            commentImageDir.mkdir();
        }
    }

    private void createDataFolder() throws IOException {
        File dataDir = new File(IMAGE_DATA_DIR);
        File pdfTargetDir = new File(pdfTargetUrl);
        File jpgTargetDir = new File(jpgTargetUrl);
        File dziTargetDir = new File(dziTargetUrl);
        File userIdDir = new File(jpgTargetUrl + UpLoad.adminId);
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        if (!pdfTargetDir.exists()) {
            pdfTargetDir.mkdir();
        }
        if (!jpgTargetDir.exists()) {
            jpgTargetDir.mkdir();
        }
        if (!dziTargetDir.exists()) {
            dziTargetDir.mkdir();
        }
        if (!userIdDir.exists()) {
            userIdDir.mkdir();
        }
    }

    // Rename the picture file randomly
    private void generateFileName() {
        // Create 13 random numbers according to the time
        long currentTime = System.currentTimeMillis();
        String prefix = String.valueOf(currentTime);
        // Create 8 random numbers
        int randomNum = (int)Math.floor(Math.random() * 100_000_000);
        String suffix = String.valueOf(randomNum);
        // The new name of the picture file is combinated by 21 numbers
        generateName = prefix + suffix + '.' + type;
    }

    // We use this function to copy picture files from original folder to the tmp folder
    // @return operation success true, else false
    public void copyPicture() throws IOException{
        createTmpFolder();
        if (pictureFile.exists() && pictureFile.isFile()) {
            File destFile = new File(TMP_DIR + generateName);

            FileInputStream FileIn = null;
            FileOutputStream FileOut = null;
            FileChannel in = null;
            FileChannel out = null;

            try {
                FileIn = new FileInputStream(pictureFile);
                FileOut = new FileOutputStream(destFile);
                in = FileIn.getChannel();
                out = FileOut.getChannel();
                in.transferTo(0, in.size(), out);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    FileIn.close();
                    FileOut.close();
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Parse the picture to create the preview images
    public void parseImage() throws IOException {
        String previewImageName = IMAGE_PREVIEW_PREFIX + generateName;
        File target = new File(TMP_DIR + previewImageName);
        int dest_height = 120;
        int dest_width = 120;
        resize(dest_width, dest_height, target);
        imageKey = System.currentTimeMillis() * 10;
    }

    public void resizeImage() throws IOException {
        if (width > height) {
            des1000Height = 1000 * height / width;
        } else {
            des1000Width = 1000 * width / height;
        }
        File bigPicFile = new File(jpgTargetUrl + UpLoad.adminId + '/' + realName + "?imageView2_1_w_452_h_300");
        File middlePicFile = new File(jpgTargetUrl + UpLoad.adminId + '/' + realName + "?imageView2_1_w_452_h_148");
        File smallPicFile = new File(jpgTargetUrl + UpLoad.adminId + '/' + realName + "?imageView2_1_w_80_h_80");
        File picFile120 = new File(jpgTargetUrl + UpLoad.adminId + '/' + realName + "?imageView2_1_w_120_h_120");
        File picFile150 = new File(jpgTargetUrl + UpLoad.adminId + '/' + realName + "?imageView2_1_w_150_h_150");
        File picFile1000 = new File(jpgTargetUrl + UpLoad.adminId + '/' + realName + "?imageView2_1_w_1000_h_1000");
        resize(desBigWidth, desBigHeight, bigPicFile);
        resize(desMiddleWidth, desMiddleHeight, middlePicFile);
        resize(desSmallWidth, desSmallHeight, smallPicFile);
        resize(des120Width, des120Height, picFile120);
        resize(des150Width, des150Height, picFile150);
        resize(des1000Width, des1000Height, picFile1000);
    }

    private void resize(int width, int height, File target) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.getGraphics().drawImage(pictureImage, 0, 0, width, height, null);
        FileOutputStream out = new FileOutputStream(target);
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        encoder.encode(image);
        out.close();
    }

    // Convert the picture file to dzi format
    public void image2dzi() throws IOException {
        String source = TMP_DIR + generateName;
        String target = TMP_DIR + realName;
        String cmdDziTransform = "/usr/local/vips/bin/vips dzsave " + source + " " + target + " --suffix .png";
        Runtime.getRuntime().exec(cmdDziTransform);
        File dziFile = new File(TMP_DIR + realName + ".dzi");
        File jsFile = new File(TMP_DIR + realName + ".js");

        while (!dziFile.exists());
        FileInputStream FileIn = new FileInputStream(dziFile);
        FileOutputStream FileOut = new FileOutputStream(jsFile);
        FileChannel in = FileIn.getChannel();
        FileChannel out = FileOut.getChannel();

        in.transferTo(0, in.size(), out);

        FileIn.close();
        FileOut.close();
        in.close();
        out.close();
    }

    public void rename() throws IOException {
        File newName = new File(jpgTargetUrl + UpLoad.adminId + '/' + realName);
        tmpFile.renameTo(newName);
        File dziFile = new File(TMP_DIR + realName + ".dzi");
        newName = new File(dziTargetUrl + realName + ".dzi");
        dziFile.renameTo(newName);
        File jsFile = new File(TMP_DIR + realName + ".js");
        newName = new File(dziTargetUrl + realName + ".js");
        jsFile.renameTo(newName);
        File filesFile = new File(TMP_DIR + realName + "_files/");
        newName = new File(dziTargetUrl + realName + "_files/");
        filesFile.renameTo(newName);
        File tmpDir = new File(TMP_DIR);
    }

    public boolean uploadImage() throws IOException, InterruptedException {
        createDataFolder();
        resizeImage();
        rename();
        File s3headerFile = new File(dziTargetUrl + realName + "_files/");
        if (s3headerFile.isDirectory()) {
            UpLoad.s3Database.S3UploadDir("data/dzi/" + realName + "_files/", s3headerFile);
            UpLoad.s3Database.S3UploadFile("data/dzi/" + realName + ".dzi", new File(dziTargetUrl + realName + ".dzi"));
            UpLoad.s3Database.S3UploadFile("data/dzi/" + realName + ".js", new File(dziTargetUrl + realName + ".js"));
            return true;
        } else {
            return false;
        }
    }

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
