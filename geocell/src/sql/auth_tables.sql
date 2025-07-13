-- users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP WITH TIME ZONE,
    CONSTRAINT valid_email_check CHECK (
        email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
    )
);

-- groups
CREATE TABLE groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

-- association user-group (many-to-many)
CREATE TABLE user_groups (
     user_id INTEGER NOT NULL REFERENCES users(id),
     group_id INTEGER NOT NULL REFERENCES groups(id),
     PRIMARY KEY (user_id, group_id)
);

-- roles
CREATE TABLE roles (
   id SERIAL PRIMARY KEY,
   name VARCHAR(50) UNIQUE NOT NULL,
   description VARCHAR(255)
);

-- association user-role (many-to-many)
CREATE TABLE user_roles (
    user_id INTEGER NOT NULL REFERENCES users(id),
    role_id INTEGER NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

-- Active sessions
CREATE TABLE user_sessions (
   id VARCHAR(36) PRIMARY KEY,
   user_id INTEGER NOT NULL REFERENCES users(id),
   session_token VARCHAR(255) NOT NULL,
   ip_address VARCHAR(45),
   user_agent VARCHAR(255),
   created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
   expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
   is_valid BOOLEAN NOT NULL DEFAULT TRUE,
   CONSTRAINT unique_session_token UNIQUE (session_token)
);

-- Indexes for better performance
CREATE INDEX users_username_like_idx ON users(username varchar_pattern_ops);
CREATE INDEX groups_name_like_idx ON groups(name varchar_pattern_ops);
CREATE INDEX user_groups_group_id_idx ON user_groups(group_id);
CREATE INDEX roles_name_like_idx ON roles(name varchar_pattern_ops);
CREATE INDEX user_roles_role_id_idx ON user_roles(role_id);
CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_user_sessions_token ON user_sessions(session_token);
CREATE INDEX user_sessions_expires_at_idx ON user_sessions(expires_at);
CREATE INDEX user_sessions_is_valid_idx ON user_sessions(is_valid);
CREATE INDEX user_sessions_user_valid_idx ON user_sessions(user_id, is_valid) WHERE is_valid = TRUE;