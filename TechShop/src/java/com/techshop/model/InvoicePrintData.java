package com.techshop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dữ liệu đầy đủ của 1 hóa đơn để render trang in.
 * Được InvoicePrintDAO tổng hợp từ Invoice + InvoiceItem + Customer + User + Branch.
 */
public class InvoicePrintData {

    // ── Thông tin hóa đơn (Invoice) ──
    private int           invoiceId;
    private String        invoiceCode;
    private LocalDateTime invoiceDate;
    private BigDecimal    totalAmount;
    private BigDecimal    discountAmount;
    private BigDecimal    finalAmount;
    private String        paymentMethod;
    private String        note;

    // ── Thông tin khách hàng (Customer) ──
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;

    // ── Thông tin cashier + chi nhánh ──
    private String cashierName;
    private String branchName;
    private String branchAddress;
    private String branchPhone;

    // ── Danh sách sản phẩm (InvoiceItem join ProductVariant) ──
    private List<InvoicePrintItem> items;

    // ── Inner class: 1 dòng sản phẩm trên hóa đơn ──
    public static class InvoicePrintItem {
        private String     variantName;
        private String     imei;
        private String     sku;
        private BigDecimal unitPrice;
        private int        warrantyMonths;

        public String     getVariantName()   { return variantName;   }
        public String     getImei()          { return imei;          }
        public String     getSku()           { return sku;           }
        public BigDecimal getUnitPrice()     { return unitPrice;     }
        public int        getWarrantyMonths(){ return warrantyMonths; }

        public void setVariantName(String v)    { this.variantName   = v; }
        public void setImei(String i)           { this.imei          = i; }
        public void setSku(String s)            { this.sku           = s; }
        public void setUnitPrice(BigDecimal p)  { this.unitPrice     = p; }
        public void setWarrantyMonths(int w)    { this.warrantyMonths = w; }
    }

    // ── Getters / Setters ──
    public int           getInvoiceId()       { return invoiceId;       }
    public String        getInvoiceCode()     { return invoiceCode;     }
    public LocalDateTime getInvoiceDate()     { return invoiceDate;     }
    public String getInvoiceDateFormatted() {
        if (invoiceDate == null) return "";
        return invoiceDate.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
    }
    public BigDecimal    getTotalAmount()     { return totalAmount;     }
    public BigDecimal    getDiscountAmount()  { return discountAmount;  }
    public BigDecimal    getFinalAmount()     { return finalAmount;     }
    public String        getPaymentMethod()  { return paymentMethod;   }
    public String        getNote()           { return note;            }
    public String        getCustomerName()   { return customerName;    }
    public String        getCustomerPhone()  { return customerPhone;   }
    public String        getCustomerEmail()  { return customerEmail;   }
    public String        getCustomerAddress(){ return customerAddress; }
    public String        getCashierName()    { return cashierName;     }
    public String        getBranchName()     { return branchName;      }
    public String        getBranchAddress()  { return branchAddress;   }
    public String        getBranchPhone()    { return branchPhone;     }
    public List<InvoicePrintItem> getItems() { return items;           }

    public void setInvoiceId(int v)                       { invoiceId       = v; }
    public void setInvoiceCode(String v)                  { invoiceCode     = v; }
    public void setInvoiceDate(LocalDateTime v)           { invoiceDate     = v; }
    public void setTotalAmount(BigDecimal v)              { totalAmount     = v; }
    public void setDiscountAmount(BigDecimal v)           { discountAmount  = v; }
    public void setFinalAmount(BigDecimal v)              { finalAmount     = v; }
    public void setPaymentMethod(String v)                { paymentMethod   = v; }
    public void setNote(String v)                         { note            = v; }
    public void setCustomerName(String v)                 { customerName    = v; }
    public void setCustomerPhone(String v)                { customerPhone   = v; }
    public void setCustomerEmail(String v)                { customerEmail   = v; }
    public void setCustomerAddress(String v)              { customerAddress = v; }
    public void setCashierName(String v)                  { cashierName     = v; }
    public void setBranchName(String v)                   { branchName      = v; }
    public void setBranchAddress(String v)                { branchAddress   = v; }
    public void setBranchPhone(String v)                  { branchPhone     = v; }
    public void setItems(List<InvoicePrintItem> v)        { items           = v; }
}