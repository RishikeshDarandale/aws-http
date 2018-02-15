package in.rishikeshdarandale.aws.http.jersey;

import in.rishikeshdarandale.aws.http.AbstractRequest;
import in.rishikeshdarandale.aws.http.RequestExecuter;

public class JerseyRequest extends AbstractRequest {

    private static RequestExecuter executer = new JerseyRequestExecuter();

    public JerseyRequest(String host) {
        this(host, executer);
    }

    JerseyRequest(String host, RequestExecuter executer) {
        super(host, executer);
    }
}
