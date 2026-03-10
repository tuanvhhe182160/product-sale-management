package com.techshop.model;

public class FinancialReportItem {
    private String period; // Có thể là ngày (2026-03-03), tháng (2026-03), hoặc năm (2026)
    private int totalOrders;
    private double totalRevenue;
    private double totalCost;
    private double totalProfit;

    // Constructors
    public FinancialReportItem() {}

    public FinancialReportItem(String period, int totalOrders, double totalRevenue, double totalCost, double totalProfit) {
        this.period = period;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.totalCost = totalCost;
        this.totalProfit = totalProfit;
    }

    // Getter & Setter
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public double getTotalProfit() { return totalProfit; }
    public void setTotalProfit(double totalProfit) { this.totalProfit = totalProfit; }
}