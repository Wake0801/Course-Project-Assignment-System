// ===== KHAI BÁO BIẾN LƯU DỮ LIỆU (SẼ LẤY TỪ API) =====
let allStudents = [];   // Sẽ chứa danh sách tất cả sinh viên lấy từ API
let allSemesters = []; // Sẽ chứa danh sách học kỳ
let allCourses = [];   // Sẽ chứa danh sách lớp tín chỉ
let allGroups = [];    // Sẽ chứa danh sách nhóm (có thể chỉ lấy khi cần)
// ===== KẾT THÚC KHAI BÁO BIẾN =====

document.addEventListener('DOMContentLoaded', () => {
    const semesterSelect = document.getElementById('semester-select');
    const courseSelect = document.getElementById('course-select');
    const searchInput = document.getElementById('search-input');
    const studentTableBody = document.getElementById('student-table-body');
    const emptyStateDiv = document.getElementById('empty-state');
    const loadingRow = studentTableBody.querySelector('.loading-row');

    // --- Các hàm xử lý (Giữ nguyên logic, nhưng hoạt động trên biến mới) ---

    function populateSemesters() {
        semesterSelect.innerHTML = '<option value="">Chọn học kỳ</option>'; // Reset
        if (!allSemesters || allSemesters.length === 0) return; // Kiểm tra nếu chưa có dữ liệu
        allSemesters.forEach(semester => { // Dùng allSemesters
            const option = document.createElement('option');
            option.value = semester.id; // Giả sử API trả về id
            option.textContent = semester.name; // Giả sử API trả về name
            semesterSelect.appendChild(option);
        });
    }

    function updateCourseOptions() {
        const selectedSemesterId = semesterSelect.value;
        courseSelect.innerHTML = '<option value="">Chọn lớp tín chỉ</option>'; // Clear

        if (selectedSemesterId) {
            // TODO: Cần lấy danh sách Courses cho semester này từ API nếu chưa có trong allCourses
            // Hoặc lọc từ allCourses nếu đã lấy hết
            console.log(`Lọc lớp tín chỉ cho học kỳ: ${selectedSemesterId}`); // Debug
            const filteredCourses = allCourses.filter(course => course.semester === selectedSemesterId); // Giả sử course có trường semester
            console.log(`Tìm thấy ${filteredCourses.length} lớp tín chỉ.`); // Debug

            if (filteredCourses.length > 0) {
                filteredCourses.forEach(course => {
                    const option = document.createElement('option');
                    option.value = course.id; // Giả sử API trả về id
                    option.textContent = course.name; // Giả sử API trả về name
                    courseSelect.appendChild(option);
                });
                 courseSelect.disabled = false;
             } else {
                 courseSelect.disabled = true;
                 courseSelect.innerHTML = '<option value="">Không có lớp tín chỉ</option>';
             }
        } else {
            courseSelect.disabled = true;
        }
        // Reset và render lại danh sách sinh viên khi thay đổi học kỳ/lớp
        courseSelect.value = ""; // Đảm bảo reset lựa chọn lớp
        renderStudentList();
    }

    function renderStudentList() {
        const selectedSemesterId = semesterSelect.value; // Không dùng trực tiếp để lọc student, chỉ dùng để lọc course/group
        const selectedCourseId = courseSelect.value;
        const searchTerm = searchInput.value.toLowerCase();

        studentTableBody.innerHTML = ''; // Clear table
        loadingRow.style.display = 'none'; // Ẩn dòng loading
        emptyStateDiv.style.display = 'none'; // Ẩn empty state


        // Nếu chưa có dữ liệu sinh viên gốc thì hiển thị loading/thông báo
        if (!allStudents || allStudents.length === 0) {
            studentTableBody.innerHTML = '<tr class="loading-row"><td colspan="6">Chưa có dữ liệu sinh viên. Đang chờ tải...</td></tr>';
            // Có thể thêm logic kiểm tra nếu API đã gọi mà vẫn rỗng
            return;
        }


        let filteredStudents = allStudents; // Bắt đầu với tất cả sinh viên đã fetch

        // Filter by search term
        if (searchTerm) {
            filteredStudents = filteredStudents.filter(student =>
                (student.id && student.id.toLowerCase().includes(searchTerm)) || // Kiểm tra tồn tại trước khi truy cập
                (student.name && student.name.toLowerCase().includes(searchTerm)) // Kiểm tra tồn tại trước khi truy cập
            );
        }

        // Tùy chọn: Lọc chỉ sinh viên thuộc lớp tín chỉ đã chọn
        // Cần logic phức tạp hơn dựa trên dữ liệu đăng ký môn học hoặc thành viên nhóm
        // if (selectedCourseId) { ... }

        if (filteredStudents.length === 0) {
            emptyStateDiv.style.display = 'block'; // Hiển thị empty state
        } else {
             emptyStateDiv.style.display = 'none';
        }

        filteredStudents.forEach(student => {
            const row = document.createElement('tr');

            // Find student's group IN THE SELECTED COURSE (Dùng allGroups)
            let groupName = 'Chưa có nhóm';
            let badgeClass = 'badge-warning';
            if (selectedCourseId && allGroups && allGroups.length > 0) { // Kiểm tra allGroups
                // Cần đảm bảo allGroups chứa dữ liệu nhóm của lớp tín chỉ đang chọn
                // TODO: Có thể cần gọi API lấy nhóm của lớp tín chỉ nếu chưa có
                const group = allGroups.find(g => g.courseId === selectedCourseId && g.members && g.members.includes(student.id)); // Kiểm tra g.members tồn tại
                if (group) {
                    groupName = group.name || 'N/A'; // Lấy tên nhóm
                    badgeClass = 'badge-primary';
                }
            } else if (selectedCourseId) {
                groupName = 'N/A'; // Chưa có dữ liệu nhóm cho lớp này
                badgeClass = '';
            }
             else {
                 groupName = '-'; // Không áp dụng khi chưa chọn lớp
                 badgeClass = '';
            }

            // Đảm bảo các trường dữ liệu tồn tại trước khi hiển thị
            const studentId = student.id || 'N/A';
            const studentName = student.name || 'N/A';
            const studentDob = student.dob || 'N/A';
            const studentMajor = student.major || 'N/A';
            const studentClass = student.class || 'N/A';


            row.innerHTML = `
                <td>${studentId}</td>
                <td>${studentName}</td>
                <td>${studentDob}</td>
                <td>${studentMajor}</td>
                <td>${studentClass}</td>
                <td><span class="badge ${badgeClass}">${groupName}</span></td>
            `;
            studentTableBody.appendChild(row);
        });
    }

    // --- Khởi tạo và gọi API ---
     async function initializePage() {
         try {
            loadingRow.style.display = 'table-row'; // Hiển thị loading ban đầu

            // TODO: Gọi API để lấy dữ liệu ban đầu
            // Ưu tiên lấy Học kỳ và Lớp tín chỉ (nếu có thể lấy hết) trước
            // Sinh viên và Nhóm có thể lấy sau khi người dùng chọn Lớp tín chỉ
            console.log("Bắt đầu tải dữ liệu ban đầu...");

            // Ví dụ gọi API song song (Nếu API độc lập)
            // const [semestersRes, coursesRes, studentsRes, groupsRes] = await Promise.all([
            //     fetch('/api/semesters').catch(e => { console.error('Fetch semesters failed:', e); return { ok: false }; }), // Thêm catch cho từng cái
            //     fetch('/api/courses').catch(e => { console.error('Fetch courses failed:', e); return { ok: false }; }),   // Lấy hết courses nếu có thể
            //     fetch('/api/students').catch(e => { console.error('Fetch students failed:', e); return { ok: false }; }), // Lấy hết students ban đầu
            //     fetch('/api/groups').catch(e => { console.error('Fetch groups failed:', e); return { ok: false }; })      // Có thể không cần lấy hết groups ban đầu
            // ]);

            // if (semestersRes.ok) allSemesters = await semestersRes.json(); else console.error("Lỗi API học kỳ");
            // if (coursesRes.ok) allCourses = await coursesRes.json(); else console.error("Lỗi API lớp tín chỉ");
            // if (studentsRes.ok) allStudents = await studentsRes.json(); else console.error("Lỗi API sinh viên");
            // if (groupsRes.ok) allGroups = await groupsRes.json(); else console.error("Lỗi API nhóm");

            // ----- Bỏ comment và thay thế bằng fetch thật -----
            // Giả lập dữ liệu tạm thời để test giao diện (XÓA SAU KHI CÓ API)
             allSemesters = [{id: 'HK1-2025', name: 'Học kỳ 1 - 2025'}, {id: 'HK2-2025', name: 'Học kỳ 2 - 2025'}];
             allCourses = [{id: 'COURSE1', name: 'Lập trình Web', semester: 'HK1-2025'}, {id: 'COURSE2', name: 'Cơ sở dữ liệu', semester: 'HK1-2025'}, {id: 'COURSE3', name: 'Mạng máy tính', semester: 'HK2-2025'}];
             allStudents = [{id: 'SVTEST1', name: 'Test Student 1', dob: '01/01/2003', major: 'CNTT', class: 'D21CN1'}, {id: 'SVTEST2', name: 'Test Student 2', dob: '02/02/2003', major: 'KTPM', class: 'D21KTPM1'}];
             allGroups = [{id: 'GROUP1', name: 'Nhóm Alpha', courseId: 'COURSE1', members: ['SVTEST1'], maxSize: 5, semester: 'HK1-2025'}, {id: 'GROUP2', name: 'Nhóm Beta', courseId: 'COURSE1', members: ['SVTEST2'], maxSize: 5, semester: 'HK1-2025'}];
            // ----- Kết thúc dữ liệu giả lập -----


            console.log("Đã tải xong dữ liệu (hoặc dùng dữ liệu giả lập).");
            console.log("Học kỳ:", allSemesters);
            console.log("Lớp tín chỉ:", allCourses);
            console.log("Sinh viên:", allStudents);


            // Sau khi có dữ liệu
            populateSemesters();
            loadingRow.style.display = 'none'; // Ẩn loading row

            // Render danh sách ban đầu (có thể là rỗng nếu chưa chọn filter)
            renderStudentList(); // Gọi render lần đầu

         } catch (error) {
              console.error("Lỗi tải dữ liệu ban đầu:", error);
              loadingRow.style.display = 'none';
              studentTableBody.innerHTML = '<tr class="loading-row"><td colspan="6">Lỗi tải dữ liệu. Vui lòng thử lại.</td></tr>';
         }

         // Gắn Event Listeners sau khi cấu trúc DOM sẵn sàng và dữ liệu (có thể) đã tải
        semesterSelect.addEventListener('change', updateCourseOptions);
        courseSelect.addEventListener('change', renderStudentList); // Gọi lại render khi chọn lớp
        searchInput.addEventListener('input', renderStudentList);
    }

    initializePage(); // Bắt đầu quá trình tải dữ liệu và khởi tạo trang

});