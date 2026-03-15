/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountingPeriod {

    private int periodId;
    private int branchId;
    private int periodMonth;
    private int periodYear;

    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private int totalInvoices;

    private Integer closedBy;
    private LocalDateTime closedAt;

    private String status;

    public AccountingPeriod() {
    }

    public AccountingPeriod(int periodId, int branchId, int periodMonth, int periodYear,
                            BigDecimal totalRevenue, BigDecimal totalProfit, int totalInvoices,
                            Integer closedBy, LocalDateTime closedAt, String status) {
        this.periodId = periodId;
        this.branchId = branchId;
        this.periodMonth = periodMonth;
        this.periodYear = periodYear;
        this.totalRevenue = totalRevenue;
        this.totalProfit = totalProfit;
        this.totalInvoices = totalInvoices;
        this.closedBy = closedBy;
        this.closedAt = closedAt;
        this.status = status;
    }

    public int getPeriodId() {
        return periodId;
    }

    public void setPeriodId(int periodId) {
        this.periodId = periodId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getPeriodMonth() {
        return periodMonth;
    }

    public void setPeriodMonth(int periodMonth) {
        this.periodMonth = periodMonth;
    }

    public int getPeriodYear() {
        return periodYear;
    }

    public void setPeriodYear(int periodYear) {
        this.periodYear = periodYear;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public int getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(int totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public Integer getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(Integer closedBy) {
        this.closedBy = closedBy;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}