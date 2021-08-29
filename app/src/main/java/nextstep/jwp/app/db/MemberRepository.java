package nextstep.jwp.app.db;


import nextstep.jwp.app.model.Member;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.core.annotation.Component;

@Component
public class MemberRepository {

    private long memberId = 0;
    private final Map<String, Member> database = new ConcurrentHashMap<>();

    public MemberRepository() {
        final Member member = new Member(++memberId, "gugu", "password", "hkkang@woowahan.com");
        final Member nabom = new Member(++memberId, "nabom", "nabom", "nabom@woowahan.com");
        database.put(member.getAccount(), member);
        database.put(nabom.getAccount(), nabom);
    }

    public void save(Member member) {
        member.setId(++memberId);
        database.put(member.getAccount(), member);
    }

    public Optional<Member> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
