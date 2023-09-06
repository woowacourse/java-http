package nextstep.servlet;

import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.servlet.ServletManger;

public class DispatcherServletManager implements ServletManger {

    @Override
    public Servlet createServlet() {
        return new DispatcherServlet();
    }
}
