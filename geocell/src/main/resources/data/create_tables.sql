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

CREATE TABLE content_type (
    id SERIAL PRIMARY KEY,
    app_label VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL
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

CREATE TABLE geocell_country (
    name VARCHAR(100) NOT NULL PRIMARY KEY,
    code VARCHAR(4) NULL,
    polygon GEOMETRY(POLYGON, 4326) NULL,
    flag VARCHAR(100) NULL
);

CREATE TABLE geocell_district (
    id VARCHAR(20) NOT NULL PRIMARY KEY,
    district VARCHAR(100) NOT NULL,
    polygon GEOMETRY(POLYGON, 4326) NULL,
    country_id VARCHAR(100) NULL REFERENCES geocell_country(name) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE geocell_county (
    id SERIAL NOT NULL PRIMARY KEY,
    id_county VARCHAR(20) NOT NULL,
    county VARCHAR(100) NOT NULL,
    polygon GEOMETRY(POLYGON, 4326) NULL,
    district_id VARCHAR(20) NULL REFERENCES geocell_district(id) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE geocell_location (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    coordinates GEOMETRY(POINT, 4326) NULL,
    address VARCHAR(100) NULL,
    address1 VARCHAR(100) NULL,
    zip4 INTEGER NOT NULL,
    zip3 INTEGER NOT NULL,
    postal_designation VARCHAR(100) NULL,
    id_county_id INTEGER NULL REFERENCES geocell_county(id) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE geocell_mccmnc (
    id SERIAL NOT NULL PRIMARY KEY,
    type VARCHAR(100) NULL,
    country_id VARCHAR(100) NULL REFERENCES geocell_country(name) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED,
    mcc INTEGER NOT NULL,
    mnc INTEGER NOT NULL,
    brand VARCHAR(100) NULL,
    operator VARCHAR(200) NULL,
    status VARCHAR(100) NULL,
    bands VARCHAR(200) NULL,
    notes VARCHAR(300) NULL,
    UNIQUE (mcc, mnc)
);

CREATE TABLE geocell_band (
    id SERIAL NOT NULL PRIMARY KEY,
    band VARCHAR(50) NULL,
    bandwidth DOUBLE PRECISION NULL,
    uplink_freq DOUBLE PRECISION NULL,
    downlink_freq DOUBLE PRECISION NULL,
    earfcn DOUBLE PRECISION NULL
);

CREATE TABLE geocell_enbgnb (
    id SERIAL NOT NULL PRIMARY KEY,
    enb_gnb INTEGER NOT NULL,
    location_id BIGINT NOT NULL REFERENCES geocell_location(id) ON DELETE DO NOTHING DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE geocell_cell (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    lac_tac VARCHAR(50) NOT NULL,
    enb_gnb_id INTEGER NULL REFERENCES geocell_enbgnb(id) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED,
    ci VARCHAR(20) NULL,
    eci_nci VARCHAR(20) NULL,
    cgi VARCHAR(30) NULL,
    paragon_cgi VARCHAR(100) NULL,
    mcc_mnc_id INTEGER NULL REFERENCES geocell_mccmnc(id) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED,
    technology INTEGER NOT NULL,
    band_id INTEGER NULL REFERENCES geocell_band(id) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED,
    direction INTEGER NOT NULL,
    name VARCHAR(200) NULL,
    location_id BIGINT NOT NULL REFERENCES geocell_location(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    created DATE NOT NULL,
    owner_id INTEGER NOT NULL REFERENCES auth_user(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    modified DATE NOT NULL,
    modifier_id INTEGER NULL REFERENCES auth_user(id) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED,
    UNIQUE (cgi, paragon_cgi, direction)
);

CREATE TABLE geocell_cellpolygon (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    polygon GEOMETRY(POLYGON, 4326) NULL,
    polygon_short GEOMETRY(POLYGON, 4326) NULL,
    cell_id BIGINT NULL REFERENCES geocell_cell(id) ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE geocell_coverage (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    cell_id BIGINT NOT NULL REFERENCES geocell_cell(id) ON DELETE RESTRICT DEFERRABLE INITIALLY DEFERRED,
    signal_strength INTEGER NULL,
    user_id INTEGER NOT NULL REFERENCES auth_user(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    timestamp TIMESTAMP with time zone NOT NULL,
    location_id BIGINT NOT NULL REFERENCES geocell_location(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE geocell_operation (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    group_id INTEGER NOT NULL REFERENCES auth_group(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    active BOOLEAN NOT NULL,
    modified TIMESTAMP with time zone NOT NULL,
    UNIQUE (name, group_id)
);

-- OperationTarget Table
CREATE TABLE geocell_operationtarget (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    operation_id BIGINT NOT NULL REFERENCES geocell_operation(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    color VARCHAR(7) NOT NULL,
    UNIQUE (name, operation_id),
    UNIQUE (color, operation_id)
);

CREATE TABLE geocell_operationcell (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES auth_user(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    cell_id BIGINT NOT NULL REFERENCES geocell_cell(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    target_id BIGINT NOT NULL REFERENCES geocell_operationtarget(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    timestamp TIMESTAMP with time zone NOT NULL,
    user_time TIMESTAMP with time zone NULL
);

CREATE TABLE geocell_userlocation (
    user_id INTEGER NOT NULL PRIMARY KEY REFERENCES auth_user(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL
);
