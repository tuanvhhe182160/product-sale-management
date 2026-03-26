/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.model;

import java.math.BigDecimal;

/**
 *
 * @author Admin
 */
public class InvoiceCustomerForm implements java.io.Serializable {

    // ── Thông tin khách hàng → bảng Customer ──
    private String customerId;   // null = khách mới, có giá trị = khách cũ
    private String phone;
    private String fullName;
    private String email;
    private String address;

    // ── Thông tin thanh toán → bảng Invoice ──
    private String     paymentMethod;   // CASH | CARD | TRANSFER | MIXED
    private BigDecimal discountAmount;  // Invoice.discount_amount
    private String     note;            // Invoice.note

    // ── Runtime flag ──
    private boolean saveCustomer; // true = lưu khách mới vào DB khi thanh toán
    private int redeemPoints;     // số điểm khách muốn đổi

    public InvoiceCustomerForm() {
        this.paymentMethod  = "CASH";
        this.discountAmount = BigDecimal.ZERO;
        this.saveCustomer   = false;
        this.redeemPoints   = 0;
    }

    // ── Getters ──
    public String     getCustomerId()     { return customerId;     }
    public String     getPhone()          { return phone;          }
    public String     getFullName()       { return fullName;       }
    public String     getEmail()          { return email;          }
    public String     getAddress()        { return address;        }
    public String     getPaymentMethod()  { return paymentMethod;  }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public String     getNote()           { return note;           }
    public boolean    isSaveCustomer()    { return saveCustomer;   }
    public int        getRedeemPoints()   { return redeemPoints;   }

    // ── Setters ──
    public void setCustomerId(String customerId)       { this.customerId     = customerId;     }
    public void setPhone(String phone)                 { this.phone          = phone;          }
    public void setFullName(String fullName)           { this.fullName       = fullName;       }
    public void setEmail(String email)                 { this.email          = email;          }
    public void setAddress(String address)             { this.address        = address;        }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod  = paymentMethod;  }
    public void setDiscountAmount(BigDecimal d)        { this.discountAmount = d;              }
    public void setNote(String note)                   { this.note           = note;           }
    public void setSaveCustomer(boolean saveCustomer)  { this.saveCustomer   = saveCustomer;   }
    public void setRedeemPoints(int redeemPoints)      { this.redeemPoints   = redeemPoints;   }
}