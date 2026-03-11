package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.BranchInventoryItem;
import com.techshop.model.PhysicalProduct;
import com.techshop.model.ProductVariant;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PhysicalProductDAO extends DBContext {

    private static final String BASE_SELECT_QUERY_STRING = "SELECT p.physical_id, p.variant_id, p.branch_id, "
            + "p.imei, p.serial_number, p.status, p.import_date, p.sale_date, p.created_at, p.updated_at, "
            + "v.variant_name, v.sku, b.branch_name "
            + "FROM PhysicalProduct p "
            + "LEFT JOIN ProductVariant v ON p.variant_id = v.variant_id "
            + "LEFT JOIN Branch b ON p.branch_id = b.branch_id ";

    public List<PhysicalProduct> getAll() {
        return getAll(null, null, null, null);
    }

    public List<PhysicalProduct> getAll(String search, Integer branchId, Integer variantId, String status) {
        List<PhysicalProduct> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT_QUERY_STRING);
        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            conditions.add("(p.imei LIKE ? OR p.serial_number LIKE ? OR v.variant_name LIKE ? OR v.sku LIKE ?)");
            String keyword = "%" + search.trim() + "%";
            parameters.add(keyword);
            parameters.add(keyword);
            parameters.add(keyword);
            parameters.add(keyword);
        }

        if (branchId != null && branchId > 0) {
            conditions.add("p.branch_id = ?");
            parameters.add(branchId);
        }

        if (variantId != null && variantId > 0) {
            conditions.add("p.variant_id = ?");
            parameters.add(variantId);
        }

        if (status != null && !status.trim().isEmpty()) {
            conditions.add("p.status = ?");
            parameters.add(status.trim());
        }

        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }

        sql.append("ORDER BY p.created_at ASC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPhysicalProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.getAll() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public PhysicalProduct getById(int physicalId) {
        String sql = BASE_SELECT_QUERY_STRING + "WHERE p.physical_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, physicalId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPhysicalProduct(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.getById() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<PhysicalProduct> getByVariantId(int variantId) {
        List<PhysicalProduct> list = new ArrayList<>();
        String sql = BASE_SELECT_QUERY_STRING + "WHERE p.variant_id = ? ORDER BY p.created_at ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, variantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPhysicalProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.getByVariantId() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public List<PhysicalProduct> getByBranchId(int branchId) {
        List<PhysicalProduct> list = new ArrayList<>();
        String sql = BASE_SELECT_QUERY_STRING + "WHERE p.branch_id = ? ORDER BY p.created_at ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPhysicalProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.getByBranchId() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public List<PhysicalProduct> getByBranchWithFilters(int branchId, String imei, String status,
                                                        Integer variantId,
                                                        java.time.LocalDate importDate,
                                                        int offset, int pageSize) {
        List<PhysicalProduct> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT_QUERY_STRING + "WHERE p.branch_id = ? ");
        List<Object> parameters = new ArrayList<>();
        parameters.add(branchId);

        if (imei != null && !imei.trim().isEmpty()) {
            sql.append("AND p.imei LIKE ? ");
            parameters.add("%" + imei.trim() + "%");
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND p.status = ? ");
            parameters.add(status.trim());
        }

        if (variantId != null && variantId > 0) {
            sql.append("AND p.variant_id = ? ");
            parameters.add(variantId);
        }

        if (importDate != null) {
            sql.append("AND CAST(p.import_date AS DATE) = ? ");
            parameters.add(Date.valueOf(importDate));
        }

        sql.append("ORDER BY p.created_at DESC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        parameters.add(offset);
        parameters.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPhysicalProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.getByBranchWithFilters() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public int countByBranchWithFilters(int branchId, String imei, String status,
                                        Integer variantId,
                                        java.time.LocalDate importDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM PhysicalProduct p "
                        + "LEFT JOIN ProductVariant v ON p.variant_id = v.variant_id "
                        + "WHERE p.branch_id = ? ");
        List<Object> parameters = new ArrayList<>();
        parameters.add(branchId);

        if (imei != null && !imei.trim().isEmpty()) {
            sql.append("AND p.imei LIKE ? ");
            parameters.add("%" + imei.trim() + "%");
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND p.status = ? ");
            parameters.add(status.trim());
        }

        if (variantId != null && variantId > 0) {
            sql.append("AND p.variant_id = ? ");
            parameters.add(variantId);
        }

        if (importDate != null) {
            sql.append("AND CAST(p.import_date AS DATE) = ? ");
            parameters.add(Date.valueOf(importDate));
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.countByBranchWithFilters() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public List<String> getDistinctStatusesByBranch(int branchId) {
        List<String> statuses = new ArrayList<>();
        String sql = "SELECT DISTINCT status FROM PhysicalProduct WHERE branch_id = ? AND status IS NOT NULL ORDER BY status";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    statuses.add(rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.getDistinctStatusesByBranch() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return statuses;
    }

    public List<ProductVariant> getVariantsByBranch(int branchId) {
        List<ProductVariant> variants = new ArrayList<>();
        String sql = "SELECT DISTINCT v.variant_id, v.variant_name, v.sku "
                + "FROM PhysicalProduct p "
                + "INNER JOIN ProductVariant v ON p.variant_id = v.variant_id "
                + "WHERE p.branch_id = ? "
                + "ORDER BY v.variant_name";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductVariant variant = new ProductVariant();
                    variant.setVariantId(rs.getInt("variant_id"));
                    variant.setVariantName(rs.getString("variant_name"));
                    variant.setSku(rs.getString("sku"));
                    variants.add(variant);
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.getVariantsByBranch() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return variants;
    }

    public List<PhysicalProduct> getByStatus(String status) {
        List<PhysicalProduct> list = new ArrayList<>();
        String sql = BASE_SELECT_QUERY_STRING + "WHERE p.status = ? ORDER BY p.created_at ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPhysicalProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.getByStatus() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public List<BranchInventoryItem> getInventoryLevelsByBranch(int branchId) {
        List<BranchInventoryItem> list = new ArrayList<>();
        String sql = "SELECT p.variant_id, v.sku, v.variant_name, COUNT(*) AS inventory_level "
                + "FROM PhysicalProduct p "
                + "INNER JOIN ProductVariant v ON p.variant_id = v.variant_id "
                + "WHERE p.branch_id = ? "
                + "GROUP BY p.variant_id, v.sku, v.variant_name "
                + "ORDER BY v.variant_name";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BranchInventoryItem item = new BranchInventoryItem();
                    item.setVariantId(rs.getInt("variant_id"));
                    item.setSku(rs.getString("sku"));
                    item.setVariantName(rs.getString("variant_name"));
                    item.setInventoryLevel(rs.getInt("inventory_level"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.getInventoryLevelsByBranch() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public int createPhysicalProduct(PhysicalProduct product) {
        String sql = "INSERT INTO PhysicalProduct (variant_id, branch_id, imei, serial_number, status, import_date, sale_date, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, product.getVariantId());
            ps.setInt(2, product.getBranchId());
            ps.setString(3, product.getImei());
            ps.setString(4, product.getSerialNumber());
            ps.setString(5, normalizeStatus(product.getStatus()));
            setNullableTimestamp(ps, 6, product.getImportDate());
            setNullableTimestamp(ps, 7, product.getSaleDate());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.createPhysicalProduct() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public boolean updatePhysicalProduct(PhysicalProduct product) {
        String sql = "UPDATE PhysicalProduct "
                + "SET variant_id = ?, branch_id = ?, imei = ?, serial_number = ?, status = ?, import_date = ?, sale_date = ?, updated_at = GETDATE() "
                + "WHERE physical_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, product.getVariantId());
            ps.setInt(2, product.getBranchId());
            ps.setString(3, product.getImei());
            ps.setString(4, product.getSerialNumber());
            ps.setString(5, normalizeStatus(product.getStatus()));
            setNullableTimestamp(ps, 6, product.getImportDate());
            setNullableTimestamp(ps, 7, product.getSaleDate());
            ps.setInt(8, product.getPhysicalId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.updatePhysicalProduct() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateStatus(int physicalId, String status) {
        String sql = "UPDATE PhysicalProduct SET status = ?, updated_at = GETDATE() WHERE physical_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, normalizeStatus(status));
            ps.setInt(2, physicalId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.updateStatus() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean isImeiExists(String imei) {
        return isImeiExists(imei, 0);
    }

    public boolean isImeiExists(String imei, int excludePhysicalId) {
        String sql = "SELECT COUNT(*) FROM PhysicalProduct WHERE imei = ? AND physical_id != ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, imei);
            ps.setInt(2, excludePhysicalId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.isImeiExists() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean isSerialNumberExists(String serialNumber) {
        return isSerialNumberExists(serialNumber, 0);
    }

    public boolean isSerialNumberExists(String serialNumber, int excludePhysicalId) {
        String sql = "SELECT COUNT(*) FROM PhysicalProduct WHERE serial_number = ? AND physical_id != ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, serialNumber);
            ps.setInt(2, excludePhysicalId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.isSerialNumberExists() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private PhysicalProduct mapPhysicalProduct(ResultSet rs) throws SQLException {
        PhysicalProduct product = new PhysicalProduct();
        product.setPhysicalId(rs.getInt("physical_id"));
        product.setVariantId(rs.getInt("variant_id"));
        product.setBranchId(rs.getInt("branch_id"));
        product.setImei(rs.getString("imei"));
        product.setSerialNumber(rs.getString("serial_number"));
        product.setStatus(rs.getString("status"));

        Timestamp importTs = rs.getTimestamp("import_date");
        if (importTs != null) {
            product.setImportDate(importTs.toLocalDateTime());
        }

        Timestamp saleTs = rs.getTimestamp("sale_date");
        if (saleTs != null) {
            product.setSaleDate(saleTs.toLocalDateTime());
        }

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            product.setCreatedAt(createdTs.toLocalDateTime());
        }

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            product.setUpdatedAt(updatedTs.toLocalDateTime());
        }

        product.setVariantName(rs.getString("variant_name"));
        product.setBranchName(rs.getString("branch_name"));
        product.setSku(rs.getString("sku"));

        return product;
    }

    private void setNullableTimestamp(PreparedStatement ps, int parameterIndex, java.time.LocalDateTime value) throws SQLException {
        if (value == null) {
            ps.setNull(parameterIndex, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(parameterIndex, Timestamp.valueOf(value));
        }
    }

    private String normalizeStatus(String status) {
        return (status == null || status.trim().isEmpty()) ? "IN_STOCK" : status.trim();
    }
}
