package in.rishikeshdarandale.aws;

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
    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    public void setAwsAccessKey(String awsAccessKey) {
        this.awsAccessKey = awsAccessKey;
    }
    public String getAwsAccessSecret() {
        return awsAccessSecret;
    }
    public void setAwsAccessSecret(String awsAccessSecret) {
        this.awsAccessSecret = awsAccessSecret;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((awsAccessKey == null) ? 0 : awsAccessKey.hashCode());
        result = prime * result + ((awsAccessSecret == null) ? 0 : awsAccessSecret.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
        return result;
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
        if (awsAccessKey == null) {
            if (other.awsAccessKey != null)
                return false;
        } else if (!awsAccessKey.equals(other.awsAccessKey))
            return false;
        if (awsAccessSecret == null) {
            if (other.awsAccessSecret != null)
                return false;
        } else if (!awsAccessSecret.equals(other.awsAccessSecret))
            return false;
        if (region == null) {
            if (other.region != null)
                return false;
        } else if (!region.equals(other.region))
            return false;
        if (serviceName == null) {
            if (other.serviceName != null)
                return false;
        } else if (!serviceName.equals(other.serviceName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AwsParams [awsAccessKey=" + mask(awsAccessKey, 4) + ", "
                + "awsAccessSecret=" + mask(awsAccessSecret, 0) + ", region="
                + region + ", serviceName=" + serviceName + "]";
    }

    private String mask(String toMask, int charToShow) {
        String masked =  null;
        if (toMask != null) {
            StringBuffer maskedBuffer = new StringBuffer();
            int masklen = toMask.length() - charToShow;
            for(int i=0;i<masklen;i++) {
                maskedBuffer.append('X');
            }
            maskedBuffer.append(toMask.substring(masklen, toMask.length()));
            masked = maskedBuffer.toString();
        }
        return masked;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }
}
