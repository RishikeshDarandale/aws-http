package io.github.rishikeshdarandale.aws.http;

import java.util.List;
import java.util.Map;

public interface Response {
    /**
     * Get the HTTP response status code
     * 
     * @return HTTP response code
     */
    int status();
    /**
     * Get the HTTP response message
     * 
     * @return message
     */
    String message();
    /**
     * Get the response as a object
     *
     * @param clazz
     * @return
     * @throws CouldNotConvertException
     */
    <T> T getAs(Class<T> clazz) throws CouldNotConvertException;
    /**
     * Get the body
     * 
     * @return
     */
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
