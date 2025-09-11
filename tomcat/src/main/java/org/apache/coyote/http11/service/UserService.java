package org.apache.coyote.http11.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequests;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.parser.HttpResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class UserService implements HttpService {

    @Override
    public void doGet(
            HttpRequests httpRequests,
            HttpResponse httpResponse
    ) {
        Session session = httpRequests.getSession();

        if (session.getAttribute("user") != null) {
            httpResponse.setContent(getRedirectHtml());
            httpResponse.setLocation("/index.html");
            httpResponse.setStatusLine("HTTP/1.1 302 FOUND");
            httpResponse.setContentType("text/html;charset=utf-8");
            return;
        }
        httpResponse.setContent(getLoginHtml());
        httpResponse.setStatusLine("HTTP/1.1 200 OK");
        httpResponse.setContentType("text/html;charset=utf-8");
    }

    @Override
    public void doPost(
            HttpRequests httpRequests,
            HttpResponse httpResponse
    ) {
        try {
            Map<String, String> query = httpRequests.getHttpBody()
                    .getBody();
            Session session = httpRequests.getSession();

            String account = query.get("account");
            String password = query.get("password");

            if (account != null) {
                User user = InMemoryUserRepository.getByAccountAndPassword(account, password);
                session.setAttribute("user", user);
                httpResponse.setContent(getRedirectHtml());
                httpResponse.setContentType("text/html;charset=utf-8");
                httpResponse.setStatusLine("HTTP/1.1 302 Found");
                httpResponse.setLocation("/index.html");
                return;
            }

            httpResponse.setContent(getLoginHtml());
            httpResponse.setContentType("text/html;charset=utf-8");
            httpResponse.setStatusLine("HTTP/1.1 200 Found");
        } catch (IllegalArgumentException e) {
            httpResponse.setContent(getAuthorizationFailHtml());
            httpResponse.setContentType("text/html;charset=utf-8");
            httpResponse.setStatusLine("HTTP/1.1 401 Unauthorized");
        }
    }

    @Override
    public void doUpdate(HttpRequests httpRequests, HttpResponse httpResponse) {
        httpResponse.setContent(getInternalErrorHtml());
        httpResponse.setContentType("text/html;charset=utf-8");
    }

    @Override
    public void doDelete(HttpRequests httpRequests, HttpResponse httpResponse) {
        throw new IllegalArgumentException("지원하지 않는 기능입니다.");
    }

    private byte[] getAuthorizationFailHtml() {
        return getHtml("static/401.html");
    }

    private byte[] getRedirectHtml() {
        return getHtml("static/index.html");
    }

    private byte[] getLoginHtml() {
        return getHtml("static/login.html");
    }

    private byte[] getInternalErrorHtml() {
        return getHtml("static/500.html");
    }

    private byte[] getHtml(String path) {
        URL resource = ClassLoader.getSystemClassLoader()
                .getResource(path);

        if (resource != null) {
            try (FileInputStream fileInputStream = new FileInputStream(resource.getFile())) {
                return fileInputStream.readAllBytes();
            } catch (IOException e) {
                throw new IllegalArgumentException("파일을 찾는데 실패하였습니다.");
            }
        }
        throw new IllegalArgumentException("파일을 찾는데 실패하였습니다.");
    }
}
