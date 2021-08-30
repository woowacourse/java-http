package nextstep.jwp.controller;

import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;

import javax.servlet.ServletException;
import java.io.IOException;

public interface Controller {

//    void service(HttpRequest request, HttpResponse response) throws IOException, ServletException;

    void process(HttpRequest request, HttpResponse response, ModelAndView mv) throws ServletException, IOException;
}
