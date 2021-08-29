package nextstep.jwp.mapper;

import nextstep.jwp.http.response.HttpResponse;

public class ExceptionMapper {
    public HttpResponse handle(Exception e) {
        return HttpResponse.error(e.getMessage());
    }
}
