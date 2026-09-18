package com.techcourse;

import com.techcourse.handler.LoginPageHandler;
import com.techcourse.handler.LoginUserHandler;
import com.techcourse.handler.RegisterPageHandler;
import com.techcourse.handler.RegisterUserHandler;
import com.techcourse.handler.StaticResourceHandler;
import java.util.List;
import org.apache.catalina.handler.ResourceResolver;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.Adapter;

public class Application {

    public static void main(String[] args) {
        final Adapter adapter = new ResourceResolver(List.of(
                new LoginPageHandler(),
                new LoginUserHandler(),
                new RegisterPageHandler(),
                new RegisterUserHandler(),
                new StaticResourceHandler()   // 가장 일반적인 핸들러는 마지막에
        ));

        final var tomcat = new Tomcat(adapter);
        tomcat.start();
    }
}
