// api client
const API_URL = 'http://localhost:8080/api';

const api = {
    async getBoards() {
        const res = await fetch(`${API_URL}/boards`);
        return res.json();
    },
    
    async getThreads(boardId) {
        const res = await fetch(`${API_URL}/threads/board/${boardId}`);
        return res.json();
    },
    
    async getThread(threadId) {
        const res = await fetch(`${API_URL}/threads/${threadId}`);
        return res.json();
    },
    
    async getPosts(threadId) {
        const res = await fetch(`${API_URL}/posts/thread/${threadId}`);
        return res.json();
    },
    
    async createThread(boardId, userId, title, content) {
        const res = await fetch(`${API_URL}/threads`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ boardId, userId, title, content })
        });
        return res.json();
    },
    
    async createPost(threadId, userId, content) {
        const res = await fetch(`${API_URL}/posts`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ threadId, userId, content, replyToPostId: null })
        });
        return res.json();
    },
    
    async createUser(username) {
        const res = await fetch(`${API_URL}/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username })
        });
        return res.json();
    },
    
    async pinThread(threadId) {
        const res = await fetch(`${API_URL}/threads/${threadId}/pin`, {
            method: 'PATCH'
        });
        return res.json();
    },
    
    async unpinThread(threadId) {
        const res = await fetch(`${API_URL}/threads/${threadId}/unpin`, {
            method: 'PATCH'
        });
        return res.json();
    },
    
    async lockThread(threadId) {
        const res = await fetch(`${API_URL}/threads/${threadId}/lock`, {
            method: 'PATCH'
        });
        return res.json();
    },
    
    async unlockThread(threadId) {
        const res = await fetch(`${API_URL}/threads/${threadId}/unlock`, {
            method: 'PATCH'
        });
        return res.json();
    }
};

// user management
function getCurrentUser() {
    let user = localStorage.getItem('forumUser');
    if (!user) {
        const username = prompt('Digite seu username:');
        if (!username) return null;
        
        api.createUser(username).then(userData => {
            localStorage.setItem('forumUser', JSON.stringify(userData));
        });
        
        return { id: 1, username }; // temporário
    }
    return JSON.parse(user);
}

// view rendering
const app = document.getElementById('app');
const breadcrumb = document.getElementById('breadcrumb');

function setBreadcrumb(items) {
    breadcrumb.innerHTML = items.map((item, i) => {
        if (i === items.length - 1) {
            return item.label;
        }
        return `<a href="#" onclick="navigate('${item.view}', ${item.id || 'null'})">${item.label}</a> / `;
    }).join('');
}

function showLoading() {
    app.innerHTML = '<div class="loading">Carregando...</div>';
}

function showError(message) {
    app.innerHTML = `<div class="error">${message}</div>`;
}

// boards view
async function showBoards() {
    showLoading();
    setBreadcrumb([{ label: 'Boards', view: 'boards' }]);
    
    try {
        const boards = await api.getBoards();
        
        app.innerHTML = `
            <div class="board-list">
                ${boards.map(board => `
                    <div class="board-card" onclick="showThreads(${board.id}, '${board.name}')">
                        <h2>${board.title}</h2>
                        <div class="name">${board.name}</div>
                        <div class="description">${board.description || ''}</div>
                        <div class="count">${board.threadCount} threads</div>
                    </div>
                `).join('')}
            </div>
        `;
    } catch (error) {
        showError('Erro ao carregar boards');
    }
}

// threads view
async function showThreads(boardId, boardName) {
    showLoading();
    setBreadcrumb([
        { label: 'Boards', view: 'boards' },
        { label: boardName, view: 'threads', id: boardId }
    ]);
    
    try {
        const threads = await api.getThreads(boardId);
        
        app.innerHTML = `
            <div class="thread-header">
                <h2>${boardName}</h2>
                <button class="btn-primary" onclick="showNewThreadForm(${boardId})">
                    Nova Thread
                </button>
            </div>
            
            <div id="thread-form-container"></div>
            
            <div class="thread-list">
                ${threads.map(thread => `
                    <div class="thread-item ${thread.pinned ? 'pinned' : ''} ${thread.locked ? 'locked' : ''}" 
                         onclick="showPosts(${thread.id})">
                        <div class="badges">
                            ${thread.pinned ? '<span class="badge pinned">📌 FIXADO</span>' : ''}
                            ${thread.locked ? '<span class="badge locked">🔒 TRAVADO</span>' : ''}
                        </div>
                        <h3>${thread.title}</h3>
                        <div class="meta">
                            Por ${thread.author?.username || 'Anônimo'} • 
                            ${thread.postCount} respostas • 
                            ${new Date(thread.createdAt).toLocaleDateString()}
                        </div>
                    </div>
                `).join('')}
            </div>
        `;
    } catch (error) {
        showError('Erro ao carregar threads');
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
                    <button type="button" class="btn-secondary" onclick="hideNewThreadForm()">
                        Cancelar
                    </button>
                </div>
            </form>
        </div>
    `;
}

function hideNewThreadForm() {
    document.getElementById('thread-form-container').innerHTML = '';
}

async function handleCreateThread(event, boardId) {
    event.preventDefault();
    const form = event.target;
    const user = getCurrentUser();
    if (!user) return;
    
    const title = form.title.value;
    const content = form.content.value;
    
    try {
        await api.createThread(boardId, user.id, title, content);
        location.reload();
    } catch (error) {
        alert('Erro ao criar thread');
    }
}

// posts view
async function showPosts(threadId) {
    showLoading();
    
    try {
        const [thread, posts] = await Promise.all([
            api.getThread(threadId),
            api.getPosts(threadId)
        ]);
        
        setBreadcrumb([
            { label: 'Boards', view: 'boards' },
            { label: 'Voltar', view: 'threads', id: thread.id },
            { label: thread.title }
        ]);
        
        app.innerHTML = `
            <div class="thread-header">
                <div>
                    <h2>${thread.title}</h2>
                    <div class="meta">
                        Por ${thread.author?.username || 'Anônimo'} • 
                        ${new Date(thread.createdAt).toLocaleDateString()}
                    </div>
                </div>
                <div class="btn-group">
                    <button class="btn-warning btn-small" onclick="togglePin(${thread.id}, ${thread.pinned})">
                        ${thread.pinned ? '📌 Desfixar' : '📍 Fixar'}
                    </button>
                    <button class="btn-danger btn-small" onclick="toggleLock(${thread.id}, ${thread.locked})">
                        ${thread.locked ? '🔓 Destravar' : '🔒 Travar'}
                    </button>
                </div>
            </div>
            
            <div class="post-list">
                <div class="post-item op">
                    <div class="post-header">
                        <span class="post-author">${thread.author?.username || 'Anônimo'}</span>
                        <span class="post-date">${new Date(thread.createdAt).toLocaleString()}</span>
                    </div>
                    <div class="post-content">${thread.content}</div>
                </div>
                
                ${posts.map(post => `
                    <div class="post-item">
                        <div class="post-header">
                            <span class="post-author">${post.author?.username || 'Anônimo'}</span>
                            <span class="post-date">${new Date(post.createAt).toLocaleString()}</span>
                        </div>
                        <div class="post-content">${post.content}</div>
                    </div>
                `).join('')}
            </div>
            
            ${!thread.locked ? `
                <div class="form-container">
                    <h3>Responder</h3>
                    <form onsubmit="handleCreatePost(event, ${threadId})">
                        <div class="form-group">
                            <label>Sua resposta</label>
                            <textarea name="content" required></textarea>
                        </div>
                        <button type="submit" class="btn-primary">Enviar</button>
                    </form>
                </div>
            ` : '<div class="error">Esta thread está travada. Não é possível adicionar respostas.</div>'}
        `;
    } catch (error) {
        showError('Erro ao carregar posts');
    }
}

async function handleCreatePost(event, threadId) {
    event.preventDefault();
    const form = event.target;
    const user = getCurrentUser();
    if (!user) return;
    
    const content = form.content.value;
    
    try {
        await api.createPost(threadId, user.id, content);
        showPosts(threadId);
    } catch (error) {
        alert(error.message || 'Erro ao criar post');
    }
}

// thread actions
async function togglePin(threadId, isPinned) {
    try {
        if (isPinned) {
            await api.unpinThread(threadId);
        } else {
            await api.pinThread(threadId);
        }
        showPosts(threadId);
    } catch (error) {
        alert('Erro ao alterar pin');
    }
}

async function toggleLock(threadId, isLocked) {
    try {
        if (isLocked) {
            await api.unlockThread(threadId);
        } else {
            await api.lockThread(threadId);
        }
        showPosts(threadId);
    } catch (error) {
        alert('Erro ao alterar lock');
    }
}

// navigation
function navigate(view, id) {
    if (view === 'boards') {
        showBoards();
    } else if (view === 'threads') {
        const boardName = breadcrumb.textContent.split('/')[1]?.trim();
        showThreads(id, boardName);
    }
}

// init
showBoards();

// -