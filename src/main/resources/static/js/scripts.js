document.addEventListener("DOMContentLoaded", () => {
    // Favoritos (seu código existente)
    document.querySelectorAll(".item button").forEach(btn => {
        btn.addEventListener("click", async (e) => {
            e.stopPropagation(); // evita event bubbling se a .item tiver click
            const symbol = btn.getAttribute("data-symbol");
            const name = btn.getAttribute("data-name");
            const category = btn.getAttribute("data-category");

            try {
                const res = await fetch("/investimentos/observados/toggle", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ symbol, name, category })
                });
                if (res.ok) {
                    window.location.reload();
                } else {
                    alert("Erro no servidor ao atualizar favoritos");
                }
            } catch (e) {
                console.error(e);
                alert("Erro na requisição");
            }
        });
    });

    // Form controls: reset page = 1 on submit or when sort/dir change
    const form = document.getElementById('searchSortForm');
    const pageInput = document.getElementById('pageInput');
    const sortSelect = document.getElementById('sortSelect');
    const dirSelect = document.getElementById('dirSelect');

    if (sortSelect && dirSelect && pageInput && form) {
        // opcional: submit automático ao trocar sort/dir
        // sortSelect.addEventListener('change', () => { pageInput.value = 1; form.submit(); });
        // dirSelect.addEventListener('change', () => { pageInput.value = 1; form.submit(); });

        // quando submeter via botão (Buscar ou Aplicar), garantir page=1
        form.addEventListener('submit', (e) => {
            pageInput.value = 1;
        });
    }
});
