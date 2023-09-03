# 톰캣 구현하기

- [x] 요청의 queryParam 파싱하기
    - [x] 복수개의 value가 올 수 있게 하기
    - [x] 요청에 쿼리가 없는 경우 빈 map 반환하도록 처리
- [x] http 요청 구현하기
    - [x] queryParam 추가하기
- [x] /login API 요청 처리
    - [x] 요청한 회원을 조회 후 로깅
    - [x] login.html 반환
    - [x] queryParam이 존재하는 경우 로그인 프로세스 진행
        - [x] 로그인 성공시 302 반환 후 index.html로 리다이렉트
        - [x] 로그인 실패시 UnauthorizedException 반환
- [ ] exception handler 구현하기
    - [ ] UnauthorizedException 반환 시 401 과 함께 401페이지 리다이렉트
