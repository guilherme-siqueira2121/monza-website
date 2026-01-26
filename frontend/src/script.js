// ============================================
// CONFIGURAÇÃO
// ============================================
const API_URL = 'http://localhost:8080/api';
const app = document.getElementById('app');
const breadcrumb = document.getElementById('breadcrumb');
const userSection = document.getElementById('userSection');
const authModal = document.getElementById('authModal');

// Estado da navegação
let currentView = { type: 'boards', data: {} };

// ============================================
// AUTENTICAÇÃO - JWT
// ============================================
class Auth {
    static getAccessToken() {
        return localStorage.getItem('accessToken');
    }

    static getRefreshToken() {
        return localStorage.getItem('refreshToken');
    }

    static setTokens(accessToken, refreshToken) {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
    }

    static getUser() {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    }

    static setUser(userData) {
        localStorage.setItem('user', JSON.stringify({
            userId: userData.userId,
            username: userData.username,
            userCode: userData.userCode,
            role: userData.role
        }));
    }

    static clearAuth() {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
    }

    static isAuthenticated() {
        return !!this.getAccessToken();
    }

    static logout() {
        this.clearAuth();
        updateUserSection();
        showNotification('Você saiu da conta', 'info');
        navigate('boards');
    }
}

// ============================================
// API CLIENT
// ============================================
class API {
    static async request(endpoint, options = {}) {
        const token = Auth.getAccessToken();

        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        try {
            let response = await fetch(`${API_URL}${endpoint}`, {
                ...options,
                headers
            });

            // Se 401 e tem refresh token, tenta renovar
            if (response.status === 401 && Auth.getRefreshToken()) {
                const refreshed = await this.refreshToken();
                if (refreshed) {
                    // Tenta novamente com novo token
                    headers['Authorization'] = `Bearer ${Auth.getAccessToken()}`;
                    response = await fetch(`${API_URL}${endpoint}`, {
                        ...options,
                        headers
                    });
                } else {
                    Auth.clearAuth();
                    updateUserSection();
                    showNotification('Sessão expirada. Faça login novamente.', 'error');
                    showAuthModal('login');
                    throw new Error('Session expired');
                }
            }

            return response;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    static async refreshToken() {
        const refreshToken = Auth.getRefreshToken();
        if (!refreshToken) return false;

        try {
            const response = await fetch(`${API_URL}/auth/refresh`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${refreshToken}`
                }
            });

            if (response.ok) {
                const data = await response.json();
                Auth.setTokens(data.accessToken, data.refreshToken);
                Auth.setUser(data);
                return true;
            }
        } catch (error) {
            console.error('Refresh error:', error);
        }
        return false;
    }

    // Auth endpoints
    static async register(username, password) {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        return response.json();
    }

    static async login(username, password) {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        return response.json();
    }

    // Board endpoints
    static async getBoards() {
        const response = await this.request('/boards');
        return response.json();
    }

    // Thread endpoints
    static async getThreads(boardId) {
        const response = await this.request(`/threads/board/${boardId}`);
        return response.json();
    }

    static async getThread(threadId) {
        const response = await this.request(`/threads/${threadId}`);
        return response.json();
    }

    static async createThread(boardId, title, content) {
        const user = Auth.getUser();
        const response = await this.request('/threads', {
            method: 'POST',
            body: JSON.stringify({ boardId, userId: user.userId, title, content })
        });
        return response.json();
    }

    static async pinThread(threadId) {
        const response = await this.request(`/threads/${threadId}/pin`, { method: 'PATCH' });
        return response.json();
    }

    static async unpinThread(threadId) {
        const response = await this.request(`/threads/${threadId}/unpin`, { method: 'PATCH' });
        return response.json();
    }

    static async lockThread(threadId) {
        const response = await this.request(`/threads/${threadId}/lock`, { method: 'PATCH' });
        return response.json();
    }

    static async unlockThread(threadId) {
        const response = await this.request(`/threads/${threadId}/unlock`, { method: 'PATCH' });
        return response.json();
    }

    // Post endpoints
    static async getPosts(threadId) {
        const response = await this.request(`/posts/thread/${threadId}`);
        return response.json();
    }

    static async createPost(threadId, content) {
        const user = Auth.getUser();
        const response = await this.request('/posts', {
            method: 'POST',
            body: JSON.stringify({ threadId, userId: user.userId, content, replyToPostId: null })
        });
        return response.json();
    }

    static async updatePost(postId, content) {
        const response = await this.request(`/posts/${postId}`, {
            method: 'PUT',
            body: JSON.stringify({ content })
        });
        return response.json();
    }

    static async deletePost(postId) {
        await this.request(`/posts/${postId}`, {
            method: 'DELETE'
        });
    }
}

// ============================================
// UI - USER SECTION
// ============================================
function updateUserSection() {
    const user = Auth.getUser();

    if (user) {
        userSection.innerHTML = `
            <div class="user-info">
                <span class="user-name">${escapeHtml(user.username)}</span>
                <span class="user-code">${escapeHtml(user.userCode)}</span>
                ${user.role === 'ADMIN' ? '<span class="badge pinned">ADMIN</span>' : ''}
                <button class="btn-danger btn-small" onclick="Auth.logout()">Sair</button>
            </div>
        `;
    } else {
        userSection.innerHTML = `
            <button class="btn-primary btn-small" onclick="showAuthModal('login')">Entrar</button>
            <button class="btn-secondary btn-small" onclick="showAuthModal('register')">Registrar</button>
        `;
    }
}

// ============================================
// UI - AUTH MODAL
// ============================================
function showAuthModal(tab = 'login') {
    const authContent = document.getElementById('authContent');

    authContent.innerHTML = `
        <div class="auth-tabs">
            <button class="auth-tab ${tab === 'login' ? 'active' : ''}"
                    onclick="switchAuthTab('login')">Login</button>
            <button class="auth-tab ${tab === 'register' ? 'active' : ''}"
                    onclick="switchAuthTab('register')">Registrar</button>
        </div>
        <div id="authFormContainer"></div>
    `;

    switchAuthTab(tab);
    authModal.classList.remove('hidden');
}

function closeAuthModal() {
    authModal.classList.add('hidden');
}

function switchAuthTab(tab) {
    const formContainer = document.getElementById('authFormContainer');

    // Atualiza abas
    document.querySelectorAll('.auth-tab').forEach(btn => {
        btn.classList.toggle('active', btn.textContent.toLowerCase() === tab);
    });

    if (tab === 'login') {
        formContainer.innerHTML = `
            <form onsubmit="handleLogin(event)">
                <div class="form-group">
                    <label>Username</label>
                    <input type="text" name="username" required autofocus>
                </div>
                <div class="form-group">
                    <label>Senha</label>
                    <input type="password" name="password" required>
                </div>
                <button type="submit" class="btn-primary" style="width: 100%">Entrar</button>
            </form>
        `;
    } else {
        formContainer.innerHTML = `
            <form onsubmit="handleRegister(event)">
                <div class="form-group">
                    <label>Username</label>
                    <input type="text" name="username" required minlength="3" maxlength="50" autofocus>
                    <small style="color: #7f8c8d">Mínimo 3 caracteres</small>
                </div>
                <div class="form-group">
                    <label>Senha</label>
                    <input type="password" name="password" required minlength="6">
                    <small style="color: #7f8c8d">Mínimo 6 caracteres</small>
                </div>
                <button type="submit" class="btn-primary" style="width: 100%">Registrar</button>
            </form>
        `;
    }
}

async function handleLogin(event) {
    event.preventDefault();
    const form = event.target;

    try {
        const result = await API.login(form.username.value, form.password.value);

        if (result.message) {
            showNotification(result.message, 'error');
        } else {
            Auth.setTokens(result.accessToken, result.refreshToken);
            Auth.setUser(result);
            closeAuthModal();
            updateUserSection();
            showNotification(`Bem-vindo, ${result.username}!`, 'success');
        }
    } catch (error) {
        showNotification('Erro ao fazer login', 'error');
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const form = event.target;

    try {
        const result = await API.register(form.username.value, form.password.value);

        if (result.message) {
            showNotification(result.message, 'error');
        } else {
            Auth.setTokens(result.accessToken, result.refreshToken);
            Auth.setUser(result);
            closeAuthModal();
            updateUserSection();
            showNotification(`Conta criada! Bem-vindo, ${result.username}!`, 'success');
        }
    } catch (error) {
        showNotification('Erro ao registrar', 'error');
    }
}

// ============================================
// UI - NOTIFICATIONS
// ============================================
function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type}`;

    setTimeout(() => {
        notification.classList.add('hidden');
    }, 4000);
}

// ============================================
// UI - BREADCRUMB
// ============================================
function setBreadcrumb(items) {
    breadcrumb.innerHTML = items.map((item, i) => {
        if (i === items.length - 1) {
            return item.label;
        }
        return `<a href="#" onclick="navigate('${item.view}', ${JSON.stringify(item.data || {}).replace(/"/g, '&quot;')}); return false;">${item.label}</a> / `;
    }).join('');
}

// ============================================
// UI - LOADING
// ============================================
function showLoading() {
    app.innerHTML = '<div class="loading">Carregando...</div>';
}

// ============================================
// NAVIGATION
// ============================================
function navigate(view, data = {}) {
    currentView = { type: view, data };

    switch(view) {
        case 'boards':
            showBoards();
            break;
        case 'threads':
            showThreads(data.boardId, data.boardName);
            break;
        case 'posts':
            showPosts(data.threadId);
            break;
    }
}

// ============================================
// VIEW - BOARDS
// ============================================
async function showBoards() {
    showLoading();
    setBreadcrumb([{ label: 'Boards', view: 'boards' }]);

    try {
        const boards = await API.getBoards();

        app.innerHTML = `
            <h2 style="margin-bottom: 1.5rem">Boards Disponíveis</h2>
            <div class="board-list">
                ${boards.map(board => `
                    <div class="board-card" onclick="navigate('threads', { boardId: ${board.id}, boardName: '${escapeHtml(board.name)}' })">
                        <h2>${escapeHtml(board.title)}</h2>
                        <div class="name">${escapeHtml(board.name)}</div>
                        <div class="description">${escapeHtml(board.description || '')}</div>
                        <div class="count">${board.threadCount} threads</div>
                    </div>
                `).join('')}
            </div>
        `;
    } catch (error) {
        app.innerHTML = '<div class="error">Erro ao carregar boards</div>';
    }
}

// ============================================
// VIEW - THREADS
// ============================================
async function showThreads(boardId, boardName) {
    showLoading();
    setBreadcrumb([
        { label: 'Boards', view: 'boards' },
        { label: boardName, view: 'threads', data: { boardId, boardName } }
    ]);

    try {
        const threads = await API.getThreads(boardId);

        app.innerHTML = `
            <div class="thread-header">
                <h2>${escapeHtml(boardName)}</h2>
                ${Auth.isAuthenticated()
                    ? `<button class="btn-primary" onclick="showNewThreadForm(${boardId})">Nova Thread</button>`
                    : `<button class="btn-secondary" onclick="showAuthModal('login')">Login para Criar Thread</button>`
                }
            </div>

            <div id="thread-form-container"></div>

            <div class="thread-list">
                ${threads.length === 0
                    ? '<p style="text-align: center; color: #7f8c8d; padding: 2rem;">Nenhuma thread ainda. Seja o primeiro a criar!</p>'
                    : threads.map(thread => `
                        <div class="thread-item ${thread.pinned ? 'pinned' : ''} ${thread.locked ? 'locked' : ''}"
                             onclick="navigate('posts', { threadId: ${thread.id} })">
                            <div class="badges">
                                ${thread.pinned ? '<span class="badge pinned">📌 FIXADO</span>' : ''}
                                ${thread.locked ? '<span class="badge locked">🔒 TRAVADO</span>' : ''}
                            </div>
                            <h3>${escapeHtml(thread.title)}</h3>
                            <div class="meta">
                                Por ${escapeHtml(thread.author?.username || 'Anônimo')} •
                                ${thread.postCount} respostas •
                                ${formatDate(thread.createdAt)}
                            </div>
                        </div>
                    `).join('')
                }
            </div>
        `;
    } catch (error) {
        app.innerHTML = '<div class="error">Erro ao carregar threads</div>';
    }
}

function showNewThreadForm(boardId) {
    const container = document.getElementById('thread-form-container');
    container.innerHTML = `
        <div class="form-container">
            <h3>Nova Thread</h3>
            <form onsubmit="handleCreateThread(event, ${boardId})">
                <div class="form-group">
                    <label>Título</label>
                    <input type="text" name="title" required maxlength="300">
                </div>
                <div class="form-group">
                    <label>Conteúdo</label>
                    <textarea name="content" required></textarea>
                </div>
                <div class="btn-group">
                    <button type="submit" class="btn-primary">Criar Thread</button>
                    <button type="button" class="btn-secondary" onclick="hideNewThreadForm()">Cancelar</button>
                </div>
            </form>
        </div>
    `;
    container.scrollIntoView({ behavior: 'smooth' });
}

function hideNewThreadForm() {
    document.getElementById('thread-form-container').innerHTML = '';
}

async function handleCreateThread(event, boardId) {
    event.preventDefault();
    const form = event.target;

    try {
        await API.createThread(boardId, form.title.value, form.content.value);
        showNotification('Thread criada com sucesso!', 'success');
        navigate('threads', currentView.data);
    } catch (error) {
        showNotification('Erro ao criar thread', 'error');
    }
}

// ============================================
// VIEW - POSTS
// ============================================
async function showPosts(threadId) {
    showLoading();

    try {
        const [thread, posts] = await Promise.all([
            API.getThread(threadId),
            API.getPosts(threadId)
        ]);

        setBreadcrumb([
            { label: 'Boards', view: 'boards' },
            { label: 'Voltar', view: 'threads', data: currentView.data },
            { label: thread.title }
        ]);

        const user = Auth.getUser();
        const isAdmin = user && user.role === 'ADMIN';

        app.innerHTML = `
            <div class="thread-header">
                <div>
                    <h2>${escapeHtml(thread.title)}</h2>
                    <div class="meta">
                        Por ${escapeHtml(thread.author?.username || 'Anônimo')} •
                        ${formatDate(thread.createdAt)}
                    </div>
                </div>
                ${isAdmin ? `
                    <div class="btn-group">
                        <button class="btn-warning btn-small" onclick="togglePin(${thread.id}, ${thread.pinned})">
                            ${thread.pinned ? '📌 Desfixar' : '📍 Fixar'}
                        </button>
                        <button class="btn-danger btn-small" onclick="toggleLock(${thread.id}, ${thread.locked})">
                            ${thread.locked ? '🔓 Destravar' : '🔒 Travar'}
                        </button>
                    </div>
                ` : ''}
            </div>

            <div class="post-list">
                <div class="post-item op">
                    <div class="post-header">
                        <span class="post-author">${escapeHtml(thread.author?.username || 'Anônimo')}</span>
                        <span class="post-date">${formatDateTime(thread.createdAt)}</span>
                    </div>
                    <div class="post-content">${escapeHtml(thread.content)}</div>
                </div>

                ${posts.map(post => {
                    const canEdit = user && (user.role === 'ADMIN' || post.author?.id === user.userId);
                    const canDelete = user && (user.role === 'ADMIN' || post.author?.id === user.userId);
                    
                    // Debug - remover depois
                    console.log('Post:', post);
                    console.log('User:', user);
                    console.log('Can Edit:', canEdit, 'Post Author ID:', post.author?.id, 'User ID:', user?.userId);
                    
                    return `
                        <div class="post-item" data-post-id="${post.id}">
                            <div class="post-header">
                                <div class="post-header-left">
                                    <span class="post-author">${escapeHtml(post.author?.username || 'Anônimo')}</span>
                                    <span class="post-date">${formatDateTime(post.createdAt || post.createAt)}</span>
                                    ${post.updatedAt ? `<span class="post-edited">(editado)</span>` : ''}
                                </div>
                                <div class="post-actions">
                                    ${canEdit ? `<button class="btn-warning btn-small" onclick="showEditPostForm(${post.id}, '${escapeHtml(post.content)}')">✏️</button>` : ''}
                                    ${canDelete ? `<button class="btn-danger btn-small" onclick="handleDeletePost(${post.id})">🗑️</button>` : ''}
                                </div>
                            </div>
                            <div class="post-content" id="post-content-${post.id}">${escapeHtml(post.content)}</div>
                        </div>
                    `;
                }).join('')}
            </div>

            ${!thread.locked ? `
                <div class="form-container">
                    <h3>Responder</h3>
                    ${Auth.isAuthenticated()
                        ? `
                            <form onsubmit="handleCreatePost(event, ${threadId})">
                                <div class="form-group">
                                    <label>Sua resposta</label>
                                    <textarea name="content" required></textarea>
                                </div>
                                <button type="submit" class="btn-primary">Enviar</button>
                            </form>
                        `
                        : `<p style="text-align: center; padding: 2rem;">
                             <button class="btn-primary" onclick="showAuthModal('login')">
                                 Faça login para responder
                             </button>
                           </p>`
                    }
                </div>
            ` : '<div class="error">Esta thread está travada. Não é possível adicionar respostas.</div>'}
        `;
    } catch (error) {
        app.innerHTML = '<div class="error">Erro ao carregar posts</div>';
    }
}

async function handleCreatePost(event, threadId) {
    event.preventDefault();
    const form = event.target;

    try {
        await API.createPost(threadId, form.content.value);
        showNotification('Resposta enviada!', 'success');
        navigate('posts', { threadId });
    } catch (error) {
        showNotification(error.message || 'Erro ao criar post', 'error');
    }
}

// ============================================
// POST ACTIONS
// ============================================
function showEditPostForm(postId, currentContent) {
    const editPostContent = document.getElementById('editPostContent');
    
    editPostContent.innerHTML = `
        <h3>Editar Post</h3>
        <form onsubmit="handleEditPost(event, ${postId})">
            <div class="form-group">
                <label>Conteúdo</label>
                <textarea name="content" required rows="6">${currentContent}</textarea>
            </div>
            <div class="btn-group">
                <button type="submit" class="btn-primary">Salvar</button>
                <button type="button" class="btn-secondary" onclick="closeEditPostModal()">Cancelar</button>
            </div>
        </form>
    `;
    
    document.getElementById('editPostModal').classList.remove('hidden');
}

function closeEditPostModal() {
    document.getElementById('editPostModal').classList.add('hidden');
}

async function handleEditPost(event, postId) {
    event.preventDefault();
    const form = event.target;
    
    try {
        await API.updatePost(postId, form.content.value);
        showNotification('Post editado com sucesso!', 'success');
        closeEditPostModal();
        
        // Atualiza o conteúdo do post na página sem recarregar
        const postContent = document.getElementById(`post-content-${postId}`);
        if (postContent) {
            postContent.textContent = form.content.value;
        }
        
        // Recarrega a página para mostrar o timestamp de edição
        navigate('posts', currentView.data);
    } catch (error) {
        showNotification(error.message || 'Erro ao editar post', 'error');
    }
}

async function handleDeletePost(postId) {
    if (!confirm('Tem certeza que deseja deletar este post?')) {
        return;
    }
    
    try {
        await API.deletePost(postId);
        showNotification('Post deletado com sucesso!', 'success');
        
        // Remove o post da página sem recarregar
        const postElement = document.querySelector(`[data-post-id="${postId}"]`);
        if (postElement) {
            postElement.remove();
        } else {
            // Se não encontrar, recarrega a página
            navigate('posts', currentView.data);
        }
    } catch (error) {
        showNotification(error.message || 'Erro ao deletar post', 'error');
    }
}

// ============================================
// THREAD ACTIONS
// ============================================
async function togglePin(threadId, isPinned) {
    try {
        if (isPinned) {
            await API.unpinThread(threadId);
            showNotification('Thread desfixada', 'success');
        } else {
            await API.pinThread(threadId);
            showNotification('Thread fixada', 'success');
        }
        navigate('posts', { threadId });
    } catch (error) {
        showNotification('Erro ao alterar pin', 'error');
    }
}

async function toggleLock(threadId, isLocked) {
    try {
        if (isLocked) {
            await API.unlockThread(threadId);
            showNotification('Thread destravada', 'success');
        } else {
            await API.lockThread(threadId);
            showNotification('Thread travada', 'success');
        }
        navigate('posts', { threadId });
    } catch (error) {
        showNotification('Erro ao alterar lock', 'error');
    }
}

// ============================================
// UTILITIES
// ============================================
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString('pt-BR');
}

function formatDateTime(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleString('pt-BR');
}

// ============================================
// INIT
// ============================================
updateUserSection();
showBoards();