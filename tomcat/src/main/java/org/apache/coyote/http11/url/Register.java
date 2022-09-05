package org.apache.coyote.http11.url;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class Register extends Url {
    public Register(String url) {
        super(url);
    }

    @Override
    public Http11Response getResponse(String httpMethod) {
        if (HttpMethod.GET.name().equals(httpMethod)) {
            System.out.println(httpMethod + " !!@Reter");
            return new Http11Response(ContentType.from(getPath()), HttpStatus.OK, getPath());
        }
        if (HttpMethod.POST.name().equals(httpMethod)) {
            return new Http11Response(ContentType.from(getPath()), HttpStatus.OK, getPath());
        }
        throw new IllegalArgumentException("올바른 HTTP Method가 아닙니다. : " + httpMethod);
    }
}
