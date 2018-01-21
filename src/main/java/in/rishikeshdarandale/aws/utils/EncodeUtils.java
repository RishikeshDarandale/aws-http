package in.rishikeshdarandale.aws.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility methods for encode functionality
 * 
 * @author Rishikesh Darandale <Rishikesh.Darandale@gmail.com>
 *
 */
public class EncodeUtils {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final Map<String, String> SUBSTITUTE
        = Stream.of(new Object[] { "+", "%20" }, new Object[] { "*", "%2A" },
                    new Object[] { "%7E", "~" }, new Object[] { "%2F", "/" })
                .collect(Collectors.toMap(s -> (String) s[0], s -> (String) s[1]));
    private static final Pattern PATTERN
        = Pattern.compile(SUBSTITUTE.keySet().stream().map(s -> Pattern.quote(s)).collect(Collectors.joining("|")));
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * This utility method to encode the URL with little deviation from {@link URLEncoder#encode(String)}
     *
     * @param srcString
     * @param path
     *          if true then / will not be encoded.
     * @return the encoded string
     */
    public static String encode(String srcString, Boolean path) {
        Objects.requireNonNull(srcString, "String to be encoded must be provided.");
        StringBuffer encodedString = new StringBuffer();
        try {
            Matcher m = PATTERN.matcher(URLEncoder.encode(srcString, DEFAULT_ENCODING));
            while (m.find()) {
                String matchGroup = m.group(0);
                if (!path && "%2F".equals(matchGroup)) {
                    m.appendReplacement(encodedString, matchGroup);
                } else {
                    m.appendReplacement(encodedString, SUBSTITUTE.get(matchGroup));
                }
            }
            m.appendTail(encodedString);
        } catch(UnsupportedEncodingException exception) {
            System.out.println("Encoding exception occurred...");
        }
        return encodedString.toString();
    }

    /**
     * Generate Hex code of String.
     *
     * @param data
     * @return
     */
    public static String generateHex(byte[] data) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data);
            byte[] digest = messageDigest.digest();
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException  e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Calculate the HMAC 
     *
     * @See <a href="https://docs.aws.amazon.com/general/latest/gr/signature-v4-examples.html#signature-v4-examples-java">
     *              Deriving the Signing Key with Java</a>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] HmacSHA256(String data, byte[] key) throws Exception {
        String algorithm="HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF8"));
    }


    /**
     * Convert byte array to hex array
     * 
     * @See <a href="https://stackoverflow.com/a/9855338/8101556">
     *         How to convert a byte array to a hex string in Java?</a>
     *
     * @param bytes that need to be converted into hex
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars).toLowerCase();
    }
}
