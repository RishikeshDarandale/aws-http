package in.rishikeshdarandale.aws.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ImmutableResponseTest {
    @Test(expected=UnsupportedOperationException.class)
    public void testImmutableResponse() {
        Response response = getResponse();
        byte[] body = response.binary();
        body[1] = 8;
        assertFalse(Arrays.equals(body, response.binary()));
        String bodyString = response.body();
        bodyString = "";
        assertNotEquals(bodyString, response.body());
        response.headers().put("My-Header", Arrays.asList("My-Header-Value"));
    }

    @Test
    public void testToString() {
        Response response = getResponse();
        StringBuilder sb = new StringBuilder(">>> Start of Response <<<\n")
                .append("HTTP/1.1 200 OK\n")
                .append("Content-Type: application/json\n")
                .append("\n")
                .append("{\"message\": \"Hello World\"}").append("\n")
                .append(">>> End of Response <<<\n");
        assertEquals(sb.toString(), response.toString());
    }

    @Test
    public void testEqualsAndHashcode() {
        Response response = getResponse();
        assertEquals(response.hashCode(), response.hashCode());
        assertTrue(response.equals(response));
        assertFalse(response.equals(null));
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        Response response1 = new ImmutableResponse(200, "OK",
                headers, "{\"message\": \"Hello World\"}".getBytes(Charset.defaultCharset()));
        assertEquals(response.hashCode(), response1.hashCode());
        assertTrue(response.equals(response1));
        Response response2 = new ImmutableResponse(201, "CREATED",
                new HashMap<String, List<String>>(), "".getBytes(Charset.defaultCharset()));
        assertNotEquals(response1.hashCode(), response2.hashCode());
        assertFalse(response1.equals(response2));
        Response response3 = new ImmutableResponse(200, "OKAY",
                headers, "{\"message\": \"Hello World\"}".getBytes(Charset.defaultCharset()));
        assertNotEquals(response1.hashCode(), response3.hashCode());
        assertFalse(response1.equals(response3));
        Response response4 = new ImmutableResponse(199, "CONTINUE",
                headers, "{\"message\": \"Hello World\"}".getBytes(Charset.defaultCharset()));
        assertNotEquals(response1.hashCode(), response4.hashCode());
        assertFalse(response1.equals(response4));
        Response response5 = new ImmutableResponse(199, "CONTINUE", null, null);
        assertNotEquals(response1.hashCode(), response5.hashCode());
        assertFalse(response1.equals(response5));
    }

    @Test
    public void testGetAs() {
        Response response = getResponse();
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/something"));
        Response response1 = new ImmutableResponse(200, "OK",
                headers, "Hello World".getBytes(Charset.defaultCharset()));
        MessageObject object;
        try {
            object = response.getAs(MessageObject.class);
            assertEquals("Hello World", object.getMessage());
            object = response1.getAs(MessageObject.class);
            assertNull(object);
        } catch (CouldNotConvertException e) {
            e.printStackTrace();
        }
    }

    @Test(expected=CouldNotConvertException.class)
    public void testGetAsException() throws CouldNotConvertException {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        Response response = new ImmutableResponse(200, "OK",
                headers, "Hello World".getBytes(Charset.defaultCharset()));
        response.getAs(MessageObject.class);
    }

    private Response getResponse() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        Response response = new ImmutableResponse(200, "OK",
                headers, "{\"message\": \"Hello World\"}".getBytes(Charset.defaultCharset()));
        return response;
    }
}

class MessageObject {
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
