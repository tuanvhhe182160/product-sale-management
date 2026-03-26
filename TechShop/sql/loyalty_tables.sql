-- Chạy script này trên database TechShopDB nếu chưa có bảng LoyaltyAccount và LoyaltyTransaction

-- Bảng tài khoản điểm thưởng
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'LoyaltyAccount')
BEGIN
    CREATE TABLE LoyaltyAccount (
        account_id      INT IDENTITY(1,1) PRIMARY KEY,
        customer_id     INT NOT NULL UNIQUE,
        total_points    INT NOT NULL DEFAULT 0,
        current_points  INT NOT NULL DEFAULT 0,
        created_at      DATETIME DEFAULT GETDATE(),
        updated_at      DATETIME NULL,
        CONSTRAINT FK_LoyaltyAccount_Customer 
            FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
    );
END

-- Bảng lịch sử giao dịch điểm
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'LoyaltyTransaction')
BEGIN
    CREATE TABLE LoyaltyTransaction (
        transaction_id   INT IDENTITY(1,1) PRIMARY KEY,
        account_id       INT NOT NULL,
        transaction_type VARCHAR(20) NOT NULL, -- EARN, REDEEM, EXPIRE, ADJUST
        points           INT NOT NULL,
        reference_id     INT NULL,             -- Invoice ID
        description      NVARCHAR(500) NULL,
        transaction_date DATETIME DEFAULT GETDATE(),
        CONSTRAINT FK_LoyaltyTransaction_Account 
            FOREIGN KEY (account_id) REFERENCES LoyaltyAccount(account_id)
    );
END

-- Tạo LoyaltyAccount cho tất cả khách hàng hiện có mà chưa có account
INSERT INTO LoyaltyAccount (customer_id, total_points, current_points, created_at)
SELECT c.customer_id, 0, 0, GETDATE()
FROM Customer c
WHERE NOT EXISTS (
    SELECT 1 FROM LoyaltyAccount la WHERE la.customer_id = c.customer_id
);

PRINT N'Loyalty tables created/verified successfully.';
