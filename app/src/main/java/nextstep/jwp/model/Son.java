package nextstep.jwp.model;

public class Son extends User{
    public Son(long id, String account, String password, String email) {
        super(id, account, password, email);
    }

    @Override
    public String toString() {
        return "I'm Son";
    }

    @Override
    public void hello() {
        System.out.println("Hello, I'm Son");
    }
}
