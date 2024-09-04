# 만들면서 배우는 스프링

## 톰캣 구현하기

### 학습목표
- 웹 서버 구현을 통해 HTTP 이해도를 높인다.
- HTTP의 이해도를 높혀 성능 개선할 부분을 찾고 적용할 역량을 쌓는다.
- 서블릿에 대한 이해도를 높인다.
- 스레드, 스레드풀을 적용해보고 동시성 처리를 경험한다.

### 시작 가이드
1. 미션을 시작하기 전에 파일, 입출력 스트림 학습 테스트를 먼저 진행합니다.
    - [File, I/O Stream](study/src/test/java/study)
    - 나머지 학습 테스트는 다음 강의 시간에 풀어봅시다.
2. 학습 테스트를 완료하면 LMS의 1단계 미션부터 진행합니다.

## 학습 테스트
1. [File, I/O Stream](study/src/test/java/study)
2. [HTTP Cache](study/src/test/java/cache)
3. [Thread](study/src/test/java/thread)

## 기능 요구 사항

### 1단계 

- [x] GET /index.html 응답하기
   - 인덱스 페이지(http://localhost:8080/index.html)에 접근
   - Http11ProcessorTest 테스트 클래스의 모든 테스트를 통과
- [x] CSS 지원하기
   - 사용자가 페이지를 열었을 때 CSS 파일을 호출하는 기능 추가
   - Accept를 분석해서 반환 content-type 설정
- Query String 파싱 
   - [ ] 로그인 페이지 접속 (login.html)
   - [ ] Query String을 파싱 후 아이디/비밀번호 일치 확인
   - [ ] 일치할 경우, 콘솔창에 로그로 회원을 조회한 결과 출력
