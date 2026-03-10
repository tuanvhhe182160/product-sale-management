package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.ProductModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ProductModel table Member 4's work - adapted for
 * Dashboard integration
 *
 * @author Member 4
 * @version 1.0
 */
public class ProductModelDAOTest extends DBContext {

    /**
     * Get all product models with category name
     */
    public List<ProductModel> getAll() {
        List<ProductModel> list = new ArrayList<>();
        String sql = "SELECT m.model_id, m.category_id, m.model_code, m.model_name, m.brand, "
                + "       m.description, m.status, m.created_at, m.updated_at, "
                + "       c.category_name "
                + "FROM ProductModel m "
                + "LEFT JOIN ProductCategory c ON m.category_id = c.category_id "
                + "ORDER BY m.model_id";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ProductModel model = extractModelFromResultSet(rs);
                list.add(model);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("ProductModelDAO.getAll() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Get model by ID
     */
    public ProductModel getById(int id) {
        String sql = "SELECT m.model_id, m.category_id, m.model_code, m.model_name, m.brand, "
                + "       m.description, m.status, m.created_at, m.updated_at, "
                + "       c.category_name "
                + "FROM ProductModel m "
                + "LEFT JOIN ProductCategory c ON m.category_id = c.category_id "
                + "WHERE m.model_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ProductModel model = extractModelFromResultSet(rs);
                rs.close();
                ps.close();
                return model;
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("ProductModelDAO.getById() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Insert new model
     */
    public boolean insert(ProductModel model) {
        String sql = "INSERT INTO ProductModel (category_id, model_code, model_name, brand, description, status, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, model.getCategoryId());
            ps.setString(2, model.getModelCode());
            ps.setString(3, model.getModelName());
            ps.setString(4, model.getBrand());
            ps.setString(5, model.getDescription());
            ps.setString(6, model.getStatus());

            int rowsAffected = ps.executeUpdate();
            ps.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("ProductModelDAO.insert() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update existing model
     */
    public boolean update(ProductModel model) {
        String sql = "UPDATE ProductModel "
                + "SET category_id = ?, model_code = ?, model_name = ?, brand = ?, description = ?, status = ?, updated_at = GETDATE() "
                + "WHERE model_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, model.getCategoryId());
            ps.setString(2, model.getModelCode());
            ps.setString(3, model.getModelName());
            ps.setString(4, model.getBrand());
            ps.setString(5, model.getDescription());
            ps.setString(6, model.getStatus());
            ps.setInt(7, model.getModelId());

            int rowsAffected = ps.executeUpdate();
            ps.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("ProductModelDAO.update() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

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

}
