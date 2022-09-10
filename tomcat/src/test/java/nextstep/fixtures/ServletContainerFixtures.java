package nextstep.fixtures;

import nextstep.jwp.servlet.DispatcherServlet;
import org.apache.catalina.core.MockServlet;
import org.apache.catalina.core.ServletContainer;

public class ServletContainerFixtures {

    public static ServletContainer 기본_URI로_생성() {
        final ServletContainer servletContainer = new ServletContainer();
        servletContainer.registerServlet("/", new DispatcherServlet());
        return servletContainer;
    }

    public static ServletContainer 여러가지_URI로_생성() {
        final ServletContainer servletContainer = 기본_URI로_생성();
        servletContainer.registerServlet("/mock", new MockServlet());
        return servletContainer;
    }
}
