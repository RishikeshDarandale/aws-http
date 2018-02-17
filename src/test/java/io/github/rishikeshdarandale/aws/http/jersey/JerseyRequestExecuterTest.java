package io.github.rishikeshdarandale.aws.http.jersey;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response.StatusType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.rishikeshdarandale.aws.http.Request;
import io.github.rishikeshdarandale.aws.http.RequestMethod;
import io.github.rishikeshdarandale.aws.http.Response;
import io.github.rishikeshdarandale.aws.http.jersey.JerseyRequest;
import io.github.rishikeshdarandale.aws.http.jersey.JerseyRequestExecuter;

@RunWith(MockitoJUnitRunner.class)
public class JerseyRequestExecuterTest {
    JerseyRequestExecuter executer;
    @Mock
    Client client;
    @Mock
    WebTarget target;
    @Mock
    Invocation.Builder builder;
    @Mock
    Invocation invocation;
    @Mock
    javax.ws.rs.core.Response jerseyResponse;
    @Mock
    StatusType statusType;

    @Before
    public void before() {
        executer = Mockito.spy(new JerseyRequestExecuter());
        Mockito.when(executer.getClient()).thenReturn(client);
        Mockito.when(client.target(Mockito.any(String.class))).thenReturn(target);
        Mockito.when(target.request()).thenReturn(builder);
    }

    @Test
    public void testGet() {
        Request request = new JerseyRequest("https://www.somehost.com")
                .path("/mypath");
        Mockito.when(builder.buildGet()).thenReturn(invocation);
        Mockito.when(invocation.invoke()).thenReturn(jerseyResponse);
        Mockito.when(jerseyResponse.getStatus()).thenReturn(200);
        Mockito.when(jerseyResponse.getStatusInfo()).thenReturn(statusType);
        Mockito.when(statusType.getReasonPhrase()).thenReturn("OK");
        MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Content-Type", "application/json");
        Mockito.when(jerseyResponse.getHeaders()).thenReturn(headers);
        Mockito.when(jerseyResponse.readEntity(String.class)).thenReturn("{\"message\": \"Hello World\"}");
        Response response = executer.execute(request);
        assertEquals(200, response.status());
        assertEquals("{\"message\": \"Hello World\"}", response.body());
    }

    @Test(expected=NullPointerException.class)
    public void testPostWithoutContentType() {
        Request request = new JerseyRequest("https://www.somehost.com")
                .path("/mypath")
                .method(RequestMethod.POST)
                .body("{\"name\": \"Test\"}");
        executer.execute(request);
    }

    @Test
    public void testPost() {
        Request request = new JerseyRequest("https://www.somehost.com")
                .path("/mypath")
                .method(RequestMethod.POST)
                .header("Content-Type", "application/json")
                .body("{\"name\": \"Test\"}");
        Mockito.when(builder.buildPost(Mockito.any(Entity.class))).thenReturn(invocation);
        Mockito.when(invocation.invoke()).thenReturn(jerseyResponse);
        Mockito.when(jerseyResponse.getStatus()).thenReturn(200);
        Mockito.when(jerseyResponse.getStatusInfo()).thenReturn(statusType);
        Mockito.when(statusType.getReasonPhrase()).thenReturn("OK");
        MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Content-Type", "application/json");
        Mockito.when(jerseyResponse.getHeaders()).thenReturn(headers);
        Mockito.when(jerseyResponse.readEntity(String.class)).thenReturn("{\"status\": \"Completed\"}");
        Response response = executer.execute(request);
        assertEquals(200, response.status());
        assertEquals("{\"status\": \"Completed\"}", response.body());
    }

    @Test(expected=NullPointerException.class)
    public void testPutWithoutContentType() {
        Request request = new JerseyRequest("https://www.somehost.com")
                .method(RequestMethod.PUT)
                .body("{\"name\": \"Test\"}");
        executer.execute(request);
    }

    @Test
    public void testPut() {
        Request request = new JerseyRequest("https://www.somehost.com")
                .path("/mypath")
                .method(RequestMethod.PUT)
                .header("Content-Type", "application/json")
                .body("{\"name\": \"Test\"}");
        Mockito.when(builder.buildPut(Mockito.any(Entity.class))).thenReturn(invocation);
        Mockito.when(invocation.invoke()).thenReturn(jerseyResponse);
        Mockito.when(jerseyResponse.getStatus()).thenReturn(201);
        Mockito.when(jerseyResponse.getStatusInfo()).thenReturn(statusType);
        Mockito.when(statusType.getReasonPhrase()).thenReturn("Created");
        MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
        Mockito.when(jerseyResponse.getHeaders()).thenReturn(headers);
        Mockito.when(jerseyResponse.readEntity(String.class)).thenReturn("");
        Response response = executer.execute(request);
        assertEquals(201, response.status());
        assertEquals("Created", response.message());
        assertEquals("", response.body());
    }

    @Test
    public void testDelete() {
        Request request = new JerseyRequest("https://www.somehost.com")
                .path("/mypath")
                .method(RequestMethod.DELETE)
                .queryParams("id", "1234");
        Mockito.when(builder.buildDelete()).thenReturn(invocation);
        Mockito.when(invocation.invoke()).thenReturn(jerseyResponse);
        Mockito.when(jerseyResponse.getStatus()).thenReturn(200);
        Mockito.when(jerseyResponse.getStatusInfo()).thenReturn(statusType);
        Mockito.when(statusType.getReasonPhrase()).thenReturn("OK");
        MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
        Mockito.when(jerseyResponse.getHeaders()).thenReturn(headers);
        Mockito.when(jerseyResponse.readEntity(String.class)).thenReturn("");
        Response response = executer.execute(request);
        assertEquals(200, response.status());
        assertEquals("OK", response.message());
        assertEquals("", response.body());
    }
}
