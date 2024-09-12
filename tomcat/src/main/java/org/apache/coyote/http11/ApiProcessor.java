package org.apache.coyote.http11;

import com.techcourse.LoginController;
import com.techcourse.RegisterController;
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
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) throws Exception {
        String requestPath = httpRequest.getRequestPath();;

        String[] splitPath = requestPath.split("\\?");
        String requestUri = splitPath[0];

        if (requestUri.equals("/login")) {
            LoginController loginController = new LoginController();
            loginController.service(httpRequest, httpResponse);
        }

        if (requestUri.equals("/register")) {
            RegisterController registerController = new RegisterController();
            registerController.service(httpRequest, httpResponse);
        }
    }
}
