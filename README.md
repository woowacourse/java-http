# HTTP 서버 구현하기

<br>

## 1단계 요구사항
- [x] `GET /index.html` 응딥하기
- [x] Query String 파싱
- [x] Http Status Code 302
- [x] Post 방식으로 회원가입
- [x] CSS 지원하기

<br>

## 2, 3단계 요구사항
- [x] 리팩토링 - 적절한 클래스를 추가하고 역할을 부여하여 객체 분리
- [x] Controller 인터페이스 추가
- [x] Cookie에 JSESSIONID 값 저장하기 
- [x] Session 구현하기

<br>

## 앞으로
- 기능
  - [ ] 컴포넌트 스캔
  - [ ] View Render 부분 기능 구현 (현재 Controller에 역할이 너무 모여있음.)
- 리팩토링
  - [ ] Http Header MultiMap으로 리팩토링.
  - [ ] Http Request Parser static 제거 및 인터페이스로 분리

