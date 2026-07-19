CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    currency_code VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS expense_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    icon VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS expense_sub_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    icon VARCHAR(255),
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES expense_categories(id)
);

CREATE TABLE IF NOT EXISTS expense_groups (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    user_id BIGINT,
    CONSTRAINT fk_group_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS expenses (
    id BIGSERIAL PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    category_id BIGINT,
    is_income BOOLEAN NOT NULL,
    date_added DATE NOT NULL,
    user_id BIGINT NOT NULL,
    expense_group_id BIGINT,
    CONSTRAINT fk_expense_category FOREIGN KEY (category_id) REFERENCES expense_sub_categories(id),
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_expense_group FOREIGN KEY (expense_group_id) REFERENCES expense_groups(id)
);

-- Insert sample data for ExpenseCategory
INSERT INTO expense_categories (name, description, icon) VALUES
('Housing', 'Housing & Rent', 'home'),
('Transportation', 'Commute & Travel', 'car'),
('Food', 'Groceries & Dining', 'restaurant'),
('Utilities', 'Bills & Utilities', 'bolt'),
('Entertainment', 'Movies & Fun', 'movie');

-- Insert sample data for SubCategory
INSERT INTO expense_sub_categories (name, description, icon, category_id) VALUES
('Rent', 'Monthly rent', 'home', 1),
('Mortgage', 'Mortgage payment', 'home', 1),
('Gas', 'Gasoline', 'local_gas_station', 2),
('Public Transit', 'Bus & Train', 'directions_bus', 2),
('Groceries', 'Supermarket', 'local_grocery_store', 3),
('Dining Out', 'Restaurants', 'restaurant', 3),
('Electricity', 'Electric bill', 'bolt', 4),
('Internet', 'Internet bill', 'wifi', 4),
('Movies', 'Movie tickets', 'movie', 5),
('Concerts', 'Concert tickets', 'music_note', 5);
