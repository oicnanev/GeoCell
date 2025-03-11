CREATE TABLE auth_permission (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    codename VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE auth_user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    email VARCHAR(254) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_staff BOOLEAN NOT NULL DEFAULT FALSE,
    is_superuser BOOLEAN NOT NULL DEFAULT FALSE,
    date_joined TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP WITH TIME ZONE
);

CREATE TABLE auth_user_user_permissions (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES auth_user(id) ON DELETE CASCADE,
    permission_id INT NOT NULL REFERENCES auth_permission(id) ON DELETE CASCADE,
    UNIQUE (user_id, permission_id)
);

CREATE TABLE auth_group (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE auth_group_permissions (
    id SERIAL PRIMARY KEY,
    group_id INT NOT NULL REFERENCES auth_group(id) ON DELETE CASCADE,
    permission_id INT NOT NULL REFERENCES auth_permission(id) ON DELETE CASCADE,
    UNIQUE (group_id, permission_id)
);

CREATE TABLE auth_user_groups (
      id SERIAL PRIMARY KEY,
      user_id INT NOT NULL REFERENCES auth_user(id) ON DELETE CASCADE,
      group_id INT NOT NULL REFERENCES auth_group(id) ON DELETE CASCADE,
      UNIQUE (user_id, group_id)
);

CREATE TABLE session (
        session_key VARCHAR(40) NOT NULL PRIMARY KEY,
        session_data TEXT NOT NULL,
        expire_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE admin_log (
          id SERIAL PRIMARY KEY,
          action_time TIMESTAMP WITH TIME ZONE NOT NULL,
          object_id TEXT,
          object_repr VARCHAR(200) NOT NULL,
          action_flag SMALLINT NOT NULL,
          change_message TEXT NOT NULL,
          content_type_id INT REFERENCES content_type(id),
          user_id INT REFERENCES auth_user(id)
);
