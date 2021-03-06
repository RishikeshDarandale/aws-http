package io.github.rishikeshdarandale.aws.http;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import io.github.rishikeshdarandale.aws.AwsSignParams;

public abstract class AbstractRequest implements Request {
    protected transient Request request;

    public AbstractRequest(String host) {
        this.request = new ImmutableRequest(host);
    }

    public AbstractRequest(String host, RequestExecuter executer) {
        this.request = new ImmutableRequest(host, executer);
    }

    @Override
    public Request path(String path) {
        return this.request.path(path);
    }

    @Override
    public Request queryParams(String name, String value) {
        return this.request.queryParams(name, value);
    }

    @Override
    public String getQueryString() {
        return this.request.getQueryString();
    }

    @Override
    public Request method(RequestMethod method) {
        return this.request.method(method);
    }

    @Override
    public Request header(String name, String value) {
        return this.request.header(name, value);
    }

    @Override
    public Request body(String body) {
        return this.request.body(body);
    }

    @Override
    public Request body(byte[] body) {
        return this.request.body(body);
    }

    @Override
    public Request timeout(int connect, int read) {
        return this.request.timeout(connect, read);
    }

    @Override
    public Request sign(AwsSignParams params) {
        return this.request.sign(params);
    }

    @Override
    public Response execute() throws IOException {
        return this.request.execute();
    }

    @Override
    public String getHost() {
        return this.request.getHost();
    }

    @Override
    public URI getUri() {
        return this.request.getUri();
    }

    @Override
    public String getPath() {
        return this.request.getPath();
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return this.request.getHeaders();
    }

    @Override
    public Map<String, List<String>> getQueryParams() {
        return this.request.getQueryParams();
    }

    @Override
    public RequestMethod getMethod() {
        return this.request.getMethod();
    }

    @Override
    public byte[] getBody() {
        return this.request.getBody();
    }

    @Override
    public int getConnectTimeout() {
        return this.request.getConnectTimeout();
    }

    @Override
    public int getReadTimeout() {
        return this.request.getReadTimeout();
    }

}
