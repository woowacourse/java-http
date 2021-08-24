package nextstep.jwp.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SonTest {
    // User, Object로 지정을 해주더라도, 정의된 클래스에서의 메소드를 호출함
    @Test
    void user() {
        User son = new Son(1L,"account", "password", "email@email.com");
        System.out.println(son);
        son.hello();
    }

    @Test
    void object() {
        Object son = new Son(1L,"account", "password", "email@email.com");
        System.out.println(son);
    }
}
