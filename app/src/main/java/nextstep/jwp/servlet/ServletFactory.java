package nextstep.jwp.servlet;

import java.io.OutputStream;
import java.util.Arrays;

public enum ServletFactory {
    INDEX_SERVLET("index.html", new IndexPageServlet()),
    LOGIN_SERVLET("login", new LoginPageServlet()),
    NOT_FOUND_SERVLET("404.html", new NotFoundPageServlet());

    private String pathName;
    private Servlet servlet;

    ServletFactory(String pathName, Servlet servlet) {
        this.pathName = pathName;
        this.servlet = servlet;
    }

    public static ServletFactory of(String requestFirstLine) {
        return Arrays.stream(values())
                .filter(servletFactory -> requestFirstLine.contains(servletFactory.pathName))
                .findAny()
                .orElse(NOT_FOUND_SERVLET);
    }

    public Servlet chooseServlet(OutputStream outputStream) {
        this.servlet.setOutputStream(outputStream);
        return this.servlet;
    }
}
