package nextstep.jwp.web.resolver;

import nextstep.jwp.web.http.response.HttpResponse;

public interface ViewResolver {

    boolean isSuitable(HttpResponse response);

    void resolve(HttpResponse response);
}