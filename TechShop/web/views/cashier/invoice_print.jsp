<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"      prefix="c"   %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"       prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"  %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <title>Hóa đơn ${invoice.invoiceCode}</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Courier New', monospace;
            font-size: 12px;
            background: #f5f5f5;
            display: flex;
            justify-content: center;
            padding: 20px;
        }

        /* Khổ giấy 80mm — chuẩn máy in nhiệt POS */
        .receipt {
            width: 302px;
            background: #fff;
            padding: 16px 14px;
            box-shadow: 0 2px 8px rgba(0,0,0,.12);
        }

        /* ── Header ── */
        .brand {
            text-align: center;
            margin-bottom: 10px;
        }
        .brand-name {
            font-size: 18px;
            font-weight: 700;
            letter-spacing: 2px;
        }
        .brand-sub {
            font-size: 10px;
            color: #555;
            margin-top: 2px;
        }

        /* ── Divider ── */
        .divider {
            border: none;
            border-top: 1px dashed #bbb;
            margin: 8px 0;
        }
        .divider-solid {
            border: none;
            border-top: 1px solid #333;
            margin: 8px 0;
        }

        /* ── Tiêu đề hóa đơn ── */
        .invoice-title {
            text-align: center;
            font-size: 13px;
            font-weight: 700;
            letter-spacing: 1px;
            margin: 6px 0 2px;
        }
        .invoice-code {
            text-align: center;
            font-size: 11px;
            color: #555;
        }

        /* ── Thông tin 2 cột ── */
        .info-row {
            display: flex;
            justify-content: space-between;
            margin: 3px 0;
            font-size: 11px;
        }
        .info-label { color: #555; }
        .info-value { font-weight: 600; text-align: right; max-width: 55%; }

        /* ── Bảng sản phẩm ── */
        .items-header {
            display: flex;
            justify-content: space-between;
            font-size: 10px;
            font-weight: 700;
            color: #333;
            margin: 4px 0 2px;
        }
        .item-row {
            margin: 5px 0;
        }
        .item-name {
            font-size: 11px;
            font-weight: 600;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        .item-detail {
            display: flex;
            justify-content: space-between;
            font-size: 10px;
            color: #555;
            margin-top: 1px;
        }
        .item-price {
            font-weight: 700;
            font-size: 11px;
            color: #111;
        }

        /* ── Tổng tiền ── */
        .total-section { margin-top: 6px; }
        .total-row {
            display: flex;
            justify-content: space-between;
            font-size: 11px;
            margin: 3px 0;
        }
        .total-row.grand {
            font-size: 14px;
            font-weight: 700;
            margin-top: 6px;
        }
        .total-row.grand span:last-child { color: #0d6efd; }

        /* ── Phương thức thanh toán ── */
        .payment-badge {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 10px;
            font-weight: 700;
        }
        .pm-CASH     { background: #d1fae5; color: #065f46; }
        .pm-CARD     { background: #dbeafe; color: #1e40af; }
        .pm-TRANSFER { background: #fef9c3; color: #92400e; }
        .pm-MIXED    { background: #f3e8ff; color: #6b21a8; }

        /* ── Ghi chú + bảo hành ── */
        .note-box {
            background: #f8f8f8;
            border: 1px dashed #ccc;
            border-radius: 4px;
            padding: 5px 8px;
            font-size: 10px;
            color: #444;
            margin: 6px 0;
        }
        .warranty-line {
            font-size: 10px;
            color: #666;
        }

        /* ── Footer ── */
        .footer {
            text-align: center;
            font-size: 10px;
            color: #888;
            margin-top: 10px;
            line-height: 1.6;
        }

        /* ── Nút in + quay lại (ẩn khi in) ── */
        .action-bar {
            display: flex;
            gap: 8px;
            justify-content: center;
            margin-top: 16px;
        }
        .btn-print {
            background: #0d6efd;
            color: #fff;
            border: none;
            padding: 8px 20px;
            border-radius: 6px;
            font-size: 13px;
            cursor: pointer;
            font-weight: 600;
        }
        .btn-back {
            background: #6c757d;
            color: #fff;
            border: none;
            padding: 8px 20px;
            border-radius: 6px;
            font-size: 13px;
            cursor: pointer;
        }

        @media print {
            body { background: #fff; padding: 0; }
            .receipt { box-shadow: none; }
            .action-bar { display: none; }
        }
    </style>
</head>
<body>

<div>
    <!-- ── Hóa đơn ── -->
    <div class="receipt" id="receiptArea">

        <!-- Header: tên cửa hàng -->
        <div class="brand">
            <div class="brand-name">TECHSHOP</div>
            <div class="brand-sub">${invoice.branchName}</div>
            <c:if test="${not empty invoice.branchAddress}">
                <div class="brand-sub">${invoice.branchAddress}</div>
            </c:if>
            <c:if test="${not empty invoice.branchPhone}">
                <div class="brand-sub">ĐT: ${invoice.branchPhone}</div>
            </c:if>
        </div>

        <hr class="divider-solid"/>

        <div class="invoice-title">HÓA ĐƠN BÁN HÀNG</div>
        <div class="invoice-code">${invoice.invoiceCode}</div>

        <hr class="divider"/>

        <!-- Thông tin hóa đơn -->
        <div class="info-row">
            <span class="info-label">Ngày:</span>
            <span class="info-value">${invoice.invoiceDateFormatted}</span>
        </div>
        <div class="info-row">
            <span class="info-label">Thu ngân:</span>
            <span class="info-value">${invoice.cashierName}</span>
        </div>

        <hr class="divider"/>

        <!-- Thông tin khách hàng -->
        <div class="info-row">
            <span class="info-label">Khách hàng:</span>
            <span class="info-value">${invoice.customerName}</span>
        </div>
        <div class="info-row">
            <span class="info-label">SĐT:</span>
            <span class="info-value">${invoice.customerPhone}</span>
        </div>
        <c:if test="${not empty invoice.customerAddress}">
            <div class="info-row">
                <span class="info-label">Địa chỉ:</span>
                <span class="info-value">${invoice.customerAddress}</span>
            </div>
        </c:if>

        <hr class="divider"/>

        <!-- Danh sách sản phẩm -->
        <div class="items-header">
            <span>SẢN PHẨM</span>
            <span>THÀNH TIỀN</span>
        </div>
        <hr class="divider"/>

        <c:forEach var="item" items="${invoice.items}" varStatus="st">
            <div class="item-row">
                <div class="item-name">${st.index + 1}. ${item.variantName}</div>
                <div class="item-detail">
                    <span>IMEI: ${item.imei}</span>
                    <span class="item-price">
                        <fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true"/>đ
                    </span>
                </div>
                <div class="warranty-line">BH: ${item.warrantyMonths} tháng</div>
            </div>
            <c:if test="${!st.last}"><hr class="divider"/></c:if>
        </c:forEach>

        <hr class="divider-solid"/>

        <!-- Tổng tiền -->
        <div class="total-section">
            <div class="total-row">
                <span>Tổng tiền hàng:</span>
                <span><fmt:formatNumber value="${invoice.totalAmount}" type="number" groupingUsed="true"/>đ</span>
            </div>
            <c:if test="${invoice.discountAmount > 0}">
                <div class="total-row">
                    <span>Giảm giá:</span>
                    <span>- <fmt:formatNumber value="${invoice.discountAmount}" type="number" groupingUsed="true"/>đ</span>
                </div>
            </c:if>
            <div class="total-row grand">
                <span>KHÁCH TRẢ:</span>
                <span><fmt:formatNumber value="${invoice.finalAmount}" type="number" groupingUsed="true"/>đ</span>
            </div>
        </div>

        <hr class="divider"/>

        <!-- Phương thức thanh toán -->
        <div class="info-row" style="align-items:center;">
            <span class="info-label">Thanh toán:</span>
            <c:choose>
                <c:when test="${invoice.paymentMethod == 'CASH'}">
                    <span class="payment-badge pm-CASH">💵 Tiền mặt</span>
                </c:when>
                <c:when test="${invoice.paymentMethod == 'TRANSFER'}">
                    <span class="payment-badge pm-TRANSFER">📱 Chuyển khoản</span>
                </c:when>
                <c:when test="${invoice.paymentMethod == 'CARD'}">
                    <span class="payment-badge pm-CARD">💳 Thẻ ngân hàng</span>
                </c:when>
                <c:otherwise>
                    <span class="payment-badge pm-MIXED">🔀 Kết hợp</span>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Ghi chú -->
        <c:if test="${not empty invoice.note}">
            <div class="note-box">Ghi chú: ${invoice.note}</div>
        </c:if>

        <hr class="divider"/>

        <!-- Footer -->
        <div class="footer">
            <div>Cảm ơn quý khách đã mua hàng!</div>
            <div>Vui lòng giữ hóa đơn để được bảo hành.</div>
            <div style="margin-top:4px;font-weight:600;">${invoice.invoiceCode}</div>
        </div>
    </div>

    <!-- Nút thao tác (ẩn khi in) -->
    <div class="action-bar">
        <button class="btn-print" onclick="window.print()">
            🖨️ In hóa đơn
        </button>
        <button class="btn-back" onclick="window.location.href='${pageContext.request.contextPath}/cashier'">
            ← Bán tiếp
        </button>
    </div>
</div>

<script>
    // Tự động mở hộp thoại in ngay khi trang load
    window.addEventListener('load', function() {
        setTimeout(function() { window.print(); }, 400);
    });
</script>

</body>
</html>
