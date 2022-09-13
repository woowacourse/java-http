package org.apache.catalina.startup;

import java.io.IOException;
import nextstep.Application;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.request.mapping.controllerscan.ControllerScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start() {
        ControllerScanner.scan(Application.class.getPackageName());
        var connector = new Connector();
        connector.start();

        try {
            // make the application wait until we press any key.
            System.in.read();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("web server stop.");
            connector.stop();
        }
    }

    public void start(final int port,
                      final int acceptCount,
                      final int maxThreadSize,
                      final int coreThreadSize,
                      final long keepAliveTimeSecond) {
        ControllerScanner.scan(Application.class.getPackageName());
        var connector = new Connector(port, acceptCount, maxThreadSize, coreThreadSize, keepAliveTimeSecond);
        connector.start();

        try {
            // make the application wait until we press any key.
            System.in.read();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("web server stop.");
            connector.stop();
        }
    }
}
