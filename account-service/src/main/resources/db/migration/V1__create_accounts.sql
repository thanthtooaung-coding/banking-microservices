CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    balance NUMERIC(19,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    from_account UUID,
    to_account UUID,
    amount NUMERIC(19,2),
    created_at TIMESTAMP DEFAULT now()
);
