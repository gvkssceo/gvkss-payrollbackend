CREATE TABLE taxes (
    id BIGSERIAL PRIMARY KEY,
    tax_type VARCHAR(50) NOT NULL,
    tax_level VARCHAR(20) NOT NULL,
    state_code VARCHAR(2),
    tax_id VARCHAR(50),
    description VARCHAR(500),
    deposit_frequency VARCHAR(50),
    rate DECIMAL(5,4),
    effective_date DATE,
    status VARCHAR(50),
    needs_action BOOLEAN DEFAULT FALSE,
    action_required VARCHAR(200),
    company_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_taxes_company_id ON taxes(company_id);
CREATE INDEX idx_taxes_tax_level ON taxes(tax_level);
CREATE INDEX idx_taxes_state_code ON taxes(state_code);
CREATE INDEX idx_taxes_needs_action ON taxes(needs_action);
CREATE INDEX idx_taxes_is_active ON taxes(is_active); 