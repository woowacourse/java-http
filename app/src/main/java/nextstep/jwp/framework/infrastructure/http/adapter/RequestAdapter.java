package nextstep.jwp.framework.infrastructure.http.adapter;

import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;

public interface RequestAdapter {

    String execute(HttpRequest httpRequest);
}
