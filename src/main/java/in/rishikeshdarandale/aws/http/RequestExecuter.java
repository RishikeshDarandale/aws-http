package in.rishikeshdarandale.aws.http;

/**
 * A Request Executer will execute the request
 *
 * @author Rishikesh Darandale <Rishikesh.Darandale@gmail.com>
 *
 */
public interface RequestExecuter {

    Response execute(Request request);
}
