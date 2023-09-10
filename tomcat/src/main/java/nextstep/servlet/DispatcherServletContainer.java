package nextstep.servlet;

public class DispatcherServletContainer implements ServletContainer {

    @Override
    public Servlet createServlet() {
        return new DispatcherServlet();
    }
}
