package org.apache.coyote.http11.application;

import java.util.function.Function;

import org.apache.coyote.http11.request.Api;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Handler {

    Function<HttpRequest, HttpResponse> getHandlerMethod(Api requestApi);
}
