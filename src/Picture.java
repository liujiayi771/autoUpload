import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;

/**
 * Created by joey on 16-3-16.
 */
public class Picture {
    /** The path of the tmp directory */
    protected static final String PICTURE_TMP_DIR = "/app-data/ljy/tmp/";
    //protected static final String PICTURE_TMP_DIR = "/home/joey/tmp/";

    /** The path of the data directory */
    protected static final String PICTURE_DATA_DIR = "/app-data/ljy/data/";
    //protected static final String PICTURE_DATA_DIR = "/home/joey/data/";

    private String jpgTargetUrl = PICTURE_DATA_DIR + "jpg/";
    private String dziTargetUrl = PICTURE_DATA_DIR + "dzi/";

    /** The prefix name of the preview picture */
    private String PICTURE_PREVIEW_PREFIX = "preview_";

    /** The file object of the original picture file */
    private File originalPictureFile;

    /** The file object of the tmp picture file */
    private File tmpPictureFile;

    /** The Image object of the picture */
    private Image pictureImage;

    /** The type of the picture */
    private String pictureType;

    /** The name of the original picture file */
    private String originalPictureName;

    /** The name of the tmp picture */
    private String tmpPictureName;

    /** The name of the tmp picture without extension */
    private String realName;

    /** The path of the original picture file */
    private String originalPicturePath;

    /** The path of the tmp picture file */
    private String tmpPicturePath;

    /** The width of the picture */
    private int width = 0;

    /** The height of the picture */
    private int height = 0;

    /** The picture ID generating randomly and uniquely */
    private long pictureID;

    /** Construct function */
    public Picture(File inputPictureFile) {
        try {
            originalPictureFile = inputPictureFile;
            originalPictureName = originalPictureFile.getName();
            originalPicturePath = originalPictureFile.getAbsolutePath();
            pictureType = originalPictureName.substring(originalPictureName.lastIndexOf('.') + 1, originalPictureName.length());

            tmpPictureName = generateTmpPictureName();
            realName = tmpPictureName.substring(0, tmpPictureName.lastIndexOf('.') - 1);
            tmpPictureFile = new File(jpgTargetUrl + Upload.adminID + '/' + realName);

            pictureImage = ImageIO.read(originalPictureFile);
            width = pictureImage.getWidth(null);
            height = pictureImage.getHeight(null);

            generateTmpPictureFile();
            generateDziFile();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: Picture.Picture");
        }
    }

    /** Create the directory of tmp */
    private void createTmpDirectory() {
        File tmpDir = new File(PICTURE_TMP_DIR);
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
    }

    /** Create the directory of data */
    private void createDataDirectory() {
        File dataDir = new File(PICTURE_DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        File jpgTargrtDir = new File(jpgTargetUrl);
        if (!jpgTargrtDir.exists()) {
            jpgTargrtDir.mkdir();
        }

        File dziTargetDir = new File(dziTargetUrl);
        if (!dziTargetDir.exists()) {
            dziTargetDir.mkdir();
        }

        File userIDDir = new File(jpgTargetUrl + Upload.adminID);
        if (!userIDDir.exists()) {
            userIDDir.mkdir();
        }
    }

    /** Generate the name of tmp picture file randomly and uniquely */
    private String generateTmpPictureName() {
        long currentTime = System.currentTimeMillis();
        String prefix = String.valueOf(currentTime);
        int randomNum = (int)Math.floor(Math.random() * 100_000_000);
        String suffix = String.valueOf(randomNum);
        return prefix + suffix + '.' + pictureType;
    }

    /** Generate the tmp picture file in tmp directory */
    private void generateTmpPictureFile() {
        createDataDirectory();

        if (originalPictureFile.exists() && originalPictureFile.isFile()) {
            FileInputStream fileInputStream = null;
            FileOutputStream fileOutputStream = null;
            FileChannel channelIn = null;
            FileChannel channelOut = null;

            try {
                fileInputStream = new FileInputStream(originalPictureFile);
                fileOutputStream = new FileOutputStream(tmpPictureFile);
                channelIn = fileInputStream.getChannel();
                channelOut = fileOutputStream.getChannel();
                channelIn.transferTo(0, channelIn.size(), channelOut);
            } catch (FileNotFoundException fnf) {
                fnf.printStackTrace();
                System.out.println("ERROR: Picture.generateTmpPictureFile");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("ERROR: Picture.generateTmpPictureFile");
            } finally {
                try {
                    fileInputStream.close();
                    fileOutputStream.close();
                    channelIn.close();
                    channelOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ERROR: Picture.generateTmpPictureFile");
                }
            }
        } else {
            System.out.println("The original picture file " + originalPictureName +
            " is not a file or does not exist");
            System.exit(1);
        }
    }

    /** Help function to resize the picture*/
    private void resizePicture(int width, int height, File destination) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            image.getGraphics().drawImage(pictureImage, 0, 0, width, height, null);
            FileOutputStream fileOutputStream = new FileOutputStream(destination);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fileOutputStream);
            encoder.encode(image);
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
            System.out.println("ERROR: Picture.resizePicture");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: Picture.resizePicture");
        }
    }

    /** Generate the dzi file */
    private void generateDziFile() {
        try {
            String source = PICTURE_DATA_DIR + "jpg/" + Upload.adminID + '/' + realName;
            String destination = dziTargetUrl + realName;

            String cmdDziTransform = "/usr/local/vips/bin/vips dzsave " + source + " " + destination + " --suffix .png";
            Runtime.getRuntime().exec(cmdDziTransform);

            File dziFile = new File(dziTargetUrl + realName + ".dzi");
            File jsFile = new File(dziTargetUrl + realName + ".js");

            // Block when dzi file has not generated
            while (!dziFile.exists());

            FileInputStream fileInputStream = new FileInputStream(dziFile);
            FileOutputStream fileOutputStream = new FileOutputStream(jsFile);
            FileChannel channelIn = fileInputStream.getChannel();
            FileChannel channelOut = fileOutputStream.getChannel();

            channelIn.transferTo(0, channelIn.size(), channelOut);

            fileInputStream.close();
            fileOutputStream.close();
            channelIn.close();
            channelOut.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: Picture.generateDziFile");
        }
    }

    /** Generate different size of pictures using in the website */
    private void generateDifferentSizePicture() {
        File bigPicFile = new File(jpgTargetUrl + Upload.adminID + '/' + realName + "?imageView2_1_w_452_h_300");
        File middlePicFile = new File(jpgTargetUrl + Upload.adminID + '/' + realName + "?imageView2_1_w_452_h_148");
        File smallPicFile = new File(jpgTargetUrl + Upload.adminID + '/' + realName + "?imageView2_1_w_80_h_80");
        File picFile120 = new File(jpgTargetUrl + Upload.adminID + '/' + realName + "?imageView2_1_w_120_h_120");
        File picFile150 = new File(jpgTargetUrl + Upload.adminID + '/' + realName + "?imageView2_1_w_150_h_150");
        File picFile1000 = new File(jpgTargetUrl + Upload.adminID + '/' + realName + "?imageView2_1_w_1000_h_1000");

        if (width > height) {
            resizePicture(1000, 1000 * height / width, picFile1000);
        } else {
            resizePicture(1000 * width / height, 1000, picFile1000);
        }

        resizePicture(452, 400, bigPicFile);
        resizePicture(452, 148, middlePicFile);
        resizePicture(80, 80, smallPicFile);
        resizePicture(120, 120, picFile120);
        resizePicture(150, 150, picFile150);

    }

    /** Upload all kinds of picture files to AWS S3 database */
    public boolean uploadPicture() {
        createDataDirectory();
        generateDifferentSizePicture();

        File s3HeaderFile = new File(dziTargetUrl +  realName + "_files/");
        if (s3HeaderFile.isDirectory()) {
            Upload.s3Transfer.S3UploadDir("data/dzi/" + realName + "_files/", s3HeaderFile);
            Upload.s3Transfer.S3UploadFile("data/dzi/" + realName + ".dzi", new File(dziTargetUrl + realName + ".dzi"));
            Upload.s3Transfer.S3UploadFile("data/dzi/" + realName + ".js", new File(dziTargetUrl + realName + ".js"));
            return true;
        } else {
            return false;
        }
    }

    public String getOriginalPictureName() { return originalPictureName; }

    public String getRealName() { return realName; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }
}
