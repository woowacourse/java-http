package nextstep.jwp.web.resolver;

import nextstep.jwp.web.http.MimeType;
import nextstep.jwp.web.http.response.body.HttpResponseBody;

public interface DataResolver {

    boolean isResourceExist(String url);

    boolean isSuitable(MimeType mimeType);

    HttpResponseBody resolve(String url);
}