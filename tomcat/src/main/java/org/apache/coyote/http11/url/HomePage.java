package org.apache.coyote.http11.url;

import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.IOUtils;

public class HomePage extends Url {

    public HomePage(final String url, final HttpMethod httpMethod) {
        super(url, httpMethod);
    }

    @Override
    public Http11Response getResource(HttpHeaders httpHeaders, String requestBody) {
        if (HttpMethod.GET.equals(getHttpMethod())) {
            return new Http11Response(getPath(), HttpStatus.OK, IOUtils.readResourceFile(getPath()));
        }
        if (HttpMethod.POST.equals(getHttpMethod())) {
            throw new IllegalArgumentException("HomePage에는 POST요청이 들어올 수 없습니다.");
        }
        throw new IllegalArgumentException("HomePage에 해당하는 HTTP Method가 아닙니다. : " + getHttpMethod());
    }
}
