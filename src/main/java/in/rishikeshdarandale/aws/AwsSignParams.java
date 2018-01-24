package in.rishikeshdarandale.aws;

import java.util.Objects;

/**
 * This holds the AWS credentials required for signing the HTTP request.
 *
 * @author Rishikesh Darandale <Rishikesh.Darandale@gmailcom>
 * @see <a href="https://docs.aws.amazon.com/general/latest/gr/
 *         aws-sec-cred-types.html#access-keys-and-secret-access-keys">
 *         Access Keys (Access Key ID and Secret Access Key)</a>
 *
 */
public class AwsSignParams {
    private String awsAccessKey;
    private String awsAccessSecret;
    private String region;
    private String serviceName;
    // record the time at the start of signing process & use throughout the signing process
    private long timeInMillis;

    public AwsSignParams(String awsAccessKey, String awsAccessSecret, String region, String serviceName) {
        this.awsAccessKey = awsAccessKey;
        this.awsAccessSecret = awsAccessSecret;
        this.region = region;
        this.serviceName = serviceName;
    }

    public AwsSignParams(String awsAccessKey, String awsAccessSecret, String serviceName) {
        this(awsAccessKey, awsAccessSecret, "us-east-1", serviceName);
    }

    public AwsSignParams() {}

    public String getAwsAccessKey() {
        return awsAccessKey;
    }
    public String getAwsAccessSecret() {
        return awsAccessSecret;
    }
    public String getRegion() {
        return region;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public long getTimeInMillis() {
        return timeInMillis;
    }
    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(awsAccessKey, awsAccessSecret, region, serviceName, timeInMillis);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AwsSignParams other = (AwsSignParams) obj;
        return Objects.equals(awsAccessKey, other.awsAccessKey) &&
                Objects.equals(awsAccessSecret, other.awsAccessSecret) &&
                Objects.equals(region, other.region) &&
                Objects.equals(serviceName, other.serviceName) &&
                Objects.equals(timeInMillis, other.timeInMillis);
    }

    @Override
    public String toString() {
        return "AwsSignParams [awsAccessKey=" + mask(awsAccessKey, 4) + ", "
                + "awsAccessSecret=" + mask(awsAccessSecret, 0) + ", region="
                + region + ", serviceName=" + serviceName
                + ", timeInMillis=" + timeInMillis + "]";
    }

    String mask(String toMask, int charToShow) {
        String masked =  null;
        if (toMask != null) {
            StringBuilder maskedBuffer = new StringBuilder();
            int masklen = toMask.length() - charToShow;
            for(int i=0;i<masklen;i++) {
                maskedBuffer.append('X');
            }
            maskedBuffer.append(toMask.substring(masklen, toMask.length()));
            masked = maskedBuffer.toString();
        }
        return masked;
    }
}
