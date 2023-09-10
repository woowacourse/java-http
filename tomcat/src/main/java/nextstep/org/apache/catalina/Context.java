package nextstep.org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import nextstep.org.apache.catalina.servlet.DefaultServlet;
import nextstep.org.apache.catalina.servlet.LoginServlet;
import nextstep.org.apache.catalina.servlet.RegisterServlet;
import nextstep.org.apache.catalina.servlet.Servlet;

public class Context {

    private static final Map<String, Servlet> servletMappings;
    private static final String EXTENSION_DELIMITER = ".";

    static {
        servletMappings = new HashMap<>();
        servletMappings.put("/login", new LoginServlet());
        servletMappings.put("/register", new RegisterServlet());
        servletMappings.put("default", new DefaultServlet());
    }

    public Servlet getServlet(String pathInfo) {
        if (servletMappings.containsKey(removeExtension(pathInfo))) {
            return servletMappings.get(removeExtension(pathInfo));
        }
        return servletMappings.get("default");
    }

    private String removeExtension(String pathInfo) {
        int idx = pathInfo.indexOf(EXTENSION_DELIMITER);
        if (idx == -1) {
            return pathInfo;
        }
        return pathInfo.substring(0,idx);
    }
}
