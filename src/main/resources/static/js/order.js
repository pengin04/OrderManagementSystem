document.addEventListener("DOMContentLoaded", () => {
    const quantityInputs = document.querySelectorAll('.quantity');
    const totalPriceElement = document.getElementById('total-price');

    function calculateTotal() {
        let total = 0;
        quantityInputs.forEach(input => {
            const price = parseInt(input.dataset.price);
            const quantity = parseInt(input.value) || 0;
            total += price * quantity;
        });
        totalPriceElement.textContent = total.toLocaleString();
    }

    quantityInputs.forEach(input => {
        input.addEventListener('input', calculateTotal);
    });

    calculateTotal(); // ページ読み込み時にも実行
});