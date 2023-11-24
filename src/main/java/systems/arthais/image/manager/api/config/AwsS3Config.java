package systems.arthais.image.manager.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AwsS3Config {

    @Value("${aws.access.key.id}")
    private String awsAccessKeyId;

    @Value("${aws.access.key.secret}")
    private String awsAccessKeySecret;

    @Value("${aws.s3.region}")
    private String awsS3Region;

    @Bean
    AmazonS3 amazonS3() {
        log.info("Configuring Amazon S3 client...");
        
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsAccessKeySecret);
        log.debug("AWS credentials created: Access Key ID: {}", awsAccessKeyId);

        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withRegion(awsS3Region)
                .withCredentials(awsStaticCredentialsProvider);

        log.debug("Amazon S3 client builder configured for region: {}", awsS3Region);
        
        AmazonS3 s3Client = builder.build();
        log.info("Amazon S3 client successfully configured.");

        return s3Client;
    }
}
