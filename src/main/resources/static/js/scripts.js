document.addEventListener("DOMContentLoaded", () => {
  
  document.querySelectorAll(".fav-btn").forEach(btn => {
    btn.addEventListener("click", async (e) => {
      e.stopPropagation();
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
          
          const text = btn.textContent.trim();
          btn.textContent = text === '+' ? '-' : '+';
        } else {
          alert("Erro no servidor ao atualizar favoritos");
        }
      } catch (e) {
        console.error(e);
        alert("Erro na requisição");
      }
    });
  });

  
  const sortInput = document.getElementById('sortInput');
  const dirInput = document.getElementById('dirInput');
  const applyBtn = document.getElementById('applyFiltersBtn');
  const pageInput = document.getElementById('pageInput');
  const form = document.getElementById('searchSortForm');

  
  function initPills() {
    const currentSort = sortInput ? sortInput.value || 'name' : 'name';
    const currentDir = dirInput ? dirInput.value || 'asc' : 'asc';
    document.querySelectorAll('.pill[data-sort]').forEach(bt => {
      bt.classList.toggle('pill-selected', bt.dataset.sort === currentSort);
    });
    document.querySelectorAll('.pill[data-dir]').forEach(bt => {
      bt.classList.toggle('pill-selected', bt.dataset.dir === currentDir);
    });
  }
  initPills();

  
  document.querySelectorAll('.pill[data-sort]').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.pill[data-sort]').forEach(b => b.classList.remove('pill-selected'));
      btn.classList.add('pill-selected');
      if (sortInput) sortInput.value = btn.dataset.sort;
    });
  });

  
  document.querySelectorAll('.pill[data-dir]').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.pill[data-dir]').forEach(b => b.classList.remove('pill-selected'));
      btn.classList.add('pill-selected');
      if (dirInput) dirInput.value = btn.dataset.dir;
    });
  });

  
  if (applyBtn && form && pageInput) {
    applyBtn.addEventListener('click', () => {
      pageInput.value = 1;
      form.submit();
    });
  }

  
  form.addEventListener('submit', () => {
    if (pageInput) pageInput.value = 1;
  });
});
