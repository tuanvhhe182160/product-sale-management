<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ include file="../common/header.jsp" %>

<div class="container-fluid px-4 py-4">

    <h3 class="mb-4">
        Chi tiết hóa đơn: 
        <span class="text-primary">${invoice.invoiceCode}</span>
    </h3>

    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <p><strong>Khách hàng:</strong> 
                ${not empty invoice.customerName ? invoice.customerName : 'Khách lẻ'}
            </p>
            <p><strong>Thu ngân:</strong> ${invoice.cashierName}</p>
            <p><strong>Chi nhánh:</strong> ${invoice.branchName}</p>
            <p><strong>Ngày:</strong> 
                ${invoice.invoiceDate.toString().replace('T',' ')}
            </p>
            <p><strong>Trạng thái:</strong> ${invoice.status}</p>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-bordered align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Sản phẩm</th>
                            <th>SKU</th>
                            <th>IMEI</th>
                            <th class="text-end">Đơn giá</th>
                            <th class="text-center">SL</th>
                            <th class="text-end">Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${items}" var="item">
                            <tr>
                                <td>${item.variantName}</td>
                                <td>${item.sku}</td>
                                <td>${item.imei}</td>
                                <td class="text-end">
                                    <fmt:formatNumber value="${item.unitPrice}" 
                                                      type="number" pattern="#,##0"/> ₫
                                </td>
                                <td class="text-center">${item.quantity}</td>
                                <td class="text-end fw-bold">
                                    <fmt:formatNumber value="${item.subtotal}" 
                                                      type="number" pattern="#,##0"/> ₫
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="text-end mt-3">
                <h5>
                    Tổng thanh toán: 
                    <span class="text-success">
                        <fmt:formatNumber value="${invoice.finalAmount}" 
                                          type="number" pattern="#,##0"/> ₫
                    </span>
                </h5>
            </div>
        </div>
    </div>

</div>

<%@ include file="../common/footer.jsp" %>