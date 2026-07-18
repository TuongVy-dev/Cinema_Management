document.addEventListener('DOMContentLoaded', () => {
    // Kích hoạt nav-link hiện tại
    document.querySelectorAll('.navbar-nav .nav-link').forEach(a => a.classList.remove('active'));
    const link = document.querySelector('.navbar-nav .nav-link[href="schedule_management.html"]');
    if (link) link.classList.add('active');

    // Khởi tạo date/time picker (cần jQuery và Tempus Dominus)
    $(function () {
        $('#showDatePicker').datetimepicker({ format: 'YYYY-MM-DD' });
        $('#timePicker').datetimepicker({ format: 'hh:mm A' });
    });

    // Xử lý sự kiện khi nhấn nút "Add" trong modal
    document.getElementById('btnSaveShow').addEventListener('click', () => {
        const time = document.getElementById('showTime').value || '08:00 AM';
        const movie = document.getElementById('movieSelect').value || 'N/A';
        const tbody = document.getElementById('scheduleBody');

        // Tạo hàng mới cho lịch chiếu
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${time}</td>
            <td>${movie}</td>
            <td class="text-center"><span class="badge badge-online">ONLINE</span></td>
            <td class="text-end">
                <button class="btn btn-outline-light btn-sm me-2"><i class="bi bi-pencil-square me-1"></i>Edit</button>
                <button class="btn btn-danger btn-sm"><i class="bi bi-x-circle me-1"></i>Delete</button>
            </td>`;

        // Thêm hàng mới vào trước hàng cuối cùng (hàng chứa nút Add/Toggle/Delete)
        const lastRow = tbody.querySelector('tr:last-child');
        tbody.insertBefore(tr, lastRow);

        // Đóng modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('showModal'));
        modal.hide();
    });

    // Thêm logic cho nút Apply, Toggle all statuses, Delete all nếu cần
    // Ví dụ cơ bản:
    document.getElementById('btnApply').addEventListener('click', () => {
        console.log('Applying filter...');
        // Thêm logic xử lý API/lọc dữ liệu tại đây
    });

    document.getElementById('btnToggleAll').addEventListener('click', () => {
        alert('Toggle all statuses logic will be implemented here.');
    });

    document.getElementById('btnDeleteAll').addEventListener('click', () => {
        const confirmation = confirm('Are you sure you want to delete all showtimes for this room and date?');
        if (confirmation) {
            alert('All showtimes deleted.');
            // Thêm logic xử lý API/Xóa dữ liệu tại đây
        }
    });
});