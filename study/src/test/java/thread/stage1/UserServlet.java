package thread.stage1;

import com.google.common.util.concurrent.Atomics;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class UserServlet {

    private final AtomicReference<List<User>> users = new AtomicReference<>(new ArrayList<>());

    public void service(final User user) {
        join(user);
    }

    private void join(final User user) {
        List<User> expected = users.get();
        List<User> newValue = new ArrayList<>(expected);
        newValue.add(new User("gugu2"));
        if (!expected.contains(user)) {
            users.compareAndSet(expected, newValue);
        }
    }

    public int size() {
        return users.get().size();
    }

    public List<User> getUsers() {
        return users.get();
    }
}
