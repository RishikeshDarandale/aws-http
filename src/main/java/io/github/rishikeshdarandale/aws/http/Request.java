package io.github.rishikeshdarandale.aws.http;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import io.github.rishikeshdarandale.aws.AwsSignParams;

/**
 * A HTTP request
 * 
 * @author Rishikesh Darandale (Rishikesh.Darandale@gmail.com)
 *
 */
public interface Request {
    /**
     * Add the path to {@code Request} object.
     * 
     * @param path - api path
     * @return a new {@code Request} object with path
     */
    Request path(String path);
    /**
    * Add queryParam to {@code Request} object
     *
     * @param name - name of the query param
     * @param value - value of the query param
     * @return a new {@code Request} object with queryParam
     */
    Request queryParams(String name, String value);
    /**
     * Add a method type to the {@code Request} object
     * 
     * @param method - http method to use
     * @return a new {@code Request} object with method
     */
    Request method(RequestMethod method);
    /**
     * Add a request header to the {@code Request} object
     * 
     * @param name - name of the http header
     * @param value - value of http header
     * @return a new {@code Request} object with header
     */
    Request header(String name, String value);
    /**
     * Add a string body to {@code Request} object
     * 
     * @param body - body of the http request
     * @return a new {@code Request} object with body
     */
    Request body(String body);
    /**
     * Add a raw body to {@code Request} object
     * 
     * @param body - body of the http request
     * @return a new {@code Request} object with body
     */
    Request body(byte[] body);
    /**
     * Set the timeouts for the {@code Request} object
     * 
     * @param connect - connect timeout
     * @param read - read timeout
     * @return a new {@code Request} object
     */
    Request timeout(int connect, int read);
    /**
     * Sign HTTP Requests for AWS
     *
     * <p>
     * This method signs the request with AWS signature version 4 and adds
     * {@code Authorization} header with the value provided as AWS credentials. for
     * example:
     *
     * <pre>
     *  String response = new JersyRequest("http://aws-request-host/")
     *     .sign(awsParams)
     *     .execute()
     *     .body();
     * </pre>
     *
     *
     * @param params {@code AwsSignParams} with required details to sign the request
     * @return a new {@code Request} object with signing information
     * 
     * @see <a href="https://tools.ietf.org/html/rfc2616#section-14.8">RFC 2616
     *      section 14.8 "Authorization"</a>
     */
    Request sign(AwsSignParams params);
    /**
     * Execute the request with {@code Request} object
     * 
     * @return a {@code Response}
     * @throws IOException throws {@link IOException} if response can not consutructed
     */
    Response execute() throws IOException;
    /**
     * Convenience method too get the host of this request
     * 
     * @return host
     */
    String getHost();
    /**
     * Convenience method too get the {@link URI} associated with this request
     * 
     * @return {@link URI} associated with this request
     */
    URI getUri();
    /**
     * Convenience method too get the path of this request
     * 
     * @return path
     */
    String getPath();
    /**
     * Convenience method too get the headers of this request
     * 
     * @return headers added to this request
     */
    Map<String, List<String>> getHeaders();
    /**
     * Convenience method too get the query params of this request
     * 
     * @return query params added to this request
     */
    Map<String, List<String>> getQueryParams();
    /**
     * Convenience method too get the query string of this request
     * 
     * @return query string constructed using query params
     */
    String getQueryString();
    /**
     * Convenience method too get mathod of this request
     * 
     * @return method of this request
     */
    RequestMethod getMethod();
    /**
     * Convenience method too get body of this request
     * 
     * @return method of this request
     */
    byte[] getBody();
    /**
     * Convenience method too get connect timeout of this request
     * 
     * @return connect timeout of this request
     */
    int getConnectTimeout();
    /**
     * Convenience method too get read timeout of this request
     * 
     * @return read timeout of this request
     */
    int getReadTimeout();
}
