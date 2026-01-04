-- table users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    user_code VARCHAR(10) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_username_not_empty CHECK (LENGTH(TRIM(username)) > 0),
    CONSTRAINT chk_user_code_format CHECK (user_code ~ '^USR[0-9]{6}$')
);

COMMENT ON TABLE users IS 'Usuarios do forum';
COMMENT ON COLUMN users.user_code IS 'Codigo unico gerado (ex: USR482910)';

-- table boards
CREATE TABLE boards (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_board_name_format CHECK (name ~ '^/[a-z0-9]+$')
);

COMMENT ON TABLE boards IS 'Boards/Categorias do forum (ex: /tech, /random)';
COMMENT ON COLUMN boards.name IS 'Nome unico do board (ex: /tech)';

-- table threads
CREATE TABLE threads (
    id BIGSERIAL PRIMARY KEY,
    board_id BIGINT NOT NULL REFERENCES boards(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(300) NOT NULL,
    content TEXT NOT NULL,
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_title_not_empty CHECK (LENGTH(TRIM(title)) > 0),
    CONSTRAINT chk_content_not_empty CHECK (LENGTH(TRIM(content)) > 0)
);

COMMENT ON TABLE threads IS 'Threads/Topicos de discussao';
COMMENT ON COLUMN threads.is_pinned IS 'Thread fixada no topo';
COMMENT ON COLUMN threads.is_locked IS 'Thread travada (sem novas respostas)';
COMMENT ON COLUMN threads.updated_at IS 'Atualizado quando recebe novos posts (bump)';

-- table posts
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    thread_id BIGINT NOT NULL REFERENCES threads(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    reply_to_post_id BIGINT REFERENCES posts(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_post_content_not_empty CHECK (LENGTH(TRIM(content)) > 0),
    CONSTRAINT chk_no_self_reply CHECK (reply_to_post_id != id)
);

COMMENT ON TABLE posts IS 'Posts/Respostas dentro de threads';
COMMENT ON COLUMN posts.reply_to_post_id IS 'ID do post sendo respondido (quote/reply)';

-- indices
CREATE INDEX idx_threads_board_pinned_updated
    ON threads(board_id, is_pinned DESC, updated_at DESC);

CREATE INDEX idx_threads_updated_at
    ON threads(updated_at DESC);

CREATE INDEX idx_posts_thread_created
    ON posts(thread_id, created_at ASC);

CREATE INDEX idx_posts_reply_to
    ON posts(reply_to_post_id)
    WHERE reply_to_post_id IS NOT NULL;

CREATE INDEX idx_threads_board_count
    ON threads(board_id);

CREATE INDEX idx_posts_thread_count
    ON posts(thread_id);

-- seed
INSERT INTO boards (name, title, description) VALUES
    ('/tech', 'Technology', 'Discussoes sobre tecnologia, programacao e desenvolvimento'),
    ('/random', 'Random', 'Discussoes aleatorias sobre qualquer assunto'),
    ('/games', 'Games', 'Jogos, consoles e cultura gamer'),
    ('/music', 'Music', 'Musica, artistas e recomendacoes'),
    ('/movies', 'Movies & TV', 'Filmes, series e entretenimento');

INSERT INTO users (username, user_code) VALUES
    ('Admin', 'USR000001');

INSERT INTO threads (board_id, user_id, title, content, is_pinned) VALUES
    (1, 1, 'Bem-vindo ao /tech!', 'Este e o board de tecnologia. Compartilhe conhecimento e faca perguntas!', true);

INSERT INTO posts (thread_id, user_id, content) VALUES
    (1, 1, 'Primeira resposta! Vamos comecar as discussoes.');