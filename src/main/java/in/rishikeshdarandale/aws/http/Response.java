package in.rishikeshdarandale.aws.http;

import java.util.List;
import java.util.Map;

public interface Response {
    int status();
    String message();
    <T> T getAs(Class<T> clazz);
    String body();
    /**
     * Raw body as a an array of bytes.
     * @return The body, in the binary form
     */
    byte[] binary();
    /**
     * Get a value of header.
     * @return The headers
     */
    List<String> header(String name);
    /**
     * Get a collection of all headers.
     * @return The headers
     */
    Map<String, List<String>> headers();
}
