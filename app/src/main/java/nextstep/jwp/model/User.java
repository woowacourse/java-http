package nextstep.jwp.model;

import java.util.concurrent.atomic.AtomicLong;

public class User {

    private static final AtomicLong CURRENT_ID = new AtomicLong();

    private final long id;
    private final String account;
    private final String password;
    private final String email;

    public User(long id, String account, String password, String email) {
        validateInputs(id, account, password, email);
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(String account, String password, String email) {
        this(getNextId(), account, password, email);
    }

    private static long getNextId() {
        return CURRENT_ID.incrementAndGet();
    }

    private void validateInputs(long id, String account, String password, String email) {
        if (id < CURRENT_ID.get() || account == null || password == null || email == null ||
            account.isBlank() || password.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public long getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", account='" + account + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
