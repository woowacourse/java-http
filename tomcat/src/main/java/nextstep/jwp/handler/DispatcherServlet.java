package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.handler.mappaing.HandlerMapping;
import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;

public class DispatcherServlet implements Servlet {

    private final HandlerMapping mapping;

    public DispatcherServlet(HandlerMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        RequestHandler handler = mapping.getHandler(request);
        // TODO null 처리
        handler.handle(request, response);
        writeResponse(response);

    }

    private static void writeResponse(HttpResponse response) {
        try {
            response.writer().write(response.toString());
            response.writer().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
