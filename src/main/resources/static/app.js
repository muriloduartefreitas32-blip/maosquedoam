const API = 'http://localhost:8080';

const getToken = () => localStorage.getItem('token');
const setToken = (t) => localStorage.setItem('token', t);
const removeToken = () => localStorage.removeItem('token');
const getUsuario = () => { try { return JSON.parse(localStorage.getItem('usuario')); } catch { return null; } };
const setUsuario = (u) => localStorage.setItem('usuario', JSON.stringify(u));
const removeUsuario = () => localStorage.removeItem('usuario');
const headers = () => ({ 'Content-Type': 'application/json', ...(getToken() ? { Authorization: `Bearer ${getToken()}` } : {}) });

function navegarTela(tela) {
  document.querySelectorAll('.page-view').forEach(view => {
    view.classList.toggle('active', view.dataset.page === tela);
  });

  document.querySelectorAll('.nav-tab').forEach(tab => {
    tab.classList.toggle('active', tab.dataset.view === tela);
  });

  if (tela === 'itens') carregarItens();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

/* ─── TOAST ─── */
function toast(msg, tipo = 'ok') {
  const el = document.getElementById('toast');
  el.textContent = msg;
  el.className = 'toast show' + (tipo === 'erro' ? ' erro' : '');
  clearTimeout(el._t);
  el._t = setTimeout(() => el.className = 'toast', 3000);
}

/* ─── MODAL ─── */
function abrirModal(html) {
  document.getElementById('modal-box').innerHTML = html;
  document.getElementById('modal-overlay').style.display = 'flex';
}

function fecharModal() {
  document.getElementById('modal-overlay').style.display = 'none';
  chatAtualSolicitacaoId = null;
}

document.getElementById('modal-overlay').addEventListener('click', (e) => {
  if (e.target === document.getElementById('modal-overlay')) fecharModal();
});

/* ─── NAVBAR ─── */
function atualizarNavbar() {
  const usuario = getUsuario();
  const logado = !!getToken() && !!usuario;

  document.getElementById('btn-login').style.display = logado ? 'none' : '';

  const perfilEl = document.getElementById('nav-perfil');
  if (logado) {
    const iniciais = (usuario.nome || 'U').split(' ').map(p => p[0]).join('').toUpperCase().slice(0, 2);
    const primeiroNome = (usuario.nome || 'Usuário').split(' ')[0];
    perfilEl.style.display = 'flex';
    perfilEl.querySelector('.nav-avatar').textContent = iniciais;
    perfilEl.querySelector('.nav-nome').textContent = primeiroNome;
    conectarWS();
    atualizarBadge();
  } else {
    perfilEl.style.display = 'none';
  }
}

/* ─── LOGIN ─── */
function abrirLogin() {
  abrirModal(`
    <h2 class="modal-title">Entrar na conta</h2>
    <div class="modal-form">
      <label>Email<input id="l-email" type="email" placeholder="seu@email.com" autofocus></label>
      <label>Senha<input id="l-senha" type="password" placeholder="••••••"></label>
      <div class="modal-erro" id="l-erro"></div>
      <div class="forgot-row">
        <button class="btn-link" onclick="abrirEsqueceuSenha()">Esqueceu a senha?</button>
      </div>
      <div class="modal-btns">
        <button class="modal-btn-cancel" onclick="fecharModal()">Cancelar</button>
        <button class="modal-btn-confirm" onclick="fazerLogin()">Entrar</button>
      </div>
      <div class="form-link">Não tem conta? <button onclick="fecharModal();abrirCadastro()">Cadastre-se</button></div>
    </div>`);
}

async function fazerLogin() {
  const email = document.getElementById('l-email').value;
  const senha = document.getElementById('l-senha').value;
  const erro = document.getElementById('l-erro');
  erro.style.display = 'none';

  try {
    const res = await fetch(`${API}/usuarios/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, senha })
    });

    if (!res.ok) {
      erro.textContent = 'Email ou senha incorretos';
      erro.style.display = 'block';
      return;
    }

    const contentType = res.headers.get('content-type') || '';
    let token, usuario;

    if (contentType.includes('application/json')) {
      const data = await res.json();
      token = data.token ?? data;
      usuario = data.usuario ?? null;
    } else {
      token = await res.text();
      usuario = null;
    }

    setToken(token);

    if (!usuario) {
      try {
        const meRes = await fetch(`${API}/usuarios/me`, { headers: { Authorization: `Bearer ${token}` } });
        if (meRes.ok) usuario = await meRes.json();
      } catch (_) {}
    }

    if (!usuario) usuario = { nome: email.split('@')[0], email };
    setUsuario(usuario);

    fecharModal();
    atualizarNavbar();
    carregarItens();
    toast('Bem-vindo de volta, ' + usuario.nome.split(' ')[0] + '!');
  } catch (err) {
    erro.textContent = 'Erro de conexão. Tente novamente.';
    erro.style.display = 'block';
  }
}

/* ─── CADASTRO ─── */
function abrirCadastro() {
  abrirModal(`
    <h2 class="modal-title">Criar conta</h2>
    <div class="modal-form">
      <label>Nome<input id="c-nome" placeholder="Seu nome completo"></label>
      <label>Email<input id="c-email" type="email" placeholder="seu@email.com"></label>
      <label>Senha<input id="c-senha" type="password" placeholder="Mínimo 6 caracteres"></label>
      <label>Perfil
        <select id="c-tipo">
          <option value="DOADOR">Doador — quero doar itens</option>
          <option value="BENEFICIARIO">Beneficiário — preciso de itens</option>
        </select>
      </label>
      <div class="modal-erro" id="c-erro"></div>
      <div class="modal-btns">
        <button class="modal-btn-cancel" onclick="fecharModal()">Cancelar</button>
        <button class="modal-btn-confirm" onclick="fazerCadastro()">Criar conta</button>
      </div>
      <div class="form-link">Já tem conta? <button onclick="fecharModal();abrirLogin()">Entrar</button></div>
    </div>`);
}

async function fazerCadastro() {
  const nome = document.getElementById('c-nome').value;
  const email = document.getElementById('c-email').value;
  const senha = document.getElementById('c-senha').value;
  const tipoUsuario = document.getElementById('c-tipo').value;
  const erro = document.getElementById('c-erro');
  erro.style.display = 'none';

  const res = await fetch(`${API}/usuarios/cadastrar`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nome, email, senha, tipoUsuario })
  });
  const data = await res.json();
  if (data.erro) { erro.textContent = data.erro; erro.style.display = 'block'; return; }

  const tokenRes = await fetch(`${API}/usuarios/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, senha })
  });

  const contentType = tokenRes.headers.get('content-type') || '';
  let token, usuario;
  if (contentType.includes('application/json')) {
    const d = await tokenRes.json();
    token = d.token ?? d;
    usuario = d.usuario ?? null;
  } else {
    token = await tokenRes.text();
    usuario = null;
  }

  setToken(token);
  if (!usuario) usuario = { nome, email };
  setUsuario(usuario);

  fecharModal();
  atualizarNavbar();
  carregarItens();
  toast('Conta criada com sucesso!');
}

/* ─── LOGOUT ─── */
function logout() {
  if (stompClient && stompClient.connected) {
    stompClient.disconnect();
    stompClient = null;
  }
  removeToken();
  removeUsuario();
  atualizarNavbar();
  toast('Até logo!');
}

/* ═══════════════════════════════════════════════
   ESQUECEU A SENHA
   ═══════════════════════════════════════════════ */
function abrirEsqueceuSenha() {
  abrirModal(`
    <h2 class="modal-title">Recuperar senha</h2>
    <p class="modal-sub">Digite seu e-mail e enviaremos um link para criar uma nova senha.</p>
    <div class="modal-form">
      <label>Email<input id="r-email" type="email" placeholder="seu@email.com" autofocus></label>
      <div class="modal-erro" id="r-erro"></div>
      <div class="modal-btns">
        <button class="modal-btn-cancel" onclick="abrirLogin()">← Voltar</button>
        <button class="modal-btn-confirm" id="r-btn" onclick="enviarLinkRecuperacao()">Enviar link</button>
      </div>
    </div>`);
}

async function enviarLinkRecuperacao() {
  const email = document.getElementById('r-email').value.trim();
  const erro = document.getElementById('r-erro');
  const btn = document.getElementById('r-btn');
  erro.style.display = 'none';

  if (!email) { erro.textContent = 'Digite seu e-mail.'; erro.style.display = 'block'; return; }

  btn.textContent = 'Enviando...';
  btn.disabled = true;

  try {
    await fetch(`${API}/usuarios/recuperar-senha`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email })
    });
  } catch (_) {}

  abrirModal(`
    <div class="modal-success">
      <div class="modal-success-icon">✉</div>
      <h2 class="modal-title">Verifique seu e-mail</h2>
      <p class="modal-sub">
        Se <strong>${email}</strong> estiver cadastrado, você receberá as instruções em breve.<br>
        Verifique também a caixa de spam.
      </p>
      <div class="modal-btns" style="justify-content:center;margin-top:1.5rem">
        <button class="modal-btn-confirm" onclick="fecharModal()">Entendido</button>
      </div>
      <div class="form-link" style="margin-top:1rem">
        <button onclick="abrirEsqueceuSenha()">Reenviar e-mail</button>
      </div>
    </div>`);
}

function verificarTokenReset() {
  const params = new URLSearchParams(window.location.search);
  const token = params.get('token');
  if (!token) return;

  window.history.replaceState({}, document.title, window.location.pathname);

  abrirModal(`
    <h2 class="modal-title">Nova senha</h2>
    <p class="modal-sub">Escolha uma senha segura com pelo menos 6 caracteres.</p>
    <div class="modal-form">
      <label>Nova senha<input id="ns-senha" type="password" placeholder="Mínimo 6 caracteres" autofocus></label>
      <label>Confirmar senha<input id="ns-confirmar" type="password" placeholder="Repita a senha"></label>
      <div class="modal-erro" id="ns-erro"></div>
      <div class="modal-btns">
        <button class="modal-btn-cancel" onclick="fecharModal()">Cancelar</button>
        <button class="modal-btn-confirm" id="ns-btn" onclick="confirmarNovaSenha('${token}')">Salvar senha</button>
      </div>
    </div>`);
}

async function confirmarNovaSenha(token) {
  const novaSenha = document.getElementById('ns-senha').value;
  const confirmar = document.getElementById('ns-confirmar').value;
  const erro = document.getElementById('ns-erro');
  const btn = document.getElementById('ns-btn');
  erro.style.display = 'none';

  if (novaSenha.length < 6) {
    erro.textContent = 'A senha deve ter pelo menos 6 caracteres.';
    erro.style.display = 'block'; return;
  }
  if (novaSenha !== confirmar) {
    erro.textContent = 'As senhas não coincidem.';
    erro.style.display = 'block'; return;
  }

  btn.textContent = 'Salvando...';
  btn.disabled = true;

  try {
    const res = await fetch(`${API}/usuarios/redefinir-senha`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token, novaSenha })
    });

    if (!res.ok) {
      const msg = await res.text().catch(() => '');
      erro.textContent = msg || 'Link inválido ou expirado. Solicite um novo.';
      erro.style.display = 'block';
      btn.textContent = 'Salvar senha';
      btn.disabled = false;
      return;
    }

    fecharModal();
    toast('Senha redefinida com sucesso! Faça login.');
    setTimeout(abrirLogin, 400);
  } catch (_) {
    erro.textContent = 'Erro de conexão. Tente novamente.';
    erro.style.display = 'block';
    btn.textContent = 'Salvar senha';
    btn.disabled = false;
  }
}

/* ─── ITENS ─── */
async function carregarItens(cat = '', busca = '') {
  const grid = document.getElementById('items-grid');
  if (!grid) return;
  grid.innerHTML = '<div class="loading-msg">Carregando itens...</div>';
  let url = `${API}/itens`;
  if (busca) url = `${API}/itens/buscar?palavra=${encodeURIComponent(busca)}`;
  else if (cat) url = `${API}/itens/categoria/${cat}`;
  try {
    const res = await fetch(url, { headers: headers() });
    const itens = await res.json();
    const disponiveis = Array.isArray(itens) ? itens.filter(i => i.status === 'DISPONIVEL') : [];
    if (disponiveis.length === 0) {
      grid.innerHTML = '<div class="loading-msg">Nenhum item disponível no momento.</div>';
      return;
    }
    grid.innerHTML = disponiveis.map(item => `
      <div class="item-card">
        <div class="item-cat">${item.categoria || 'item'}</div>
        <div class="item-title">${item.titulo}</div>
        <div class="item-desc">${item.descricao}</div>
        <div class="item-foot">
          <div class="item-loc">📍 ${item.localizacao || 'Não informado'} · ${item.quantidade || 0} unid.</div>
          <button class="item-btn" onclick="solicitarItem(${item.id}, '${item.titulo.replace(/'/g, "\\'")}')">Solicitar</button>
        </div>
      </div>`).join('');
  } catch (_) {
    grid.innerHTML = '<div class="loading-msg">Erro ao carregar itens.</div>';
  }
}

function solicitarItem(itemId, titulo) {
  if (!getToken()) { toast('Faça login para solicitar!', 'erro'); abrirLogin(); return; }
  abrirModal(`
    <h2 class="modal-title">Solicitar item</h2>
    <p style="color:rgba(240,237,230,0.5);font-size:0.85rem;margin-bottom:1.25rem">${titulo}</p>
    <div class="modal-form">
      <label>Mensagem para o doador (opcional)
        <textarea id="s-msg" placeholder="Ex: Tenho uma família de 4 pessoas..."></textarea>
      </label>
      <div class="modal-btns">
        <button class="modal-btn-cancel" onclick="fecharModal()">Cancelar</button>
        <button class="modal-btn-confirm" onclick="confirmarSolicitacao(${itemId})">Confirmar</button>
      </div>
    </div>`);
}

async function confirmarSolicitacao(itemId) {
  const mensagem = document.getElementById('s-msg').value;
  const res = await fetch(`${API}/solicitacoes/solicitar`, {
    method: 'POST', headers: headers(), body: JSON.stringify({ itemId, mensagem })
  });
  if (!res.ok) { toast('Erro ao solicitar item', 'erro'); return; }
  const solicitacao = await res.json();
  fecharModal();
  toast('Solicitação enviada!');
  carregarItens();

  // Abre o chat automaticamente após solicitar
  setTimeout(() => {
    const doador = solicitacao.item?.doador;
    const iniciais = (doador?.nome || '?').split(' ').map(p => p[0]).join('').toUpperCase().slice(0, 2);
    abrirChat(solicitacao.id, doador?.nome || 'Doador', iniciais);
  }, 600);
}

/* ─── FORM DOAÇÃO ─── */
document.getElementById('donation-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  if (!getToken()) { toast('Faça login para cadastrar um item!', 'erro'); abrirLogin(); return; }
  const payload = {
    titulo: document.getElementById('item-titulo').value,
    descricao: document.getElementById('item-descricao').value,
    categoria: document.getElementById('item-categoria').value,
    estado: document.getElementById('item-estado').value,
    quantidade: Number(document.getElementById('item-quantidade').value) || 1,
    localizacao: document.getElementById('item-localizacao').value,
  };
  if (!payload.titulo || !payload.descricao || !payload.estado) { toast('Preencha os campos obrigatórios', 'erro'); return; }
  const btn = e.currentTarget.querySelector('.btn-submit');
  btn.textContent = 'Cadastrando...';
  btn.disabled = true;
  const res = await fetch(`${API}/itens/cadastrar`, { method: 'POST', headers: headers(), body: JSON.stringify(payload) });
  btn.textContent = 'Cadastrar item';
  btn.disabled = false;
  if (!res.ok) { toast('Erro ao cadastrar item', 'erro'); return; }
  e.currentTarget.reset();
  toast('Item cadastrado com sucesso!');
  carregarItens();
  navegarTela('itens');
});

/* ─── EVENT LISTENERS ─── */
document.getElementById('btn-login').addEventListener('click', abrirLogin);

document.querySelectorAll('[data-view]').forEach(el => {
  el.addEventListener('click', () => navegarTela(el.dataset.view));
});

document.querySelectorAll('.filter-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    carregarItens(btn.dataset.cat);
  });
});

document.getElementById('btn-busca').addEventListener('click', () => {
  carregarItens('', document.getElementById('busca-input').value);
});

document.getElementById('busca-input').addEventListener('keydown', (e) => {
  if (e.key === 'Enter') carregarItens('', e.target.value);
});

/* ─── COUNTER ANIMATION ─── */
const counters = document.querySelectorAll('[data-count]');
const obs = new IntersectionObserver((entries) => {
  entries.forEach(e => {
    if (e.isIntersecting) {
      const el = e.target;
      const target = Number(el.dataset.count);
      const start = performance.now();
      const tick = (now) => {
        const p = Math.min((now - start) / 1400, 1);
        el.textContent = Math.floor(target * (1 - Math.pow(1 - p, 3))).toLocaleString('pt-BR');
        if (p < 1) requestAnimationFrame(tick);
      };
      requestAnimationFrame(tick);
      obs.unobserve(el);
    }
  });
}, { threshold: 0.5 });
counters.forEach(c => obs.observe(c));

/* ═══════════════════════════════════════════
   CHAT / MENSAGENS
   ═══════════════════════════════════════════ */

let stompClient = null;
let chatAtualSolicitacaoId = null;
let inscricaoAtual = null;

/* ─── WebSocket ─── */
function conectarWS() {
  if (stompClient && stompClient.connected) return;
  if (!getToken()) return;

  const socket = new SockJS(`${API}/ws`);
  stompClient = Stomp.over(socket);
  stompClient.debug = null;

  stompClient.connect(
    { Authorization: `Bearer ${getToken()}` },
    () => console.log('[WS] Conectado'),
    (err) => console.warn('[WS] Erro:', err)
  );
}

function inscreverSolicitacao(solicitacaoId) {
  if (!stompClient || !stompClient.connected) return;
  if (inscricaoAtual) {
    try { inscricaoAtual.unsubscribe(); } catch (_) {}
  }
  inscricaoAtual = stompClient.subscribe(`/topic/solicitacao/${solicitacaoId}`, (frame) => {
    const msg = JSON.parse(frame.body);
    if (chatAtualSolicitacaoId === solicitacaoId) {
      renderNovaMensagem(msg);
    }
    atualizarBadge();
  });
}

/* ─── Badge ─── */
async function atualizarBadge() {
  if (!getToken()) return;
  try {
    const res = await fetch(`${API}/mensagens/nao-lidas`, { headers: headers() });
    if (!res.ok) return;
    const msgs = await res.json();
    const badge = document.getElementById('msg-badge');
    if (!badge) return;
    if (msgs.length > 0) {
      badge.textContent = msgs.length;
      badge.style.display = 'inline';
    } else {
      badge.style.display = 'none';
    }
  } catch (_) {}
}

/* ─── Lista de conversas ─── */
async function abrirConversas() {
  if (!getToken()) { abrirLogin(); return; }

  abrirModal(`
    <h2 class="modal-title">💬 Mensagens</h2>
    <div class="chat-vazio">Carregando conversas...</div>`);

  try {
    const [minhas, recebidas] = await Promise.all([
      fetch(`${API}/solicitacoes/minhas`, { headers: headers() }).then(r => r.json()),
      fetch(`${API}/solicitacoes/recebidas`, { headers: headers() }).then(r => r.json())
    ]);

    const todas = [
      ...(Array.isArray(minhas) ? minhas : []),
      ...(Array.isArray(recebidas) ? recebidas : [])
    ];

    if (todas.length === 0) {
      abrirModal(`
        <h2 class="modal-title">💬 Mensagens</h2>
        <div class="chat-vazio">Nenhuma conversa ainda.<br>Solicite um item para começar!</div>`);
      return;
    }

    const usuario = getUsuario();
    const itens = todas.map(sol => {
      const euSouBeneficiario = sol.beneficiario?.email === usuario?.email;
      const outro = euSouBeneficiario ? sol.item?.doador : sol.beneficiario;
      const iniciais = (outro?.nome || '?').split(' ').map(p => p[0]).join('').toUpperCase().slice(0, 2);
      const statusLabel = { PENDENTE: '⏳ Pendente', ACEITA: '✅ Aceita', RECUSADA: '❌ Recusada', CONCLUIDA: '🎁 Concluída', CANCELADA: '🚫 Cancelada' };
      return `
        <div class="conv-item" onclick="abrirChat(${sol.id}, '${(outro?.nome || 'Usuário').replace(/'/g, "\\'")}', '${iniciais}')">
          <div class="conv-avatar">${iniciais}</div>
          <div class="conv-info">
            <div class="conv-nome">${outro?.nome || 'Usuário'}</div>
            <div class="conv-item-titulo">📦 ${sol.item?.titulo || 'Item'}</div>
            <div class="conv-preview">${statusLabel[sol.status] || sol.status}</div>
          </div>
        </div>`;
    }).join('');

    abrirModal(`
      <h2 class="modal-title">💬 Mensagens</h2>
      <div class="conv-list">${itens}</div>`);

  } catch (_) {
    abrirModal(`
      <h2 class="modal-title">💬 Mensagens</h2>
      <div class="chat-vazio">Erro ao carregar conversas.</div>`);
  }
}

/* ─── Chat de uma solicitação ─── */
async function abrirChat(solicitacaoId, nomeOutro, iniciaisOutro) {
  chatAtualSolicitacaoId = solicitacaoId;

  abrirModal(`
    <div class="chat-header">
      <button class="chat-back" onclick="abrirConversas()">←</button>
      <div class="conv-avatar" style="width:34px;height:34px;font-size:0.8rem">${iniciaisOutro}</div>
      <div>
        <div style="font-weight:600;color:#f0ede6;font-size:0.95rem">${nomeOutro}</div>
      </div>
    </div>
    <div class="chat-messages" id="chat-msgs">
      <div class="chat-vazio">Carregando mensagens...</div>
    </div>
    <div class="chat-input-row">
      <textarea class="chat-input" id="chat-input" placeholder="Digite uma mensagem..." rows="1"
        onkeydown="if(event.key==='Enter'&&!event.shiftKey){event.preventDefault();enviarMensagemChat();}"></textarea>
      <button class="chat-send" onclick="enviarMensagemChat()">➤</button>
    </div>`);

  // Conectar WS e inscrever no tópico desta solicitação
  if (!stompClient || !stompClient.connected) {
    conectarWS();
    setTimeout(() => inscreverSolicitacao(solicitacaoId), 800);
  } else {
    inscreverSolicitacao(solicitacaoId);
  }

  // Carregar histórico
  try {
    const res = await fetch(`${API}/mensagens/solicitacao/${solicitacaoId}`, { headers: headers() });
    const msgs = await res.json();
    const container = document.getElementById('chat-msgs');
    if (!container) return;

    if (!Array.isArray(msgs) || msgs.length === 0) {
      container.innerHTML = '<div class="chat-vazio">Nenhuma mensagem ainda.<br>Diga olá! 👋</div>';
      return;
    }

    const usuario = getUsuario();
    container.innerHTML = msgs.map(m => renderMensagem(m, usuario)).join('');
    container.scrollTop = container.scrollHeight;

    // Marcar como lidas
    msgs
      .filter(m => m.destinatario?.email === usuario?.email && !m.lida)
      .forEach(m => fetch(`${API}/mensagens/${m.id}/lida`, { method: 'PATCH', headers: headers() }).catch(() => {}));

    atualizarBadge();
  } catch (_) {
    const container = document.getElementById('chat-msgs');
    if (container) container.innerHTML = '<div class="chat-vazio">Erro ao carregar mensagens.</div>';
  }
}

/* ─── Renderizar mensagem ─── */
function renderMensagem(msg, usuario) {
  const euEnviei = msg.remetente?.email === usuario?.email;
  const hora = msg.dataEnvio
    ? new Date(msg.dataEnvio).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })
    : '';
  return `
    <div class="msg-bubble ${euEnviei ? 'enviada' : 'recebida'}">
      ${msg.conteudo}
      <div class="msg-hora">${hora}</div>
    </div>`;
}

function renderNovaMensagem(msg) {
  const container = document.getElementById('chat-msgs');
  if (!container) return;
  const vazio = container.querySelector('.chat-vazio');
  if (vazio) vazio.remove();
  const usuario = getUsuario();
  container.insertAdjacentHTML('beforeend', renderMensagem(msg, usuario));
  container.scrollTop = container.scrollHeight;
}

/* ─── Enviar mensagem ─── */
async function enviarMensagemChat() {
  const input = document.getElementById('chat-input');
  if (!input) return;
  const conteudo = input.value.trim();
  if (!conteudo || !chatAtualSolicitacaoId) return;

  input.value = '';
  input.focus();

  // Tenta WebSocket, fallback para REST
  if (stompClient && stompClient.connected) {
    stompClient.send(
      '/app/chat',
      { Authorization: `Bearer ${getToken()}` },
      JSON.stringify({ solicitacaoId: chatAtualSolicitacaoId, conteudo })
    );
  } else {
    try {
      const res = await fetch(`${API}/mensagens/enviar`, {
        method: 'POST',
        headers: headers(),
        body: JSON.stringify({ solicitacaoId: chatAtualSolicitacaoId, conteudo })
      });
      if (res.ok) {
        const msg = await res.json();
        renderNovaMensagem(msg);
      } else {
        toast('Erro ao enviar mensagem', 'erro');
      }
    } catch (_) {
      toast('Erro ao enviar mensagem', 'erro');
    }
  }
}

/* ─── INIT ─── */
atualizarNavbar();
carregarItens();
verificarTokenReset();
// Atualiza badge a cada 30s enquanto logado
setInterval(() => { if (getToken()) atualizarBadge(); }, 30000);
