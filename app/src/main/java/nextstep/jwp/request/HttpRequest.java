package nextstep.jwp.request;

import nextstep.jwp.request.basic.HttpMethod;

public interface HttpRequest {

    HttpMethod httpMethod();

    String httpUrl();
}
