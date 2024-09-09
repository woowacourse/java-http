package org.apache.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.coyote.http11.request.Http11Method;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public abstract class AbstractController implements Controller {

    protected Pattern endPointPattern;

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        Http11Method method = request.getMethod();

        if (method == Http11Method.GET) {
            doGet(request, response);
            return;
        }
        if (method == Http11Method.POST) {
            doPost(request, response);
        }
    }

    @Override
    public boolean isMatch(String endPoint) {
        Matcher matcher = endPointPattern.matcher(endPoint);
        return matcher.matches();
    }

    protected abstract void doPost(Http11Request request, Http11Response response) throws Exception;
    protected abstract void doGet(Http11Request request, Http11Response response) throws Exception;
}
