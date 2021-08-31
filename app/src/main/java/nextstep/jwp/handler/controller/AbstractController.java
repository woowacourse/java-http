package nextstep.jwp.handler.controller;

import nextstep.jwp.exception.IncorrectHandlerException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public abstract class AbstractController implements Handler {

    @Override
    public ModelAndView handle(HttpRequest request, HttpResponse response){
        return doService(request, response);
    }

    public ModelAndView doService(HttpRequest request, HttpResponse response){
        HttpMethod httpMethod = request.httpMethod();
        if(httpMethod.isGet()){
            return doGet(request, response);
        }

        if(httpMethod.isPost()){
            return doPost(request, response);
        }
        throw new IncorrectHandlerException();
    }

    protected abstract ModelAndView doPost(HttpRequest request, HttpResponse response);
    protected abstract ModelAndView doGet(HttpRequest request, HttpResponse response);
}
