package io.github.rishikeshdarandale.aws.http.jersey;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
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

import io.github.rishikeshdarandale.aws.http.ImmutableResponse;
import io.github.rishikeshdarandale.aws.http.Request;
import io.github.rishikeshdarandale.aws.http.RequestMethod;
import io.github.rishikeshdarandale.aws.http.Response;
import io.github.rishikeshdarandale.aws.http.jersey.JerseyRequest;
import io.github.rishikeshdarandale.aws.http.jersey.JerseyRequestExecuter;

@RunWith(MockitoJUnitRunner.class)
public class JerseyRequestTest {
    @Mock
    JerseyRequestExecuter executer;

    @Before
    public void before() {
    }

    @Test
    public void testGetJerseyRequest() throws IOException {
        Request request = new JerseyRequest("https://www.somehost.com", executer)
                .method(RequestMethod.GET)
                .path("/mypath")
                .queryParams("message", "hello*world")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{}");
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        Response response1 = new ImmutableResponse(200, "OK",
                headers, "{\"message\": \"Hello World\"}".getBytes(Charset.defaultCharset()));
        Mockito.when(executer.execute(Mockito.any((Request.class)))).thenReturn(response1);
        Response response = request.execute();
        assertEquals(response.status(), response.status());
    }

}
