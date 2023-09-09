package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Adaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private final Adaptor adaptor;

    public Tomcat(Adaptor adaptor) {
        this.adaptor = adaptor;
    }

    public void start() {
        var connector = new Connector(adaptor);
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
