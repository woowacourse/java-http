package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.ConnectorConfiguration;
import org.apache.catalina.connector.ServerXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start() {
        ConnectorConfiguration configuration = ServerXmlParser.parse();
        var connector = new Connector(configuration);
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
