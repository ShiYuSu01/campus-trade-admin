package com.campus.trade.common;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.campus.trade.entity.Order;
import com.campus.trade.entity.Report;

/**
 * Excel 导出工具，使用 Apache POI 生成 xlsx。
 */
public final class ExcelUtil {

    private ExcelUtil() {
    }

    public static void writeOrders(HttpServletResponse response, List<Order> orders) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("订单数据");
            String[] headers = {"订单号", "商品", "买家", "卖家", "金额", "订单状态",
                    "交付方式", "收货地址", "下单时间"};
            writeHeader(workbook, sheet, headers);
            int rowIndex = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowIndex++);
                write(row, 0, order.getOrderNo());
                write(row, 1, order.getProductTitle());
                write(row, 2, order.getBuyerName());
                write(row, 3, order.getSellerName());
                write(row, 4, order.getAmountText());
                write(row, 5, order.getStatusLabel());
                write(row, 6, order.getDeliveryLabel());
                write(row, 7, order.getAddress());
                write(row, 8, order.getCreateTimeText());
            }
            finish(workbook, sheet, headers.length);
            download(response, workbook, "orders-" + LocalDate.now() + ".xlsx");
        }
    }

    public static void writeReports(HttpServletResponse response, List<Report> reports) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("举报数据");
            String[] headers = {"编号", "对象类型", "被举报对象", "举报原因", "举报人",
                    "状态", "处理人", "处罚动作", "处理说明", "举报时间", "处理时间"};
            writeHeader(workbook, sheet, headers);
            int rowIndex = 1;
            for (Report report : reports) {
                Row row = sheet.createRow(rowIndex++);
                write(row, 0, report.getId());
                write(row, 1, report.getTargetTypeLabel());
                write(row, 2, report.getTargetTitle());
                write(row, 3, report.getReasonLabel());
                write(row, 4, report.getReporterName());
                write(row, 5, report.getStatusLabel());
                write(row, 6, report.getHandlerName());
                write(row, 7, report.getActionLabel());
                write(row, 8, report.getHandleResult());
                write(row, 9, report.getCreateTimeText());
                write(row, 10, report.getHandledAtText());
            }
            finish(workbook, sheet, headers.length);
            download(response, workbook, "reports-" + LocalDate.now() + ".xlsx");
        }
    }

    private static void writeHeader(Workbook workbook, Sheet sheet, String[] headers) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        Row header = sheet.createRow(0);
        header.setHeightInPoints(24);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
        sheet.createFreezePane(0, 1);
    }

    private static void finish(Workbook workbook, Sheet sheet, int columns) {
        CellStyle body = workbook.createCellStyle();
        body.setVerticalAlignment(VerticalAlignment.CENTER);
        body.setBorderBottom(BorderStyle.THIN);
        body.setBorderTop(BorderStyle.THIN);
        body.setBorderLeft(BorderStyle.THIN);
        body.setBorderRight(BorderStyle.THIN);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            row.setHeightInPoints(20);
            for (int j = 0; j < columns; j++) {
                Cell cell = row.getCell(j);
                if (cell != null) {
                    cell.setCellStyle(body);
                }
            }
        }
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
            int width = Math.min(sheet.getColumnWidth(i) + 800, 15000);
            sheet.setColumnWidth(i, width);
        }
    }

    private static void write(Row row, int index, Object value) {
        Cell cell = row.createCell(index);
        if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else {
            cell.setCellValue(value == null ? "" : value.toString());
        }
    }

    private static void download(HttpServletResponse response, Workbook workbook, String filename)
            throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setHeader("Cache-Control", "no-store");
        workbook.write(response.getOutputStream());
        response.flushBuffer();
    }
}
