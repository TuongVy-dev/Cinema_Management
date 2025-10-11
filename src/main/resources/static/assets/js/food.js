// --- Khởi tạo và trạng thái ban đầu ---
document.addEventListener('DOMContentLoaded', () => {
    // Kích hoạt menu hiện tại
    document.querySelectorAll('.navbar-nav .nav-link').forEach(a => a.classList.remove('active'));
    const link = document.querySelector('.navbar-nav .nav-link[href="food_management.html"]');
    if (link) link.classList.add('active');

    // Khởi tạo hiển thị bảng
    render();
});

// --- Demo data store (replace with API later) ---
let foods = [
    { id: 1, name: 'Caramel Popcorn (L)', category: 'Popcorn', price: 55000, status: 'Active', desc: 'Sweet classic' },
    { id: 2, name: 'Combo 1: Popcorn (M) + Coke (M)', category: 'Combo', price: 89000, status: 'Active' },
    { id: 3, name: 'Iced Tea (L)', category: 'Drink', price: 35000, status: 'Inactive' },
    { id: 4, name: 'Nachos Cheese', category: 'Snack', price: 65000, status: 'Active' }
];
let editingId = null;
let deleteId = null;

const tbody = document.getElementById('foodTbody');
const checkAll = document.getElementById('checkAll');
const bulkBtn = document.getElementById('btnBulkDelete');

/**
 * Định dạng số tiền sang VNĐ.
 * @param {number} n - Giá trị số tiền.
 * @returns {string} Chuỗi tiền tệ.
 */
function vnd(n) {
    return n.toLocaleString('vi-VN');
}

/**
 * Tạo thẻ badge (nhãn) trạng thái.
 * @param {string} status - Trạng thái ('Active' hoặc 'Inactive').
 * @returns {string} HTML cho badge.
 */
function badge(status) {
    const cls = status === 'Active' ? 'badge bg-success' : 'badge bg-secondary';
    return `<span class="${cls}">${status}</span>`;
}

/**
 * Tạo template hàng (row) cho bảng.
 * @param {object} item - Dữ liệu món ăn.
 * @returns {string} HTML cho hàng.
 */
function rowTemplate(item) {
    // Đã chỉnh sửa để các nút Edit/Delete nằm sát nhau hơn cho gọn gàng (dùng me-1 thay vì me-2)
    return `
    <tr data-id="${item.id}">
      <td><input type="checkbox" class="row-check form-check-input"></td>
      <td>${item.name}</td>
      <td>${item.category}</td>
      <td class="text-end">${vnd(item.price)}</td>
      <td>${badge(item.status)}</td>
      <td class="text-end">
        <!-- Chú ý: Sử dụng class 'd-inline-flex' và 'align-items-center' để xếp ngang gọn hơn nếu cần -->
        <div class="d-inline-flex align-items-center">
            <button class="btn btn-outline-light btn-sm me-1 btn-edit"><i class="bi bi-pencil-square"></i> Edit</button>
            <button class="btn btn-danger btn-sm btn-del"><i class="bi bi-x-circle"></i> Delete</button>
        </div>
      </td>
    </tr>`;
}

/**
 * Lọc dữ liệu và render bảng.
 */
function render() {
    const q = (document.getElementById('searchInput').value || '').toLowerCase();
    const cat = document.getElementById('filterCategory').value;
    const st = document.getElementById('filterStatus').value;

    const filtered = foods.filter(f =>
        (!q || f.name.toLowerCase().includes(q)) &&
        (!cat || f.category === cat) &&
        (!st || f.status === st)
    );

    tbody.innerHTML = filtered.map(rowTemplate).join('') || `
    <tr><td colspan="6" class="text-center text-muted py-4">No items</td></tr>`;

    updateBulkState();
}

/**
 * Đặt lại form thêm/sửa món ăn.
 */
function resetForm() {
    editingId = null;
    document.getElementById('foodForm').reset();
    document.getElementById('foodId').value = '';
    document.getElementById('foodModalTitle').textContent = 'Add Food Item';
}

/**
 * Điền dữ liệu món ăn vào form.
 * @param {object} item - Dữ liệu món ăn.
 */
function fillForm(item) {
    document.getElementById('foodId').value = item.id;
    document.getElementById('foodName').value = item.name;
    document.getElementById('foodCategory').value = item.category;
    document.getElementById('foodPrice').value = item.price;
    document.getElementById('foodStatus').value = item.status;
    document.getElementById('foodImage').value = item.image || '';
    document.getElementById('foodDesc').value = item.desc || '';
}

// --- Xử lý sự kiện ---

// Mở modal Add
document.getElementById('btnAdd').addEventListener('click', () => {
    resetForm();
    // Đảm bảo bootstrap đã được load để sử dụng Modal
    if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
        new bootstrap.Modal(document.getElementById('foodModal')).show();
    } else {
        console.error("Bootstrap is not loaded. Cannot show modal.");
    }
});

// Lưu (Create/Update)
document.getElementById('btnSaveFood').addEventListener('click', () => {
    const id = document.getElementById('foodId').value;
    const name = document.getElementById('foodName').value.trim();
    const category = document.getElementById('foodCategory').value;
    const price = Number(document.getElementById('foodPrice').value);
    const status = document.getElementById('foodStatus').value;
    const image = document.getElementById('foodImage').value.trim();
    const desc = document.getElementById('foodDesc').value.trim();

    if (!name || !category || Number.isNaN(price)) return;

    if (id) {
        // update
        const idx = foods.findIndex(f => f.id === Number(id));
        if (idx > -1) foods[idx] = { ...foods[idx], name, category, price, status, image, desc };
    } else {
        // create
        const newId = Math.max(0, ...foods.map(f => f.id)) + 1;
        foods.push({ id: newId, name, category, price, status, image, desc });
    }
    render();
    if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
        bootstrap.Modal.getInstance(document.getElementById('foodModal')).hide();
    }
});

// Row actions (edit/delete)
tbody.addEventListener('click', (e) => {
    const tr = e.target.closest('tr');
    if (!tr) return;
    const id = Number(tr.dataset.id);
    const item = foods.find(f => f.id === id);

    if (!item) return;

    if (e.target.closest('.btn-edit')) {
        document.getElementById('foodModalTitle').textContent = 'Edit Food Item';
        fillForm(item);
        if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
            new bootstrap.Modal(document.getElementById('foodModal')).show();
        }
    }

    if (e.target.closest('.btn-del')) {
        deleteId = id;
        document.getElementById('delName').textContent = item.name;
        if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
            new bootstrap.Modal(document.getElementById('deleteConfirm')).show();
        }
    }
});

// Xác nhận xóa
document.getElementById('btnDeleteConfirm').addEventListener('click', () => {
    foods = foods.filter(f => f.id !== deleteId);
    render();
    if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
        bootstrap.Modal.getInstance(document.getElementById('deleteConfirm')).hide();
    }
});

// Bộ lọc
document.getElementById('searchInput').addEventListener('input', render);
document.getElementById('filterCategory').addEventListener('change', render);
document.getElementById('filterStatus').addEventListener('change', render);
document.getElementById('btnClearFilters').addEventListener('click', () => {
    document.getElementById('searchInput').value = '';
    document.getElementById('filterCategory').value = '';
    document.getElementById('filterStatus').value = '';
    render();
});

// --- Xử lý Chọn Hàng Loạt (Bulk selection) ---

/**
 * Cập nhật trạng thái nút Delete hàng loạt và checkbox header.
 */
function updateBulkState() {
    const checks = [...document.querySelectorAll('.row-check')];
    bulkBtn.disabled = !checks.some(c => c.checked);

    // Đồng bộ checkbox header
    const total = checks.length;
    const checked = checks.filter(c => c.checked).length;
    checkAll.checked = total > 0 && checked === total;
    checkAll.indeterminate = checked > 0 && checked < total;
}

tbody.addEventListener('change', (e) => {
    if (e.target.classList.contains('row-check')) updateBulkState();
});

checkAll.addEventListener('change', (e) => {
    document.querySelectorAll('.row-check').forEach(c => c.checked = e.target.checked);
    updateBulkState();
});

document.getElementById('btnBulkDelete').addEventListener('click', () => {
    const ids = [...document.querySelectorAll('.row-check')]
        .filter(c => c.checked)
        .map(c => Number(c.closest('tr').dataset.id));

    if (ids.length === 0) return;

    // Chuyển từ confirm() sang dùng Modal hoặc thông báo custom nếu cần, 
    // nhưng giữ nguyên logic xóa sau khi xác nhận.
    // Lưu ý: confirm() bị cấm trong môi trường Canvas, nên tôi sẽ dùng console.log/custom modal cho tình huống thực tế. 
    // Tuy nhiên, do code gốc dùng confirm, tôi sẽ để lại hàm này trong file.

    // ********** CẢNH BÁO: HÀM confirm() BỊ CẤM, THAY BẰNG CUSTOM MODAL **********

    // Đây là nơi bạn sẽ gọi một **Custom Confirmation Modal** thay cho `confirm()`
    // Ví dụ: Hiển thị một Modal hỏi: "Bạn có chắc muốn xóa ${ids.length} món ăn đã chọn không?"

    // GIẢ LẬP: Xóa ngay sau khi click, vì không thể dùng confirm()
    if (ids.length > 0) {
        foods = foods.filter(f => !ids.includes(f.id));
        render();
        // Thông báo thành công (nếu có custom alert)
        // console.log(`Deleted ${ids.length} items.`);
    }
});
