package in.rishikeshdarandale.aws.utils;

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;

import org.junit.Test;

import in.rishikeshdarandale.aws.utils.EncodeUtils;

/* Test classes are extended from utility classes to increase the test coverage[1].
   Basically, I opted for not to influence the actual code to increase the test
   coverage.

   See: https://stackoverflow.com/questions/9700179/junit-testing-helper-class-with-only-static-methods

   [1] https://stackoverflow.com/a/9700354/8101556
*/
public class EncodeUtilsTest extends EncodeUtils {
    @Test
    public void testEncode() {
        assertEquals("/documents%20and%20settings/", EncodeUtils.encode("/documents and settings/", true));
        assertEquals("%2Fdocuments%20and%20settings%2F", EncodeUtils.encode("/documents and settings/", false));
        assertEquals("/documents~and~settings/", EncodeUtils.encode("/documents~and~settings/", true));
    }

    @Test
    public void testGenerateHexCode() {
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                EncodeUtils.generateHex("".getBytes()));
    }

    @Test
    public void testHmacSHA256() throws Exception {
        // below code is generated using https://www.liavaag.org/English/SHA-Generator/HMAC/
        assertEquals("2978f26e613778b12f10dfc2144f01fc82bc3af63dea11f5ec0b58e733fbe2aa", 
                EncodeUtils.bytesToHex(EncodeUtils.HmacSHA256("plain text", "key".getBytes(Charset.defaultCharset()))));
    }

    @Test
    public void testByteArrayToHex() throws Exception {
        byte[] array = {(byte)15 };
        assertEquals("0f", EncodeUtils.bytesToHex(array));
        byte[] array1 = {(byte)0, (byte)0, (byte)134, (byte)0, (byte)61};
        assertEquals("000086003d", EncodeUtils.bytesToHex(array1));
    }
}
