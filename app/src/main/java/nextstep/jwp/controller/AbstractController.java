package nextstep.jwp.controller;

import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    private static final String UNSUPPORTED_METHOD_ERROR_FORMAT = "%s는 %s을 지원하지 않습니다.";

    @Override
    public void service(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) throws IOException {
        HttpMethod httpMethod = httpRequestMessage.getHeader().httpMethod();
        switch (httpMethod) {
            case GET:
                doGet(httpRequestMessage, httpResponseMessage);
                break;
            case POST:
                doPost(httpRequestMessage, httpResponseMessage);
                break;
        }
    }

    protected void doGet(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) throws IOException {
        throw new UnsupportedOperationException(
                String.format(UNSUPPORTED_METHOD_ERROR_FORMAT, getClass().getName(), HttpMethod.GET)
        );
    }

    protected void doPost(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) throws IOException {
        throw new UnsupportedOperationException(
                String.format(UNSUPPORTED_METHOD_ERROR_FORMAT, getClass().getName(), HttpMethod.POST)
        );
    }
}
