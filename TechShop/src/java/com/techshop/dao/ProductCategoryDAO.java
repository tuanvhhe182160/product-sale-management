package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.ProductCategory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDAO extends DBContext {

    public List<ProductCategory> getAllCategories() {
        List<ProductCategory> list = new ArrayList<>();

        String sql = """
            SELECT category_id,
                   category_code,
                   category_name,
                   description,
                   status,
                   created_at,
                   updated_at
            FROM ProductCategory
            ORDER BY category_id DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProductCategory c = new ProductCategory();

                c.setCategoryId(rs.getInt("category_id"));
                c.setCategoryCode(rs.getString("category_code"));
                c.setCategoryName(rs.getString("category_name"));
                c.setDescription(rs.getString("description"));
                c.setStatus(rs.getString("status"));

                Timestamp created = rs.getTimestamp("created_at");
                if (created != null) {
                    c.setCreatedAt(created.toLocalDateTime());
                }

                Timestamp updated = rs.getTimestamp("updated_at");
                if (updated != null) {
                    c.setUpdatedAt(updated.toLocalDateTime());
                }

                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Lấy category đang ACTIVE
    public List<ProductCategory> getActiveCategories() {
        List<ProductCategory> list = new ArrayList<>();

        String sql = """
            SELECT category_id,
                   category_code,
                   category_name,
                   description,
                   status,
                   created_at,
                   updated_at
            FROM ProductCategory
            WHERE status = 'ACTIVE'
            ORDER BY category_id DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProductCategory c = new ProductCategory();

                c.setCategoryId(rs.getInt("category_id"));
                c.setCategoryCode(rs.getString("category_code"));
                c.setCategoryName(rs.getString("category_name"));
                c.setDescription(rs.getString("description"));
                c.setStatus(rs.getString("status"));

                Timestamp created = rs.getTimestamp("created_at");
                if (created != null) {
                    c.setCreatedAt(created.toLocalDateTime());
                }

                Timestamp updated = rs.getTimestamp("updated_at");
                if (updated != null) {
                    c.setUpdatedAt(updated.toLocalDateTime());
                }

                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int createCategory(ProductCategory c) {
        String sql = """
        INSERT INTO ProductCategory (category_code, category_name, description)
        OUTPUT INSERTED.category_id
        VALUES (?, ?, ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getCategoryCode());
            ps.setString(2, c.getCategoryName());

            if (c.getDescription() == null) {
                ps.setNull(3, java.sql.Types.NVARCHAR);
            } else {
                ps.setString(3, c.getDescription());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean existsByCode(String code) {
        String sql = "SELECT 1 FROM ProductCategory WHERE category_code = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ProductCategory getById(int id) {
        String sql = """
        SELECT category_id, category_code, category_name, description, status, created_at, updated_at
        FROM ProductCategory
        WHERE category_id = ?
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProductCategory c = new ProductCategory();
                    c.setCategoryId(rs.getInt("category_id"));
                    c.setCategoryCode(rs.getString("category_code"));
                    c.setCategoryName(rs.getString("category_name"));
                    c.setDescription(rs.getString("description"));
                    c.setStatus(rs.getString("status"));

                    Timestamp created = rs.getTimestamp("created_at");
                    if (created != null) {
                        c.setCreatedAt(created.toLocalDateTime());
                    }

                    Timestamp updated = rs.getTimestamp("updated_at");
                    if (updated != null) {
                        c.setUpdatedAt(updated.toLocalDateTime());
                    }

                    return c;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateCategory(ProductCategory c) {
        String sql = """
        UPDATE ProductCategory
        SET category_code = ?,
            category_name = ?,
            description = ?,
            status = ?,
            updated_at = GETDATE()
        WHERE category_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getCategoryCode());
            ps.setString(2, c.getCategoryName());

            if (c.getDescription() == null || c.getDescription().trim().isEmpty()) {
                ps.setNull(3, java.sql.Types.NVARCHAR);
            } else {
                ps.setString(3, c.getDescription());
            }

            ps.setString(4, c.getStatus());
            ps.setInt(5, c.getCategoryId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsByCodeExceptId(String code, int id) {
        String sql = "SELECT 1 FROM ProductCategory WHERE category_code = ? AND category_id <> ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        ProductCategoryDAO dao = new ProductCategoryDAO();
        System.out.println("dsadad");
        System.out.println("Size" + dao.getAllCategories().size());

    }
}
