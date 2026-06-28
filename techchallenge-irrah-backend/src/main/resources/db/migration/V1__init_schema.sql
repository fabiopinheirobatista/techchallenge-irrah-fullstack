CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id VARCHAR(14) NOT NULL UNIQUE,
    document_type VARCHAR(4) NOT NULL CHECK (document_type IN ('CPF', 'CNPJ')),
    name VARCHAR(150) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    plan_type VARCHAR(8) CHECK (plan_type IN ('PREPAID', 'POSTPAID')),
    balance NUMERIC(12, 2),
    monthly_limit NUMERIC(12, 2),
    monthly_usage NUMERIC(12, 2),
    billing_cycle_month DATE,
    role VARCHAR(6) NOT NULL CHECK (role IN ('CLIENT', 'ADMIN')),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_client_financial_profile CHECK (
        (role = 'ADMIN' AND plan_type IS NULL AND balance IS NULL AND monthly_limit IS NULL
            AND monthly_usage IS NULL AND billing_cycle_month IS NULL)
        OR
        (role = 'CLIENT' AND plan_type = 'PREPAID' AND balance >= 0 AND monthly_limit IS NULL
            AND monthly_usage IS NULL AND billing_cycle_month IS NULL)
        OR
        (role = 'CLIENT' AND plan_type = 'POSTPAID' AND balance IS NULL AND monthly_limit >= 0
            AND monthly_usage >= 0 AND monthly_usage <= monthly_limit AND billing_cycle_month IS NOT NULL)
    )
);

CREATE TABLE auth_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recipients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    contact VARCHAR(150) NOT NULL,
    contact_type VARCHAR(10) NOT NULL CHECK (contact_type IN ('PHONE', 'EMAIL')),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_recipient_contact UNIQUE (contact, contact_type)
);

CREATE TABLE conversations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    recipient_id UUID NOT NULL REFERENCES recipients(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_conversation_client_recipient UNIQUE (client_id, recipient_id)
);

CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    content VARCHAR(2000) NOT NULL,
    direction VARCHAR(8) NOT NULL CHECK (direction IN ('INBOUND', 'OUTBOUND')),
    priority VARCHAR(6) NOT NULL CHECK (priority IN ('NORMAL', 'URGENT')),
    status VARCHAR(10) NOT NULL CHECK (status IN ('QUEUED', 'PROCESSING', 'SENT', 'FAILED', 'RECEIVED')),
    cost NUMERIC(12, 2) NOT NULL CHECK (cost >= 0),
    failure_reason VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processing_at TIMESTAMPTZ,
    sent_at TIMESTAMPTZ,
    read_at TIMESTAMPTZ,
    CONSTRAINT chk_message_direction_status CHECK (
        (direction = 'INBOUND' AND status = 'RECEIVED' AND cost = 0)
        OR (direction = 'OUTBOUND' AND status <> 'RECEIVED')
    ),
    CONSTRAINT chk_message_failure CHECK (
        (status = 'FAILED' AND failure_reason IS NOT NULL)
        OR (status <> 'FAILED' AND failure_reason IS NULL)
    )
);

CREATE TABLE financial_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    type VARCHAR(16) NOT NULL CHECK (type IN ('CREDIT', 'DEBIT', 'LIMIT_ADJUSTMENT', 'PLAN_CONVERSION')),
    amount NUMERIC(12, 2) NOT NULL,
    balance_after NUMERIC(12, 2),
    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_auth_tokens_client_active ON auth_tokens (client_id, revoked, expires_at);
CREATE INDEX idx_conversations_client_updated ON conversations (client_id, updated_at DESC);
CREATE INDEX idx_messages_conversation_created ON messages (conversation_id, created_at DESC);
CREATE INDEX idx_messages_queue ON messages (priority, created_at) WHERE status IN ('QUEUED', 'PROCESSING');
CREATE INDEX idx_financial_transactions_client_created ON financial_transactions (client_id, created_at DESC);
