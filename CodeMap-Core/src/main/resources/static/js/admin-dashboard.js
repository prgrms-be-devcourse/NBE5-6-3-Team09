// admin-dashboard.js
// 관리자 대시보드 전용 JavaScript

document.addEventListener('DOMContentLoaded', function() {
  console.log('🛠️ 관리자 대시보드 로드 완료');

  // ✅ 초기화
  initDashboard();

  // ✅ 이벤트 리스너 등록
  attachEventListeners();

  // ✅ 실시간 업데이트 시작 (30초마다)
  startRealtimeUpdates();
});

/**
 * 대시보드 초기화
 */
function initDashboard() {
  // 메트릭 카드 숫자 애니메이션
  animateMetricValues();

  // 시스템 바 애니메이션
  animateSystemBars();

  // 상태 표시기 애니메이션
  animateStatusIndicator();
}

/**
 * 이벤트 리스너 등록
 */
function attachEventListeners() {
  // 새로고침 버튼
  const refreshBtn = document.querySelector('.refresh-btn');
  if (refreshBtn) {
    refreshBtn.addEventListener('click', handleRefresh);
  }

  // 메트릭 카드 클릭 효과
  const metricCards = document.querySelectorAll('.metric-card');
  metricCards.forEach(card => {
    card.addEventListener('click', handleMetricCardClick);
    card.addEventListener('mouseenter', handleMetricCardHover);
  });

  // 빠른 액션 카드 클릭
  const actionCards = document.querySelectorAll('.quick-action-card');
  actionCards.forEach(card => {
    card.addEventListener('click', handleActionCardClick);
  });

  // 활동 아이템 클릭
  const activityItems = document.querySelectorAll('.activity-item');
  activityItems.forEach(item => {
    item.addEventListener('click', handleActivityClick);
  });
}

/**
 * 메트릭 값 애니메이션
 */
function animateMetricValues() {
  const metricValues = document.querySelectorAll('.metric-value');

  metricValues.forEach(element => {
    const finalValue = parseInt(element.textContent) || 0;
    element.textContent = '0';

    animateNumber(element, 0, finalValue, 1500);
  });
}

/**
 * 숫자 카운트업 애니메이션
 */
function animateNumber(element, start, end, duration) {
  const startTime = performance.now();

  function updateNumber(currentTime) {
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / duration, 1);

    // easeOutCubic 이징 함수
    const easeProgress = 1 - Math.pow(1 - progress, 3);
    const currentValue = Math.floor(start + (end - start) * easeProgress);

    element.textContent = currentValue.toLocaleString();

    if (progress < 1) {
      requestAnimationFrame(updateNumber);
    } else {
      element.textContent = end.toLocaleString();
    }
  }

  requestAnimationFrame(updateNumber);
}

/**
 * 시스템 바 애니메이션
 */
function animateSystemBars() {
  const bars = document.querySelectorAll('.bar-fill');

  bars.forEach((bar, index) => {
    const finalWidth = bar.style.width;
    bar.style.width = '0%';

    setTimeout(() => {
      bar.style.transition = 'width 1s ease-out';
      bar.style.width = finalWidth;
    }, index * 200);
  });
}

/**
 * 상태 표시기 애니메이션
 */
function animateStatusIndicator() {
  const statusDot = document.querySelector('.status-dot');
  if (statusDot) {
    // 펄스 효과 강화
    statusDot.style.animation = 'pulse 2s ease-in-out infinite';
  }
}

/**
 * 새로고침 버튼 핸들러
 */
function handleRefresh(event) {
  event.preventDefault();

  const button = event.target;
  const originalText = button.textContent;

  // 로딩 상태
  button.textContent = '새로고침 중...';
  button.disabled = true;
  button.style.opacity = '0.6';

  // 시스템 메트릭 시뮬레이션 업데이트
  updateSystemMetrics();

  // 2초 후 원래 상태로 복원
  setTimeout(() => {
    button.textContent = originalText;
    button.disabled = false;
    button.style.opacity = '1';

    showNotification('시스템 상태가 업데이트되었습니다.', 'success');
  }, 2000);
}

/**
 * 메트릭 카드 클릭 핸들러
 */
function handleMetricCardClick(event) {
  const card = event.currentTarget;

  // 클릭 애니메이션
  card.style.transform = 'scale(0.95)';
  setTimeout(() => {
    card.style.transform = '';
  }, 150);

  // 카드 타입에 따른 액션
  const cardClass = card.className;
  if (cardClass.includes('primary')) {
    console.log('총 사용자 카드 클릭');
    // 나중에 사용자 상세 모달이나 페이지로 이동
  } else if (cardClass.includes('secondary')) {
    console.log('활성 사용자 카드 클릭');
  } else if (cardClass.includes('accent')) {
    console.log('총 질문 카드 클릭');
  } else if (cardClass.includes('warning')) {
    console.log('시스템 알림 카드 클릭');
  }
}

/**
 * 메트릭 카드 호버 핸들러
 */
function handleMetricCardHover(event) {
  const card = event.currentTarget;
  const metricValue = card.querySelector('.metric-value');

  // 호버 시 글로우 효과
  metricValue.style.textShadow = '0 0 20px currentColor';

  card.addEventListener('mouseleave', () => {
    metricValue.style.textShadow = '';
  }, { once: true });
}

/**
 * 액션 카드 클릭 핸들러
 */
function handleActionCardClick(event) {
  const card = event.currentTarget;
  const href = card.getAttribute('href');

  // 클릭 효과
  card.style.transform = 'scale(0.95)';

  setTimeout(() => {
    if (href && !event.ctrlKey && !event.metaKey) {
      window.location.href = href;
    }
  }, 100);
}

/**
 * 활동 아이템 클릭 핸들러
 */
function handleActivityClick(event) {
  const item = event.currentTarget;
  const activityTitle = item.querySelector('.activity-title').textContent;

  console.log(`활동 클릭: ${activityTitle}`);

  // 클릭 피드백
  item.style.background = 'rgba(66, 153, 225, 0.1)';
  setTimeout(() => {
    item.style.background = '';
  }, 300);
}

/**
 * 시스템 메트릭 업데이트 (시뮬레이션)
 */
function updateSystemMetrics() {
  const systemBars = document.querySelectorAll('.bar-fill');

  systemBars.forEach(bar => {
    // 랜덤한 값으로 업데이트 (실제로는 서버에서 데이터 가져옴)
    const currentWidth = parseInt(bar.style.width);
    const variation = (Math.random() - 0.5) * 10; // ±5% 변화
    const newWidth = Math.max(0, Math.min(100, currentWidth + variation));

    bar.style.width = newWidth + '%';

    const valueElement = bar.parentElement.parentElement.querySelector('.system-value');
    if (valueElement) {
      valueElement.textContent = Math.round(newWidth) + '%';

      // 색상 업데이트
      valueElement.className = 'system-value';
      bar.className = 'bar-fill';

      if (newWidth >= 80) {
        valueElement.classList.add('warning');
        bar.classList.add('warning');
      } else if (newWidth <= 50) {
        valueElement.classList.add('good');
        bar.classList.add('good');
      }
    }
  });
}

/**
 * 실시간 업데이트 시작
 */
function startRealtimeUpdates() {
  // 30초마다 데이터 업데이트
  setInterval(() => {
    console.log('🔄 실시간 데이터 업데이트');

    // 실제 구현에서는 서버 API 호출
    // updateDashboardData();

    // 현재는 시뮬레이션
    if (Math.random() > 0.7) { // 30% 확률로 업데이트
      updateSystemMetrics();
    }
  }, 30000);
}

/**
 * 알림 표시 함수
 */
function showNotification(message, type = 'info') {
  // 간단한 토스트 알림 (나중에 더 예쁘게 만들 수 있음)
  const notification = document.createElement('div');
  notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === 'success' ? 'var(--admin-success)' : 'var(--admin-accent)'};
        color: white;
        padding: 1rem 2rem;
        border-radius: 8px;
        z-index: 10000;
        box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        transform: translateX(100%);
        transition: transform 0.3s ease;
    `;
  notification.textContent = message;

  document.body.appendChild(notification);

  // 애니메이션
  setTimeout(() => {
    notification.style.transform = 'translateX(0)';
  }, 100);

  // 3초 후 제거
  setTimeout(() => {
    notification.style.transform = 'translateX(100%)';
    setTimeout(() => {
      document.body.removeChild(notification);
    }, 300);
  }, 3000);
}

/**
 * 키보드 단축키
 */
document.addEventListener('keydown', function(event) {
  // Ctrl + R 또는 F5 - 새로고침
  if ((event.ctrlKey && event.key === 'r') || event.key === 'F5') {
    event.preventDefault();
    const refreshBtn = document.querySelector('.refresh-btn');
    if (refreshBtn) {
      refreshBtn.click();
    }
  }

  // ESC - 알림 닫기 등
  if (event.key === 'Escape') {
    const notifications = document.querySelectorAll('[style*="position: fixed"]');
    notifications.forEach(notification => {
      if (notification.parentElement) {
        notification.parentElement.removeChild(notification);
      }
    });
  }
});

// 전역 함수로 내보내기 (필요한 경우)
window.AdminDashboard = {
  refresh: handleRefresh,
  showNotification: showNotification,
  updateMetrics: updateSystemMetrics
};