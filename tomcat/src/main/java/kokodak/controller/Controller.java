package kokodak.controller;

import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;

public interface Controller {

    void service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception;
}
