package io.github.rishikeshdarandale.aws.http.jersey;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.client.ClientProperties;

import io.github.rishikeshdarandale.aws.http.ImmutableResponse;
import io.github.rishikeshdarandale.aws.http.Request;
import io.github.rishikeshdarandale.aws.http.RequestExecuter;
import io.github.rishikeshdarandale.aws.http.Response;

public class JerseyRequestExecuter implements RequestExecuter {
    @Override
    public Response execute(Request request) {
        Client client = getClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, request.getConnectTimeout());
        client.property(ClientProperties.READ_TIMEOUT,    request.getReadTimeout());
        WebTarget webTarget = client.target(request.getHost());
        if (request.getPath() != null) {
            webTarget.path(request.getPath());
        }
        request.getQueryParams().forEach((k, v) -> {
            v.stream().forEach(value -> {
                webTarget.queryParam(k, value);
            });
        });
        Invocation.Builder invocationBuilder = webTarget.request();
        request.getHeaders().forEach((k, v) -> {
            String value = v.stream().collect(Collectors.joining(","));
            invocationBuilder.header(k, value);
        });
        Invocation invocation = null;
        switch(request.getMethod()) {
            case POST:
                Objects.requireNonNull(request.getHeaders().get("Content-Type").get(0),
                        "Content-Type header must be supplied for POST request.");
                invocation = invocationBuilder.buildPost(Entity.entity(request.getBody(),
                        request.getHeaders().get("Content-Type").get(0)));
                break;
            case PUT:
                Objects.requireNonNull(request.getHeaders().get("Content-Type").get(0),
                        "Content-Type header must be supplied for PUT request.");
                invocation = invocationBuilder.buildPut(Entity.entity(request.getBody(),
                        request.getHeaders().get("Content-Type").get(0)));
                break;
            case DELETE:
                invocation = invocationBuilder.buildDelete();
                break;
            case GET:
            default:
                invocation = invocationBuilder.buildGet();
        }
        javax.ws.rs.core.Response response = invocation.invoke();
        return new ImmutableResponse(response.getStatus(), response.getStatusInfo().getReasonPhrase(),
                getHeaders(response.getHeaders()),
                response.readEntity(String.class).getBytes(Charset.defaultCharset()));
    }

    Map<String, List<String>> getHeaders(MultivaluedMap<String, Object> multivaluedMap) {
        Map<String, List<String>> map = new HashMap<>();
        multivaluedMap.forEach((k,v) -> {
            List<String> values = v.stream().map(o -> o.toString()).collect(Collectors.toList());
            map.put(k, values);
        });
        return map;
    }

    Client getClient() {
        return ClientBuilder.newClient();
    }

}
