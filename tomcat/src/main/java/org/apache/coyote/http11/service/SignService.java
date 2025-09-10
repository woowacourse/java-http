package org.apache.coyote.http11.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequests;
import org.apache.coyote.http11.parser.HttpResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class SignService implements HttpService {

    @Override
    public void doGet(
            HttpRequests httpRequests, HttpResponse httpResponse
    ) {
        httpResponse.setContent(getSignupHtml());
        httpResponse.setContentType("text/html;charset=utf-8");
        httpResponse.setStatusLine("HTTP/1.1 200 OK");
    }

    private byte[] getSignupHtml() {
        URL resource = ClassLoader.getSystemClassLoader()
                .getResource("static/register.html");

        if (resource != null) {
            try (FileInputStream fileInputStream = new FileInputStream(resource.getFile())) {
                return fileInputStream.readAllBytes();
            } catch (IOException e) {
                throw new IllegalArgumentException("파일을 찾는데 실패하였습니다.");
            }
        }
        throw new IllegalArgumentException("파일을 찾는데 실패하였습니다.");
    }

    @Override
    public void doPost(
            HttpRequests httpRequests,
            HttpResponse httpResponse
    ) {
        Map<String, String> query = httpRequests.getHttpBody()
                .getBody();

        String account = query.get("account");
        String password = query.get("password");
        String email = query.get("email");

        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("회원가입 정보는 null일 수 없습니다.");
        }

        if (InMemoryUserRepository.existByAccount(account)) {
            httpResponse.setContent(getInternalErrorHtml());
            httpResponse.setContentType("text/html;charset=utf-8");
            httpResponse.setStatusLine("HTTP/1.1 200 OK");
        }

        InMemoryUserRepository.save(new User(account, password, email));

        httpResponse.setContent(getRedirectHtml());
        httpResponse.setContentType("text/html;charset=utf-8");
        httpResponse.setStatusLine("HTTP/1.1 200 OK");
    }

    @Override
    public void doUpdate(HttpRequests httpRequests, HttpResponse httpResponse) {
        httpResponse.setContent(getInternalErrorHtml());
        httpResponse.setContentType("text/html;charset=utf-8");
        httpResponse.setStatusLine("HTTP/1.1 200 OK");
    }

    @Override
    public void doDelete(HttpRequests httpRequests, HttpResponse httpResponse) {
        throw new IllegalArgumentException("지원하지 않는 기능입니다.;");
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

    private byte[] getRedirectHtml() {
        return getHtml("static/index.html");
    }
}
