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

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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

    // Lấy category đang ACTIVE (khuyến nghị dùng cho Admin view)
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

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
    
    public static void main(String[] args) {
        ProductCategoryDAO dao = new ProductCategoryDAO();
        System.out.println("dsadad");
        System.out.println("Size"+dao.getAllCategories().size());
                
    }
}
