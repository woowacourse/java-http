package org.apache.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.common.HttpHeader;
import nextstep.jwp.common.HttpRequest;
import nextstep.jwp.common.HttpResponse;
import org.apache.cookie.Cookie;
import org.apache.session.Session;
import org.apache.session.SessionManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import static nextstep.jwp.common.StatusCode.FOUND;
import static nextstep.jwp.common.StatusCode.OK;
import static org.reflections.Reflections.log;

public class LoginController extends AbstractController {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException {
        super.service(httpRequest, httpResponse);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> userInfo = readUserInfo(httpRequest.getRequestBody());
        try {
            User user = InMemoryUserRepository
                    .findByAccount(userInfo.get("account"))
                    .orElseThrow(() -> new NoSuchElementException("해당 계정이 존재하지 않습니다."));
            if (!user.checkPassword(userInfo.get("password"))) {
                throw new IllegalArgumentException("유효하지 않은 비밀번호입니다");
            }
            log.info("로그인 성공 : user = {}", user);

            String jSessionId = UUID.randomUUID().toString();
            Map<String, Object> value = new HashMap<>();
            value.put("user", user);
            Session session = new Session(jSessionId, value);
            SessionManager.add(session);
            System.out.println("세션 추가 완료");
            httpResponse.setVersion("HTTP/1.1");
            httpResponse.setStatusCode(FOUND);
            httpResponse.setHttpHeader(HttpHeader.from(
                    List.of("Location: /index.html",
                            "Set-Cookie: JSESSIONID=" + jSessionId)
            ));


        } catch (NoSuchElementException | IllegalArgumentException e) {
            httpResponse.setVersion("HTTP/1.1");
            httpResponse.setStatusCode(FOUND);
            httpResponse.setHttpHeader(HttpHeader.from(
                    List.of("Location: /401.html")
            ));
        }
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException {
        if (httpRequest.getHttpHeader().hasHeader("Cookie")) {
            Cookie cookie = Cookie.from(httpRequest.getHttpHeader().findValue("Cookie"));
            if (cookie.hasJSessionId()){
                String jsessionid = cookie.getCookies().get("JSESSIONID");
                if (SessionManager.findSession(jsessionid) != null) {
                    Session session = SessionManager.findSession(jsessionid);
                    if (session.getAttribute("user") != null) {
                        httpResponse.setVersion("HTTP/1.1");
                        httpResponse.setStatusCode(FOUND);
                        httpResponse.setHttpHeader(HttpHeader.from(
                                List.of("Location: /index.html")
                        ));
                        return;
                    }
                }
            }
        }
        String responseBody = new String(readBytes(readPath(httpRequest.getHttpLine().getUrl())));
        HttpHeader httpResponseHeader = HttpHeader.from(List.of(
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length));

        httpResponse.setVersion("HTTP/1.1");
        httpResponse.setStatusCode(OK);
        httpResponse.setHttpHeader(httpResponseHeader);
        httpResponse.setResponseBody(responseBody);
    }

    private Path readPath(String url) throws URISyntaxException {
        URL resourcePath = getClass().getClassLoader().getResource("static" + url + ".html");
        return Path.of(resourcePath.toURI());
    }

    private byte[] readBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    private Map<String, String> readUserInfo(String body) {
        return Arrays.stream(body.split("&"))
                .map(value -> value.split("="))
                .collect(Collectors.toMap(value -> value[0], value -> value[1]));
    }
}
