```mermaid

erDiagram
    BRANCH ||--o{ ACCOUNT : "has"
    CUSTOMER ||--o{ ACCOUNT : "owns"

    BRANCH {
        bigint branch_id PK
        varchar name
        varchar ifsc_code UK
        varchar address
        varchar city
        varchar state
        varchar postal_code
    }

    CUSTOMER {
        bigint customer_id PK
        varchar first_name
        varchar last_name
        varchar email UK
        varchar phone_number
        date date_of_birth
        varchar address
        timestamp created_at
    }

    ACCOUNT {
        bigint account_id PK
        bigint customer_id FK
        bigint branch_id FK
        varchar account_number UK
        decimal balance
        varchar status
        timestamp created_at
    }

```

**Constraints**
- chk_account_balance: `balance >= 0`
- chk_account_status: `status is one of ACTIVE, INACTIVE, FROZEN, CLOSED`
- fk_account_customer: `account.customer_id references customer(customer_id)`
- fk_account_branch: `account.branch_id references branch(branch_id)`
- UNIQUE: `branch.ifsc_code, customer.email, account.account_number`
- Defaults: `balance is 0.00, status is ACTIVE, and created_at is CURRENT_TIMESTAMP`