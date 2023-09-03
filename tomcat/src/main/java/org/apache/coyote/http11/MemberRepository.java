package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemberRepository {

    private static final Map<String, String> memberRepository = new ConcurrentHashMap<>();

    static {
        memberRepository.put("gugu", "password");
    }

    public MemberRepository() {
    }

    public static String getPassword(String account) {
        return memberRepository.get(account);
    }
}
