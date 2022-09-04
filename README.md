# 톰캣 구현하기

## 1. HTTP Status Code 302

로그인 여부에 따라 다른 페이지로 이동시켜보자.
/login 페이지에서 아이디는 gugu, 비밀번호는 password를 입력하자.
로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 /index.html로 리다이렉트 한다.
로그인에 실패하면 401.html로 리다이렉트한다.

- [x] 로그인 성공시 응답에 302, Location(index.html) 헤더 반환하는 기능
- [ ] POST 방식으로 회원가입, 로그인 기능
