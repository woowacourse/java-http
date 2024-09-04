package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStateCode;
import org.apache.coyote.http11.MimeType;

public class RegisterController implements Controller{

    @Override
    public HttpResponse run(HttpRequest request) {
        return new HttpResponse(HttpStateCode.FOUND, "/register.html", MimeType.HTML);
    }
}
