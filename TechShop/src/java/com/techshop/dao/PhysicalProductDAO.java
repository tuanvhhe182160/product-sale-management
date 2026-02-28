package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.PhysicalProduct;
import com.techshop.model.ProductVariant;
import java.sql.*;
import java.util.List;

//TODO: Implement methods
public class PhysicalProductDAO extends DBContext {
    public List<PhysicalProduct> getAll() {
        return null;
    }
    
    public PhysicalProduct getById(int physicalId) {
        return null;
    }
    
    public List<PhysicalProduct> getByVariantId(int variantId) {
        return null;
    }

    public List<PhysicalProduct> getByBranchId(int branchId) {
        return null;
    }
}
