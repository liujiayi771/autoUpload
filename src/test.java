import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by joey on 16-3-14.
 */
public class test {
    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        File f = new File("/home/joey/pictures");
        S3Database s3Database = new S3Database();
        s3Database.S3UploadDir("test/", f);
    }
}
