package io.github.rishikeshdarandale.aws.http.jdk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import io.github.rishikeshdarandale.aws.http.ImmutableResponse;
import io.github.rishikeshdarandale.aws.http.Request;
import io.github.rishikeshdarandale.aws.http.RequestExecuter;
import io.github.rishikeshdarandale.aws.http.RequestMethod;
import io.github.rishikeshdarandale.aws.http.Response;

/**
 * Implementation of {@link RequestExecuter} using jdk {@link HttpURLConnection}
 *
 * @author Rishikesh Darandale (Rishikesh.Darandale@gmail.com)
 *
 */
public class JdkRequestExecuter implements RequestExecuter {

    @Override
    public Response execute(Request request) throws IOException {
        HttpURLConnection connection = getConnection(request.getHost()+request.getPath()+request.getQueryString());
        Response response = null;
        try {
            connection.setReadTimeout(request.getReadTimeout());
            connection.setConnectTimeout(request.getConnectTimeout());
            connection.setRequestMethod(request.getMethod().toString());
            request.getHeaders().forEach((k, v) -> {
                String value = v.stream().collect(Collectors.joining(","));
                connection.addRequestProperty(k, value);
            });
            if (request.getMethod() == RequestMethod.POST || request.getMethod() == RequestMethod.PUT) {
                connection.setDoOutput(true);
                final OutputStream outputStream = connection.getOutputStream();
                try {
                    outputStream.write(request.getBody());
                } finally {
                    outputStream.close();
                }
            }
            connection.connect();
            response = new ImmutableResponse(
                    connection.getResponseCode(),
                    connection.getResponseMessage(), connection.getHeaderFields(),
                    this.body(connection));
        } catch (IOException e) {
            throw new IOException("Can not construct the response:", e);
        } finally {
            connection.disconnect();
        }
        return response;
    }

    /**
     * Get response body of connection.
     * @param conn Connection
     * @return Body
     * @throws IOException
     */
    private byte[] body(final HttpURLConnection conn) throws IOException {
        final InputStream input;
        if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
            input = conn.getErrorStream();
        } else {
            input = conn.getInputStream();
        }
        final byte[] body;
        if (input == null) {
            body = new byte[0];
        } else {
            try {
                final byte[] buffer = new byte[8192];
                final ByteArrayOutputStream output =
                    new ByteArrayOutputStream();
                for (int bytes = input.read(buffer); bytes != -1;
                    bytes = input.read(buffer)) {
                    output.write(buffer, 0, bytes);
                }
                body = output.toByteArray();
            } finally {
                input.close();
            }
        }
        return body;
    }

    HttpURLConnection getConnection(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
