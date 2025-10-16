// Logic xử lý Modal và gửi request POST để xóa Staff
document.addEventListener('DOMContentLoaded', function () {
    // Lấy các phần tử cần thiết từ DOM
    const deleteModal = document.getElementById('deleteStaffModal');
    const confirmDeleteBtn = document.getElementById('confirmDeleteStaffBtn');
    const deleteStaffIdInput = document.getElementById('deleteStaffId');
    const deleteStaffIdDisplay = document.getElementById('deleteStaffIdDisplay');
    const deleteStaffNameDisplay = document.getElementById('deleteStaffName');

    // 1. Xử lý khi modal được hiển thị (Hiển thị thông tin Staff cần xóa)
    // Sự kiện 'show.bs.modal' của Bootstrap 5
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function (event) {
            // Lấy nút đã kích hoạt modal (nút Delete trong bảng)
            const button = event.relatedTarget;

            // Lấy thông tin Staff ID và Name từ data-* attributes của nút
            const staffId = button.getAttribute('data-id');
            const staffName = button.getAttribute('data-name');

            // Cập nhật nội dung modal
            deleteStaffIdInput.value = staffId; // Gán vào input hidden để JS dùng
            deleteStaffIdDisplay.textContent = staffId; // Hiển thị ID
            deleteStaffNameDisplay.textContent = staffName; // Hiển thị Tên
        });
    }

    // 2. Xử lý khi bấm nút "Delete" xác nhận (Gửi request xóa POST)
    if (confirmDeleteBtn) {
        confirmDeleteBtn.addEventListener('click', function () {
            const staffIdToDelete = deleteStaffIdInput.value;

            if (staffIdToDelete) {
                // Tạo một form ảo trong bộ nhớ để gửi request POST,
                // vì các trình duyệt hiện đại không cho phép gửi request DELETE trực tiếp dễ dàng.
                const form = document.createElement('form');
                form.method = 'POST';
                // Đích đến: mapping trong StaffController là @PostMapping("/delete/{id}")
                form.action = `/staffs/delete/${staffIdToDelete}`;

                // Gửi form
                document.body.appendChild(form);
                form.submit();
            }
        });
    }
});
