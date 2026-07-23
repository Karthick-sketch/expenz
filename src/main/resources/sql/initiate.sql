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
    icon VARCHAR(255),
    color_hex CHAR(7)
);

CREATE TABLE IF NOT EXISTS expense_sub_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    icon VARCHAR(255),
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES expense_categories (id)
);

CREATE TABLE IF NOT EXISTS expense_groups (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    user_id BIGINT,
    CONSTRAINT fk_group_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS expenses (
    id BIGSERIAL PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    sub_category_id BIGINT,
    is_income BOOLEAN NOT NULL,
    date_added DATE NOT NULL,
    user_id BIGINT NOT NULL,
    expense_group_id BIGINT,
    CONSTRAINT fk_expense_sub_category FOREIGN KEY (sub_category_id) REFERENCES expense_sub_categories (id),
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_expense_group FOREIGN KEY (expense_group_id) REFERENCES expense_groups (id)
);

-- Categories
INSERT INTO
    expense_categories (name, icon, color_hex)
VALUES ('Housing', 'home', '#8B5E3C'),
    ('Vehicle', 'car', '#4A90D9'),
    (
        'Transportation',
        'bus',
        '#50C878'
    ),
    (
        'Food & Drinks',
        'utensils',
        '#E67E22'
    ),
    (
        'Utilities',
        'plug',
        '#F1C40F'
    ),
    (
        'Shopping',
        'shopping-bag',
        '#E91E63'
    ),
    (
        'Entertainment',
        'film',
        '#9B59B6'
    ),
    (
        'Health & Wellness',
        'heart-pulse',
        '#E74C3C'
    ),
    (
        'Fitness',
        'dumbbell',
        '#16A085'
    ),
    (
        'Communication',
        'phone',
        '#3498DB'
    ),
    (
        'Education',
        'graduation-cap',
        '#2C3E50'
    ),
    (
        'Pets',
        'paw-print',
        '#D35400'
    ),
    ('Travel', 'plane', '#1ABC9C'),
    ('Income', 'wallet', '#27AE60'),
    (
        'Investments',
        'trending-up',
        '#F39C12'
    ),
    (
        'Bills & Finance',
        'receipt',
        '#7F8C8D'
    ),
    ('Personal', 'user', '#C0392B'),
    ('Other', 'other', '#95A5A6');

-- Keep BIGSERIAL in sync
SELECT setval( 'expense_categories_id_seq', 18, true );

-- Subcategories
-- NOTE: column order corrected to (category_id, name, icon) to match the values below
INSERT INTO expense_sub_categories (name, icon, category_id) VALUES

-- Housing
('Housing', 'home', 1),
('Rent', 'key', 1),
('Mortgage', 'landmark', 1),
(
    'Property Tax',
    'file-text',
    1
),
(
    'Home Maintenance',
    'wrench',
    1
),
('Furniture', 'sofa', 1),
(
    'Home Appliances',
    'refrigerator',
    1
),
(
    'House Cleaning',
    'spray-can',
    1
),
('Security', 'shield', 1),
(
    'Other Housing',
    'more-horizontal',
    1
),

-- Vehicle
('Vehicle', 'car', 2),
('Fuel', 'fuel', 2),
(
    'Service & Maintenance',
    'wrench',
    2
),
('Repairs', 'hammer', 2),
(
    'Insurance',
    'shield-check',
    2
),
(
    'Parking',
    'parking-circle',
    2
),
('Toll', 'road', 2),
('Car Wash', 'droplets', 2),
('Accessories', 'settings', 2),
(
    'Registration',
    'file-check',
    2
),
('Loan EMI', 'credit-card', 2),
(
    'Other Vehicle',
    'more-horizontal',
    2
),

-- Transportation
('Transportation', 'bus', 3),
('Bus', 'bus', 3),
('Train', 'train-front', 3),
('Metro', 'train', 3),
('Taxi', 'car-taxi-front', 3),
('Auto Rickshaw', 'car', 3),
('Flight', 'plane', 3),
(
    'Cab (Uber/Ola)',
    'car-taxi-front',
    3
),
('Bike Taxi', 'bike', 3),
(
    'Other Transportation',
    'more-horizontal',
    3
),

-- Food & Drinks
(
    'Food & Drinks',
    'utensils',
    4
),
(
    'Groceries',
    'shopping-cart',
    4
),
(
    'Dining Out',
    'utensils-crossed',
    4
),
('Coffee', 'coffee', 4),
('Snacks', 'cookie', 4),
('Fast Food', 'sandwich', 4),
('Bakery', 'croissant', 4),
('Beverages', 'cup-soda', 4),
('Food Delivery', 'bike', 4),
(
    'Other Food',
    'more-horizontal',
    4
),

-- Utilities
('Utilities', 'plug', 5),
('Electricity', 'zap', 5),
('Water', 'droplet', 5),
('Gas', 'flame', 5),
('Internet', 'wifi', 5),
('Wi-Fi', 'wifi', 5),
(
    'Mobile Recharge',
    'smartphone',
    5
),
('DTH/Cable TV', 'tv', 5),
(
    'Garbage Collection',
    'trash-2',
    5
),
(
    'Other Utilities',
    'more-horizontal',
    5
),

-- Shopping
('Shopping', 'shopping-bag', 6),
('Clothing', 'shirt', 6),
('Footwear', 'footprints', 6),
('Accessories', 'watch', 6),
('Electronics', 'laptop', 6),
('Gadgets', 'smartphone', 6),
('Books', 'book', 6),
('Gifts', 'gift', 6),
(
    'Beauty Products',
    'sparkles',
    6
),
(
    'Other Shopping',
    'more-horizontal',
    6
),

-- Entertainment
('Entertainment', 'film', 7),
('Movies', 'clapperboard', 7),
('OTT Subscriptions', 'tv', 7),
('Games', 'gamepad-2', 7),
('Music', 'music', 7),
('Concerts', 'mic-2', 7),
('Events', 'calendar', 7),
('Hobbies', 'palette', 7),
(
    'Amusement Park',
    'ferris-wheel',
    7
),
(
    'Other Entertainment',
    'more-horizontal',
    7
),

-- Health & Wellness
(
    'Health & Wellness',
    'heart-pulse',
    8
),
('Doctor', 'stethoscope', 8),
('Medicines', 'pill', 8),
('Hospital', 'hospital', 8),
(
    'Lab Tests',
    'flask-conical',
    8
),
('Dental', 'smile', 8),
('Eye Care', 'eye', 8),
('Therapy', 'brain', 8),
(
    'Health Insurance',
    'shield-plus',
    8
),
(
    'Other Health',
    'more-horizontal',
    8
),

-- Fitness
('Fitness', 'dumbbell', 9),
(
    'Gym Membership',
    'dumbbell',
    9
),
(
    'Personal Trainer',
    'user-check',
    9
),
('Supplements', 'pill', 9),
('Sports', 'trophy', 9),
('Yoga', 'flower-2', 9),
('Running', 'footprints', 9),
('Equipment', 'dumbbell', 9),
(
    'Other Fitness',
    'more-horizontal',
    9
),

-- Communication
('Communication', 'phone', 10),
(
    'Mobile Bill',
    'smartphone',
    10
),
('Internet Bill', 'wifi', 10),
('Landline', 'phone-call', 10),
('Postage', 'mail', 10),
('Courier', 'package', 10),
(
    'Other Communication',
    'more-horizontal',
    10
),

-- Education
(
    'Education',
    'graduation-cap',
    11
),
('Tuition', 'book-open', 11),
(
    'Online Courses',
    'monitor-play',
    11
),
('Books', 'book', 11),
('Certifications', 'award', 11),
('Exams', 'file-text', 11),
('Stationery', 'pencil', 11),
(
    'Other Education',
    'more-horizontal',
    11
),

-- Pets
('Pets', 'paw-print', 12),
('Pet Food', 'bone', 12),
('Vet', 'stethoscope', 12),
('Grooming', 'scissors', 12),
('Accessories', 'tag', 12),
(
    'Other Pets',
    'more-horizontal',
    12
),

-- Travel
('Travel', 'plane', 13),
('Hotel', 'bed', 13),
('Flight', 'plane', 13),
('Train', 'train-front', 13),
('Bus', 'bus', 13),
('Fuel', 'fuel', 13),
('Sightseeing', 'camera', 13),
('Visa', 'file-text', 13),
(
    'Travel Insurance',
    'shield-check',
    13
),
(
    'Other Travel',
    'more-horizontal',
    13
),

-- Income
('Income', 'wallet', 14),
('Salary', 'banknote', 14),
('Bonus', 'gift', 14),
('Freelancing', 'laptop', 14),
('Business', 'briefcase', 14),
('Rental Income', 'home', 14),
(
    'Dividends',
    'trending-up',
    14
),
('Interest', 'percent', 14),
('Cashback', 'rotate-ccw', 14),
('Refund', 'undo-2', 14),
('Gifts Received', 'gift', 14),
(
    'Other Income',
    'more-horizontal',
    14
),

-- Investments
(
    'Investments',
    'trending-up',
    15
),
(
    'Mutual Funds',
    'pie-chart',
    15
),
('Stocks', 'line-chart', 15),
('ETF', 'bar-chart-2', 15),
('Gold', 'gem', 15),
(
    'Fixed Deposit',
    'piggy-bank',
    15
),
('PPF', 'landmark', 15),
('NPS', 'landmark', 15),
(
    'Cryptocurrency',
    'bitcoin',
    15
),
(
    'Other Investments',
    'more-horizontal',
    15
),

-- Bills & Finance
(
    'Bills & Finance',
    'receipt',
    16
),
(
    'Credit Card Payment',
    'credit-card',
    16
),
(
    'Bank Charges',
    'landmark',
    16
),
('Loan EMI', 'credit-card', 16),
('Taxes', 'file-text', 16),
(
    'Insurance Premium',
    'shield',
    16
),
(
    'Interest Paid',
    'percent',
    16
),
(
    'Other Finance',
    'more-horizontal',
    16
),

-- Personal
('Personal', 'user', 17),
('Salon', 'scissors', 17),
('Cosmetics', 'sparkles', 17),
('Grooming', 'scissors', 17),
('Laundry', 'shirt', 17),
(
    'Charity',
    'heart-handshake',
    17
),
('Donations', 'hand-heart', 17),
('Miscellaneous', 'box', 17),
(
    'Other Personal',
    'more-horizontal',
    17
),

-- Other
('Other', 'other', 18);

-- Keep BIGSERIAL in sync
SELECT setval(
        'expense_sub_categories_id_seq', (
            SELECT MAX(id)
            FROM expense_sub_categories
        ), true
    );