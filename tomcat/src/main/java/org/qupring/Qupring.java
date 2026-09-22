package org.qupring;

import com.techcourse.RegisterController;
import org.apache.catalina.startup.Tomcat;
import org.qupring.annotation.QupringApplication;
import org.qupring.mvc.ApplicationScanner;
import com.techcourse.LoginController;
import org.qupring.mvc.QupringMvc;
import org.qupring.mvc.handler.RequestMapping;

public class Qupring {
    public void run(Class<?> application) {
        validateApplicationClass(application);

        ApplicationScanner applicationScanner = new ApplicationScanner();
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.addResourceMappings(applicationScanner.scanForResources());
        requestMapping.addController("/login", new LoginController());
        requestMapping.addController("/register", new RegisterController());

        QupringMvc qupringMvc = new QupringMvc(requestMapping);

        final var tomcat = new Tomcat(qupringMvc);
        tomcat.start();
    }

    private void validateApplicationClass(Class<?> application) {
        if (!application.isAnnotationPresent(QupringApplication.class)) {
            throw new IllegalArgumentException("해당 애플리케이션은 @QupringApplication이 붙어있지 않습니다.");
        }
    }
}
