import java.io.File;

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

public class S3TransferProgressSample {

    private AWSCredentials credentials = null;
    private TransferManager tx;
    private String bucketName;
    private AmazonS3 s3 = null;
    private ClientConfiguration configuration = null;

    private MultipleFileUpload MutiUpload;
    private Upload upload;

    public S3TransferProgressSample() throws InterruptedException {
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
            configuration = new ClientConfiguration();
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

        bucketName = "aliyuntest";

    }

    public void S3UploadFile(String s3fileName, File localFile) throws InterruptedException {

        System.out.println("Upload file %" + s3fileName + "% begin");
        upload = tx.upload(bucketName, s3fileName, localFile);
        upload.waitForCompletion();
        System.out.println("Upload file end");
    }

    public void S3UploadDir(String s3fileName, File localFile) throws InterruptedException {
        System.out.println("Upload directory %" + s3fileName + "% begin");
        MutiUpload = tx.uploadDirectory(bucketName, s3fileName, localFile, true);
        MutiUpload.waitForCompletion();
        System.out.println("Upload directory end");
    }

    public void shutdownClient() {
        tx.shutdownNow(true);
    }
}
