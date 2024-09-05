package org.apache.coyote.http11;

import static org.apache.coyote.http11.MethodType.GET;
import static org.apache.coyote.http11.MethodType.POST;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ResourceFileLoader;

public class ApiProcessor {

    private static final Logger log = LoggerFactory.getLogger(ApiProcessor.class);

    private final PageProcessor pageProcessor;

    public ApiProcessor() {
        this.pageProcessor = new PageProcessor();
    }

    public void process(
            Socket connection,
            String requestPath,
            MethodType methodType,
            Map<String, String> requestHeader,
            Map<String, String> requestBody
    ) throws IOException {
        OutputStream outputStream = connection.getOutputStream();

        String[] splitPath = requestPath.split("\\?");
        String requestUri = splitPath[0];

        if (requestUri.equals("/login")) {
            SessionManager sessionManager = SessionManager.getInstance();
            String jsessionid = requestHeader.get("Cookie").split("=")[1];
            log.info("jsessionId = " + jsessionid);
            Session session = sessionManager.findSession(jsessionid);
            boolean a = session == null;
            if (methodType == POST) {
                processLogin(outputStream, requestBody);
                return;
            }
            if (methodType == GET) {
                if (session != null && session.getAttribute("user") != null) {
                    User user = (User) session.getAttribute("user");
                    loginSuccess(outputStream, user);
                    return;
                }
                processLoginPage(outputStream);
                return;
            }
        }

        if (requestUri.equals("/register")) {
            if (methodType == POST) {
                processRegisterPost(outputStream, requestBody);
            }
            if (methodType == GET) {
                pageProcessor.process(outputStream, "register");
                return;
            }
        }
    }

    private void processRegisterPost(OutputStream outputStream, Map<String, String> requestBody) throws IOException {
        String account = requestBody.get("account");
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        User user = new User(account, email, password);
        InMemoryUserRepository.save(user);

        pageProcessor.processWithHttpStatus(outputStream, "index", HttpStatus.CREATED);
    }

    private Map<String, String> findParameterEntries(String requestPath) {
        String[] queryParameters = findQueryParameters(requestPath);
        Map<String, String> parameterMap = new HashMap<>();
        for (String queryParameter : queryParameters) {
            String[] queryParameterEntry = queryParameter.split("=");
            parameterMap.put(queryParameterEntry[0], queryParameterEntry[1]);
        }
        return parameterMap;
    }

    private String[] findQueryParameters(String requestPath) {
        String[] splitPath = requestPath.split("\\?");
        return splitPath[1].split("&");
    }

    private void processLogin(OutputStream outputStream, Map<String, String> requestBody) throws IOException {
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 User입니다."));

        if (user.checkPassword(password)) {
            loginSuccess(outputStream, user);
            return;
        }
        loginFail(outputStream);
    }

    private void processLoginPage(OutputStream outputStream) throws IOException {
        pageProcessor.process(outputStream, "login");

    }

    private void loginSuccess(OutputStream outputStream, User user) throws IOException {
        Session session = new Session(user.getId().toString());
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        String contentType = "text/html";
        String responseBody = ResourceFileLoader.loadFileToString("static/" + "index" + ".html");
        final var response = String.join("\r\n",
                "HTTP/1.1 " + HttpStatus.FOUND.getHeaderForm(),
                "Set-Cookie: JSESSIONID=" + user.getId().toString(),
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void loginFail(OutputStream outputStream) throws IOException {
        pageProcessor.processWithHttpStatus(outputStream, "401", HttpStatus.UNAUTHORIZED);
    }
}
