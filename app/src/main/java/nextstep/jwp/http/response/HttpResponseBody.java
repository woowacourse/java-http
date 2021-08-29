package nextstep.jwp.http.response;

import nextstep.jwp.resource.FileType;

public interface HttpResponseBody {

    String body();
    FileType fileType();
}
