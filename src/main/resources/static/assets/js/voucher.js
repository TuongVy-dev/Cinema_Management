document.addEventListener("DOMContentLoaded", function () {
    const discountTypeSelect = document.getElementById("discountType");
    const discountValueInput = document.getElementById("discountValue");

    function applyValidation() {
        const type = discountTypeSelect.value;

        if (type === "percentage") {
            discountValueInput.max = 100;
            discountValueInput.min = 1;

            // Kiểm tra ngay nếu giá trị hiện tại không hợp lệ
            const currentValue = parseInt(discountValueInput.value);
            if (currentValue > 100 || currentValue < 1) {
                discountValueInput.setCustomValidity("Percentage discount must be between 1 and 100.");
            } else {
                discountValueInput.setCustomValidity("");
            }
        } else {
            discountValueInput.removeAttribute("max");
            discountValueInput.min = 1;
            discountValueInput.setCustomValidity("");
        }
    }

    // Áp dụng khi đổi type
    discountTypeSelect.addEventListener("change", applyValidation);

    // Áp dụng khi người dùng nhập
    discountValueInput.addEventListener("input", applyValidation);

    // Gọi lần đầu khi load trang (trường hợp người dùng reload)
    applyValidation();
});