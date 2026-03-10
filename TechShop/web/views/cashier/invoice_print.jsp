<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"      prefix="c"   %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"       prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"  %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>In hóa đơn - TechShop</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Courier New', monospace;
            font-size: 12px;
            background: #eef1f5;
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 30px 20px;
            gap: 30px;
            min-height: 100vh;
        }

        /* ── Thông báo nhiều hóa đơn ── */
        .multi-badge {
            background: #0d6efd;
            color: #fff;
            padding: 6px 16px;
            border-radius: 20px;
            font-family: Arial, sans-serif;
            font-size: 13px;
            font-weight: 600;
            letter-spacing: .3px;
        }

        /* ── Khổ giấy 80mm — chuẩn máy in nhiệt POS ── */
        .receipt {
            width: 302px;
            background: #fff;
            padding: 18px 16px;
            border-radius: 4px;
            box-shadow: 0 2px 12px rgba(0,0,0,.1);
            position: relative;
        }

        /* Đánh số hóa đơn khi có nhiều tờ */
        .receipt-number {
            position: absolute;
            top: -10px;
            right: 12px;
            background: #0d6efd;
            color: #fff;
            font-family: Arial, sans-serif;
            font-size: 10px;
            font-weight: 700;
            padding: 2px 10px;
            border-radius: 10px;
        }

        /* ── Header cửa hàng ── */
        .brand { text-align: center; margin-bottom: 10px; }
        .brand-name {
            font-size: 20px;
            font-weight: 700;
            letter-spacing: 3px;
        }
        .brand-sub {
            font-size: 10px;
            color: #555;
            margin-top: 2px;
            line-height: 1.4;
        }

        /* ── Divider ── */
        .divider {
            border: none;
            border-top: 1px dashed #bbb;
            margin: 8px 0;
        }
        .divider-bold {
            border: none;
            border-top: 1.5px solid #333;
            margin: 8px 0;
        }

        /* ── Tiêu đề hóa đơn ── */
        .invoice-title {
            text-align: center;
            font-size: 14px;
            font-weight: 700;
            letter-spacing: 2px;
            margin: 8px 0 2px;
        }
        .invoice-code {
            text-align: center;
            font-size: 11px;
            color: #555;
            margin-bottom: 4px;
        }

        /* ── Thông tin 2 cột ── */
        .info-row {
            display: flex;
            justify-content: space-between;
            margin: 3px 0;
            font-size: 11px;
            line-height: 1.4;
        }
        .info-label { color: #666; flex-shrink: 0; }
        .info-value {
            font-weight: 600;
            text-align: right;
            max-width: 58%;
            word-break: break-word;
        }

        /* ── Bảng sản phẩm ── */
        .items-header {
            display: flex;
            justify-content: space-between;
            font-size: 10px;
            font-weight: 700;
            color: #333;
            text-transform: uppercase;
            letter-spacing: .5px;
            margin: 4px 0 2px;
        }
        .item-row {
            margin: 6px 0;
        }
        .item-name {
            font-size: 11px;
            font-weight: 600;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        .item-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 10px;
            color: #555;
            margin-top: 2px;
        }
        .item-imei {
            font-family: 'Courier New', monospace;
            background: #f0f0f0;
            padding: 1px 4px;
            border-radius: 2px;
            font-size: 9px;
        }
        .item-price {
            font-weight: 700;
            font-size: 11px;
            color: #111;
        }
        .item-warranty {
            font-size: 9px;
            color: #888;
            margin-top: 1px;
        }

        /* ── Tổng tiền ── */
        .total-section { margin-top: 6px; }
        .total-row {
            display: flex;
            justify-content: space-between;
            font-size: 11px;
            margin: 3px 0;
        }
        .total-row.discount { color: #dc3545; }
        .total-row.grand {
            font-size: 15px;
            font-weight: 700;
            margin-top: 8px;
            padding-top: 6px;
            border-top: 1px dashed #999;
        }

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

        /* ── Ghi chú ── */
        .note-box {
            background: #fafafa;
            border: 1px dashed #ccc;
            border-radius: 4px;
            padding: 5px 8px;
            font-size: 10px;
            color: #444;
            margin: 6px 0;
            line-height: 1.4;
        }

        /* ── Footer ── */
        .footer {
            text-align: center;
            font-size: 10px;
            color: #888;
            margin-top: 10px;
            line-height: 1.7;
        }
        .footer-code {
            margin-top: 6px;
            font-weight: 700;
            font-size: 11px;
            color: #333;
            letter-spacing: 1px;
        }

        /* ── Nút thao tác ── */
        .action-bar {
            display: flex;
            gap: 10px;
            justify-content: center;
            padding: 10px 0 20px;
        }
        .btn-action {
            border: none;
            padding: 10px 24px;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            font-family: Arial, sans-serif;
            transition: opacity .15s;
        }
        .btn-action:hover { opacity: .85; }
        .btn-print { background: #0d6efd; color: #fff; }
        .btn-back  { background: #6c757d; color: #fff; }

        /* ── In ấn ── */
        @media print {
            body { background: #fff; padding: 0; gap: 0; }
            .receipt {
                box-shadow: none;
                border-radius: 0;
                page-break-after: always;
            }
            .receipt:last-of-type { page-break-after: auto; }
            .action-bar, .multi-badge, .receipt-number { display: none !important; }
        }
    </style>
</head>
<body>

<%-- Badge thông báo khi có nhiều hóa đơn --%>
<c:if test="${fn:length(invoices) > 1}">
    <div class="multi-badge">${fn:length(invoices)} hóa đơn</div>
</c:if>

<c:forEach var="inv" items="${invoices}" varStatus="invSt">
<div class="receipt">

    <%-- Đánh số khi có nhiều hóa đơn --%>
    <c:if test="${fn:length(invoices) > 1}">
        <div class="receipt-number">${invSt.index + 1} / ${fn:length(invoices)}</div>
    </c:if>

    <%-- ══ Header cửa hàng ══ --%>
    <div class="brand">
        <div class="brand-name">TECHSHOP</div>
        <div class="brand-sub">${inv.branchName}</div>
        <c:if test="${not empty inv.branchAddress}">
            <div class="brand-sub">${inv.branchAddress}</div>
        </c:if>
        <c:if test="${not empty inv.branchPhone}">
            <div class="brand-sub">ĐT: ${inv.branchPhone}</div>
        </c:if>
    </div>

    <hr class="divider-bold"/>

    <div class="invoice-title">HÓA ĐƠN BÁN HÀNG</div>
    <div class="invoice-code">${inv.invoiceCode}</div>

    <hr class="divider"/>

    <%-- ══ Thông tin hóa đơn ══ --%>
    <div class="info-row">
        <span class="info-label">Ngày:</span>
        <span class="info-value">${inv.invoiceDateFormatted}</span>
    </div>
    <div class="info-row">
        <span class="info-label">Thu ngân:</span>
        <span class="info-value">${inv.cashierName}</span>
    </div>

    <hr class="divider"/>

    <%-- ══ Thông tin khách hàng ══ --%>
    <div class="info-row">
        <span class="info-label">Khách hàng:</span>
        <span class="info-value">${inv.customerName}</span>
    </div>
    <div class="info-row">
        <span class="info-label">SĐT:</span>
        <span class="info-value">${inv.customerPhone}</span>
    </div>
    <c:if test="${not empty inv.customerEmail}">
        <div class="info-row">
            <span class="info-label">Email:</span>
            <span class="info-value">${inv.customerEmail}</span>
        </div>
    </c:if>
    <c:if test="${not empty inv.customerAddress}">
        <div class="info-row">
            <span class="info-label">Địa chỉ:</span>
            <span class="info-value">${inv.customerAddress}</span>
        </div>
    </c:if>

    <hr class="divider"/>

    <%-- ══ Danh sách sản phẩm ══ --%>
    <div class="items-header">
        <span>Sản phẩm (${fn:length(inv.items)})</span>
        <span>Thành tiền</span>
    </div>
    <hr class="divider"/>

    <c:forEach var="item" items="${inv.items}" varStatus="st">
        <div class="item-row">
            <div class="item-name">${st.index + 1}. ${item.variantName}</div>
            <div class="item-meta">
                <span class="item-imei">${item.imei}</span>
                <span class="item-price">
                    <fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true"/>đ
                </span>
            </div>
            <div class="item-warranty">Bảo hành: ${item.warrantyMonths} tháng</div>
        </div>
        <c:if test="${!st.last}"><hr class="divider"/></c:if>
    </c:forEach>

    <hr class="divider-bold"/>

    <%-- ══ Tổng tiền ══ --%>
    <div class="total-section">
        <div class="total-row">
            <span>Tổng tiền hàng (${fn:length(inv.items)} SP):</span>
            <span><fmt:formatNumber value="${inv.totalAmount}" type="number" groupingUsed="true"/>đ</span>
        </div>
        <c:if test="${inv.discountAmount > 0}">
            <div class="total-row discount">
                <span>Giảm giá:</span>
                <span>- <fmt:formatNumber value="${inv.discountAmount}" type="number" groupingUsed="true"/>đ</span>
            </div>
        </c:if>
        <div class="total-row grand">
            <span>THANH TOÁN:</span>
            <span><fmt:formatNumber value="${inv.finalAmount}" type="number" groupingUsed="true"/>đ</span>
        </div>
    </div>

    <hr class="divider"/>

    <%-- ══ Phương thức thanh toán ══ --%>
    <div class="info-row" style="align-items:center;">
        <span class="info-label">Thanh toán:</span>
        <c:choose>
            <c:when test="${inv.paymentMethod == 'CASH'}">
                <span class="payment-badge pm-CASH">Tiền mặt</span>
            </c:when>
            <c:when test="${inv.paymentMethod == 'TRANSFER'}">
                <span class="payment-badge pm-TRANSFER">Chuyển khoản</span>
            </c:when>
            <c:when test="${inv.paymentMethod == 'CARD'}">
                <span class="payment-badge pm-CARD">Thẻ ngân hàng</span>
            </c:when>
            <c:otherwise>
                <span class="payment-badge pm-MIXED">Kết hợp</span>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- ══ Ghi chú ══ --%>
    <c:if test="${not empty inv.note}">
        <div class="note-box">Ghi chú: ${inv.note}</div>
    </c:if>

    <hr class="divider"/>

    <%-- ══ Footer ══ --%>
    <div class="footer">
        <div>Cảm ơn quý khách đã mua hàng!</div>
        <div>Vui lòng giữ hóa đơn để được hỗ trợ bảo hành.</div>
        <div class="footer-code">${inv.invoiceCode}</div>
    </div>

</div>
</c:forEach>

<%-- ══ Nút thao tác (ẩn khi in) ══ --%>
<div class="action-bar">
    <button class="btn-action btn-print" onclick="window.print()">
        &#128424; In hóa đơn
    </button>
    <button class="btn-action btn-back"
            onclick="window.location.href='${pageContext.request.contextPath}/cashier'">
        &#8592; Bán tiếp
    </button>
</div>

<script>
    window.addEventListener('load', function() {
        setTimeout(function() { window.print(); }, 500);
    });
</script>

</body>
</html>
