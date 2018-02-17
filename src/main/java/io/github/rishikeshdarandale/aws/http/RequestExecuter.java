package io.github.rishikeshdarandale.aws.http;

/**
 * A Request Executer will execute the request
 *
 * @author Rishikesh Darandale (Rishikesh.Darandale@gmail.com)
 *
 */
public interface RequestExecuter {

    /**
     * Execute the provided {@code Request} object and returns the reponse
     *
     * @param request - immutable request object 
     * @return {@code Response}
     */
    Response execute(Request request);
}
