DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS project_category;

CREATE TABLE project(
project_id INT NOT NULL,
project_name VARCHAR(128) NOT NULL,
estimated_hours DECIMAL(7,2),
actual_hours DECIMAL(7,2),
difficulty INT,
notes TEXT
PRIMARY KEY (project_id)
);


CREATE TABLE project_category(
project_id INT NOT NULL,
category_id INT NOT NULL
FOREIGN KEY (project_id) unique key(category_id)
FOREIGN KEY (category_id) unique key(project_id)
);

CREATE TABLE category(
category_id INT NOT NULL,
category_name VARCHAR(128)
PRIMARY KEY (category_id)
);

CREATE TABLE step(
step_id INT NOT NULL,
project_id INT NOT NULL,
step_text TEXT NOT NULL,
step_order INT NOT NULL
PRIMARY KEY (step_id)
FOREIGN KEY (project_id)
);

CREATE TABLE material(
material_id INT NOT NULL,
project_id INT NOT NULL,
material_name VARCHAR(128) NOT NULL,
num_required INT,
cost DECIMAL(7,2)
PRIMARY KEY (material_id)
FOREIGN KEY (project_id)
);

