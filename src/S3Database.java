import java.io.File;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * Created by joey on 16-3-14.
 */
public class S3Database {

    private String bucketName = "aliyuntest";
    private AWSCredentials credentials = null;
    private AmazonS3 s3 = null;
    private Region usWest2 = null;

    public S3Database() {
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        s3 = new AmazonS3Client(credentials);
        usWest2 = Region.getRegion(Regions.US_WEST_2);
        s3.setRegion(usWest2);
        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon S3");
        System.out.println("===========================================\n");
    }

    public void S3UploadFile(String s3fileName, File localFile) throws InterruptedException {
        try {
            System.out.println("Uploading a new object to S3 from a file");
            System.out.println("Uploading: " + s3fileName + "\n");
            s3.putObject(new PutObjectRequest(bucketName, s3fileName, localFile));
        } catch (AmazonServiceException ase) {
            AmazonServiceExceptionLog(ase);
        } catch (AmazonClientException ace) {
            AmazonClientExceptionLog(ace);
        }
    }

    public void S3UploadDir(String s3fileName, File localFile) throws InterruptedException {
        try {
            System.out.println("Uploading a new object to S3 from a directory");
            for (File file : localFile.listFiles()) {
                if (file.isFile()) {
                    S3UploadFile(s3fileName + file.getName(), file);
                } else {
                    S3UploadDir(s3fileName + file.getName() + '/', file);
                }
            }
        } catch (AmazonServiceException ase) {
            AmazonServiceExceptionLog(ase);
        } catch (AmazonClientException ace) {
            AmazonClientExceptionLog(ace);
        }
    }

    private void AmazonServiceExceptionLog(AmazonServiceException ase) {
        System.out.println("Caught an AmazonServiceException, which means your request made it "
                + "to Amazon S3, but was rejected with an error response for some reason.");
        System.out.println("Error Message:    " + ase.getMessage());
        System.out.println("HTTP Status Code: " + ase.getStatusCode());
        System.out.println("AWS Error Code:   " + ase.getErrorCode());
        System.out.println("Error Type:       " + ase.getErrorType());
        System.out.println("Request ID:       " + ase.getRequestId());
    }

    private void AmazonClientExceptionLog(AmazonClientException ace) {
        System.out.println("Caught an AmazonClientException, which means the client encountered "
                + "a serious internal problem while trying to communicate with S3, "
                + "such as not being able to access the network.");
        System.out.println("Error Message: " + ace.getMessage());
    }
}
