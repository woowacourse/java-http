# 4단계 - 동시성 확장하기

핵심은 WAS에서 Thread Pool이 어떻게 동작하는지 이해하는 것!!

## 기능 요구 사항
- [ ] Executors로 Thread Pool 적용
  - `Connector` 생성자에서 `ExecutorService` 생성
  - 스레드 개수를 `maxThreads` 변수로 지정
  - `process()`에서 `new Thread()` 대신 스레드 풀에 작업 제출
  - 서버 종료하면 스레드 풀도 함께 종료
- [ ] 동시성 컬렉션 사용


