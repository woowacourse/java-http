package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemberRepository {

    private static final Map<String, Member> inMemoryRepository = new ConcurrentHashMap<>();

    static {
        inMemoryRepository.put("gugu", new Member("gugu", "password", "a@a.com"));
    }

    private MemberRepository() {
    }

    public static Member findMember(String account) {
        return inMemoryRepository.get(account);
    }

    public static boolean exists(String account) {
        return inMemoryRepository.containsKey(account);
    }

    public static void register(Member member) {
        inMemoryRepository.put(member.getAccount(), member);
    }
}
