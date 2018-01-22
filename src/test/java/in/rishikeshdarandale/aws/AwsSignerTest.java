package in.rishikeshdarandale.aws;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import in.rishikeshdarandale.aws.AwsSigner;
import in.rishikeshdarandale.aws.http.ImmutableRequest;
import in.rishikeshdarandale.aws.http.Request;
import in.rishikeshdarandale.aws.utils.DateUtils;

public class AwsSignerTest {
    Request request;

    @Before
    public void before() {
        request = new ImmutableRequest("http://www.somehost.com");
    }

    @Test
    public void testGetCanonicalURI() throws UnsupportedEncodingException {
        request = request.path("/documents%20and%20settings/");
        assertEquals("/documents%20and%20settings/", new AwsSigner(request, null).getCanonicalURI());
    }

    @Test
    public void testGetCanonicalQueryString() throws UnsupportedEncodingException {
        request = request.path("/documents%20and%20settings/")
                         .queryParams("Version", "2010-05-08")
                         .queryParams("X-Amz-Date", "20150830T123600Z")
                         .queryParams("Action", "ListUsers")
                         .queryParams("X-Amz-Algorithm", "AWS4-HMAC-SHA256")
                         .queryParams("X-Amz-SignedHeaders", "content-type;host;x-amz-date")
                         .queryParams("X-Amz-Credential", "AKIDEXAMPLE/20150830/us-east-1/iam/aws4_request");
        StringBuilder queryParam = new StringBuilder()
                .append("Action=ListUsers&")
                .append("Version=2010-05-08&")
                .append("X-Amz-Algorithm=AWS4-HMAC-SHA256&")
                .append("X-Amz-Credential=AKIDEXAMPLE%2F20150830%2Fus-east-1%2Fiam%2Faws4_request&")
                .append("X-Amz-Date=20150830T123600Z&")
                .append("X-Amz-SignedHeaders=content-type%3Bhost%3Bx-amz-date");
        assertEquals(queryParam.toString(), new AwsSigner(request, null).getCanonicalQueryString());
    }

    @Test
    public void testGetCanonicalHeaderString() {
        request = request.path("/documents%20and%20settings/")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .header("My-header1", "    a   b   c  ")
                .header("X-Amz-Date", "20150830T123600Z")
                .header("My-Header2", "    \"a   b   c\"  ");
        StringBuilder headers = new StringBuilder()
               .append("content-type:application/x-www-form-urlencoded; charset=utf-8\n")
               .append("host:www.somehost.com\n")
               .append("my-header1:a b c\n")
               .append("my-header2:\"a b c\"\n")
               .append("x-amz-date:20150830T123600Z\n");
        assertEquals(headers.toString(), new AwsSigner(request, null).getCanonicalHeaderString());
    }

    @Test
    public void testGetSignedHeaders() {
        request = request.path("/documents%20and%20settings/")
                .header("X-Amz-Date", "20150830T123600Z")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        assertEquals("content-type;host;x-amz-date", new AwsSigner(request, null).getSignedHeaders());
    }

    @Test
    public void testGetHashedPayload() {
        request = request.path("/documents%20and%20settings/")
                .header("X-Amz-Date", "20150830T123600Z")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                new AwsSigner(request, null).getHashedPayload());
    }

    @Test
    public void testGetCanonicalRequest() {
        StringBuilder sb = new StringBuilder()
                .append("GET").append("\n")
                .append("/").append("\n")
                .append("Action=ListUsers&Version=2010-05-08").append("\n")
                .append("content-type:application/x-www-form-urlencoded; charset=utf-8").append("\n")
                .append("host:iam.amazonaws.com").append("\n")
                .append("x-amz-date:20150830T123600Z").append("\n")
                .append("\n")
                .append("content-type;host;x-amz-date").append("\n")
                .append("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        request = new ImmutableRequest("https://iam.amazonaws.com/").queryParams("Action", "ListUsers")
                    .queryParams("Version", "2010-05-08")
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                    .header("X-Amz-Date", "20150830T123600Z");
        assertEquals(sb.toString(),  new AwsSigner(request, null).getCanonicalRequest());
    }

    @Test
    public void testGetStringToSign() {
        AwsSignParams params = new AwsSignParams("ACCESS_KEY", "SECRET_KEY", "iam");
        params.setTimeInMillis(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder()
                .append("AWS4-HMAC-SHA256").append("\n")
                .append(DateUtils.getDate(params.getTimeInMillis(), "yyyyMMdd'T'HHmmss'Z'")).append("\n")
                .append(DateUtils.getDate(params.getTimeInMillis(), "yyyyMMdd"))
                .append("/us-east-1/iam/aws4_request").append("\n")
                .append("f536975d06c0309214f805bb90ccff089219ecd68b2577efef23edd43b7e1a59");
        request = new ImmutableRequest("https://iam.amazonaws.com/").queryParams("Action", "ListUsers")
                .queryParams("Version", "2010-05-08")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .header("X-Amz-Date", "20150830T123600Z");

        assertEquals(sb.toString(),  new AwsSigner(request, params).getStringToSign());
    }

    @Test
    public void testVanillaRequestSignaure() throws UnsupportedEncodingException {
        AwsSignParams params = new AwsSignParams("AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY", "service");
        params.setTimeInMillis(1440938160000L);
        request = new ImmutableRequest("https://example.amazonaws.com/")
                .header("X-Amz-Date", "20150830T123600Z");
        AwsSigner signer = new AwsSigner(request, params);
        StringBuilder sb = new StringBuilder()
                .append("GET").append("\n")
                .append("/").append("\n")
                .append("\n")
                .append("host:example.amazonaws.com").append("\n")
                .append("x-amz-date:20150830T123600Z").append("\n")
                .append("\n")
                .append("host;x-amz-date").append("\n")
                .append("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        // verify canonical request
        assertEquals(sb.toString(), signer.getCanonicalRequest());
        sb = new StringBuilder()
                .append("AWS4-HMAC-SHA256").append("\n")
                .append(DateUtils.getDate(params.getTimeInMillis(), "yyyyMMdd'T'HHmmss'Z'")).append("\n")
                .append(DateUtils.getDate(params.getTimeInMillis(), "yyyyMMdd"))
                .append("/us-east-1/service/aws4_request").append("\n")
                .append("bb579772317eb040ac9ed261061d46c1f17a8133879d6129b6e1c25292927e63");
        // verify the string to sign
        assertEquals(sb.toString(), signer.getStringToSign());
        // verify the signature value
        assertEquals("5fa00fa31553b73ebf1942676e86291e8372ff2a2260956d9b8aae1d763fbf31", signer.calculateSignature(signer.getStringToSign()));
        sb = new StringBuilder("AWS4-HMAC-SHA256 Credential=AKIDEXAMPLE/20150830/us-east-1/service/aws4_request,"
                + " SignedHeaders=host;x-amz-date,"
                + " Signature=5fa00fa31553b73ebf1942676e86291e8372ff2a2260956d9b8aae1d763fbf31");
        // verify final authorization header value
        assertEquals(sb.toString(), signer.getSigningInformation());
    }

    @Test
    public void testHeaderKeyDuplicateRequestSignature() {
        AwsSignParams params = new AwsSignParams("AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY", "service");
        params.setTimeInMillis(1440938160000L);
        request = new ImmutableRequest("https://example.amazonaws.com/")
                .header("My-Header1", "value2")
                .header("My-Header1", "value2")
                .header("My-Header1", "value1")
                .header("X-Amz-Date", "20150830T123600Z");
        AwsSigner signer = new AwsSigner(request, params);
        StringBuilder sb = new StringBuilder()
                .append("GET").append("\n")
                .append("/").append("\n")
                .append("\n")
                .append("host:example.amazonaws.com").append("\n")
                .append("my-header1:value2,value2,value1").append("\n")
                .append("x-amz-date:20150830T123600Z").append("\n")
                .append("\n")
                .append("host;my-header1;x-amz-date").append("\n")
                .append("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        System.out.println("Request: \n" + request);
        // verify canonical request
        assertEquals(sb.toString(), signer.getCanonicalRequest());
        sb = new StringBuilder()
                .append("AWS4-HMAC-SHA256").append("\n")
                .append(DateUtils.getDate(params.getTimeInMillis(), "yyyyMMdd'T'HHmmss'Z'")).append("\n")
                .append(DateUtils.getDate(params.getTimeInMillis(), "yyyyMMdd"))
                .append("/us-east-1/service/aws4_request").append("\n")
                .append("dc7f04a3abfde8d472b0ab1a418b741b7c67174dad1551b4117b15527fbe966c");
        // verify the string to sign
        assertEquals(sb.toString(), signer.getStringToSign());
        // verify the signature value
        assertEquals("c9d5ea9f3f72853aea855b47ea873832890dbdd183b4468f858259531a5138ea", signer.calculateSignature(signer.getStringToSign()));
        sb = new StringBuilder("AWS4-HMAC-SHA256 Credential=AKIDEXAMPLE/20150830/us-east-1/service/aws4_request,"
                + " SignedHeaders=host;my-header1;x-amz-date,"
                + " Signature=c9d5ea9f3f72853aea855b47ea873832890dbdd183b4468f858259531a5138ea");
        // verify final authorization header value
        assertEquals(sb.toString(), signer.getSigningInformation());
    }
}
