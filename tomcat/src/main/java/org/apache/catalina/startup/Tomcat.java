package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;

public class Tomcat {

    public void start() {
        var connector = new Connector();
        connector.start();
    }
}
