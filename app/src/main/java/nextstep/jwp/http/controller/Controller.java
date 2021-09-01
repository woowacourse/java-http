package nextstep.jwp.http.controller;

import nextstep.jwp.http.http_request.JwpHttpRequest;
import nextstep.jwp.http.http_response.JwpHttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public interface Controller {

    JwpHttpResponse handle(JwpHttpRequest request) throws URISyntaxException, IOException;

    JwpHttpResponse doGet(JwpHttpRequest request) throws URISyntaxException, IOException;

    JwpHttpResponse doPost(JwpHttpRequest request) throws URISyntaxException, IOException;
}
