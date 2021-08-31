package nextstep.jwp.webserver.response;

import nextstep.jwp.webserver.exception.NoFileExistsException;

public interface HttpResponse {

    void addStatus(StatusCode statusCode);

    void addHeader(String key, String ... values);

    void addBody(String content);

    String totalResponse();

    void addPage(String path) throws NoFileExistsException;

    void addRedirectUrl(String url);

    void flush();
}
