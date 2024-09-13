package org.apache.coyote.http11.message.request;

public class HttpRequestBody {

    private static final byte[] EMPTY_BODY = new byte[0];

    private final byte[] body;
    private final FormParameters formParameters;

    public HttpRequestBody(byte[] body, FormParameters formParameters) {
        this.body = body;
        this.formParameters = formParameters;
    }

    public HttpRequestBody() {
        this(EMPTY_BODY, new FormParameters());
    }

    public boolean hasFormParameters() {
        return formParameters.hasParameters();
    }

    public String getFormParameter(String key) {
        return formParameters.getSingleValueByKey(key);
    }
}
