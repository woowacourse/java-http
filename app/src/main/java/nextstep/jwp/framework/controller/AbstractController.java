package nextstep.jwp.framework.controller;

import nextstep.jwp.framework.common.HttpMethod;
import nextstep.jwp.framework.message.request.HttpRequestMessage;
import nextstep.jwp.framework.message.response.HttpResponseMessage;

public abstract class AbstractController implements Controller {

    private static final String UNSUPPORTED_METHOD_ERROR_FORMAT = "%s는 %s을 지원하지 않습니다.";

    @Override
    public final HttpResponseMessage service(HttpRequestMessage httpRequestMessage) {
        HttpMethod httpMethod = httpRequestMessage.httpMethod();
        if (httpMethod == HttpMethod.GET) {
            return doGet(httpRequestMessage);
        }
        if (httpMethod == HttpMethod.POST) {
            return doPost(httpRequestMessage);
        }
        throw new IllegalStateException("Controller 의 Service 메서드에 문제가 있습니다.");
    }

    protected HttpResponseMessage doGet(HttpRequestMessage httpRequestMessage) {
        throw new UnsupportedOperationException(
                String.format(UNSUPPORTED_METHOD_ERROR_FORMAT, getClass().getName(), HttpMethod.GET)
        );
    }

    protected HttpResponseMessage doPost(HttpRequestMessage httpRequestMessage) {
        throw new UnsupportedOperationException(
                String.format(UNSUPPORTED_METHOD_ERROR_FORMAT, getClass().getName(), HttpMethod.POST)
        );
    }
}
