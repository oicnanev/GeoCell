DROP TABLE IF EXISTS portugal_grid_500m;
DROP FUNCTION IF EXISTS generate_portugal_grid_500m();
DROP INDEX IF EXISTS idx_portugal_grid_geom;
DROP INDEX IF EXISTS idx_portugal_grid_bbox;

CREATE TABLE portugal_grid_500m (
    cell_id VARCHAR(9) PRIMARY KEY,  -- Format AA.12.34
    center_geom GEOMETRY(POINT, 4326),
    bbox_geom GEOMETRY(POLYGON, 4326),
    grid_row INTEGER,
    grid_col INTEGER
);

-- Performance índexes
CREATE INDEX idx_portugal_grid_geom ON portugal_grid_500m USING GIST(center_geom);
CREATE INDEX idx_portugal_grid_bbox ON portugal_grid_500m USING GIST(bbox_geom);

-- Continental Portugal -------------------------------------------------------------------
-- Populate function
CREATE OR REPLACE FUNCTION generate_portugal_grid_500m()
RETURNS VOID AS $$
DECLARE
    -- Portugal continental bounds (approximate)
    min_lat FLOAT := 36.96;   -- South
    max_lat FLOAT := 42.15;   -- North
    min_lng FLOAT := -9.5;    -- West
    max_lng FLOAT := -6.19;   -- East

    -- Cell size (500m in degrees)
    cell_size_deg_lat FLOAT := 500.0 / 111320;
    cell_size_deg_lng FLOAT := 500.0 / (111320 * COS(RADIANS((min_lat + max_lat)/2)));

    -- Grid settings
    cells_per_block INTEGER := 100; -- 100 cells = 50km (100*500m)
    num_rows INTEGER;
    num_cols INTEGER;
    num_block_rows INTEGER;
    num_block_cols INTEGER;
    total_blocks INTEGER;

    -- Counters
    row INTEGER;
    col INTEGER;
    block_num INTEGER;
    first_letter CHAR;
    second_letter CHAR;
    block_id TEXT;
    cell_id TEXT;
BEGIN
    -- Calculate grid dimensions
    num_rows := CEIL((max_lat - min_lat) / cell_size_deg_lat);
    num_cols := CEIL((max_lng - min_lng) / cell_size_deg_lng);

    -- Calculate number of 50km blocks needed
    num_block_rows := CEIL(num_rows::FLOAT / cells_per_block);
    num_block_cols := CEIL(num_cols::FLOAT / cells_per_block);
    total_blocks := num_block_rows * num_block_cols;

    -- Clear existing table
    TRUNCATE portugal_grid_500m;

    -- Populate grid
    FOR row IN 0..num_rows-1 LOOP
        FOR col IN 0..num_cols-1 LOOP
            -- Calculate block number (0-based)
            block_num := (row / cells_per_block) * num_block_cols + (col / cells_per_block);

            -- Convert block number to AA-ZZ format
            first_letter := CHR(65 + (block_num / 26)::INT);
            second_letter := CHR(65 + (block_num % 26));
            block_id := first_letter || second_letter;

            -- Format: LL.NN.NN
            cell_id :=
                block_id || '.' ||
                LPAD((row % cells_per_block)::TEXT, 2, '0') || '.' ||
                LPAD((col % cells_per_block)::TEXT, 2, '0');

            -- Insert into table
            INSERT INTO portugal_grid_500m (cell_id, center_geom, bbox_geom, grid_row, grid_col)
            VALUES (
                cell_id,
                ST_SetSRID(ST_MakePoint(
                    min_lng + (col + 0.5) * cell_size_deg_lng,
                    min_lat + (row + 0.5) * cell_size_deg_lat
                ), 4326),
                ST_MakeEnvelope(
                    min_lng + col * cell_size_deg_lng,
                    min_lat + row * cell_size_deg_lat,
                    min_lng + (col + 1) * cell_size_deg_lng,
                    min_lat + (row + 1) * cell_size_deg_lat,
                    4326
                ),
                row,
                col
            );
        END LOOP;
    END LOOP;

    RAISE NOTICE 'Grid generated with success! % blocks of 50km (from AA to %)',
        total_blocks,
        CHR(65 + ((total_blocks-1)/26)::INT) || CHR(65 + ((total_blocks-1)%26));
END;
$$ LANGUAGE plpgsql;

-- Madeira ------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION generate_madeira_grid_500m()
RETURNS VOID AS $$
DECLARE
    -- Madeira bounds (approximate)
    min_lat FLOAT := 32.38;   -- South
    max_lat FLOAT := 33.15;   -- North
    min_lng FLOAT := -17.31;  -- West
    max_lng FLOAT := -16.16;  -- East

    -- Cell size (500m in degrees)
    cell_size_deg_lat FLOAT := 500.0 / 111320;
    cell_size_deg_lng FLOAT := 500.0 / (111320 * COS(RADIANS((min_lat + max_lat)/2)));

    -- Grid settings
    cells_per_block INTEGER := 100; -- 100 cells = 50km (100*500m)
    num_rows INTEGER;
    num_cols INTEGER;
    num_block_rows INTEGER;
    num_block_cols INTEGER;
    total_blocks INTEGER;

    -- Counters
    r INTEGER;  -- Renomeado de 'row' para evitar conflito
    c INTEGER;  -- Renomeado de 'col' para evitar conflito
    block_num INTEGER;
    first_letter CHAR;
    second_letter CHAR;
    current_block_id TEXT;  -- Renomeado de 'block_id'
    current_cell_id TEXT;   -- Renomeado de 'cell_id'
BEGIN
    -- Calculate grid dimensions
    num_rows := CEIL((max_lat - min_lat) / cell_size_deg_lat);
    num_cols := CEIL((max_lng - min_lng) / cell_size_deg_lng);

    -- Calculate number of 50km blocks needed
    num_block_rows := CEIL(num_rows::FLOAT / cells_per_block);
    num_block_cols := CEIL(num_cols::FLOAT / cells_per_block);
    total_blocks := num_block_rows * num_block_cols;

    -- Clear only Madeira's existing records
    DELETE FROM portugal_grid_500m WHERE cell_id ~ '^M[A-Z]\\..*';

    -- Populate grid
    FOR r IN 0..num_rows-1 LOOP
        FOR c IN 0..num_cols-1 LOOP
            -- Calculate block number (0-based)
            block_num := (r / cells_per_block) * num_block_cols + (c / cells_per_block);

            -- Convert block number to MA, MB, MC... sequence
            first_letter := 'M';  -- Fixo 'M' para Madeira
            second_letter := CHR(65 + (block_num % 26));  -- A-Z
            current_block_id := first_letter || second_letter;

            -- Format: LL.NN.NN
            current_cell_id :=
                current_block_id || '.' ||
                LPAD((r % cells_per_block)::TEXT, 2, '0') || '.' ||
                LPAD((c % cells_per_block)::TEXT, 2, '0');

            -- Insert into table
            INSERT INTO portugal_grid_500m (cell_id, center_geom, bbox_geom, grid_row, grid_col)
            VALUES (
                current_cell_id,
                ST_SetSRID(ST_MakePoint(
                    min_lng + (c + 0.5) * cell_size_deg_lng,
                    min_lat + (r + 0.5) * cell_size_deg_lat
                ), 4326),
                ST_MakeEnvelope(
                    min_lng + c * cell_size_deg_lng,
                    min_lat + r * cell_size_deg_lat,
                    min_lng + (c + 1) * cell_size_deg_lng,
                    min_lat + (r + 1) * cell_size_deg_lat,
                    4326
                ),
                r,
                c
            );
        END LOOP;
    END LOOP;

    RAISE NOTICE 'Grid da Madeira gerado com sucesso! % blocos de 50km (de MA a M%)',
        total_blocks, CHR(65 + ((total_blocks-1) % 26));
END;
$$ LANGUAGE plpgsql;


-- Azores
CREATE OR REPLACE FUNCTION generate_acores_grid_500m()
RETURNS VOID AS $$
DECLARE
    -- Bounding box dos Açores
    min_lat FLOAT := 36.55;   -- Sul (Santa Maria)
    max_lat FLOAT := 39.80;   -- Norte (Flores)
    min_lng FLOAT := -31.55;  -- Oeste (Flores)
    max_lng FLOAT := -24.60;  -- Leste (São Miguel)

    -- Configurações da grelha
    cell_size_deg_lat FLOAT := 500.0 / 111320;
    cell_size_deg_lng FLOAT := 500.0 / (111320 * COS(RADIANS((min_lat + max_lat)/2)));
    cells_per_block INTEGER := 100;
    num_rows INTEGER;
    num_cols INTEGER;
    num_block_rows INTEGER;
    num_block_cols INTEGER;
    total_blocks INTEGER;
    r INTEGER;
    c INTEGER;
    block_num INTEGER;
    first_letter CHAR;
    second_letter CHAR;
    current_block_id TEXT;
    current_cell_id TEXT;
BEGIN
    -- Limpar apenas células dos Açores (prefixos Z e Y)
    DELETE FROM portugal_grid_500m WHERE cell_id ~ '^[ZY][A-Z]\\..*';

    -- Calcular dimensões
    num_rows := CEIL((max_lat - min_lat) / cell_size_deg_lat);
    num_cols := CEIL((max_lng - min_lng) / cell_size_deg_lng);
    num_block_rows := CEIL(num_rows::FLOAT / cells_per_block);
    num_block_cols := CEIL(num_cols::FLOAT / cells_per_block);
    total_blocks := num_block_rows * num_block_cols;

    -- Gerar grelha com sequência estendida
    FOR r IN 0..num_rows-1 LOOP
        FOR c IN 0..num_cols-1 LOOP
            block_num := (r / cells_per_block) * num_block_cols + (c / cells_per_block);

            -- Sequência estendida: ZA-ZZ, YA-YZ, XA-XZ, etc.
            first_letter := CHR(90 - (block_num / 26)); -- Z(90), Y(89), X(88), ...
            second_letter := CHR(65 + (block_num % 26)); -- A-Z
            current_block_id := first_letter || second_letter;

            -- Formato: ZA.00.00, ZZ.00.00, YA.00.00, etc.
            current_cell_id :=
                current_block_id || '.' ||
                LPAD((r % cells_per_block)::TEXT, 2, '0') || '.' ||
                LPAD((c % cells_per_block)::TEXT, 2, '0');

            -- Inserir (ignorar células duplicadas se necessário)
            INSERT INTO portugal_grid_500m (cell_id, center_geom, bbox_geom, grid_row, grid_col)
            VALUES (
                current_cell_id,
                ST_SetSRID(ST_MakePoint(
                    min_lng + (c + 0.5) * cell_size_deg_lng,
                    min_lat + (r + 0.5) * cell_size_deg_lat
                ), 4326),
                ST_MakeEnvelope(
                    min_lng + c * cell_size_deg_lng,
                    min_lat + r * cell_size_deg_lat,
                    min_lng + (c + 1) * cell_size_deg_lng,
                    min_lat + (r + 1) * cell_size_deg_lat,
                    4326
                ),
                r,
                c
            )
            ON CONFLICT (cell_id) DO NOTHING; -- Ignora conflitos se existirem
        END LOOP;
    END LOOP;

    RAISE NOTICE 'Grelha dos Açores gerada! Blocos de ZA a %',
        CHR(90 - ((total_blocks-1)/26)) || CHR(65 + ((total_blocks-1)%26));
END;
$$ LANGUAGE plpgsql;

select generate_portugal_grid_500m();
select generate_madeira_grid_500m();
select generate_acores_grid_500m();
