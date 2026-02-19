/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.dao;

import com.techshop.dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductSearchDAO extends DBContext {

    // DTO nội bộ để JSP render (không cần tạo file model riêng)
    public static class ProductSearchRow {
        public int categoryId;
        public String categoryCode;
        public String categoryName;

        public int modelId;
        public String modelCode;
        public String modelName;

        public int variantId;
        public String sku;
        public String variantName;
        public String color;
        public String ram;
        public double price;
        public String status;
    }

    /**
     * type: ALL | SKU | CATEGORY | MODEL | VARIANT
     * q: keyword
     */
    public List<ProductSearchRow> search(String q, String type) {
        List<ProductSearchRow> list = new ArrayList<>();

        String keyword = (q == null) ? "" : q.trim();
        String t = (type == null) ? "ALL" : type.trim().toUpperCase();

        // nếu user không nhập gì -> trả list rỗng (cashier search theo yêu cầu)
        if (keyword.isEmpty()) return list;

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT
                c.category_id, c.category_code, c.category_name,
                m.model_id, m.model_code, m.model_name,
                v.variant_id, v.sku, v.variant_name, v.color, v.ram, v.price, v.status
            FROM ProductCategory c
            JOIN ProductModel m ON m.category_id = c.category_id
            JOIN ProductVariant v ON v.model_id = m.model_id
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        // ưu tiên SKU exact nếu ALL và keyword giống SKU (để cashier gõ SKU ra ngay)
        boolean looksLikeSku = keyword.length() >= 3; // bạn có thể sửa rule này
        if ("ALL".equals(t) && looksLikeSku) {
            sql.append(" AND (v.sku = ? OR (");
            params.add(keyword);

            sql.append("""
                  c.category_code LIKE ? OR c.category_name LIKE ?
               OR m.model_code LIKE ? OR m.model_name LIKE ?
               OR v.sku LIKE ? OR v.variant_name LIKE ?
               OR v.color LIKE ? OR v.ram LIKE ?
            """);

            String like = "%" + keyword + "%";
            params.add(like); params.add(like);
            params.add(like); params.add(like);
            params.add(like); params.add(like);
            params.add(like); params.add(like);

            sql.append(" )) ");
        } else {
            switch (t) {
                case "SKU" -> {
                    sql.append(" AND v.sku = ? ");
                    params.add(keyword);
                }
                case "CATEGORY" -> {
                    sql.append(" AND (c.category_code LIKE ? OR c.category_name LIKE ?) ");
                    String like = "%" + keyword + "%";
                    params.add(like); params.add(like);
                }
                case "MODEL" -> {
                    sql.append(" AND (m.model_code LIKE ? OR m.model_name LIKE ?) ");
                    String like = "%" + keyword + "%";
                    params.add(like); params.add(like);
                }
                case "VARIANT" -> {
                    sql.append(" AND (v.sku LIKE ? OR v.variant_name LIKE ? OR v.color LIKE ? OR v.ram LIKE ?) ");
                    String like = "%" + keyword + "%";
                    params.add(like); params.add(like); params.add(like); params.add(like);
                }
                default -> { // ALL
                    sql.append("""
                        AND (
                               c.category_code LIKE ? OR c.category_name LIKE ?
                            OR m.model_code LIKE ? OR m.model_name LIKE ?
                            OR v.sku LIKE ? OR v.variant_name LIKE ?
                            OR v.color LIKE ? OR v.ram LIKE ?
                        )
                    """);
                    String like = "%" + keyword + "%";
                    params.add(like); params.add(like);
                    params.add(like); params.add(like);
                    params.add(like); params.add(like);
                    params.add(like); params.add(like);
                }
            }
        }

        sql.append(" ORDER BY m.model_name, v.variant_name ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductSearchRow row = new ProductSearchRow();
                    row.categoryId = rs.getInt("category_id");
                    row.categoryCode = rs.getString("category_code");
                    row.categoryName = rs.getString("category_name");

                    row.modelId = rs.getInt("model_id");
                    row.modelCode = rs.getString("model_code");
                    row.modelName = rs.getString("model_name");

                    row.variantId = rs.getInt("variant_id");
                    row.sku = rs.getString("sku");
                    row.variantName = rs.getString("variant_name");
                    row.color = rs.getString("color");
                    row.ram = rs.getString("ram");
                    row.price = rs.getDouble("price");
                    row.status = rs.getString("status");

                    list.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
