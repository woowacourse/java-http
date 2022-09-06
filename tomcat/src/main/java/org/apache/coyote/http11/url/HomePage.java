package org.apache.coyote.http11.url;

import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.IOUtils;

public class HomePage extends Url {

    public HomePage(final String url, final String httpMethod) {
        super(url, httpMethod);
    }

    @Override
    public Http11Response getResponse(HttpHeaders httpHeaders) {
        return new Http11Response(getPath(), HttpStatus.OK, IOUtils.readResourceFile(getPath()));
    }

    @Override
    public Http11Response postResponse(HttpHeaders httpHeaders, String requestBody) {
        throw new IllegalArgumentException("HomePage에는 POST요청이 들어올 수 없습니다.");
    }
}
