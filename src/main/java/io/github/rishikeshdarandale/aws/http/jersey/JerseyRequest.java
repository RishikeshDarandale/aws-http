package io.github.rishikeshdarandale.aws.http.jersey;

import io.github.rishikeshdarandale.aws.http.AbstractRequest;
import io.github.rishikeshdarandale.aws.http.RequestExecuter;

/**
 * Implementation of {@link io.github.rishikeshdarandale.aws.http.Request} using jersey-client
 *
 * @author Rishikesh Darandale (Rishikesh.Darandale@gmail.com)
 *
 */
public class JerseyRequest extends AbstractRequest {

    private static RequestExecuter executer = new JerseyRequestExecuter();

    public JerseyRequest(String host) {
        this(host, executer);
    }

    JerseyRequest(String host, RequestExecuter executer) {
        super(host, executer);
    }
}
