# 톰캣 구현하기

## 기능 요구 사항

### 1단계 - HTTP 서버 구현하기

- [X] http://localhost:8080/index.html 페이지에 접근 가능하다.
  - [X] Http Method가 GET인지 확인한다.
  - [X] / 일 경우 안녕 문자열을 응답하고 그 외에는 파일을 읽어온다. 
- [X] 접근한 페이지의 js, css 파일을 불러올 수 있다.
  - [X] css: text/css
  - [X] js: application/javascript
- [X] uri의 QueryString을 파싱하는 기능이 있다.
  - [X] http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html) 페이지에 보여준다.
  - [X] Query String을 파싱해서 아이디, 비밀번호가 일치하면 회원을 조회한 결과를 출력한다.

### 추가로 고려해볼 수 있는 부분

- [ ] NOT FOUND 처리 로직을 추가한다. 
- [ ] 커스텀 예외 코드를 만든다. 
