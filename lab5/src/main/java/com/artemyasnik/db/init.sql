CREATE TABLE clients
(
    id SERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password VARCHAR(56) NOT NULL
);

CREATE TABLE persons
(
	id SERIAL PRIMARY KEY,
	name TEXT NOT NULL CHECK (name != ''),
	passport_id TEXT NOT NULL UNIQUE,
	hair_color VARCHAR(8) NOT NULL CHECK (hair_color IN ('RED', 'BLUE', 'YELLOW', 'ORANGE', 'WHITE')),
	eye_color VARCHAR(8) CHECK (eye_color IN ('GREEN', 'RED', 'BLACK', 'BLUE', 'ORANGE')),
	nationality VARCHAR(8) CHECK (nationality IN ('USA', 'SPAIN', 'CHINA', 'VATICAN'))
);

CREATE TABLE study_groups
(
	id SERIAL PRIMARY KEY,
	name TEXT NOT NULL CHECK (name != ''),
	coordinate_x DOUBLE PRECISION NOT NULL,
	coordinate_y BIGINT NOT NULL CHECK (coordinate_y > -365),	students_count INTEGER CHECK (students_count > 0),
	transferred_students INTEGER NOT NULL CHECK (transferred_students > 0),
	semester_enum VARCHAR(8) NOT NULL CHECK (semester_enum IN ('SECOND', 'SEVENTH', 'EIGHTH')),
	form_of_education VARCHAR(32) CHECK (form_of_education IN ('DISTANCE_EDUCATION', 'FULL_TIME_EDUCATION', 'EVENING_CLASSES')),
	group_admin_id INTEGER REFERENCES persons (id) ON DELETE SET NULL,
	creation_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
	owner_id INTEGER NOT NULL REFERENCES clients (id) ON DELETE CASCADE
);