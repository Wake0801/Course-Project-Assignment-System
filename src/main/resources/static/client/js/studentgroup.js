 // ===== KHAI BÁO BIẾN LƯU DỮ LIỆU (SẼ LẤY TỪ API) =====
 let allStudents = [];   // Cần để biết thông tin sinh viên khi thêm/xóa
 let allSemesters = [];
 let allCourses = [];
 let courseGroups = []; // Chỉ lưu nhóm của lớp tín chỉ đang chọn
 let courseDetails = {}; // Lưu thông tin maxSize, etc. của lớp đang chọn
 let courseStudents = []; // Sinh viên thuộc lớp tín chỉ đang chọn (nếu API hỗ trợ)
 // ===== KẾT THÚC KHAI BÁO BIẾN =====

 document.addEventListener('DOMContentLoaded', () => {
     // --- DOM Elements ---
     const semesterSelect = document.getElementById('semester-select');
     const courseSelect = document.getElementById('course-select');
     const saveAllButton = document.getElementById('save-all-button');
     const addGroupSection = document.getElementById('add-group-section');
     const addGroupForm = document.getElementById('add-group-form');
     const newGroupNameInput = document.getElementById('new-group-name');
     const newGroupMaxSizeInput = document.getElementById('new-group-max-size');
     const groupListSection = document.getElementById('group-list-section');
     const groupListTitle = document.getElementById('group-list-title');
     const groupListContainer = document.getElementById('group-list-container');
     const groupListLoading = document.getElementById('group-list-loading');
     const groupEmptyState = document.getElementById('group-empty-state');
     const groupDetailsSection = document.getElementById('group-details-section');
     const selectedGroupTitle = document.getElementById('selected-group-title');
     const unassignedStudentsList = document.getElementById('unassigned-students-list');
     const unassignedLoading = document.getElementById('unassigned-loading');
     const unassignedEmptyState = document.getElementById('unassigned-empty-state');
     const groupMembersList = document.getElementById('group-members-list');
     const membersLoading = document.getElementById('members-loading');
     const membersEmptyState = document.getElementById('members-empty-state');
     const memberCountSpan = document.getElementById('member-count');
     const maxSizeSpan = document.getElementById('max-size');
     const groupFullWarning = document.getElementById('group-full-warning');
     const otherGroupsList = document.getElementById('other-groups-list');
     const otherGroupsLoading = document.getElementById('other-groups-loading');
     const otherGroupsEmptyState = document.getElementById('other-groups-empty-state');
     const alertContainer = document.getElementById('alert-container');
     const topicAssignmentSection = document.getElementById('topic-assignment-section');
     const assignTopicButton = document.getElementById('assign-topic-button');


     // --- State Variables ---
     let currentSelectedGroupId = null;
     // let unsavedChanges = false; // Tạm thời chưa dùng cho đến khi có logic API save

     // --- Helper Functions ---
     function showAlert(message, type = 'success', duration = 3000) {
         const alertDiv = document.createElement('div');
         alertDiv.className = `alert alert-${type}`;
         alertDiv.textContent = message;

         // Xóa alert cũ nếu có
         const existingAlert = alertContainer.querySelector('.alert');
         if (existingAlert) {
             existingAlert.remove();
         }

         alertContainer.appendChild(alertDiv);

         // Tự động ẩn sau khoảng thời gian
         setTimeout(() => {
              // Thêm hiệu ứng mờ dần trước khi xóa (tùy chọn)
              alertDiv.style.transition = 'opacity 0.5s ease-out';
              alertDiv.style.opacity = '0';
              setTimeout(() => alertDiv.remove(), 500); // Xóa khỏi DOM sau khi mờ
         }, duration);
     }

     function getStudentById(id) {
         // Tìm trong danh sách sinh viên của lớp hiện tại trước (nếu có)
         let student = courseStudents.find(s => s.id === id);
         // Nếu không tìm thấy, thử tìm trong danh sách tất cả sinh viên (dự phòng)
         if (!student) {
             student = allStudents.find(s => s.id === id);
         }
         return student;
     }
     function getCourseById(id) { return allCourses.find(c => c.id === id); }
     function getSemesterById(id) { return allSemesters.find(sem => sem.id === id); }
     function updateSaveButtonState() {
          // TODO: Logic kiểm tra thay đổi chưa lưu dựa trên state và dữ liệu gốc từ API
          saveAllButton.disabled = true; // Tạm thời luôn disable
     }

      function setLoadingState(element, isLoading) {
          if (element) {
              element.classList.toggle('loading', isLoading);
              // Có thể thêm/xóa attribute disabled cho các nút bên trong nếu cần
              const buttons = element.querySelectorAll('button');
              buttons.forEach(btn => btn.disabled = isLoading);
          }
      }


     // --- Render Functions ---
     function renderSemesters() {
          semesterSelect.innerHTML = '<option value="">Chọn học kỳ</option>';
          if (!allSemesters || allSemesters.length === 0) return;
          allSemesters.forEach(semester => {
             const option = document.createElement('option');
             option.value = semester.id;
             option.textContent = semester.name;
             semesterSelect.appendChild(option);
          });
     }

     function renderCourses() {
          const selectedSemesterId = semesterSelect.value;
          courseSelect.innerHTML = '<option value="">Chọn lớp tín chỉ</option>';
          currentSelectedGroupId = null;
          groupListSection.style.display = 'none';
          groupDetailsSection.style.display = 'none';
          addGroupSection.style.display = 'none';
          topicAssignmentSection.style.display = 'none';
          courseGroups = [];
          courseStudents = []; // Reset sinh viên của lớp
          courseDetails = {};

          if (selectedSemesterId) {
              // TODO: Gọi API lấy Courses cho semester này nếu cần, hoặc lọc từ allCourses
              console.log(`Lọc lớp tín chỉ cho học kỳ: ${selectedSemesterId}`);
              const filteredCourses = allCourses.filter(c => c.semester === selectedSemesterId);
              console.log(`Tìm thấy ${filteredCourses.length} lớp tín chỉ.`);

              if (filteredCourses.length > 0) {
                  filteredCourses.forEach(course => {
                      const option = document.createElement('option');
                      option.value = course.id;
                      option.textContent = course.name;
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
          renderGroups(); // Trigger ẩn/hiện section
          updateSaveButtonState();
     }

     async function renderGroups() {
          const selectedCourseId = courseSelect.value;
          groupListContainer.innerHTML = ''; // Xóa thẻ cũ
          currentSelectedGroupId = null;
          groupDetailsSection.style.display = 'none';
          groupEmptyState.style.display = 'none';
          groupListLoading.style.display = 'none';

          if (!selectedCourseId) {
              groupListSection.style.display = 'none';
              addGroupSection.style.display = 'none';
              topicAssignmentSection.style.display = 'none';
              return;
          }

          groupListSection.style.display = 'block';
          addGroupSection.style.display = 'block'; // Luôn hiển thị form thêm khi đã chọn lớp
          groupListLoading.style.display = 'block'; // Hiển thị loading

          try {
               console.log(`Đang tải nhóm cho lớp: ${selectedCourseId}`);
               // TODO: Gọi API lấy danh sách nhóm
               // const groupsResponse = await fetch(`/api/groups?courseId=${selectedCourseId}`);
               // if (!groupsResponse.ok) throw new Error('Lỗi tải nhóm');
               // courseGroups = await groupsResponse.json();

               // TODO: Gọi API lấy thông tin lớp (maxSize, name)
               // const courseResponse = await fetch(`/api/courses/${selectedCourseId}`);
               // if (!courseResponse.ok) throw new Error('Lỗi tải thông tin lớp');
               // courseDetails = await courseResponse.json();

               // ----- Dữ liệu giả lập (XÓA SAU) -----
                await new Promise(resolve => setTimeout(resolve, 300)); // Giả lập chờ
                courseGroups = allGroups.filter(g => g.courseId === selectedCourseId); // Lọc từ allGroups giả lập
                courseDetails = allCourses.find(c => c.id === selectedCourseId) || {};
                courseDetails.maxSize = courseDetails.maxGroupSize || 4; // Đảm bảo có maxSize
               // ----- Kết thúc dữ liệu giả lập -----

               console.log(`Đã tải ${courseGroups.length} nhóm. Chi tiết lớp:`, courseDetails);

               newGroupMaxSizeInput.value = courseDetails.maxSize || 4;
               groupListTitle.textContent = `Danh sách nhóm - ${courseDetails.name || 'N/A'}`;


               groupListLoading.style.display = 'none'; // Ẩn loading

               if (courseGroups.length === 0) {
                   groupEmptyState.style.display = 'block';
                   topicAssignmentSection.style.display = 'none'; // Ẩn nút phân công nếu không có nhóm
               } else {
                    groupEmptyState.style.display = 'none';
                    topicAssignmentSection.style.display = 'block'; // Hiển thị nút phân công
               }

               courseGroups.forEach(group => {
                   const groupCard = document.createElement('div');
                   groupCard.className = 'group-card';
                   groupCard.dataset.groupId = group.id;

                   const topicDisplay = group.topic ? `<div class="group-topic">Đề tài: ${group.topic}</div>` : '';
                    // Giả sử API trả về saved flag, nếu không thì mặc định là true hoặc false tùy logic
                    const isSaved = group.saved === undefined ? true : group.saved;
                    const savedStatus = isSaved ? 'Đã lưu' : 'Chưa lưu';
                    const savedClass = isSaved ? 'status-saved' : 'status-unsaved';

                   groupCard.innerHTML = `
                       <div class="group-title">
                           <span>${group.name || 'N/A'}</span>
                           <button class="btn btn-danger btn-sm" data-action="delete" title="Xóa nhóm">&times;</button>
                       </div>
                       <div class="group-meta">
                           <span class="status-indicator ${savedClass}"></span>
                           ${savedStatus} |
                           ${group.members?.length || 0}/${group.maxSize || 'N/A'} thành viên
                       </div>
                       ${topicDisplay}
                   `;

                   groupCard.addEventListener('click', (e) => {
                       e.stopPropagation(); // Ngăn chặn sự kiện nổi bọt
                       const action = e.target.dataset.action;
                       if (action === 'delete') {
                           handleRemoveGroup(group.id);
                       } else {
                            // Chỉ chọn nếu không click vào nút xóa
                           if (!e.target.closest('button')) {
                                handleSelectGroup(group.id);
                           }
                       }
                   });
                   groupListContainer.appendChild(groupCard);
               });
          } catch (error) {
               console.error("Lỗi tải danh sách nhóm:", error);
               groupListLoading.style.display = 'none';
               showAlert(`Lỗi tải danh sách nhóm: ${error.message}`, 'danger');
          } finally {
                updateSaveButtonState();
          }
     }

     async function renderGroupDetails() {
          if (!currentSelectedGroupId) {
              groupDetailsSection.style.display = 'none';
              return;
          }

          const group = courseGroups.find(g => g.id === currentSelectedGroupId);
          if (!group) {
               console.warn(`Không tìm thấy nhóm với ID: ${currentSelectedGroupId} trong courseGroups`);
               groupDetailsSection.style.display = 'none';
               return;
          }

          groupDetailsSection.style.display = 'block';
          selectedGroupTitle.textContent = `Chi tiết nhóm: ${group.name || 'N/A'}`;

          // --- Hiển thị Loading ---
          unassignedStudentsList.innerHTML = ''; unassignedLoading.style.display = 'block'; unassignedEmptyState.style.display = 'none';
          groupMembersList.innerHTML = ''; membersLoading.style.display = 'block'; membersEmptyState.style.display = 'none';
          otherGroupsList.innerHTML = ''; otherGroupsLoading.style.display = 'block'; otherGroupsEmptyState.style.display = 'none';

          try {
               console.log(`Tải chi tiết cho nhóm: ${group.id}, Lớp: ${group.courseId}`);
               // TODO: Gọi API lấy danh sách sinh viên của lớp (nếu chưa có) và sinh viên chưa có nhóm
               // Ví dụ:
               // const studentsResponse = await fetch(`/api/students?courseId=${group.courseId}`);
               // if (!studentsResponse.ok) throw new Error('Lỗi tải sinh viên');
               // courseStudents = await studentsResponse.json(); // Lưu lại sinh viên của lớp này

               // Xác định sinh viên chưa có nhóm DỰA TRÊN courseGroups và courseStudents
               const memberIdsInCourse = courseGroups.flatMap(g => g.members || []);
               const unassignedStudents = courseStudents.filter(s => !memberIdsInCourse.includes(s.id));

               // ----- Dữ liệu giả lập (XÓA SAU) -----
                await new Promise(resolve => setTimeout(resolve, 300)); // Giả lập chờ
                courseStudents = allStudents; // Giả lập lấy hết SV làm SV của lớp
                const memberIdsInCourseSim = courseGroups.flatMap(g => g.members || []);
                const unassignedStudentsSim = courseStudents.filter(s => !memberIdsInCourseSim.includes(s.id));
               // ----- Kết thúc dữ liệu giả lập -----


               unassignedLoading.style.display = 'none'; // Ẩn loading
               if (unassignedStudentsSim.length === 0) {
                   unassignedEmptyState.style.display = 'block';
               } else {
                    unassignedEmptyState.style.display = 'none';
                   unassignedStudentsSim.forEach(student => {
                       // Kiểm tra xem sinh viên này có thực sự tồn tại không (phòng trường hợp dữ liệu API lỗi)
                       if (student && student.id && student.name) {
                           const studentItem = createStudentItem(student, 'add', group.id);
                           unassignedStudentsList.appendChild(studentItem);
                       } else {
                            console.warn("Dữ liệu sinh viên không hợp lệ:", student);
                       }
                   });
               }

               // Render thành viên nhóm
               membersLoading.style.display = 'none';
               const currentMembers = group.members || []; // Đảm bảo members là mảng
               memberCountSpan.textContent = currentMembers.length;
               maxSizeSpan.textContent = group.maxSize || 'N/A';
               groupFullWarning.style.display = currentMembers.length >= (group.maxSize || Infinity) ? 'block' : 'none';

               if (currentMembers.length === 0) {
                   membersEmptyState.style.display = 'block';
               } else {
                    membersEmptyState.style.display = 'none';
                   currentMembers.forEach(studentId => {
                       const student = getStudentById(studentId); // Lấy thông tin SV từ courseStudents hoặc allStudents
                       if (student && student.id && student.name) {
                           const studentItem = createStudentItem(student, 'remove', group.id);
                           groupMembersList.appendChild(studentItem);
                       } else {
                           console.warn(`Không tìm thấy thông tin cho thành viên ID: ${studentId}`);
                            // Có thể hiển thị ID nếu không có tên
                            const missingItem = document.createElement('div');
                            missingItem.className = 'student-item';
                            missingItem.innerHTML = `<div class="student-info"><span style="color: red;">Không tìm thấy SV ID: ${studentId}</span></div>`;
                            groupMembersList.appendChild(missingItem);
                       }
                   });
               }

               // Render nhóm khác
               otherGroupsLoading.style.display = 'none';
               const otherGroups = courseGroups.filter(g => g.id !== currentSelectedGroupId);
               if (otherGroups.length === 0) {
                   otherGroupsEmptyState.style.display = 'block';
               } else {
                    otherGroupsEmptyState.style.display = 'none';
                   otherGroups.forEach(otherGroup => {
                        const otherGroupItem = document.createElement('div');
                        otherGroupItem.className = 'student-item'; // Re-use styling
                        otherGroupItem.style.cursor = 'pointer';
                        otherGroupItem.innerHTML = `
                            <div class="student-info">
                               <div class="student-name">${otherGroup.name || 'N/A'}</div>
                               <div class="student-id">${otherGroup.members?.length || 0}/${otherGroup.maxSize || 'N/A'} thành viên</div>
                           </div>
                        `;
                        otherGroupItem.addEventListener('click', () => handleSelectGroup(otherGroup.id));
                        otherGroupsList.appendChild(otherGroupItem);
                    });
               }

          } catch (error) {
              console.error("Lỗi tải chi tiết nhóm:", error);
               unassignedLoading.style.display = 'none';
               membersLoading.style.display = 'none';
               otherGroupsLoading.style.display = 'none';
               showAlert(`Lỗi tải chi tiết nhóm: ${error.message}`, 'danger');
                // Hiển thị lỗi ở các cột
               unassignedStudentsList.innerHTML = `<p style="color:red;">Lỗi tải</p>`;
               groupMembersList.innerHTML = `<p style="color:red;">Lỗi tải</p>`;
               otherGroupsList.innerHTML = `<p style="color:red;">Lỗi tải</p>`;
          }
     }

     function createStudentItem(student, actionType, groupId) {
          const item = document.createElement('div');
          item.className = 'student-item';
          item.dataset.studentId = student.id;

          const actionButton = document.createElement('button');
           // Xác định lớp và nội dung nút dựa trên actionType
          let btnClass, btnText, btnTitle;
          if (actionType === 'add') {
              btnClass = 'btn-primary';
              btnText = '+'; // Dùng icon hoặc text ngắn gọn
              btnTitle = 'Thêm vào nhóm';
          } else { // 'remove'
              btnClass = 'btn-danger';
              btnText = '&times;'; // Dấu X
              btnTitle = 'Xóa khỏi nhóm';
          }
          actionButton.className = `btn ${btnClass} btn-sm`;
          actionButton.innerHTML = btnText;
          actionButton.title = btnTitle; // Thêm tooltip
          actionButton.dataset.action = actionType;
          actionButton.dataset.studentId = student.id;
          actionButton.dataset.groupId = groupId;


          // Disable 'Add' button nếu nhóm đã đầy
          if (actionType === 'add') {
              const group = courseGroups.find(g => g.id === groupId);
              if (group && (group.members?.length || 0) >= (group.maxSize || Infinity)) {
                  actionButton.disabled = true;
                  actionButton.title = 'Nhóm đã đầy'; // Cập nhật tooltip
              }
          }

          item.innerHTML = `
               <div class="student-avatar">${student.name ? student.name.charAt(0).toUpperCase() : '?'}</div>
               <div class="student-info">
                   <div class="student-name">${student.name || 'N/A'}</div>
                   <div class="student-id">${student.id || 'N/A'}</div>
               </div>
          `;
          item.appendChild(actionButton); // Thêm nút vào cuối

          // Gắn sự kiện cho nút (dùng event delegation tốt hơn nếu list lớn)
          actionButton.addEventListener('click', (e) => {
               e.stopPropagation(); // Ngăn click vào cả student-item
              const studentId = e.target.dataset.studentId;
              const targetGroupId = e.target.dataset.groupId;
              const action = e.target.dataset.action;

              if (action === 'add') {
                  handleAddStudentToGroup(targetGroupId, studentId, e.target); // Truyền nút vào để xử lý loading
              } else {
                  handleRemoveStudentFromGroup(targetGroupId, studentId, e.target); // Truyền nút vào
              }
          });

          return item;
      }


     // --- Event Handlers (Gọi API) ---
     function handleSelectGroup(groupId) {
          if (currentSelectedGroupId === groupId) return; // Không chọn lại nhóm đang chọn

          console.log(`Chọn nhóm ID: ${groupId}`);
          currentSelectedGroupId = groupId;
          // Highlight active card
          document.querySelectorAll('.group-card').forEach(card => {
              card.classList.toggle('active', card.dataset.groupId === groupId);
          });
          renderGroupDetails(); // Trigger load và render chi tiết
      }

     async function handleAddGroup(event) {
          event.preventDefault();
          const selectedCourseId = courseSelect.value;
          if (!selectedCourseId) {
              showAlert('Vui lòng chọn lớp tín chỉ trước.', 'warning');
              return;
          }
          const nameInput = newGroupNameInput;
          const sizeInput = newGroupMaxSizeInput;
          const name = nameInput.value.trim(); // Lấy tên tùy chọn, để trống nếu muốn backend tự đặt
          const maxSize = parseInt(sizeInput.value);

          if (isNaN(maxSize) || maxSize < 1) {
              showAlert('Số lượng tối đa không hợp lệ.', 'warning');
              sizeInput.focus();
              return;
          }

          const button = event.target.querySelector('button[type="submit"]');
          const originalButtonText = button.textContent;
          button.disabled = true;
          button.textContent = 'Đang thêm...';

          try {
               console.log(`Gửi yêu cầu thêm nhóm: courseId=${selectedCourseId}, name=${name || '(default)'}, maxSize=${maxSize}`);
               // TODO: Gọi API POST /api/groups
               // const response = await fetch('/api/groups', {
               //     method: 'POST',
               //     headers: { 'Content-Type': 'application/json' },
               //     body: JSON.stringify({ courseId: selectedCourseId, name: name || null, maxSize: maxSize }) // Gửi null nếu tên trống
               // });
               // if (!response.ok) {
               //     const errData = await response.json().catch(() => ({ message: 'Thêm nhóm thất bại' }));
               //     throw new Error(errData.message);
               // }
               // const newGroup = await response.json();

              // ----- Bỏ comment và thay bằng fetch thật -----
               await new Promise(resolve => setTimeout(resolve, 500)); // Giả lập chờ API
                // Giả lập newGroup trả về từ API
               const newGroup = { id: `G${Date.now()}`, name: name || `Nhóm mới ${courseGroups.length + 1}`, courseId: selectedCourseId, members: [], maxSize: maxSize, saved: false, topic: null, semester: courseDetails.semester };
               courseGroups.push(newGroup); // Thêm vào mảng cục bộ để render lại
              // ----- Kết thúc giả lập -----


               showAlert('Thêm nhóm thành công!');
               nameInput.value = ''; // Reset form
               // Không reset maxSize về mặc định, giữ nguyên giá trị người dùng nhập gần nhất

               // Render lại danh sách nhóm (không cần await nếu chỉ cập nhật từ mảng cục bộ)
               renderGroups(); // Gọi lại để hiển thị nhóm mới

          } catch (error) {
               console.error("Lỗi thêm nhóm:", error);
               showAlert(`Thêm nhóm thất bại: ${error.message}`, 'danger');
          } finally {
               button.disabled = false;
               button.textContent = originalButtonText;
          }
     }

     async function handleRemoveGroup(groupId) {
          const group = courseGroups.find(g => g.id === groupId);
          if (!group) return;

          if (!confirm(`Bạn có chắc chắn muốn xóa nhóm "${group.name || groupId}" không?`)) {
              return;
          }

          const groupCard = document.querySelector(`.group-card[data-group-id="${groupId}"]`);
          setLoadingState(groupCard, true); // Đặt trạng thái loading cho thẻ

          try {
              console.log(`Gửi yêu cầu xóa nhóm ID: ${groupId}`);
              // TODO: Gọi API DELETE /api/groups/:groupId
              // const response = await fetch(`/api/groups/${groupId}`, { method: 'DELETE' });
              // if (!response.ok) {
              //     const errData = await response.json().catch(() => ({ message: 'Xóa nhóm thất bại' }));
              //     throw new Error(errData.message);
              // }

              // ----- Bỏ comment và thay bằng fetch thật -----
               await new Promise(resolve => setTimeout(resolve, 500)); // Giả lập chờ API
              // ----- Kết thúc giả lập -----

               showAlert('Xóa nhóm thành công!');

               // Xóa khỏi mảng cục bộ
               courseGroups = courseGroups.filter(g => g.id !== groupId);

               // Nếu nhóm đang chọn bị xóa, ẩn chi tiết
               if (currentSelectedGroupId === groupId) {
                   currentSelectedGroupId = null;
                   groupDetailsSection.style.display = 'none';
               }

              // Render lại danh sách nhóm
               renderGroups(); // Không cần await nếu chỉ render từ mảng cục bộ

          } catch (error) {
               console.error("Lỗi xóa nhóm:", error);
               showAlert(`Xóa nhóm thất bại: ${error.message}`, 'danger');
               setLoadingState(groupCard, false); // Bỏ loading nếu lỗi
          }
     }

     async function handleAddStudentToGroup(groupId, studentId, buttonElement) {
          if (!buttonElement) return; // Cần nút để hiển thị loading
          setLoadingState(buttonElement.closest('.student-item'), true); // Loading cả item

          try {
              console.log(`Gửi yêu cầu thêm SV ${studentId} vào nhóm ${groupId}`);
             // TODO: Gọi API POST /api/groups/:groupId/members
             // const response = await fetch(`/api/groups/${groupId}/members`, {
             //     method: 'POST',
             //     headers: { 'Content-Type': 'application/json' },
             //     body: JSON.stringify({ studentId: studentId })
             // });
             // if (!response.ok) {
             //     const errorData = await response.json().catch(() => ({ message: 'Thêm sinh viên thất bại' }));
             //     throw new Error(errorData.message || 'Thêm sinh viên thất bại');
             // }
             // const updatedGroupData = await response.json(); // API có thể trả về nhóm đã cập nhật

             // ----- Bỏ comment và thay bằng fetch thật -----
              await new Promise(resolve => setTimeout(resolve, 500)); // Giả lập chờ API
              // Giả lập cập nhật thành công trong mảng cục bộ
              const groupIndex = courseGroups.findIndex(g => g.id === groupId);
              if (groupIndex > -1 && !courseGroups[groupIndex].members.includes(studentId)) {
                  // Xóa SV khỏi nhóm khác (nếu có) trong CÙNG courseGroups
                  courseGroups.forEach((g, idx) => {
                     if (idx !== groupIndex && g.members && g.members.includes(studentId)) {
                         g.members = g.members.filter(id => id !== studentId);
                         g.saved = false; // Đánh dấu nhóm cũ là chưa lưu
                     }
                  });
                  // Thêm vào nhóm mới
                  courseGroups[groupIndex].members.push(studentId);
                  courseGroups[groupIndex].saved = false; // Đánh dấu nhóm mới là chưa lưu
              }
              // ----- Kết thúc giả lập -----

              showAlert('Thêm sinh viên vào nhóm thành công!');

              // Tải lại chi tiết nhóm để cập nhật cả 2 cột (thành viên và chưa có nhóm)
              // Và tải lại danh sách nhóm để cập nhật trạng thái saved
              await Promise.all([renderGroupDetails(), renderGroups()]);


          } catch (error) {
              console.error("Lỗi thêm sinh viên:", error);
              showAlert(`Thêm sinh viên thất bại: ${error.message}`, 'danger');
              setLoadingState(buttonElement.closest('.student-item'), false); // Bỏ loading nếu lỗi
          }
          // Không cần bật lại nút vì item sẽ được render lại
     }

     async function handleRemoveStudentFromGroup(groupId, studentId, buttonElement) {
           if (!buttonElement) return;
           setLoadingState(buttonElement.closest('.student-item'), true); // Loading cả item

           try {
                console.log(`Gửi yêu cầu xóa SV ${studentId} khỏi nhóm ${groupId}`);
               // TODO: Gọi API DELETE /api/groups/:groupId/members/:studentId
               // const response = await fetch(`/api/groups/${groupId}/members/${studentId}`, { method: 'DELETE' });
               // if (!response.ok) {
               //     const errorData = await response.json().catch(() => ({ message: 'Xóa sinh viên thất bại' }));
               //     throw new Error(errorData.message || 'Xóa sinh viên thất bại');
               // }
               // const updatedGroupData = await response.json(); // API có thể trả về nhóm đã cập nhật


               // ----- Bỏ comment và thay bằng fetch thật -----
                await new Promise(resolve => setTimeout(resolve, 500)); // Giả lập chờ API
                // Giả lập cập nhật thành công trong mảng cục bộ
                const groupIndex = courseGroups.findIndex(g => g.id === groupId);
                if (groupIndex > -1) {
                     courseGroups[groupIndex].members = (courseGroups[groupIndex].members || []).filter(id => id !== studentId);
                     courseGroups[groupIndex].saved = false; // Đánh dấu chưa lưu
                }
               // ----- Kết thúc giả lập -----


                showAlert('Xóa sinh viên khỏi nhóm thành công!');

                // Tải lại chi tiết nhóm và danh sách nhóm
                await Promise.all([renderGroupDetails(), renderGroups()]);


           } catch (error) {
               console.error("Lỗi xóa sinh viên:", error);
               showAlert(`Xóa sinh viên thất bại: ${error.message}`, 'danger');
                // Không cần bỏ loading vì item sẽ được render lại hoặc biến mất
           }
      }

     async function handleSaveAll() {
          // TODO: Logic gọi API để lưu trạng thái của tất cả nhóm trong courseGroups
          showAlert('Chức năng Lưu cần được kết nối với API backend.', 'warning');
     }

     async function handleAssignTopics() {
          const selectedCourseId = courseSelect.value;
          if (!selectedCourseId || courseGroups.length === 0) {
               showAlert('Vui lòng chọn lớp tín chỉ có nhóm.', 'warning');
               return;
          }

          if (!confirm(`Phân công đề tài ngẫu nhiên cho ${courseGroups.length} nhóm trong lớp này? Thao tác này có thể đánh dấu các nhóm là chưa lưu (tùy vào cài đặt backend).`)) {
              return;
          }

          const button = document.getElementById('assign-topic-button');
          const originalText = button.textContent;
          button.disabled = true;
          button.textContent = 'Đang phân công...';

          try {
             console.log(`Gửi yêu cầu phân công đề tài cho lớp: ${selectedCourseId}`);
             // TODO: Gọi API POST /api/courses/:courseId/assign-topics (hoặc tương tự)
             // const response = await fetch(`/api/courses/${selectedCourseId}/assign-topics`, { method: 'POST' });
             // if (!response.ok) {
             //      const errorData = await response.json().catch(() => ({ message: 'Phân công đề tài thất bại' }));
             //      throw new Error(errorData.message || 'Phân công đề tài thất bại');
             // }
             // const updatedGroups = await response.json(); // API nên trả về danh sách nhóm đã cập nhật

             // ----- Bỏ comment và thay bằng fetch thật -----
              await new Promise(resolve => setTimeout(resolve, 500)); // Giả lập chờ API
              // Giả lập cập nhật topic và saved status
              let topicCounter = 1;
              courseGroups = courseGroups.map(g => ({
                  ...g,
                  topic: `Đề tài ngẫu nhiên ${topicCounter++}`,
                  saved: false // Giả sử việc gán đề tài làm trạng thái chưa lưu
              }));
             // ----- Kết thúc giả lập -----

             showAlert('Phân công đề tài thành công!');

             // Render lại danh sách nhóm và chi tiết nếu có nhóm đang chọn
             await renderGroups();
             if(currentSelectedGroupId) {
                  // Cần đảm bảo nhóm đang chọn được cập nhật trong courseGroups trước khi render lại chi tiết
                  await renderGroupDetails();
             }

          } catch (error) {
              console.error("Lỗi phân công đề tài:", error);
              showAlert(`Phân công đề tài thất bại: ${error.message}`, 'danger');
          } finally {
             button.disabled = false;
             button.textContent = originalText;
          }
     }

     // --- Initialization ---
     async function initializePage() {
         try {
             console.log("Khởi tạo trang Quản lý nhóm...");
             // TODO: Gọi API lấy allSemesters ban đầu
             // TODO: Có thể gọi API lấy allCourses và allStudents nếu cần thiết ở đây hoặc lấy sau
             // const semestersRes = await fetch('/api/semesters');
             // if (!semestersRes.ok) throw new Error("Lỗi tải học kỳ");
             // allSemesters = await semestersRes.json();

             // ----- Dữ liệu giả lập (XÓA SAU) -----
             allSemesters = [{id: 'HK1-2025', name: 'Học kỳ 1 - 2025'}, {id: 'HK2-2025', name: 'Học kỳ 2 - 2025'}];
             allCourses = [{id: 'COURSE1', name: 'Lập trình Web', semester: 'HK1-2025', maxGroupSize: 5}, {id: 'COURSE2', name: 'Cơ sở dữ liệu', semester: 'HK1-2025', maxGroupSize: 4}, {id: 'COURSE3', name: 'Mạng máy tính', semester: 'HK2-2025', maxGroupSize: 3}];
             allStudents = [{id: 'SVTEST1', name: 'Test Student 1', dob: '01/01/2003', major: 'CNTT', class: 'D21CN1'}, {id: 'SVTEST2', name: 'Test Student 2', dob: '02/02/2003', major: 'KTPM', class: 'D21KTPM1'}, {id: 'SVTEST3', name: 'Test Student 3', dob: '03/03/2003', major: 'CNTT', class: 'D21CN1'}];
             // Khởi tạo allGroups rỗng, sẽ được load khi chọn lớp
             allGroups = [ // Tạm thời giữ lại để giả lập load nhóm
                  {id: 'GROUP1', name: 'Nhóm Alpha', courseId: 'COURSE1', members: ['SVTEST1'], maxSize: 5, semester: 'HK1-2025', saved: true, topic: 'Xây dựng Web TMĐT'},
                  {id: 'GROUP2', name: 'Nhóm Beta', courseId: 'COURSE1', members: ['SVTEST2'], maxSize: 5, semester: 'HK1-2025', saved: false, topic: null},
                  {id: 'GROUP3', name: 'Nhóm Gamma', courseId: 'COURSE2', members: ['SVTEST1', 'SVTEST3'], maxSize: 4, semester: 'HK1-2025', saved: true, topic: 'Phân tích CSDL'},
             ];
             // ----- Kết thúc dữ liệu giả lập -----

             console.log("Đã tải xong dữ liệu khởi tạo (hoặc dùng dữ liệu giả lập).");
             renderSemesters(); // Hiển thị học kỳ

         } catch (error) {
             console.error("Lỗi khởi tạo trang Quản lý nhóm:", error);
             showAlert(`Lỗi tải dữ liệu ban đầu: ${error.message}`, 'danger');
         }

          // Gắn Event Listeners
         semesterSelect.addEventListener('change', renderCourses);
         courseSelect.addEventListener('change', renderGroups); // Sẽ trigger load nhóm khi chọn lớp
         addGroupForm.addEventListener('submit', handleAddGroup);
         saveAllButton.addEventListener('click', handleSaveAll);
         assignTopicButton.addEventListener('click', handleAssignTopics);
     }

     initializePage(); // Bắt đầu quá trình tải và khởi tạo

 });