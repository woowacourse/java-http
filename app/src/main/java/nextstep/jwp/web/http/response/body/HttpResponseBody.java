package nextstep.jwp.web.http.response.body;

import nextstep.jwp.resource.FileType;

public interface HttpResponseBody {

    String body();
    FileType fileType();
}
