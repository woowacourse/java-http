package thread.stage2;

import org.springframework.stereotype.Component;

@Component
public class HelloWorldService {

    public String helloWorld() {
        return "Hello World";
    }
}
