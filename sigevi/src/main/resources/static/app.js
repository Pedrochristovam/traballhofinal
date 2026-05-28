(() => {
  const TOKEN_KEY = "sigevi_token";
  const ME_KEY = "sigevi_me";

  const apiBase = `${window.location.origin}/api`;

  function $(id) {
    return document.getElementById(id);
  }

  function setText(id, text) {
    const el = $(id);
    if (el) el.textContent = text;
  }

  function showAlert(el, type, msg) {
    if (!el) return;
    el.classList.remove("hidden", "alert--ok", "alert--warn", "alert--error");
    el.classList.add("alert", `alert--${type}`);
    el.textContent = msg;
  }

  function hideAlert(el) {
    if (!el) return;
    el.classList.add("hidden");
    el.textContent = "";
  }

  function saveToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
  }

  function getToken() {
    return localStorage.getItem(TOKEN_KEY);
  }

  function clearSession() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(ME_KEY);
  }

  function saveMe(me) {
    localStorage.setItem(ME_KEY, JSON.stringify(me));
  }

  function getMe() {
    const raw = localStorage.getItem(ME_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  }

  async function apiFetch(path, options = {}) {
    const headers = new Headers(options.headers || {});
    headers.set("Accept", "application/json");
    if (!headers.has("Content-Type") && options.body && !(options.body instanceof FormData)) {
      headers.set("Content-Type", "application/json");
    }

    const token = getToken();
    if (token) headers.set("Authorization", `Bearer ${token}`);

    const res = await fetch(`${apiBase}${path}`, { ...options, headers });
    const text = await res.text();
    const data = text ? safeJson(text) : null;
    if (!res.ok) {
      const message = data?.message || `Erro HTTP ${res.status}`;
      const err = new Error(message);
      err.status = res.status;
      err.body = data;
      throw err;
    }
    return data;
  }

  async function apiFetchBlob(path) {
    const headers = new Headers();
    const token = getToken();
    if (token) headers.set("Authorization", `Bearer ${token}`);
    const res = await fetch(`${apiBase}${path}`, { method: "GET", headers });
    if (!res.ok) {
      const text = await res.text();
      const data = text ? safeJson(text) : null;
      const message = data?.message || `Erro HTTP ${res.status}`;
      const err = new Error(message);
      err.status = res.status;
      err.body = data;
      throw err;
    }
    return await res.blob();
  }

  function safeJson(text) {
    try {
      return JSON.parse(text);
    } catch {
      return { raw: text };
    }
  }

  function go(path) {
    window.location.href = path;
  }

  function isLoginPage() {
    return window.location.pathname.endsWith("/index.html") || window.location.pathname.endsWith("/api/") || window.location.pathname.endsWith("/api");
  }

  function isAppPage() {
    return window.location.pathname.endsWith("/app.html");
  }

  function ensureAuthed() {
    const token = getToken();
    if (!token) go("./index.html");
  }

  function setTodayDateInput(id) {
    const el = $(id);
    if (!el) return;
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, "0");
    const dd = String(today.getDate()).padStart(2, "0");
    el.value = `${yyyy}-${mm}-${dd}`;
  }

  async function initLogin() {
    setText("apiBase", apiBase);
    const form = $("loginForm");
    if (!form) return;

    const emailEl = $("email");
    const senhaEl = $("senha");
    const errEl = $("loginError");
    const okEl = $("loginOk");
    const btn = $("loginBtn");

    if (emailEl && !emailEl.value) emailEl.value = "admin@sigevi.com";
    if (senhaEl && !senhaEl.value) senhaEl.value = "Admin@123";

    form.addEventListener("submit", async (e) => {
      e.preventDefault();
      hideAlert(errEl);
      hideAlert(okEl);
      btn.disabled = true;
      btn.textContent = "Entrando...";
      try {
        const payload = {
          email: emailEl.value.trim(),
          senha: senhaEl.value
        };
        const resp = await apiFetch("/auth/login", {
          method: "POST",
          body: JSON.stringify(payload)
        });
        saveToken(resp.token);
        saveMe(resp.usuario);
        showAlert(okEl, "ok", "Login realizado. Indo pro painel...");
        setTimeout(() => go("./app.html"), 350);
      } catch (err) {
        showAlert(errEl, "error", err.message || "Falha no login");
      } finally {
        btn.disabled = false;
        btn.textContent = "Entrar";
      }
    });
  }

  async function initApp() {
    ensureAuthed();
    setText("apiBase", apiBase);

    const me = getMe();
    if (me) setText("meLabel", `${me.nome} (${me.role})`);

    const logoutBtn = $("logoutBtn");
    if (logoutBtn) {
      logoutBtn.addEventListener("click", () => {
        clearSession();
        go("./index.html");
      });
    }

    setTodayDateInput("dataVistoria");

    const imovelForm = $("imovelForm");
    const imovelMsg = $("imovelMsg");
    if (imovelForm) {
      imovelForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        hideAlert(imovelMsg);
        const body = {
          matricula: $("matricula").value.trim(),
          endereco: $("endereco").value.trim(),
          cidade: $("cidade").value.trim(),
          estado: $("estado").value.trim(),
          cep: $("cep").value.trim(),
          tipo: $("tipo").value,
          areaM2: $("areaM2").value ? Number($("areaM2").value) : null,
          descricao: $("descricao").value || null
        };
        try {
          const created = await apiFetch("/imoveis", { method: "POST", body: JSON.stringify(body) });
          showAlert(imovelMsg, "ok", `Imóvel cadastrado. ID: ${created.id} | Matrícula: ${created.matricula}`);
          setText("lastImovelId", String(created.id));
          const vistoriaImovelId = $("vistoriaImovelId");
          if (vistoriaImovelId) vistoriaImovelId.value = created.id;
        } catch (err) {
          showAlert(imovelMsg, "warn", err.message);
        }
      });
    }

    const vistoriaForm = $("vistoriaForm");
    const vistoriaMsg = $("vistoriaMsg");
    if (vistoriaForm) {
      vistoriaForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        hideAlert(vistoriaMsg);
        const body = {
          imovelId: Number($("vistoriaImovelId").value),
          inspetorId: me?.id ? Number(me.id) : 1,
          dataVistoria: $("dataVistoria").value,
          observacoes: $("observacoes").value || null
        };
        try {
          const created = await apiFetch("/vistorias", { method: "POST", body: JSON.stringify(body) });
          showAlert(vistoriaMsg, "ok", `Vistoria criada. ID: ${created.id} | Status: ${created.status}`);
          setText("lastVistoriaId", String(created.id));
          const statusId = $("statusVistoriaId");
          const fotoId = $("fotoVistoriaId");
          const relId = $("relatorioVistoriaId");
          if (statusId) statusId.value = created.id;
          if (fotoId) fotoId.value = created.id;
          if (relId) relId.value = created.id;
        } catch (err) {
          showAlert(vistoriaMsg, "error", err.message);
        }
      });
    }

    const statusForm = $("statusForm");
    const statusMsg = $("statusMsg");
    if (statusForm) {
      statusForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        hideAlert(statusMsg);
        const id = Number($("statusVistoriaId").value);
        const body = { status: $("statusNovo").value };
        try {
          const updated = await apiFetch(`/vistorias/${id}/status`, { method: "PATCH", body: JSON.stringify(body) });
          showAlert(statusMsg, "ok", `Status atualizado. Agora: ${updated.status}`);
        } catch (err) {
          showAlert(statusMsg, "warn", err.message);
        }
      });
    }

    const fotoForm = $("fotoForm");
    const fotoMsg = $("fotoMsg");
    if (fotoForm) {
      fotoForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        hideAlert(fotoMsg);
        const vistoriaId = Number($("fotoVistoriaId").value);
        const fileInput = $("fotoArquivo");
        if (!fileInput.files || fileInput.files.length === 0) {
          showAlert(fotoMsg, "warn", "Selecione um arquivo de imagem.");
          return;
        }
        const formData = new FormData();
        formData.append("arquivo", fileInput.files[0]);
        try {
          const created = await apiFetch(`/fotos/vistoria/${vistoriaId}`, { method: "POST", body: formData });
          showAlert(fotoMsg, "ok", `Foto enviada. ID: ${created.id} | Nome: ${created.nomeArquivo}`);
        } catch (err) {
          showAlert(fotoMsg, "error", err.message);
        }
      });
    }

    const relatorioForm = $("relatorioForm");
    const relatorioMsg = $("relatorioMsg");
    const downloadBtn = $("downloadRelatorioBtn");
    let lastRelatorioId = null;
    if (relatorioForm) {
      relatorioForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        hideAlert(relatorioMsg);
        lastRelatorioId = null;
        if (downloadBtn) downloadBtn.disabled = true;
        const vistoriaId = Number($("relatorioVistoriaId").value);
        const body = { tipo: $("relatorioTipo").value };
        try {
          const created = await apiFetch(`/relatorios/vistoria/${vistoriaId}`, { method: "POST", body: JSON.stringify(body) });
          showAlert(relatorioMsg, "ok", `Relatório gerado. ID: ${created.id} | Tipo: ${created.tipo}`);
          setText("lastRelatorioId", String(created.id));
          lastRelatorioId = created.id;
          if (downloadBtn) downloadBtn.disabled = false;
        } catch (err) {
          showAlert(relatorioMsg, "error", err.message);
        }
      });
    }

    // pdf direto no link dá 403 — tem que mandar o token no fetch
    if (downloadBtn) {
      downloadBtn.addEventListener("click", async () => {
        if (!lastRelatorioId) return;
        hideAlert(relatorioMsg);
        downloadBtn.disabled = true;
        downloadBtn.textContent = "Baixando...";
        try {
          const blob = await apiFetchBlob(`/relatorios/${lastRelatorioId}/download`);
          const url = URL.createObjectURL(blob);
          const a = document.createElement("a");
          a.href = url;
          a.download = `relatorio-${lastRelatorioId}.pdf`;
          document.body.appendChild(a);
          a.click();
          a.remove();
          URL.revokeObjectURL(url);
          showAlert(relatorioMsg, "ok", "PDF baixado com sucesso.");
        } catch (err) {
          showAlert(relatorioMsg, "error", err.message);
        } finally {
          downloadBtn.disabled = false;
          downloadBtn.textContent = "Baixar PDF";
        }
      });
    }
  }

  if (isLoginPage()) {
    initLogin();
  } else if (isAppPage()) {
    initApp();
  }
})();

