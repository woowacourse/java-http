package org.apache.coyote.http11;

// 미래를 위해 남기는 PathMatcher
// 리뷰 X
public class PathMatcher {

    /**
     * 주어진 URL과 Ant 스타일 패턴이 일치하는지 확인합니다.
     *
     * @param pattern Ant 스타일 패턴 (예: "/api/**", "/files/*.txt")
     * @param path    매칭할 URL 경로 (예: "/api/users", "/files/document.txt")
     * @return 일치하면 true, 아니면 false
     */
    public boolean matches(String pattern, String path) {
        String[] patternSegments = pattern.split("/");
        String[] pathSegments = path.split("/");

        if (patternSegments.length > pathSegments.length && !pattern.endsWith("/**")) {
            return false;
        }

        for (int i = 0; i < patternSegments.length; i++) {
            String patternSegment = patternSegments[i];

            if ("**".equals(patternSegment)) {
                if (i == patternSegments.length - 1) {
                    return true;
                }
                for (int j = i; j < pathSegments.length; j++) {
                    if (matches(substring(patternSegments, i + 1), substring(pathSegments, j))) {
                        return true;
                    }
                }
                return false;
            }

            if (patternSegment.contains("*")) {
                if (!isWildcardMatch(patternSegment, pathSegments[i])) {
                    return false;
                }
            } else if (!patternSegment.equals(pathSegments[i])) {
                return false;
            }
        }

        return patternSegments.length == pathSegments.length;
    }

    private boolean isWildcardMatch(String pattern, String path) {
        String regex = pattern.replace("*", ".*");
        return path.matches(regex);
    }

    private String substring(String[] array, int start) {
        return String.join("/", java.util.Arrays.copyOfRange(array, start, array.length));
    }
}
