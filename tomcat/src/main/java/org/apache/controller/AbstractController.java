package org.apache.controller;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.request.Http11RequestStartLine;
import org.apache.coyote.http11.request.HttpMethod;

public abstract class AbstractController implements Controller {

    protected Pattern endPointPattern;

    protected AbstractController(String path) {
        this.endPointPattern = Pattern.compile("^" + path + "$");
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        HttpMethod method = request.getMethod();

        if (method == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (method == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    @Override
    public boolean isMatch(Http11RequestStartLine startLine) {
        String endPoint = startLine.getEndPoint();
        Matcher matcher = endPointPattern.matcher(endPoint);
        return matcher.matches();
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response);
    protected abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;
}
