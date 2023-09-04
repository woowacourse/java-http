package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemberRepository {

    private static final Map<String, Member> memberRepository = new ConcurrentHashMap<>();

    static {
        memberRepository.put("gugu", new Member("gugu", "password", "a@a.com"));
    }

    public static Member findMember(String account) {
        return memberRepository.get(account);
    }

    public static void register(Member member) {
        memberRepository.put(member.getAccount(), member);
    }
}
