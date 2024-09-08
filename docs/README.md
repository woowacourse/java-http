## 요구사항

---

- [x] 로컬 호스트로 "/"에 요청을 보내면 `Hello World!` 라는 문구가 보인다.


- [x] `/index.html`로 요청을 보내는 경우 인덱스 페이지가 응답한다.
    - [x] `css` 파일도 응답이 가능하도록 기능을 구현한다.


- [x] `/login?account=gugu&password=password` 로 요청을 보내는 경우 로그인 페이지를 응답한다.
    - [x] `QueryString`을 파싱할 수 있다.
        - [x] 아이디와 비밀번호가 일치하는 경우 로그를 남긴다.
