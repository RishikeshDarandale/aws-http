package in.rishikeshdarandale.aws.http;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.concurrent.Immutable;

import in.rishikeshdarandale.aws.AwsSignParams;
import in.rishikeshdarandale.aws.AwsSigner;
import in.rishikeshdarandale.aws.utils.DateUtils;

@Immutable
public final class ImmutableRequest implements Request {
    private final static String HOST_HEADER = "Host";
    public final static String X_AMZ_DATE_HEADER = "X-Amz-Date";
    private transient String host;
    private transient String path;
    private transient RequestMethod method;
    private transient Map<String, List<String>> queryParams;
    private transient Map<String, String> headers;
    private transient byte[] body;
    private transient int connectTimeout;
    private transient int readTimeout;

    public ImmutableRequest(String host) {
        this(host, "/", RequestMethod.GET, Collections.unmodifiableMap(new HashMap<>()),
                Collections.unmodifiableMap(new HashMap<>()), new byte[0], 0, 0);
    }

    private ImmutableRequest(final String host, final String path,
            final RequestMethod method, final Map<String, List<String>> queryParams,
            final Map<String, String> headers, final byte[] body,
            final int connectTimeout, final int readTimeout) {
        this.host = host;
        this.path = path;
        this.method = method;
        this.queryParams = getImmutableQueryParams(queryParams);
        HashMap<String, String> newHeaderList = new HashMap<>(headers);
        newHeaderList.put(HOST_HEADER, this.getHostHeaderValue());
        this.headers = Collections.unmodifiableMap(new HashMap<>(newHeaderList));
        this.body = Arrays.copyOf(body, body.length);
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Request path(String path) {
        return new ImmutableRequest(this.host, path, this.method, this.queryParams,
                this.headers, this.body, this.connectTimeout, this.readTimeout);
    }

    @Override
    public Request queryParams(String name, String value) {
        Map<String, List<String>> params = getMutableQueryParams(this.queryParams);
        if (params.containsKey(name)) {
            params.get(name).add(value);
        } else {
            params.put(name, Arrays.asList(value));
        }
        return new ImmutableRequest(this.host, this.path, this.method, params,
                this.headers, this.body, this.connectTimeout, this.readTimeout);
    }

    @Override
    public Request method(RequestMethod method) {
        return new ImmutableRequest(this.host, this.path, method, this.queryParams,
                this.headers, this.body, this.connectTimeout, this.readTimeout);
    }

    @Override
    public Request header(String name, String value) {
        Map<String, String> headers = getNewMap(this.headers);
        headers.put(name, value);
        return new ImmutableRequest(this.host, this.path, this.method, this.queryParams,
                headers, this.body, this.connectTimeout, this.readTimeout);
    }

    @Override
    public Request body(String body) {
        return new ImmutableRequest(this.host, this.path, this.method, this.queryParams,
                this.headers, body.getBytes(), this.connectTimeout, this.readTimeout);
    }

    @Override
    public Request body(byte[] body) {
        return new ImmutableRequest(this.host, this.path, this.method, this.queryParams,
                this.headers, body, this.connectTimeout, this.readTimeout);
    }

    @Override
    public Request timeout(int connect, int read) {
        return new ImmutableRequest(this.host, this.path, this.method, this.queryParams,
                this.headers, this.body, connect, read);
    }

    @Override
    public Request sign(AwsSignParams params) {
        params.setTimeInMillis(System.currentTimeMillis());
        Request requWithDate
                = this.header(X_AMZ_DATE_HEADER, DateUtils.getDate(params.getTimeInMillis(), "yyyyMMdd'T'HHmmss'Z'"));
        return requWithDate
                .header(AwsSigner.AUTHORIZATION_HEADER, new AwsSigner(requWithDate, params).getSigningInformation());
    }

    @Override
    public Response execute() {
        throw new UnsupportedOperationException("This operation is not supported.");
    }

    @Override
    public String toString() {
        final URI uri = this.getUri();
        final StringBuilder text = new StringBuilder(">>> Start of Request <<<\n")
            .append(this.method.toString()).append(' ')
            .append(uri.getPath())
            .append(this.getQueryString())
            .append(" HTTP/1.1\n");
        this.headers.forEach((k,v) -> text.append(String.format("%s: %s\n", k, v)));
        return text.append('\n')
            .append(new String(this.body)).append("\n")
            .append(">>> End of Request <<<\n")
            .toString();
    }

    private Map<String, String> getNewMap(Map<String, String> unmodifiableMap) {
        HashMap<String, String> map = new HashMap<>();
        unmodifiableMap.forEach((k, v) -> map.put(k, v));
        return map;
    }

    private Map<String, List<String>> getMutableQueryParams(Map<String, List<String>> immutableQueryParams) {
        HashMap<String, List<String>> map = new HashMap<>();
        queryParams.forEach((k, v) -> {
            map.put(k, new ArrayList<>(v));
        });
        return map;
    }

    private Map<String, List<String>> getImmutableQueryParams(Map<String, List<String>> queryParams) {
        HashMap<String, List<String>> map = new HashMap<>();
        queryParams.forEach((k, v) -> {
            map.put(k, Collections.unmodifiableList(v));
        });
        return Collections.unmodifiableMap(map);
    }

    private String getHostHeaderValue() {
        URI uri = this.getUri();
        StringBuilder hostString = new StringBuilder(uri.getHost());
        if (uri.getPort() != -1) {
            hostString.append(":").append(uri.getPort());
        }
        return hostString.toString();
    }

    private String getQueryString() {
        if (this.queryParams.size() > 0) {
            return "?" + this.queryParams.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> {
                        return entry.getValue().stream()
                                .map(value -> entry.getKey() + "=" + value).collect(Collectors.joining("&"));
                    })
                    .collect(Collectors.joining("&"));
        }
        return "";
    }

    public String getHost() {
        return host;
    }

    public URI getUri() {
        Objects.requireNonNull(this.host, "Host must be specified for the request");
        return URI.create(this.host + this.path + getQueryString());
    }

    public String getPath() {
        return path;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return Arrays.copyOf(this.body, this.body.length);
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public int hashCode() {
        Object[] array = {body, connectTimeout, headers, host, method, path, queryParams, readTimeout};
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
        ImmutableRequest other = (ImmutableRequest) obj;
        return Arrays.equals(body, other.body) &&
                connectTimeout == other.connectTimeout &&
                Objects.equals(headers, other.headers) &&
                Objects.equals(host, other.host) &&
                Objects.equals(method, other.method) &&
                Objects.equals(path, other.path) &&
                Objects.equals(queryParams, other.queryParams) &&
                readTimeout == other.readTimeout;
    }
}
