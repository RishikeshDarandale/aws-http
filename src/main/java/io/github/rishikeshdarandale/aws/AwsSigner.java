package io.github.rishikeshdarandale.aws;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.github.rishikeshdarandale.aws.http.Request;
import io.github.rishikeshdarandale.aws.utils.DateUtils;
import io.github.rishikeshdarandale.aws.utils.EncodeUtils;

// http://www.javaquery.com/2016/01/aws-version-4-signing-process-complete.html
// https://github.com/aws/aws-sdk-java/blob/master/aws-java-sdk-core/src/main/java/com/amazonaws/auth/AWS4Signer.java

/**
 * This is AWS signer class for HTTP request.
 *
 * @author Rishikesh Darandale <Rishikesh.Darandale@gmail.com>
 * @See <a href="https://docs.aws.amazon.com/general/latest/gr/
 *         sigv4_signing.html">Signing AWS Requests with Signature Version 4
 *         </a>
 */
public class AwsSigner {
    private Request request;
    private AwsSignParams params;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String NEW_LINE = "\n";
    public static final String AWS4_REQUEST_STRING = "aws4_request";

    public AwsSigner(Request request, AwsSignParams params) {
        this.request = request;
        this.params = params;
    }

    /**
     * Task 1: Create a Canonical Request for Signature Version 4
     * 
     * @see  <a href="https://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html">
     *       Task 1: Create a Canonical Request for Signature Version 4</a>
     *
     * @return the canonical request
     */
    public String getCanonicalRequest() {
        return new StringBuilder(this.request.getMethod().toString()).append(NEW_LINE)
                .append(getCanonicalURI()).append(NEW_LINE)
                .append(getCanonicalQueryString()).append(NEW_LINE)
                .append(getCanonicalHeaderString()).append(NEW_LINE)
                .append(getSignedHeaders()).append(NEW_LINE)
                .append(getHashedPayload())
                .toString();
    }

    /**
     * Task 2: Create a String to Sign for Signature Version 4
     * 
     * @see  <a href="https://docs.aws.amazon.com/general/latest/gr/sigv4-create-string-to-sign.html">
     *       Task 2: Create a String to Sign for Signature Version 4</a>
     * @return
     */
    public String getStringToSign() {
        return new StringBuilder("AWS4-HMAC-SHA256").append(NEW_LINE)
                .append(DateUtils.getDate(this.params.getTimeInMillis(), "yyyyMMdd'T'HHmmss'Z'")).append(NEW_LINE)
                .append(getCredentialScope()).append(NEW_LINE)
                .append(EncodeUtils.generateHex(getCanonicalRequest().getBytes(Charset.defaultCharset())))
                .toString();
    }

    /**
     * Task 3: Calculate the Signature for AWS Signature Version 4
     *
     * @see  <a href="https://docs.aws.amazon.com/general/latest/gr/sigv4-calculate-signature.html">
     *       Task 3: Calculate the Signature for AWS Signature Version 4</a>
     *
     * @param stringToSign
     * @return
     */
    public String calculateSignature(String stringToSign) {
        String signature = null;
        try {
            byte[] signatureKey = getDerivedSignKey();
            signature = EncodeUtils.bytesToHex(EncodeUtils.hmacSHA256(stringToSign, signatureKey));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return signature;
    }

    /**
     * Task 4: Add the Signing Information to the Request
     *
     * @see  <a href="https://docs.aws.amazon.com/general/latest/gr/sigv4-add-signature-to-request.html">
     *       Task 4: Add the Signing Information to the Request</a>
     *
     * @return signing information needed for Authorization header
     */
    public String getSigningInformation() {
        return new StringBuilder()
            .append("AWS4-HMAC-SHA256")
            .append(" Credential=")
            .append(this.params.getAwsAccessKey()).append("/").append(getCredentialScope()).append(",")
            .append(" SignedHeaders=").append(getSignedHeaders()).append(",")
            .append(" Signature=").append(calculateSignature(getStringToSign()))
            .toString();
    }

    // task 1.2
    String getCanonicalURI() {
        String uri = this.request.getUri().normalize().getPath();
        return EncodeUtils.encode(uri, true);
    }

    // task 1.3
    String getCanonicalQueryString() {
        if (Objects.isNull(this.request.getUri().getQuery())) {
            return "";
        }
        return Arrays.stream(this.request.getUri().getQuery().split("&"))
                     .map( entry -> {
                            String [] queryPair = entry.split("=");
                            return new StringBuilder()
                                        .append(EncodeUtils.encode(queryPair[0], false))
                                        .append("=")
                                        .append(EncodeUtils.encode(queryPair[1], false))
                                        .toString();
                     })
                     .collect(Collectors.joining("&"));
    }

    // task 1.4
    String getCanonicalHeaderString() {
        Map<String, List<String>> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.putAll(this.request.getHeaders());
        return map
                .keySet()
                .stream()
                .map(key -> key.toLowerCase())
                .sorted()
                .map( key -> {
                    String multiValue = map.get(key).stream().map(value -> {
                        return new StringBuilder()
                                    .append(value.trim().replaceAll(" +", " ")) // trim all the inner spaces
                                    .toString();
                    }).collect(Collectors.joining(","));

                    return new StringBuilder(key)
                            .append(":")
                            .append(multiValue)
                            .append("\n")
                            .toString();
                }).collect(Collectors.joining());
    }

    // task 1.5
    String getSignedHeaders() {
        return this.request.getHeaders()
                .keySet()
                .stream()
                .map(key -> key.toLowerCase())
                .sorted()
                .collect(Collectors.joining(";"));
    }

    // task 1.6
    String getHashedPayload() {
        return EncodeUtils.generateHex(this.request.getBody()).toLowerCase();
    }

    // task 2.3
    String getCredentialScope() {
        return new StringBuilder(DateUtils.getDate(this.params.getTimeInMillis(), "yyyyMMdd")).append("/")
                .append(this.params.getRegion()).append("/")
                .append(this.params.getServiceName()).append("/")
                .append(AWS4_REQUEST_STRING)
                .toString();
    }

    // task 3.1
    // https://docs.aws.amazon.com/general/latest/gr/signature-v4-examples.html#signature-v4-examples-java
    byte[] getDerivedSignKey() throws Exception {
        byte[] kSecret = ("AWS4" + this.params.getAwsAccessSecret()).getBytes("UTF8");
        byte[] kDate = EncodeUtils.hmacSHA256(DateUtils.getDate(this.params.getTimeInMillis(), "yyyyMMdd"), kSecret);
        byte[] kRegion = EncodeUtils.hmacSHA256(this.params.getRegion(), kDate);
        byte[] kService = EncodeUtils.hmacSHA256(this.params.getServiceName(), kRegion);
        byte[] kSigning = EncodeUtils.hmacSHA256(AWS4_REQUEST_STRING, kService);
        return kSigning;
    }
}
