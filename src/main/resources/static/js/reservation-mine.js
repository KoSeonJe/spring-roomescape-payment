document.addEventListener('DOMContentLoaded', () => {
  /*
  TODO: [2단계] 내 예약 목록 조회 기능
        endpoint 설정
   */
  fetch('/reservations/mine') // 내 예약 목록 조회 API 호출
      .then(response => {
        if (response.status === 200) return response.json();
        throw new Error('Read failed');
      })
      .then(render)
      .catch(error => console.error('Error fetching reservations:', error));
});

function render(data) {
  const tableBody = document.getElementById('table-body');
  tableBody.innerHTML = '';

  data.forEach(item => {
    const row = tableBody.insertRow();

    const theme = item.theme;
    const date = item.date;
    const time = item.time;
    const status = item.status;
    const rank = item.rank;

    row.insertCell(0).textContent = theme;
    row.insertCell(1).textContent = date;
    row.insertCell(2).textContent = time;
    switch (status) {
      case "CONFIRMED":
        row.insertCell(3).textContent = "예약";
        break;
      case "ACCEPTED":
        row.insertCell(3).textContent = "대기 승인";
        break;
      case "DENIED":
        row.insertCell(3).textContent = "대기 거절";
        break;
      case "CANCELED":
        row.insertCell(3).textContent = "취소";
        break;
      case "PENDING":
        row.insertCell(3).textContent = rank + "번째 대기중";
        break;
      default:
        row.insertCell(3).textContent = "";
        break;
    }

    /*
    TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 기능 구현 후 활성화
     */
    if (status === "PENDING") { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
      const cancelCell = row.insertCell(4);
      const cancelButton = document.createElement('button');
      cancelButton.textContent = '취소';
      cancelButton.className = 'btn btn-danger';
      cancelButton.onclick = function () {
        requestDeleteWaiting(item.id).then(() => window.location.reload());
      };
      cancelCell.appendChild(cancelButton);
    } else { // 예약 완료 상태일 때
      row.insertCell(4).textContent = '';
    }
  });
}

function requestDeleteWaiting(id) {
  /*
  TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
   */
  const endpoint = `/reservations/waiting/${id}/cancel`;
  return fetch(endpoint, {
    method: 'PATCH'
  }).then(response => {
    if (response.status === 204) return;
    throw new Error('Delete failed');
  });
}
