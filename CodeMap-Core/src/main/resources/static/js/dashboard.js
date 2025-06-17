// ✅ 페이지 로드 즉시 상단으로 스크롤 강제 실행
document.addEventListener('DOMContentLoaded', function() {
  // 즉시 상단으로 스크롤
  window.scrollTo(0, 0);
  document.documentElement.scrollTop = 0;
  document.body.scrollTop = 0;

  // 추가 보장을 위해 약간의 지연 후 다시 실행
  setTimeout(() => {
    window.scrollTo(0, 0);
    document.documentElement.scrollTop = 0;
    document.body.scrollTop = 0;
  }, 100);

  // 사이드바 메인화면 active 처리
  const mainNavItem = document.querySelector('a[href="/user/main"]');
  if (mainNavItem) {
    mainNavItem.classList.add('active');
  }
});

// 투두 체크박스 이벤트 처리
const checkboxes = document.querySelectorAll('.todo-checkbox');
checkboxes.forEach(checkbox => {
  checkbox.addEventListener('click', function () {
    const todoId = this.getAttribute('data-todo-id');
    const today = new Date().toISOString().split('T')[0];

    fetch(`/todos/${todoId}/complete?date=${today}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(response => {
      if (response.ok) {
        this.classList.toggle('checked');
        const todoItem = this.closest('.todo-item');
        todoItem.classList.toggle('todo-completed');
        this.innerHTML = this.classList.contains('checked') ? '<span>✓</span>' : '';
      } else {
        alert('서버에서 완료 상태 업데이트 실패');
      }
    })
    .catch(error => {
      console.error('에러 발생:', error);
    });
  });
});

// ✅ 브라우저 뒤로가기/앞으로가기 시에도 상단 스크롤 보장
window.addEventListener('pageshow', function(event) {
  window.scrollTo(0, 0);
});

// ✅ 모든 로드 이벤트에서 스크롤 리셋
window.addEventListener('load', function() {
  window.scrollTo(0, 0);
});