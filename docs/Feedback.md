# 코드리뷰 피드백

## 1단계 - HTTP 서버 구현하기 1차 피드백

- [x] queryParam 일급 컬렉션 만들고 객체 생성 책임 분리
    - [ ] QueryStringHandler 사용 목적
    - [x] QueryStringHandler 매직넘버
- [x] MyHttpResponse 메서드 수정
    - [x] String으로 변경해주는 메서드 생성
- [x] Http11Processor - 불필요한 `;` 제거

## 2, 3, 4단계 - 2차 피드백

- [x] get().get() 제거
- [x] depth 줄이기
    - LoginController
    - RequestHeader
- [x] 미사용 log -> 사용하거나 제거하거나
- [x] 클래스와 필드 이름이 동일할 경우 헷갈리기때문에 이름 변경 필요
- [x] 오타 수정
    - RequestBody - DELIMITER
- [x] Log 사용 시 {}를 통한 문자열 출력
- [x] 미사용 생성자 제거
    - HttpResponse
