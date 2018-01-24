package in.rishikeshdarandale.aws.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Test;

import in.rishikeshdarandale.aws.AwsSignParams;
import in.rishikeshdarandale.aws.AwsSigner;

public class ImmutableRequestTest {
    @Test
    public void testImmutableRequest() {
        Request request = new ImmutableRequest("http://www.somehost.com");
        Request requestWithPath = request.path("/mypath");
        assertNotEquals(requestWithPath, request);
    }

    @Test
    public void testUri() {
        Request requestWithoutQueryString = new ImmutableRequest("http://www.somehost.com");
        assertNull(requestWithoutQueryString.getUri().getQuery());

        Request request = new ImmutableRequest("http://www.somehost.com:8080")
                            .path("/mypath")
                            .queryParams("key", "value1")
                            .queryParams("key", "value2")
                            .method(RequestMethod.POST)
                            .header("Content-Type", "application/json")
                            .body("{}")
                            .timeout(1000, 1000);
        assertEquals("www.somehost.com", request.getUri().getHost());
        assertEquals("/mypath", request.getUri().getPath());
        assertEquals("key=value1&key=value2", request.getUri().getQuery());
    }

    @Test
    public void testToString() {
        Request request = new ImmutableRequest("http://www.somehost.com:8080")
                .path("/mypath");
        StringBuilder sb = new StringBuilder(">>> Start of Request <<<\n")
                .append("GET /mypath HTTP/1.1\n")
                .append("Host: www.somehost.com:8080\n")
                .append("\n")
                .append(new String(new byte[0], Charset.defaultCharset())).append("\n")
                .append(">>> End of Request <<<\n");
        assertEquals(sb.toString(), request.toString());
    }

    @Test
    public void testImmutableHeaders() {
        Request request = new ImmutableRequest("http://www.somehost.com")
                                .header("Accept", "application/json");
        Request requestWithContentType = request.header("Content-Type", "application/json");
        assertEquals(2, request.getHeaders().size());
        assertNotEquals(requestWithContentType, request);
        assertEquals(3, requestWithContentType.getHeaders().size());
    }

    @Test
    public void testEqualsAndHashcode() {
        Request request1 = new ImmutableRequest("http://www.somehost.com");
        Request request2 = new ImmutableRequest("http://www.somehost.com");
        assertTrue(request2.equals(request1));
        assertEquals(request2.hashCode(), request1.hashCode());
        assertTrue(request1.equals(request1));
        assertEquals(request1.hashCode(), request1.hashCode());
        assertFalse(request1.equals(null));
        request1 = request1.queryParams("key", "value");
        assertFalse(request1.equals(request2));
        request2 = request2.queryParams("key", "value");
        assertTrue(request2.equals(request1));
        assertEquals(request2.hashCode(), request1.hashCode());
        request1 = request1.body("{}");
        assertFalse(request1.equals(request2));
        request2 = request2.body("{}");
        assertTrue(request2.equals(request1));
        assertEquals(request2.hashCode(), request1.hashCode());
        Request request3 = new ImmutableRequest("http://www.anotherhost.com");
        assertFalse(request1.equals(request3));
        assertFalse(request3.equals(new Object()));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testExecute() {
        Request request = new ImmutableRequest("http://www.somehost.com")
                .header("Accept", "application/json");
        request.execute();
    }

    @Test
    public void testSign() {
        Request request = new ImmutableRequest("http://www.somehost.com")
                .header("Accept", "application/json");
        AwsSignParams params = new AwsSignParams("id", "secret", "es");
        Request signedRequest = request.sign(params);
        assertNotNull(signedRequest.getHeaders().get(AwsSigner.AUTHORIZATION_HEADER));
        assertEquals(1, signedRequest.getHeaders().get(AwsSigner.AUTHORIZATION_HEADER).size());
    }

    @Test
    public void testBody() {
        Request request = new ImmutableRequest("http://www.somehost.com")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{\"message\": \"hello world\"}".getBytes(Charset.defaultCharset()));
        assertTrue(Arrays.equals("{\"message\": \"hello world\"}".getBytes(Charset.defaultCharset()),
                request.getBody()));
        byte[] body = request.getBody();
        body[0] = 9;
        assertTrue(Arrays.equals("{\"message\": \"hello world\"}".getBytes(Charset.defaultCharset()),
                request.getBody()));
    }
}
