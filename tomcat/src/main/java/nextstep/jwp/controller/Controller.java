package nextstep.jwp.controller;

import org.apache.http.Request;
import org.apache.http.Response;

public interface Controller {

    void service(Request request, Response response);
}
