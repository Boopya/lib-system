-- create a sequence for patron login id
CREATE SEQUENCE patron_id_seq
    INCREMENT BY 1
    START WITH 201800001
    NOCYCLE
    NOCACHE;

-- create a sequence fro transaction id
CREATE SEQUENCE transaction_id_seq
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
    NOCACHE;

-- 1
INSERT INTO PATRON VALUES (
    patron_id_seq.NEXTVAL,  -- patron login id
    'Rajan',            -- first name
    'Nanini',           -- middle name
    'Elio',             -- last name
    '69lemau',          -- password
    '420',              -- house no
    'Lamesa',           -- street
    '1',                -- barangay
    'San Mateo, Rizal', -- city
    0.00                -- unpaid fine
);

-- 2
INSERT INTO PATRON VALUES (
    patron_id_seq.NEXTVAL,  -- patron login id
    'James',            -- first name
    'Ohana',            -- middle name
    'De Leon',          -- last name
    '420adobexd',       -- password
    '69',               -- house no
    'Samgyup',          -- street
    '12',               -- barangay
    'Caloocan City',    -- city
    0.00                -- unpaid fine
);

-- 3
INSERT INTO PATRON VALUES (
    patron_id_seq.NEXTVAL,  -- patron login id
    'Mark',             -- first name
    'Moses',            -- middle name
    'Egana',            -- last name
    'klme22',           -- password
    '22',               -- house no
    'Hayabusa',         -- street
    '42',               -- barangay
    'Manila City',      -- city
    0.00                -- unpaid fine
);

-- 4
INSERT INTO PATRON VALUES (
    patron_id_seq.NEXTVAL,  -- patron login id
    'Kendrew',          -- first name
    'Yap',              -- middle name
    'Javelosa',         -- last name
    'davie504',         -- password
    '24',               -- house no
    'Citi',             -- street
    '25',               -- barangay
    'Lucena City',      -- city
    0.00                -- unpaid fine
);

-- 5
INSERT INTO PATRON VALUES (
    patron_id_seq.NEXTVAL,  -- patron login id
    'Rikiya',           -- first name
    'Eduarte',          -- middle name
    'Yamazaki',         -- last name
    'nanidesuka09',     -- password
    '16',               -- house no
    'Nice',             -- street
    '96',               -- barangay
    'Jolo, Sulu',       -- city
    0.00                -- unpaid fine
);

-- 6
INSERT INTO PATRON VALUES (
    patron_id_seq.NEXTVAL,  -- patron login id
    'Carl James',       -- first name
    'Manuelito',        -- middle name
    'Rebloriza',        -- last name
    'okichinko1',       -- password
    '65',               -- house no
    'Rearutad',         -- street
    '2',                -- barangay
    'Manila City',      -- city
    0.00                -- unpaid fine
);

-- 7
INSERT INTO PATRON VALUES (
    patron_id_seq.NEXTVAL,  -- patron login id
    'Albert',           -- first name
    'Joshy',            -- middle name
    'Dizon',            -- last name
    'rickroll123',      -- password
    '77',               -- house no
    'Thiznuts',         -- street
    '7',                -- barangay
    'Orion, Bataan',    -- city
    0.00                -- unpaid fine
);

-- 8
INSERT INTO PATRON VALUES (
    patron_id_seq.NEXTVAL,  -- patron login id
    'Jotaro',           -- first name
    'Zawarudo',         -- middle name
    'Kujo',             -- last name
    'crazydiamond05',   -- password
    '90',               -- house no
    'Morioh',           -- street
    '54',               -- barangay
    'Cebu City',        -- city
    0.00                -- unpaid fine
);

-- 9
INSERT INTO PATRON VALUES (
    patron_id_seq.NEXTVAL,  -- patron login id
    'Koichi',           -- first name
    'Echoes',           -- middle name
    'Hirose',           -- last name
    'act3freeze',       -- password
    '26',               -- house no
    'Ikuru',            -- street
    '23',               -- barangay
    'Quezon City',      -- city
    0.00                -- unpaid fine
);

-- 10
INSERT INTO PATRON VALUES (
    patron_id_seq.NEXTVAL,  -- patron login id
    'Josuke',           -- first name
    'Puratinun',        -- middle name
    'Higashikata',      -- last name
    'staru145',         -- password
    '55',               -- house no
    'Omoida',           -- street
    '33',               -- barangay
    'Taguig City',      -- city
    0.00                -- unpaid fine
);

COMMIT;