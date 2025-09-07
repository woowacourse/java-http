package org.apache.catalina.startup;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;

@Slf4j
public class Tomcat {

    public void start() {
        try (final Connector connector = new Connector();) {
            connector.start();

            // make the application wait until we press any key.
            final int ignored = System.in.read();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
