package nextstep.jwp;

import java.util.HashMap;
import nextstep.jwp.presentation.HelloWorldController;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.RegisterController;

public class ApplicationContextFactory {

    public static ApplicationContext create() {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("/", new HelloWorldController());
        hashMap.put("/login", new LoginController());
        hashMap.put("/register", new RegisterController());

        return new ApplicationContext(hashMap);
    }
}
