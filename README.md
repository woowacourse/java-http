# 톰캣 구현하기

## 요구사항

- [X] `GET /index.html` 요청에 대해 index 페이지와 상태코드 200을 응답한다.
    - [X] 요청으로부터 요청 uri를 가져온다.
    - [X] url에 따라 적절한 resource를 응답한다.
- [X] `ContentType` header값의 내용을 적절하게 응답한다.
    - [X] 요청 헤더의 `Accept` 값을 가져온다.
    - [X] 응답 헤더에서 알맞은 값을 응답한다.
- [X] `GET /login?account=gugu&password=password` 요청에 대해 query string을 parsing하여 회원을 조회한다.
    - [X] 해당 요청에 대해 `login.html` 페이지를 응답한다.
    - [X] 요청 uri로부터 query parameter를 추출한다.
    - [X] account와 password를 확인하여 유저 정보를 조회한 후 콘솔에 로깅한다.
