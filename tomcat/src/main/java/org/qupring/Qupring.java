package org.qupring;

import com.techcourse.UserController;
import java.util.List;
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

        /*
        //임시
        handlerMapping.addControllerMappings(List.of(UserController.class));
         */

        QupringMvc qupringMvc = new QupringMvc(handlerMapping);

        final var tomcat = new Tomcat(qupringMvc);
        tomcat.start();
    }

    private void validateApplicationClass(Class<?> application) {
        if (!application.isAnnotationPresent(QupringApplication.class)) {
            throw new IllegalArgumentException("해당 애플리케이션은 @QupringApplication이 붙어있지 않습니다.");
        }
    }
}
