package nextstep.jwp.controller;

import java.util.Map;

import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.model.RequestParser;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        Map<String, String> queries = RequestParser.parseUri(request.getUri());
        if (request.isGet() && queries.isEmpty()) {
            doGet(request, response);
            return;
        }
        if (request.isPost() && queries.isEmpty()) {
            doPost(request, response);
            return;
        }
        response.statusCode(HttpStatus.NOT_FOUND);
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        response.statusCode(HttpStatus.NOT_FOUND);
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        response.statusCode(HttpStatus.NOT_FOUND);
    }
}
