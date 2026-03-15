/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Admin
 */
package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.ProductModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductModelDAO extends DBContext {

    /**
     * Get all product models with category name
     */
    public List<ProductModel> getAll() {
        List<ProductModel> list = new ArrayList<>();
        String sql = "SELECT m.model_id, m.category_id, m.model_code, m.model_name, m.brand, " +
                     "       m.description, m.status, m.created_at, m.updated_at, " +
                     "       c.category_name " +
                     "FROM ProductModel m " +
                     "LEFT JOIN ProductCategory c ON m.category_id = c.category_id " +
                     "ORDER BY m.model_id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) { list.add(extractModelFromResultSet(rs)); }
            rs.close(); ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }


    public List<ProductModel> getModelsByCategoryId(int categoryId) {
        List<ProductModel> list = new ArrayList<>();

        String sql = """
            SELECT model_id,
                   category_id,
                   model_code,
                   model_name,
                   brand,
                   description,
                   status,
                   created_at,
                   updated_at
            FROM ProductModel
            WHERE category_id = ?
            ORDER BY model_id DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductModel m = new ProductModel();

                    m.setModelId(rs.getInt("model_id"));
                    m.setCategoryId(rs.getInt("category_id"));
                    m.setModelCode(rs.getString("model_code"));
                    m.setModelName(rs.getString("model_name"));
                    m.setBrand(rs.getString("brand"));
                    m.setDescription(rs.getString("description"));
                    m.setStatus(rs.getString("status"));

                    Timestamp created = rs.getTimestamp("created_at");
                    if (created != null) {
                        m.setCreatedAt(created.toLocalDateTime());
                    }

                    Timestamp updated = rs.getTimestamp("updated_at");
                    if (updated != null) {
                        m.setUpdatedAt(updated.toLocalDateTime());
                    }

                    list.add(m);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<ProductModel> getActiveModelsByCategoryId(int categoryId) {
        List<ProductModel> list = new ArrayList<>();

        String sql = """
            SELECT model_id,
                   category_id,
                   model_code,
                   model_name,
                   brand,
                   description,
                   status,
                   created_at,
                   updated_at
            FROM ProductModel
            WHERE category_id = ?
              AND status = 'ACTIVE'
            ORDER BY model_id DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductModel m = new ProductModel();

                    m.setModelId(rs.getInt("model_id"));
                    m.setCategoryId(rs.getInt("category_id"));
                    m.setModelCode(rs.getString("model_code"));
                    m.setModelName(rs.getString("model_name"));
                    m.setBrand(rs.getString("brand"));
                    m.setDescription(rs.getString("description"));
                    m.setStatus(rs.getString("status"));

                    Timestamp created = rs.getTimestamp("created_at");
                    if (created != null) {
                        m.setCreatedAt(created.toLocalDateTime());
                    }

                    Timestamp updated = rs.getTimestamp("updated_at");
                    if (updated != null) {
                        m.setUpdatedAt(updated.toLocalDateTime());
                    }

                    list.add(m);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public ProductModel getModelById(int id) {
        String sql = """
        SELECT * FROM ProductModel
        WHERE model_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProductModel m = new ProductModel();
                    m.setModelId(rs.getInt("model_id"));
                    m.setCategoryId(rs.getInt("category_id"));
                    m.setModelCode(rs.getString("model_code"));
                    m.setModelName(rs.getString("model_name"));
                    m.setBrand(rs.getString("brand"));
                    m.setDescription(rs.getString("description"));
                    m.setStatus(rs.getString("status"));
                    return m;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateModel(ProductModel m) {
        String sql = """
        UPDATE ProductModel
        SET model_code = ?,
            model_name = ?,
            brand = ?,
            description = ?,
            status = ?,
            updated_at = GETDATE()
        WHERE model_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, m.getModelCode());
            ps.setString(2, m.getModelName());
            ps.setString(3, m.getBrand());
            ps.setString(4, m.getDescription());
            ps.setString(5, m.getStatus());
            ps.setInt(6, m.getModelId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int countBrands(int categoryId) {
        String sql = """
            SELECT COUNT(DISTINCT brand)
            FROM ProductModel
            WHERE category_id = ?
            AND brand IS NOT NULL
            AND brand <> ''
        """;

        try 
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ps.setInt(1, categoryId);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Helper method to extract ProductModel from ResultSet
     */
    private ProductModel extractModelFromResultSet(ResultSet rs) throws SQLException {
        ProductModel model = new ProductModel();
        model.setModelId(rs.getInt("model_id"));
        model.setCategoryId(rs.getInt("category_id"));
        model.setModelCode(rs.getString("model_code"));
        model.setModelName(rs.getString("model_name"));
        model.setBrand(rs.getString("brand"));
        model.setDescription(rs.getString("description"));
        model.setStatus(rs.getString("status"));
        
        // JOIN data
        model.setCategoryName(rs.getString("category_name"));
        
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            model.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            model.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        return model;
    }

    public static void main(String[] args) {
        ProductModelDAO dao = new ProductModelDAO();
        System.out.println("Size = " + dao.getModelsByCategoryId(1).size());
    }

    public List<ProductModel> searchModels(int categoryId, String q, String status) {
        List<ProductModel> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT model_id, category_id, model_code, model_name, brand, description, status, updated_at
        FROM ProductModel
        WHERE category_id = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(categoryId);

        if (status != null && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND status = ? ");
            params.add(status);
        }

        if (q != null && !q.isBlank()) {
            sql.append("""
            AND (
                model_code LIKE ?
                OR model_name LIKE ?
                OR brand LIKE ?
            )
        """);
            String kw = "%" + q + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        sql.append(" ORDER BY model_id DESC ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProductModel m = new ProductModel();
                m.setModelId(rs.getInt("model_id"));
                m.setCategoryId(rs.getInt("category_id"));
                m.setModelCode(rs.getString("model_code"));
                m.setModelName(rs.getString("model_name"));
                m.setBrand(rs.getString("brand"));
                m.setDescription(rs.getString("description"));
                m.setStatus(rs.getString("status"));
                Timestamp ts = rs.getTimestamp("updated_at");
                if (ts != null) {
                    m.setUpdatedAt(ts.toLocalDateTime());
                } else {
                    m.setUpdatedAt(null);
                }

                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insertModel(ProductModel m) {
        String sql = """
        INSERT INTO ProductModel (category_id, model_code, model_name, brand, description, status, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, m.getCategoryId());
            ps.setString(2, m.getModelCode());
            ps.setString(3, m.getModelName());
            ps.setString(4, m.getBrand());
            ps.setString(5, m.getDescription());
            ps.setString(6, m.getStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int countModels(int categoryId, String q, String status) {
        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(*)
        FROM ProductModel
        WHERE category_id = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(categoryId);

        if (status != null && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND status = ? ");
            params.add(status);
        }

        if (q != null && !q.isBlank()) {
            sql.append("""
            AND (
                model_code LIKE ?
                OR model_name LIKE ?
                OR brand LIKE ?
            )
        """);
            String kw = "%" + q + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
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

    public List<ProductModel> searchModelsPaged(int categoryId, String q, String status, int page, int pageSize) {
        List<ProductModel> list = new ArrayList<>();

        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder("""
        SELECT model_id, category_id, model_code, model_name, brand, description, status, updated_at
        FROM ProductModel
        WHERE category_id = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(categoryId);

        if (status != null && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND status = ? ");
            params.add(status);
        }

        if (q != null && !q.isBlank()) {
            sql.append("""
            AND (
                model_code LIKE ?
                OR model_name LIKE ?
                OR brand LIKE ?
            )
        """);
            String kw = "%" + q + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        sql.append(" ORDER BY model_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        params.add(offset);
        params.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductModel m = new ProductModel();
                    m.setModelId(rs.getInt("model_id"));
                    m.setCategoryId(rs.getInt("category_id"));
                    m.setModelCode(rs.getString("model_code"));
                    m.setModelName(rs.getString("model_name"));
                    m.setBrand(rs.getString("brand"));
                    m.setDescription(rs.getString("description"));
                    m.setStatus(rs.getString("status"));

                    Timestamp ts = rs.getTimestamp("updated_at");
                    if (ts != null) {
                        m.setUpdatedAt(ts.toLocalDateTime());
                    }

                    list.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countActiveModels(String q) {

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(*)
        FROM ProductModel
        WHERE status = 'ACTIVE'
    """);

        List<Object> params = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            sql.append("""
            AND (
                model_code LIKE ?
                OR model_name LIKE ?
                OR brand LIKE ?
            )
        """);

            String kw = "%" + q + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<ProductModel> searchActiveModelsPaged(String q, int page, int pageSize) {

        List<ProductModel> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder("""
        SELECT model_id, category_id, model_code, model_name, brand, description, status, updated_at
        FROM ProductModel
        WHERE status = 'ACTIVE'
    """);

        List<Object> params = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            sql.append("""
            AND (
                model_code LIKE ?
                OR model_name LIKE ?
                OR brand LIKE ?
            )
        """);

            String kw = "%" + q + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        sql.append(" ORDER BY model_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        params.add(offset);
        params.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ProductModel m = new ProductModel();
                m.setModelId(rs.getInt("model_id"));
                m.setCategoryId(rs.getInt("category_id"));
                m.setModelCode(rs.getString("model_code"));
                m.setModelName(rs.getString("model_name"));
                m.setBrand(rs.getString("brand"));
                m.setDescription(rs.getString("description"));
                m.setStatus(rs.getString("status"));

                list.add(m);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
