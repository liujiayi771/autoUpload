/**
 * Created by joey on 16-3-17.
 */
import java.io.File;
import java.util.Date;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

public class S3Transfer {
    /** AWS credential file, default location is ~/.aws/credentials */
    private AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();;

    /** The TransferManager object controlling the transfer to AWS */
    private TransferManager tx = null;

    /** The name of bucket in S3 */
    private String bucketName = "aliyuntest";

    private AmazonS3 s3 = null;

    /** The configuration of the AmazonS3 client */
    private ClientConfiguration configuration = new ClientConfiguration();

    /** Contain some state information */
    private Upload upload;
    private MultipleFileUpload MutiUpload;

    /** The construct function */
    public S3Transfer() {
        try {
            configuration.setConnectionTimeout(50_000);
            configuration.setMaxConnections(500);
            configuration.setSocketTimeout(50_000);
            configuration.setMaxErrorRetry(10);

            s3 = new AmazonS3Client(credentials, configuration);
            Region usWest2 = Region.getRegion(Regions.US_WEST_2);
            s3.setRegion(usWest2);

            tx = new TransferManager(s3);

        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
    }

    /** Tranfer a single file to S3 */
    public void S3UploadFile(String s3FileName, File localFile) {
        try {
            System.out.println(new Date() + " Upload file " + localFile.getAbsoluteFile() + " begin");
            upload = tx.upload(bucketName, s3FileName, localFile);
            upload.waitForCompletion();
            System.out.println(new Date() + " Upload file " + localFile.getAbsoluteFile() + " end");
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            System.out.println("ERROR: S3Transfer.S3UploadFile");
        }
    }

    /** Transfer a directory to S3 */
    public void S3UploadDir(String s3FileName, File localFile) {
        try {
            MutiUpload = tx.uploadDirectory(bucketName, s3FileName, localFile, true);
            MutiUpload.waitForCompletion();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            System.out.println("ERROR: S3Transfer.S3UploadDir");
        }
    }

    /** Shutdown the transfer and client connection */
    public void shutdownClient() { tx.shutdownNow(true); }
}
