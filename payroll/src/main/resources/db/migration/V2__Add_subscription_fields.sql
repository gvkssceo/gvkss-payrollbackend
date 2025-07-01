-- Add missing fields to company_subscriptions table
DO $$
BEGIN
    -- Add billing_details column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'company_subscriptions' AND column_name = 'billing_details') THEN
        ALTER TABLE company_subscriptions ADD COLUMN billing_details JSONB DEFAULT '{}';
    END IF;
    
    -- Add stripe_subscription_id column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'company_subscriptions' AND column_name = 'stripe_subscription_id') THEN
        ALTER TABLE company_subscriptions ADD COLUMN stripe_subscription_id VARCHAR(255);
    END IF;
    
    -- Add stripe_customer_id column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'company_subscriptions' AND column_name = 'stripe_customer_id') THEN
        ALTER TABLE company_subscriptions ADD COLUMN stripe_customer_id VARCHAR(255);
    END IF;
    
    -- Add trial_ends_at column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'company_subscriptions' AND column_name = 'trial_ends_at') THEN
        ALTER TABLE company_subscriptions ADD COLUMN trial_ends_at TIMESTAMP;
    END IF;
    
    -- Add cancelled_at column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'company_subscriptions' AND column_name = 'cancelled_at') THEN
        ALTER TABLE company_subscriptions ADD COLUMN cancelled_at TIMESTAMP;
    END IF;
END $$;

-- Add indexes for new fields
CREATE INDEX IF NOT EXISTS idx_company_subscriptions_stripe_subscription_id ON company_subscriptions(stripe_subscription_id);
CREATE INDEX IF NOT EXISTS idx_company_subscriptions_stripe_customer_id ON company_subscriptions(stripe_customer_id);
CREATE INDEX IF NOT EXISTS idx_company_subscriptions_trial_ends_at ON company_subscriptions(trial_ends_at); 