package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatus;

import java.util.HashMap;
import java.util.Map;

public class RegisterPostController extends AbstractController {

    @Override
    public boolean isSupported(HttpRequest request) {
        return request.isPOST() && request.isSamePath("/register");
    }


    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        if (request.isNotExistBody()) {
            throw new IllegalArgumentException("회원가입 정보가 입력되지 않았습니다.");
        }
        Map<String, String> parsedRequestBody = parseRequestBody(request);
        InMemoryUserRepository.save(new User(
                Long.getLong(parsedRequestBody.get("id")),
                parsedRequestBody.get("account"),
                parsedRequestBody.get("password"),
                parsedRequestBody.get("email")
        ));
        HttpResponseHeader responseHeader = new HttpResponseHeader(
                getContentType(request.getAccept(), request.getPath()),
                String.valueOf(0), "/index.html", null);
        return HttpResponse.of(HttpResponseStatus.FOUND, responseHeader, null);
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
}
