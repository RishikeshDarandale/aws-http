package io.github.rishikeshdarandale.aws.http;

import java.io.IOException;

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
     * @throws IOException in case {@link Response} can not be constructed.
     */
    Response execute(Request request) throws IOException;
}
