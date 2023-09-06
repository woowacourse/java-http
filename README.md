# 톰캣 구현하기

### ✔ Greeting Page
- `GET /`

### ✔ 정적 파일
- `GET /{파일경로}`

### ✔ 로그인 페이지
- `GET /login`
  - 단 해당 페이지는 이미 로그인한 경우 쿠키로 인해서 `/index.html` 로 리다이렉트

### ✔ 로그인 요청
- `POST /login`
  - 요청 바디: `account`, `password`
  - 로그인 성공 이후 `/index.html` 리다이렉트
  - 로그인 실패 이후 `/401.html` 리다이렉트 

### ✔ 회원가입 요청 페이지
- `GET /register`
  
### ✔ 회원가입 요청
- `POST /register`
  - 요청 바디: `account`, `email`, `password`
  - 회원 가입 성공 이후 `/index.html`
  - 아이디 중복인 경우 `400 Bad Request` 반환

### ✔ 존재하지 않는 경로
- `404.html` 반환

### ✔ 서버에러 시
- `500/html` 반환
