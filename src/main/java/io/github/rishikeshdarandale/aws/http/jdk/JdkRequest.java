package io.github.rishikeshdarandale.aws.http.jdk;

import io.github.rishikeshdarandale.aws.http.AbstractRequest;
import io.github.rishikeshdarandale.aws.http.Request;
import io.github.rishikeshdarandale.aws.http.RequestExecuter;

/**
 * Implementation of {@link Request} using jdk
 *
 * @author Rishikesh Darandale (Rishikesh.Darandale@gmail.com)
 *
 */
public class JdkRequest extends AbstractRequest {

    private static RequestExecuter executer = new JdkRequestExecuter();

    public JdkRequest(String host) {
        this(host, executer);
    }

    public JdkRequest(String host, RequestExecuter executer) {
        super(host, executer);
    }
}
