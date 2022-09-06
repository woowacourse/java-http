package nextstep;

import nextstep.jwp.servlet.GetHomeServlet;
import nextstep.jwp.servlet.GetIndexServlet;
import nextstep.jwp.servlet.GetLoginServlet;
import nextstep.jwp.servlet.PostLoginServlet;
import nextstep.jwp.servlet.PostRegisterServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http.ServletMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) {
        log.info("web server start.");

        final ServletMapper servletMapper = ServletMapper.of(
                new GetIndexServlet(),
                new GetHomeServlet(),
                new PostLoginServlet(),
                new GetLoginServlet(),
                new PostRegisterServlet()
        );

        final var tomcat = new Tomcat();
        tomcat.start(servletMapper);
    }
}
