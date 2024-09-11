package org.apache.coyote.http11;

import com.techcourse.auth.SessionManager;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpRequestParser;
import com.techcourse.http.HttpResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.StaticResourceProvider;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String JSESSIONID = "JSESSIONID";

    private final RequestMapping requestMapping = new RequestMapping();
    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest request = HttpRequestParser.parse(inputStream);
            HttpResponse response = new HttpResponse();
            service(request, response);

            outputStream.write(response.build());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void service(HttpRequest request, HttpResponse response) throws IOException {
        try {
            jSessionInterceptor(request, response);
            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            response.clear();
            response.notFound()
                    .setBody(StaticResourceProvider.getStaticResource("/404.html"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.clear();
            response.internalServerError()
                    .setBody(StaticResourceProvider.getStaticResource("/500.html"));
        }
    }

    private void jSessionInterceptor(HttpRequest request, HttpResponse response) {
        String jSession = request.getCookie(JSESSIONID);
        if (isInvalidJSession(jSession)) {
            response.setLocation("/login.html")
                    .setCookie(JSESSIONID, jSession)
                    .setCookie("Max-Age", "0");
        }
    }

    private boolean isInvalidJSession(String jSession) {
        return jSession != null && sessionManager.findSession(jSession) == null;
    }
}
