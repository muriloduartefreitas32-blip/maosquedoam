const API = 'http://localhost:8080';

const getToken = () => localStorage.getItem('token');
const setToken = (t) => localStorage.setItem('token', t);
const removeToken = () => localStorage.removeItem('token');
const headers = () => ({ 'Content-Type': 'application/json', ...(getToken() ? { Authorization: `Bearer ${getToken()}` } : {}) });

function toast(msg, tipo = 'ok') {
	const el = document.getElementById('toast');
	el.textContent = msg;
	el.className = 'toast show' + (tipo === 'erro' ? ' erro' : '');
	clearTimeout(el._t);
	el._t = setTimeout(() => el.className = 'toast', 3000);
}

function abrirModal(html) {
	document.getElementById('modal-box').innerHTML = html;
	document.getElementById('modal-overlay').style.display = 'flex';
}

function fecharModal() {
	document.getElementById('modal-overlay').style.display = 'none';
}

document.getElementById('modal-overlay').addEventListener('click', (e) => {
	if (e.target === document.getElementById('modal-overlay')) fecharModal();
});

function atualizarNavbar() {
	const logado = !!getToken();
	document.getElementById('btn-login').style.display = logado ? 'none' : '';
	document.getElementById('btn-cadastro').style.display = logado ? '' : 'none';
	document.getElementById('btn-logout').style.display = logado ? '' : 'none';
}

function abrirLogin() {
	abrirModal(`
    <h2 class="modal-title">Entrar na conta</h2>
    <div class="modal-form">
      <label>Email<input id="l-email" type="email" placeholder="seu@email.com"></label>
      <label>Senha<input id="l-senha" type="password" placeholder="••••••"></label>
      <div class="modal-erro" id="l-erro"></div>
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
	const res = await fetch(`${API}/usuarios/login`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, senha }) });
	if (!res.ok) { erro.textContent = 'Email ou senha incorretos'; erro.style.display = 'block'; return; }
	const token = await res.text();
	setToken(token);
	fecharModal();
	atualizarNavbar();
	carregarItens();
	toast('Login realizado com sucesso!');
}

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
	const res = await fetch(`${API}/usuarios/cadastrar`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ nome, email, senha, tipoUsuario }) });
	const data = await res.json();
	if (data.erro) { erro.textContent = data.erro; erro.style.display = 'block'; return; }
	const tokenRes = await fetch(`${API}/usuarios/login`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, senha }) });
	const token = await tokenRes.text();
	setToken(token);
	fecharModal();
	atualizarNavbar();
	carregarItens();
	toast('Conta criada com sucesso!');
}

function logout() {
	removeToken();
	atualizarNavbar();
	toast('Até logo!');
}

async function carregarItens(cat = '', busca = '') {
	const grid = document.getElementById('items-grid');
	if (!grid) return;
	grid.innerHTML = '<div class="loading-msg">Carregando itens...</div>';
	let url = `${API}/itens`;
	if (busca) url = `${API}/itens/buscar?palavra=${encodeURIComponent(busca)}`;
	else if (cat) url = `${API}/itens/categoria/${cat}`;
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
	const res = await fetch(`${API}/solicitacoes/solicitar`, { method: 'POST', headers: headers(), body: JSON.stringify({ itemId, mensagem }) });
	if (!res.ok) { toast('Erro ao solicitar item', 'erro'); return; }
	fecharModal();
	toast('Solicitação enviada!');
	carregarItens();
}

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
});

document.getElementById('btn-login').addEventListener('click', abrirLogin);
document.getElementById('btn-cadastro').addEventListener('click', abrirCadastro);
document.getElementById('btn-logout').addEventListener('click', logout);

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

atualizarNavbar();
carregarItens();