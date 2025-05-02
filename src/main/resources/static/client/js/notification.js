// Danh sách sinh viên cố định
const students = [
    { id: 1, name: "Nguyễn Văn An", mssv: "SV001", class: "CNTT01", group: "Nhóm 1" },
    { id: 2, name: "Trần Thị Bình", mssv: "SV002", class: "CNTT01", group: "Nhóm 1" },
    { id: 3, name: "Lê Văn Cường", mssv: "SV003", class: "CNTT01", group: "Nhóm 2" },
    { id: 4, name: "Phạm Thị Dung", mssv: "SV004", class: "CNTT01", group: "Nhóm 2" },
    { id: 5, name: "Hoàng Văn Em", mssv: "SV005", class: "CNTT01", group: "Nhóm 3" }
];

// Chuyển đổi tab
function showSection(sectionId) {
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    
    document.querySelectorAll('.tab').forEach(tab => {
        tab.classList.remove('active');
    });
    
    document.getElementById(sectionId).classList.add('active');
    document.querySelector(`button[onclick="showSection('${sectionId}')"]`).classList.add('active');
}

// Quản lý thông báo
let notifications = JSON.parse(localStorage.getItem('notifications')) || [];

document.getElementById('notificationForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const content = document.getElementById('notificationContent').value.trim();
    const week = document.getElementById('week').value;
    const date = document.getElementById('date').value;
    
    if (!content || !week || !date) {
        alert('Vui lòng điền đầy đủ thông tin!');
        return;
    }
    
    const id = Date.now();
    const notification = { 
        id, 
        content, 
        week, 
        date, 
        teacher: "GV_BM1" 
    };
    
    notifications.push(notification);
    localStorage.setItem('notifications', JSON.stringify(notifications));
    
    renderNotifications();
    this.reset();
});

function renderNotifications() {
    const tbody = document.getElementById('notificationBody');
    tbody.innerHTML = '';
    
    // Sắp xếp thông báo theo ngày (mới nhất lên đầu)
    notifications.sort((a, b) => new Date(b.date) - new Date(a.date));
    
    notifications.forEach((notif, index) => {
        const row = document.createElement('tr');
        
        // Định dạng ngày hiển thị
        const displayDate = new Date(notif.date).toLocaleDateString('vi-VN');
        
        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${notif.content}</td>
            <td>Tuần ${notif.week}</td>
            <td>${displayDate}</td>
            <td>${notif.teacher}</td>
            <td><button class="delete-btn" onclick="deleteNotification(${notif.id})">Xóa</button></td>
        `;
        
        tbody.appendChild(row);
    });
}

function deleteNotification(id) {
    if (confirm('Bạn có chắc chắn muốn xóa thông báo này?')) {
        notifications = notifications.filter(notif => notif.id !== id);
        localStorage.setItem('notifications', JSON.stringify(notifications));
        renderNotifications();
    }
}

// Quản lý điểm
let grades = JSON.parse(localStorage.getItem('grades')) || [];
let gradesSubmitted = JSON.parse(localStorage.getItem('gradesSubmitted')) || false;

function renderGradingTable() {
    const tbody = document.getElementById('gradingBody');
    tbody.innerHTML = '';
    
    students.forEach((student, index) => {
        // Tìm điểm của sinh viên nếu có
        const studentGrade = grades.find(g => g.studentId === student.id) || { 
            score: '', 
            bonus: '', 
            absent: false 
        };
        
        const row = document.createElement('tr');
        
        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${student.name}</td>
            <td>${student.mssv}</td>
            <td>${student.class}</td>
            <td>${student.group}</td>
            <td>
                <input type="number" class="score" data-student-id="${student.id}" 
                       value="${studentGrade.score}" 
                       ${studentGrade.absent || gradesSubmitted ? 'disabled' : ''} 
                       min="0" max="10" step="0.1">
            </td>
            <td>
                <input type="number" class="bonus" data-student-id="${student.id}" 
                       value="${studentGrade.bonus}" 
                       ${studentGrade.absent || gradesSubmitted ? 'disabled' : ''} 
                       min="0" max="2" step="0.1">
            </td>
            <td>
                <input type="checkbox" class="absent" data-student-id="${student.id}" 
                       ${studentGrade.absent ? 'checked' : ''} 
                       ${gradesSubmitted ? 'disabled' : ''}>
            </td>
        `;
        
        tbody.appendChild(row);
    });
    
    // Xử lý sự kiện cho checkbox vắng
    document.querySelectorAll('.absent').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const studentId = parseInt(this.dataset.studentId);
            const scoreInput = document.querySelector(`.score[data-student-id="${studentId}"]`);
            const bonusInput = document.querySelector(`.bonus[data-student-id="${studentId}"]`);
            
            if (this.checked) {
                scoreInput.value = 0;
                bonusInput.value = 0;
                scoreInput.disabled = true;
                bonusInput.disabled = true;
            } else {
                scoreInput.disabled = false;
                bonusInput.disabled = false;
            }
            
            saveGrades();
        });
    });
    
    // Xử lý sự kiện thay đổi điểm
    document.querySelectorAll('.score, .bonus').forEach(input => {
        input.addEventListener('change', saveGrades);
    });
    
    // Cập nhật trạng thái nút gửi điểm
    const submitBtn = document.getElementById('submitGradesBtn');
    if (gradesSubmitted) {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Đã gửi điểm';
        submitBtn.style.backgroundColor = '#95a5a6';
    } else {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Gửi điểm';
        submitBtn.style.backgroundColor = '#2ecc71';
    }
}

function saveGrades() {
    grades = [];
    
    students.forEach(student => {
        const scoreInput = document.querySelector(`.score[data-student-id="${student.id}"]`);
        const bonusInput = document.querySelector(`.bonus[data-student-id="${student.id}"]`);
        const absentCheckbox = document.querySelector(`.absent[data-student-id="${student.id}"]`);
        
        grades.push({
            studentId: student.id,
            score: scoreInput.value,
            bonus: bonusInput.value,
            absent: absentCheckbox.checked
        });
    });
    
    localStorage.setItem('grades', JSON.stringify(grades));
}

function submitGrades() {
    if (gradesSubmitted) {
        alert('Điểm đã được gửi, không thể chỉnh sửa!');
        return;
    }
    
    // Kiểm tra xem đã nhập đủ điểm chưa
    let allScoresEntered = true;
    grades.forEach(grade => {
        if (grade.score === '' || grade.bonus === '') {
            allScoresEntered = false;
        }
    });
    
    if (!allScoresEntered) {
        alert('Vui lòng nhập đầy đủ điểm cho tất cả sinh viên!');
        return;
    }
    
    if (confirm('Bạn có chắc chắn muốn gửi bảng điểm? Sau khi gửi sẽ không thể chỉnh sửa.')) {
        gradesSubmitted = true;
        localStorage.setItem('gradesSubmitted', JSON.stringify(gradesSubmitted));
        
        alert('Gửi điểm thành công!');
        renderGradingTable();
    }
}

// Khởi tạo trang
document.addEventListener('DOMContentLoaded', function() {
    renderNotifications();
    renderGradingTable();
    
    // Đặt ngày mặc định là hôm nay
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('date').value = today;
});