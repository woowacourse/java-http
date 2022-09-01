# 톰캣 구현하기

## 요구사항

- [x] GET /index.html 응답하기
```text
인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있도록 만들자.
Http11ProcessorTest 테스트 클래스의 모든 테스트를 통과하면 된다.
브라우저에서 요청한 HTTP Request Header는 다음과 같다.

```
- [x] CSS 지원하기
```text
인덱스 페이지에 접속하니까 화면이 이상하게 보인다.
개발자 도구를 열어서 에러 메시지를 체크해보니 브라우저가 CSS를 못 찾고 있다.
사용자가 페이지를 열었을 때 CSS 파일도 호출하도록 기능을 추가하자.
```
- [ ] Query String 파싱
```text
http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)를 보여주도록 만들자.
그리고 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 회원을 조회한 결과가 나오도록 만들자.
```
