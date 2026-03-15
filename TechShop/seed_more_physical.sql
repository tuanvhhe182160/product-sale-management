USE TechShopDB;
GO

-- Thêm nhiều PhysicalProduct cho mỗi Variant (tất cả ở branch 1)
-- Variant 1: iPhone 15 Pro Max 256GB Black (đã có physical_id 1,2)
INSERT INTO PhysicalProduct (variant_id, branch_id, imei, serial_number, status, import_date) VALUES
(1, 1, 'IMEI-V1-003', 'SN-V1-003', 'IN_STOCK', GETDATE()),
(1, 1, 'IMEI-V1-004', 'SN-V1-004', 'IN_STOCK', GETDATE()),
(1, 1, 'IMEI-V1-005', 'SN-V1-005', 'IN_STOCK', GETDATE());

-- Variant 2: iPhone 15 Pro Max 512GB Blue (đã có physical_id 3)
INSERT INTO PhysicalProduct (variant_id, branch_id, imei, serial_number, status, import_date) VALUES
(2, 1, 'IMEI-V2-002', 'SN-V2-002', 'IN_STOCK', GETDATE()),
(2, 1, 'IMEI-V2-003', 'SN-V2-003', 'IN_STOCK', GETDATE()),
(2, 1, 'IMEI-V2-004', 'SN-V2-004', 'IN_STOCK', GETDATE());

-- Variant 3: Samsung S24 Ultra 256GB Gray (đã có physical_id 4)
INSERT INTO PhysicalProduct (variant_id, branch_id, imei, serial_number, status, import_date) VALUES
(3, 1, 'IMEI-V3-002', 'SN-V3-002', 'IN_STOCK', GETDATE()),
(3, 1, 'IMEI-V3-003', 'SN-V3-003', 'IN_STOCK', GETDATE()),
(3, 1, 'IMEI-V3-004', 'SN-V3-004', 'IN_STOCK', GETDATE()),
(3, 1, 'IMEI-V3-005', 'SN-V3-005', 'IN_STOCK', GETDATE());

-- Variant 4: Redmi K60 8GB 256GB Black (đã có physical_id 5 branch1, 6 branch2)
INSERT INTO PhysicalProduct (variant_id, branch_id, imei, serial_number, status, import_date) VALUES
(4, 1, 'IMEI-V4-003', 'SN-V4-003', 'IN_STOCK', GETDATE()),
(4, 1, 'IMEI-V4-004', 'SN-V4-004', 'IN_STOCK', GETDATE()),
(4, 1, 'IMEI-V4-005', 'SN-V4-005', 'IN_STOCK', GETDATE()),
(4, 1, 'IMEI-V4-006', 'SN-V4-006', 'IN_STOCK', GETDATE()),
(4, 1, 'IMEI-V4-007', 'SN-V4-007', 'IN_STOCK', GETDATE());

-- Variant 5: MacBook Pro 14 M3 512GB (đã có physical_id 7)
INSERT INTO PhysicalProduct (variant_id, branch_id, imei, serial_number, status, import_date) VALUES
(5, 1, 'IMEI-V5-002', 'SN-V5-002', 'IN_STOCK', GETDATE()),
(5, 1, 'IMEI-V5-003', 'SN-V5-003', 'IN_STOCK', GETDATE());

PRINT 'Added more PhysicalProducts successfully!';
GO
