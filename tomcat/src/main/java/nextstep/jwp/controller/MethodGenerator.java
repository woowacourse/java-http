package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

@FunctionalInterface
public interface MethodGenerator {

    void generate(HttpRequest httpRequest, HttpResponse response) throws Exception;
}
