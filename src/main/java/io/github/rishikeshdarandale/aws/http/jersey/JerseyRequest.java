package io.github.rishikeshdarandale.aws.http.jersey;

import io.github.rishikeshdarandale.aws.http.AbstractRequest;
import io.github.rishikeshdarandale.aws.http.RequestExecuter;

public class JerseyRequest extends AbstractRequest {

    private static RequestExecuter executer = new JerseyRequestExecuter();

    public JerseyRequest(String host) {
        this(host, executer);
    }

    JerseyRequest(String host, RequestExecuter executer) {
        super(host, executer);
    }
}
