# 톰캣 구현하기

- [x] `GET /` 요청시 index.html 응답
- [x] GET 요청시 URI에 대응되는 정적 자원 응답하기
  - [x] URI에 확장자가 포함되지 않은 경우, 디폴트로 `.html` 확장자 추가하여 자원 조회
  - [x] 요청한 자원의 확장자에 따라 Content-Type 설정하여 응답 (text/html, text/css, text/javascript, etc)

- [x] 요청 처리 과정에서 문제가 발생한 경우, 그에 대응되는 메시지 응답하기
  - [x] 응답 메시지에 관련 상태코드 추가(e.g., 404 NOT FOUND)
  - [x] 존재하지 않는 자원을 요청한 경우, 404.html 응답
  - [x] 예상하지 못한 문제인 경우, 500.html 응답
  - [x] 상태코드가 500이 아니더라도, 대응되는 html 파일이 없는 경우 500.html 응답

- [x] 요청에 parameter 값이 포함되면 파싱하여 처리한다.
  - [x] Content-Type 헤더 값에 따라 Request Body의 파라미터 값을 파싱할 수 있다.
  - [x] uri에 query string이 포함되면 파싱하여 처리한다.

- [x] `POST /login` 요청시 form에 입력된 값에 따라 302 혹은 401을 응답한다.
  - [x] `account`와 `password` 정보에 대응되는 사용자가 서버에 존재하는 경우 302를 응답하여 /index.html 페이지로 이동시킨다.
  - [x] 잘못된 사용자 정보를 입력한 경우, 401을 응답하며, /401.html 페이지를 보여준다.

- [x] `POST /register` 요청시 form에 입력된 값에 따라 302 혹은 400을 응답한다.
  - [x] 새로운 `account` 정보로 사용자를 생성하려는 경우, 302를 응답하여 /index.html 페이지로 이동시킨다.
  - [x] 이미 `account`에 대응되는 사용자가 서버에 존재하는 경우 400를 응답하며, /400.html 페이지를 보여준다.
