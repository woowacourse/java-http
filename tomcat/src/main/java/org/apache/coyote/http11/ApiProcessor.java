package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.requestLine.MethodType;
import org.apache.coyote.http11.response.HttpResponse;
import util.ResourceFileLoader;

public class ApiProcessor {

    private final PageProcessor pageProcessor;

    public ApiProcessor() {
        this.pageProcessor = new PageProcessor();
    }

    public void process(
            Socket connection,
            HttpRequest httpRequest
    ) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        String requestPath = httpRequest.getRequestPath();
        MethodType methodType = httpRequest.getMethodType();
        Map<String, String> requestHeader = httpRequest.getRequestHeader();

        String[] splitPath = requestPath.split("\\?");
        String requestUri = splitPath[0];

        if (requestUri.equals("/login")) {
            if (methodType.isPost()) {
                processLogin(outputStream, httpRequest.getRequestBody());
                return;
            }
            if (methodType.isGet()) {
                SessionManager sessionManager = SessionManager.getInstance();
                if (requestHeader.containsKey("Cookie")) {
                    String jsessionid = requestHeader.get("Cookie").split("=")[1];
                    Session session = sessionManager.findSession(jsessionid);
                    if (session != null && session.getAttribute("user") != null) {
                        User user = (User) session.getAttribute("user");
                        loginSuccess(outputStream, user);
                        return;
                    }
                }
                processLoginPage(outputStream);
                return;
            }
        }

        if (requestUri.equals("/register")) {
            if (methodType.isPost()) {
                processRegisterPost(outputStream, httpRequest.getRequestBody());
                return;
            }
            if (methodType.isGet()) {
                HttpResponse httpResponse = HttpResponse.createHttpResponse(HttpStatus.OK);
                httpResponse.setContentType(ContentType.text_html);
                httpResponse.setResponseBody(ResourceFileLoader.loadStaticFileToString("register.html"));
                pageProcessor.process(outputStream, httpResponse);
            }
        }
    }

    private void processRegisterPost(OutputStream outputStream, Map<String, String> requestBody) throws IOException {
        String account = requestBody.get("account");
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        HttpResponse httpResponse = HttpResponse.createHttpResponse(HttpStatus.FOUND);
        httpResponse.setLocation("http://localhost:8080/");
        httpResponse.setCookie("JSESSIONID=" + user.getId().toString());
        pageProcessor.process(outputStream, httpResponse);
    }

    private Map<String, String> findParameterEntries(String requestPath) {
        String[] splitPath = requestPath.split("\\?");
        String[] queryParameters = splitPath[1].split("&");
        Map<String, String> parameterMap = new HashMap<>();
        for (String queryParameter : queryParameters) {
            String[] queryParameterEntry = queryParameter.split("=");
            parameterMap.put(queryParameterEntry[0], queryParameterEntry[1]);
        }
        return parameterMap;
    }

    private void processLogin(OutputStream outputStream, Map<String, String> requestBody) throws IOException {
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(password)) {
                loginSuccess(outputStream, user);
                return;
            }
        }
        loginFail(outputStream);
    }

    private void processLoginPage(OutputStream outputStream) throws IOException {
        HttpResponse httpResponse = HttpResponse.createHttpResponse(HttpStatus.OK);
        httpResponse.setContentType(ContentType.text_html);
        httpResponse.setResponseBody(ResourceFileLoader.loadStaticFileToString("login.html"));
        pageProcessor.process(outputStream, httpResponse);

    }

    private void loginSuccess(OutputStream outputStream, User user) throws IOException {
        Session session = new Session(user.getId().toString());
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        HttpResponse httpResponse = HttpResponse.createHttpResponse(HttpStatus.FOUND);
        httpResponse.setLocation("http://localhost:8080/");
        httpResponse.setCookie("JSESSIONID=" + user.getId().toString());
        pageProcessor.process(outputStream, httpResponse);
    }

    private void loginFail(OutputStream outputStream) throws IOException {
        HttpResponse httpResponse = HttpResponse.createHttpResponse(HttpStatus.UNAUTHORIZED);
        httpResponse.setResponseBody(ResourceFileLoader.loadStaticFileToString("401.html"));
        pageProcessor.process(outputStream, httpResponse);
    }
}
