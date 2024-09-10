package org.apache.catalina.startup;

import org.apache.catalina.config.DefaultTomcatConfig;
import org.apache.catalina.config.TomcatConfig;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final TomcatConfig tomcatConfig;

    public Tomcat() {
        this.tomcatConfig = new DefaultTomcatConfig();
    }

    public Tomcat(TomcatConfig tomcatConfig) {
        this.tomcatConfig = tomcatConfig;
    }

    public void start() {
        var connector = new Connector(tomcatConfig);
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
