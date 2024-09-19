package thread.stage1;

import java.util.ArrayList;
import java.util.List;

public class UserServlet {

    private final List<User> users = new ArrayList<>();

    /**
     * synchronized 키워드를 사용해서 한 스레드가 해당 메서드를 사용하면 다른 스레드는 접근하지 못하게 락(Lock)을 거는 방식으로 동시성 문제를 해결할 수 있다.
     *
     * @param user
     */
    public synchronized void service(final User user) {
        join(user);
    }

    private void join(final User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    public int size() {
        return users.size();
    }

    public List<User> getUsers() {
        return users;
    }
}
