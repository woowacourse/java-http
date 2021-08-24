package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.HttpRequest;

public class NoController implements Controller {

    @Override
    public void get(HttpRequest request) {
        Map<String, String> queryParams = request.extractURIQueryParams();
        System.out.println(queryParams);
    }

    @Override
    public void post(HttpRequest request) {

    }

    @Override
    public void put(HttpRequest request) {

    }

    @Override
    public void patch(HttpRequest request) {

    }

    @Override
    public void delete(HttpRequest request) {

    }
}
