document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".item button").forEach(btn => {
        btn.addEventListener("click", async () => {
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
                    // simples reload para refletir a mudança
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
});