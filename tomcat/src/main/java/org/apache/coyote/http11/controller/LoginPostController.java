package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoginPostController extends AbstractController {
    public static final String JSESSIONID = "JSESSIONID";

    @Override
    public boolean isSupported(HttpRequest request) {
        return request.isPOST() && request.isSamePath("/login");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        if (request.isNotExistBody()) {
            throw new IllegalArgumentException("로그인 정보가 입력되지 않았습니다.");
        }
        final HttpCookie cookie = request.getCookie();
        Map<String, String> parsedRequestBody = parseRequestBody(request);
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(
                parsedRequestBody.get("account"));
        if (userOptional.isPresent()
                && userOptional.get().checkPassword(parsedRequestBody.get("password"))) {
            String setCookie = null;
            if (!cookie.isExist(JSESSIONID)) {
                String jSessionId = String.valueOf(UUID.randomUUID());
                setCookie = JSESSIONID + "=" + jSessionId;
                SessionManager.InstanceOf().addLoginSession(jSessionId, userOptional.get());
            }
            HttpResponseHeader responseHeader = new HttpResponseHeader(
                    getContentType(request.getAccept(), request.getPath()),
                    String.valueOf(0), "/index.html", setCookie);
            return HttpResponse.of(HttpResponseStatus.FOUND, responseHeader, null);
        }
        return handle401(request);
    }

    private Map<String, String> parseRequestBody(HttpRequest request) {
        Map<String, String> parsedRequestBody = new HashMap<>();
        String[] queryTokens = request.getBody().split("&");
        for (String queryToken : queryTokens) {
            int equalSeparatorIndex = queryToken.indexOf("=");
            if (equalSeparatorIndex != -1) {
                parsedRequestBody.put(queryToken.substring(0, equalSeparatorIndex),
                        queryToken.substring(equalSeparatorIndex + 1));

            }
        }
        return parsedRequestBody;
    }

    private HttpResponse handle401(HttpRequest request) throws URISyntaxException, IOException {
        String responseBody = getHtmlFile(getClass().getResource("/static/401.html"));
        HttpResponseHeader responseHeader = new HttpResponseHeader(
                getContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.UNAUTHORIZATION, responseHeader, responseBody);
    }
}
