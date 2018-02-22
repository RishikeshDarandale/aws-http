package io.github.rishikeshdarandale.aws.http.jdk;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@RunWith(MockitoJUnitRunner.class)
public class JdkRequestExecuterTest {
    JdkRequestExecuter executer;
    @Mock
    HttpURLConnection connection;

    @Before
    public void before() {
        executer = Mockito.spy(new JdkRequestExecuter());
    }

    @Test
    public void testPost() throws IOException {
        Request request = new JdkRequest("https://www.somehost.com")
                .path("/mypath")
                .method(RequestMethod.POST)
                .header("Content-Type", "application/json")
                .body("{\"name\": \"Test\"}");
        Mockito.when(executer.getConnection("https://www.somehost.com/mypath")).thenReturn(connection);
        Mockito.when(connection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getResponseMessage()).thenReturn("OK");
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        Mockito.when(connection.getHeaderFields()).thenReturn(headers);
        Mockito.when(connection.getInputStream())
            .thenReturn(new ByteArrayInputStream("{\"status\": \"Completed\"}".getBytes(Charset.defaultCharset())));
        Response response = executer.execute(request);
        Mockito.verify(connection, Mockito.atMost(1)).connect();
        assertEquals(200, response.status());
        assertEquals("{\"status\": \"Completed\"}", response.body());
    }

    @Test(expected=IOException.class)
    public void testPostError() throws IOException {
        Request request = new JdkRequest("https://www.somehost.com")
                .path("/mypath")
                .method(RequestMethod.POST)
                .header("Content-Type", "application/json")
                .body("{\"name\": \"Test\"}");
        Mockito.when(executer.getConnection("https://www.somehost.com/mypath")).thenReturn(connection);
        Mockito.when(connection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        Mockito.doThrow(new IOException()).when(connection).connect();
        executer.execute(request);
    }

    @Test
    public void testPut() throws IOException {
        Request request = new JdkRequest("https://www.somehost.com")
                .path("/mypath")
                .method(RequestMethod.PUT)
                .header("Content-Type", "application/json")
                .body("{\"name\": \"Test\"}");
        Mockito.when(executer.getConnection("https://www.somehost.com/mypath")).thenReturn(connection);
        Mockito.when(connection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        Mockito.when(connection.getResponseCode()).thenReturn(201);
        Mockito.when(connection.getResponseMessage()).thenReturn("CREATED");
        Map<String, List<String>> headers = new HashMap<>();
        Mockito.when(connection.getHeaderFields()).thenReturn(headers);
        Mockito.when(connection.getInputStream()).thenReturn(null);
        Response response = executer.execute(request);
        Mockito.verify(connection, Mockito.atMost(1)).connect();
        assertEquals(201, response.status());
        assertEquals("", response.body());
    }

    @Test
    public void testGet() throws IOException {
        Request request = new JdkRequest("https://www.somehost.com")
                .path("/mypath");
        Mockito.when(executer.getConnection("https://www.somehost.com/mypath")).thenReturn(connection);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getResponseMessage()).thenReturn("OK");
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        Mockito.when(connection.getHeaderFields()).thenReturn(headers);
        Mockito.when(connection.getInputStream())
            .thenReturn(new ByteArrayInputStream("{\"message\": \"Hello World\"}".getBytes(Charset.defaultCharset())));
        Response response = executer.execute(request);
        Mockito.verify(connection, Mockito.atMost(1)).connect();
        Mockito.verify(connection, Mockito.never()).getOutputStream();
        assertEquals(200, response.status());
        assertEquals("{\"message\": \"Hello World\"}", response.body());
    }

    @Test
    public void testGetBadRequest() throws IOException {
        Request request = new JdkRequest("https://www.somehost.com")
                .path("/pathdoesnotexists");
        Mockito.when(executer.getConnection("https://www.somehost.com/pathdoesnotexists")).thenReturn(connection);
        Mockito.when(connection.getResponseCode()).thenReturn(400);
        Mockito.when(connection.getResponseMessage()).thenReturn("Bad Request");
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        Mockito.when(connection.getHeaderFields()).thenReturn(headers);
        Mockito.when(connection.getErrorStream())
            .thenReturn(new ByteArrayInputStream("{\"error\": \"Bad Request\"}".getBytes(Charset.defaultCharset())));
        Response response = executer.execute(request);
        Mockito.verify(connection, Mockito.atMost(1)).connect();
        Mockito.verify(connection, Mockito.never()).getOutputStream();
        Mockito.verify(connection, Mockito.never()).getInputStream();
        assertEquals(400, response.status());
        assertEquals("{\"error\": \"Bad Request\"}", response.body());
    }
}