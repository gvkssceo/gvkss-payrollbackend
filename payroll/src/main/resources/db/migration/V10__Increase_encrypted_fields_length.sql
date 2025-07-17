ALTER TABLE employees
    ALTER COLUMN ssn_encrypted TYPE text,
    ALTER COLUMN ssn_encrypted_iv TYPE text,
    ALTER COLUMN bank_account_number_encrypted TYPE text,
    ALTER COLUMN bank_account_number_encrypted_iv TYPE text,
    ALTER COLUMN bank_routing_number_encrypted TYPE text,
    ALTER COLUMN bank_routing_number_encrypted_iv TYPE text; 