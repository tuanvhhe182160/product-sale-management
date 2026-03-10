package com.techshop.dao;

import com.techshop.dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class VariantAttributeDAO extends DBContext {

    // =====================================================
    // Get attributes by variant_id
    // =====================================================
    public Map<String, String> getAttributesByVariantId(int variantId) {

        Map<String, String> attributes = new HashMap<>();

        String sql = """
                     SELECT attribute_name, attribute_value
                     FROM VariantAttribute
                     WHERE variant_id = ?
                     """;

        try  
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, variantId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String name = rs.getString("attribute_name");
                String value = rs.getString("attribute_value");

                attributes.put(name, value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return attributes;
    }

    // =====================================================
    // Insert single attribute
    // =====================================================
    public void insertAttribute(int variantId, String name, String value) {

        String sql = """
                     INSERT INTO VariantAttribute
                     (variant_id, attribute_name, attribute_value)
                     VALUES (?, ?, ?)
                     """;

        try  
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, variantId);
            ps.setString(2, name);
            ps.setString(3, value);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // Delete all attributes of a variant
    // =====================================================
    public void deleteAttributesByVariantId(int variantId) {

        String sql = """
                     DELETE FROM VariantAttribute
                     WHERE variant_id = ?
                     """;

        try  
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, variantId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // Insert multiple attributes (recommended for form save)
    // =====================================================
    public void insertAttributes(int variantId, Map<String, String> attributes) {

        String sql = """
                     INSERT INTO VariantAttribute
                     (variant_id, attribute_name, attribute_value)
                     VALUES (?, ?, ?)
                     """;

        try 
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            for (Map.Entry<String, String> entry : attributes.entrySet()) {

                ps.setInt(1, variantId);
                ps.setString(2, entry.getKey());
                ps.setString(3, entry.getValue());

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}