package nextstep.jwp.framework.infrastructure.adapter;

import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;

public interface RequestAdapter {

    HttpResponse doService(HttpRequest httpRequest);
}
