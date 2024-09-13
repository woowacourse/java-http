package thread.stage1;

import java.util.ArrayList;
import java.util.List;

public class UserServlet {

    private final List<User> users = new ArrayList<>();

    public void service(final User user) {
        join(user);
    }

    // 이유 : .add()에 스레드 별로 중단점을 걸게 되면 회원이 등록되지 않는다.
    // 이 상태에서 늦게 요청한 firstThread가 중복 검사할 때 회원이 등록되지 않았으므로 중복 검사를 통과하게 된다.
    // 해결 방법은 synchronized 키워드롤 붙여서 thread를 동기화 시켜준다.
    private synchronized void join(final User user) {
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
