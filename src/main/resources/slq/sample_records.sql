-- Insert a test user if not already present
-- password : pass1234
INSERT INTO
    users (
        "name",
        "email",
        "password",
        "currency_code",
        "active"
    )
VALUES (
        'Test User',
        'testuser@example.com',
        '$2a$10$NEP0D4hyLzX/pm47l55C1ONTtsOtdGWeqXHcAO10E6UGj4esOwZEK',
        'USD',
        true
    )
ON CONFLICT (email) DO NOTHING;

-- Insert 5 expense groups
INSERT INTO
    expense_groups (title, description, user_id)
SELECT 'Travel', 'Trips, vacations, and transport expenses', id
FROM users
WHERE
    email = 'testuser@example.com'
UNION ALL
SELECT 'Groceries', 'Daily food, beverages, and supermarket purchases', id
FROM users
WHERE
    email = 'testuser@example.com'
UNION ALL
SELECT 'Entertainment', 'Movies, concerts, streaming subscriptions, and games', id
FROM users
WHERE
    email = 'testuser@example.com'
UNION ALL
SELECT 'Utilities', 'Electricity, water, gas, internet, and phone bills', id
FROM users
WHERE
    email = 'testuser@example.com'
UNION ALL
SELECT 'Fitness', 'Gym membership, sports gear, and health supplements', id
FROM users
WHERE
    email = 'testuser@example.com';

-- Insert 30 expenses (5 per group for the 5 groups = 25 grouped, plus 5 ungrouped)
INSERT INTO
    expenses (
        amount,
        title,
        description,
        category,
        is_income,
        date_added,
        user_id,
        expense_group_id
    )
VALUES
    -- Group: Travel
    (
        120.50,
        'Flight Ticket',
        'One-way ticket to NY',
        'Travel',
        false,
        '2026-07-01',
        (
            SELECT id
            FROM users
            WHERE
                email = 'testuser@example.com'
        ),
        (
            SELECT id
            FROM expense_groups
            WHERE
                title = 'Travel'
            LIMIT 1
        )
    ),
    (
        45.00,
        'Uber Ride',
        'Ride to the hotel',
        'Travel',
        false,
        '2026-07-02',
        (
            SELECT id
            FROM users
            WHERE
                email = 'testuser@example.com'
        ),
        (
            SELECT id
            FROM expense_groups
            WHERE
                title = 'Travel'
            LIMIT 1
        )
    ),
    (
        350.00,
        'Hotel Stay',
        '3 nights at Downtown Hotel',
        'Travel',
        false,
        '2026-07-03',
        (
            SELECT id
            FROM users
            WHERE
                email = 'testuser@example.com'
        ),
        (
            SELECT id
            FROM expense_groups
            WHERE
                title = 'Travel'
            LIMIT 1
        )
    ),
    (
        25.00,
        'Subway Pass',
        'Weekly subway pass',
        'Travel',
        false,
        '2026-07-04',
        (
            SELECT id
            FROM users
            WHERE
                email = 'testuser@example.com'
        ),
        (
            SELECT id
            FROM expense_groups
            WHERE
                title = 'Travel'
            LIMIT 1
        )
    ),
    (
        85.00,
        'Museum Tour',
        'Guided tour of Metropolitan Museum',
        'Travel',
        false,
        '2026-07-05',
        (
            SELECT id
            FROM users
            WHERE
                email = 'testuser@example.com'
        ),
        (
            SELECT id
            FROM expense_groups
            WHERE
                title = 'Travel'
            LIMIT 1
        )
    ),

-- Group: Groceries
(
    64.20,
    'Weekly Groceries',
    'Milk, eggs, vegetables, and bread',
    'Groceries',
    false,
    '2026-07-02',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Groceries'
        LIMIT 1
    )
),
(
    18.50,
    'Coffee Beans',
    'Premium roasted coffee beans',
    'Groceries',
    false,
    '2026-07-04',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Groceries'
        LIMIT 1
    )
),
(
    42.10,
    'Snacks and Drinks',
    'Soda, chips, and cookies for party',
    'Groceries',
    false,
    '2026-07-06',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Groceries'
        LIMIT 1
    )
),
(
    12.80,
    'Fresh Fruits',
    'Apples, bananas, and strawberries',
    'Groceries',
    false,
    '2026-07-08',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Groceries'
        LIMIT 1
    )
),
(
    55.00,
    'Organic Vegetables',
    'Assorted organic greens',
    'Groceries',
    false,
    '2026-07-10',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Groceries'
        LIMIT 1
    )
),

-- Group: Entertainment
(
    15.99,
    'Netflix Subscription',
    'Monthly premium plan',
    'Entertainment',
    false,
    '2026-07-01',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Entertainment'
        LIMIT 1
    )
),
(
    60.00,
    'Concert Ticket',
    'Rock band live concert',
    'Entertainment',
    false,
    '2026-07-03',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Entertainment'
        LIMIT 1
    )
),
(
    70.00,
    'Video Game',
    'Pre-ordered new RPG game',
    'Entertainment',
    false,
    '2026-07-05',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Entertainment'
        LIMIT 1
    )
),
(
    30.00,
    'Cinema Tickets',
    'Tickets for 2 people with popcorn',
    'Entertainment',
    false,
    '2026-07-08',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Entertainment'
        LIMIT 1
    )
),
(
    9.99,
    'Spotify Premium',
    'Monthly music streaming subscription',
    'Entertainment',
    false,
    '2026-07-12',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Entertainment'
        LIMIT 1
    )
),

-- Group: Utilities
(
    85.40,
    'Electricity Bill',
    'Power usage for June',
    'Utilities',
    false,
    '2026-07-05',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Utilities'
        LIMIT 1
    )
),
(
    30.00,
    'Water Bill',
    'Water usage for June',
    'Utilities',
    false,
    '2026-07-06',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Utilities'
        LIMIT 1
    )
),
(
    50.00,
    'Internet Service',
    'High-speed fiber connection',
    'Utilities',
    false,
    '2026-07-10',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Utilities'
        LIMIT 1
    )
),
(
    15.00,
    'Gas Bill',
    'Gas utility charge',
    'Utilities',
    false,
    '2026-07-12',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Utilities'
        LIMIT 1
    )
),
(
    45.00,
    'Mobile Phone Plan',
    'Unlimited talk and data plan',
    'Utilities',
    false,
    '2026-07-15',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Utilities'
        LIMIT 1
    )
),

-- Group: Fitness
(
    50.00,
    'Gym Membership',
    'Monthly gym pass',
    'Fitness',
    false,
    '2026-07-01',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Fitness'
        LIMIT 1
    )
),
(
    120.00,
    'Running Shoes',
    'Athletic training shoes',
    'Fitness',
    false,
    '2026-07-04',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Fitness'
        LIMIT 1
    )
),
(
    35.00,
    'Whey Protein',
    '1kg chocolate protein powder',
    'Fitness',
    false,
    '2026-07-08',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Fitness'
        LIMIT 1
    )
),
(
    15.00,
    'Yoga Mat',
    'Non-slip yoga mat',
    'Fitness',
    false,
    '2026-07-11',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Fitness'
        LIMIT 1
    )
),
(
    28.00,
    'Resistance Bands',
    'Set of 5 resistance bands',
    'Fitness',
    false,
    '2026-07-14',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    (
        SELECT id
        FROM expense_groups
        WHERE
            title = 'Fitness'
        LIMIT 1
    )
),

-- Ungrouped/Other Expenses
(
    2500.00,
    'Monthly Salary',
    'Direct deposit from employer',
    'Salary',
    true,
    '2026-07-01',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    NULL
),
(
    45.00,
    'Haircut',
    'Haircut and styling',
    'Personal Care',
    false,
    '2026-07-07',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    NULL
),
(
    12.50,
    'Lunch Out',
    'Fast food lunch',
    'Food',
    false,
    '2026-07-09',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    NULL
),
(
    150.00,
    'Birthday Gift',
    'Gift for friend',
    'Gifts',
    false,
    '2026-07-12',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    NULL
),
(
    20.00,
    'Book Purchase',
    'Fiction novel',
    'Books',
    false,
    '2026-07-16',
    (
        SELECT id
        FROM users
        WHERE
            email = 'testuser@example.com'
    ),
    NULL
);