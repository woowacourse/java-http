package nextstep.jwp.controller;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatus;

import java.net.URL;

public class LoginGetController extends AbstractController {
    public static final String JSESSIONID = "JSESSIONID";

    @Override
    public boolean isSupported(HttpRequest request) {
        return request.isGET() && request.isSamePath("/login");
    }


    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final HttpCookie cookie = request.getCookie();
        URL filePathUrl;
        if (isLogin(cookie)) {
            HttpResponseHeader responseHeader = new HttpResponseHeader.Builder(
                    readContentType(request.getAccept(), request.getPath()),
                    String.valueOf(0))
                    .addLocation("/index.html")
                    .build();
            response.updateResponse(HttpResponseStatus.FOUND, responseHeader, "");
        }
        filePathUrl = getClass().getResource("/static/login.html");
        String responseBody = readHtmlFile(filePathUrl);

        HttpResponseHeader responseHeader = new HttpResponseHeader.Builder(
                readContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length))
                .build();
        response.updateResponse(HttpResponseStatus.OK, responseHeader, responseBody);
    }

    private boolean isLogin(HttpCookie cookie) {
        return cookie.isExist(JSESSIONID)
                && SessionManager.instanceOf().findSession(cookie.findCookie(JSESSIONID)) != null;
    }
}
