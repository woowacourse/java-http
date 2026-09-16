package org.qupring;

import org.apache.catalina.startup.Tomcat;
import org.qupring.annotation.QupringApplication;
import org.qupring.mvc.ApplicationScanner;
import org.qupring.mvc.QupringMvc;
import org.qupring.mvc.handler.HandlerMapping;

public class Qupring {
    public void run(Class<?> application) {
        validateApplicationClass(application);

        ApplicationScanner applicationScanner = new ApplicationScanner();
        HandlerMapping handlerMapping = new HandlerMapping();
        handlerMapping.addResourceMappings(applicationScanner.scanForResources());

        QupringMvc qupringMvc = new QupringMvc(handlerMapping);

        final var tomcat = new Tomcat(qupringMvc);
        tomcat.start();
    }

    private void validateApplicationClass(Class<?> application) {
        if (!application.isAnnotationPresent(QupringApplication.class)) {
            throw new IllegalArgumentException("The application class must be annotated with @QupringApplication");
        }
    }
}
