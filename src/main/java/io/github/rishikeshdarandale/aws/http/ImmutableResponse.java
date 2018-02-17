package io.github.rishikeshdarandale.aws.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Immutable Response Object
 *
 * @author Rishikesh Darandale <Rishikesh.Darandale@gmail.com>
 *
 */
public final class ImmutableResponse extends AbstractImmutable implements Response {
    private transient int status;
    private transient String message;
    private transient Map<String, List<String>> headers;
    private transient byte[] body;

    public ImmutableResponse(int status, String message, Map<String, List<String>> headers, byte[] body) {
        this.status = status;
        this.message = message;
        this.headers = getImmutableMap(headers);
        this.body = Arrays.copyOf(body == null ? new byte[0]:body, body == null ? 0:body.length);
    }
    @Override
    public int status() {
        return this.status;
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public <T> T getAs(Class<T> clazz) throws CouldNotConvertException {
        String contentType = this.header("Content-Type").get(0);
        ObjectMapper mapper = null;
        if ("application/json".equals(contentType)) {
            mapper = new ObjectMapper();
        } else if ("application/xml".equals(contentType)) {
            mapper = new XmlMapper();
        }
        if (mapper != null) {
            try {
                return (T) mapper.readValue(this.binary(), clazz);
            } catch (IOException e) {
                throw new CouldNotConvertException(
                        "Error while mapping response " + this.body() + " to " + clazz, e);
            }
        }
        return null;
    }

    @Override
    public String body() {
        return new String(this.body, Charset.defaultCharset());
    }

    @Override
    public byte[] binary() {
        return Arrays.copyOf(this.body, this.body.length);
    }

    @Override
    public List<String> header(String name) {
        return this.headers.get(name);
    }

    @Override
    public Map<String, List<String>> headers() {
        return this.headers;
    }

    @Override
    public String toString() {
        final StringBuilder text = new StringBuilder(">>> Start of Response <<<\n")
            .append("HTTP/1.1").append(' ')
            .append(this.status()).append(" ")
            .append(this.message()).append("\n");
        this.headers().forEach((k,v) -> {
            v.stream()
                .forEach(value -> text.append(String.format("%s: %s%n", k, value)));
        });
        return text.append('\n')
            .append(new String(this.binary(), Charset.defaultCharset())).append("\n")
            .append(">>> End of Response <<<\n")
            .toString();
    }

    @Override
    public int hashCode() {
        Object[] array = {body, headers, message, status};
        // http://errorprone.info/bugpattern/ArrayHashCode
        return Arrays.deepHashCode(array);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImmutableResponse other = (ImmutableResponse) obj;
        return Arrays.equals(body, other.body) &&
                Objects.equals(headers, other.headers) &&
                Objects.equals(message, other.message) &&
                Objects.equals(status, other.status);
    }
}
