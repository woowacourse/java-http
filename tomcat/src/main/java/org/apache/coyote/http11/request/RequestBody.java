package org.apache.coyote.http11.request;

public class RequestBody {

    // TODO: json, binary 등을 담으려면 byte로 저장한다. 현재 요구사항에서는 비범위.
    private final Parameters parameters;

    public RequestBody(final String requestBody) {
        this.parameters = new Parameters(requestBody);
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }
}
