package com.girigiri.controller.utils;

import com.girigiri.dao.models.ComponentRequest;
import com.girigiri.dao.models.Customer;
import com.girigiri.dao.models.Device;
import com.girigiri.dao.models.Request;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.security.access.method.P;

import java.util.Calendar;
import java.util.List;

/**
 * Created by JianGuo on 7/3/16.
 * Utils for {@link HSSFWorkbook}
 */
public class PoiUtil {
    public static HSSFWorkbook saveConfirmExcel(Request request, Customer customer) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sample sheet");
        CellStyle centerCellStyle = workbook.createCellStyle();
        centerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        centerCellStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);

        sheet.createRow(0);
        Cell cell = sheet.getRow(0).createCell(0);
        cell.setCellValue("***沈阳市计算机服务有限公司取机凭证***");
        cell.setCellStyle(centerCellStyle);

        sheet.createRow(1);
        cell = sheet.getRow(1).createCell(0);
        cell.setCellValue("接受日期");
        cell = sheet.getRow(1).createCell(1);

        cell.setCellValue(getDate(request.getCreated()));
        cell = sheet.getRow(1).createCell(2);
        cell.setCellValue("维修编号");
        cell = sheet.getRow(1).createCell(3);
        cell.setCellValue("No." + request.getRepairHistory().getId());

        sheet.createRow(2);
        cell = sheet.getRow(2).createCell(0);
        cell.setCellValue("产品类型");
        cell = sheet.getRow(2).createCell(1);
        cell.setCellValue(getType(request.getDevice().getType()));
        cell = sheet.getRow(2).createCell(2);
        cell.setCellValue("机器品牌");
        cell = sheet.getRow(2).createCell(3);
        cell.setCellValue(request.getDevice().getBrand());

        sheet.createRow(3);
        cell = sheet.getRow(3).createCell(0);
        cell.setCellValue("产品型号");
        cell = sheet.getRow(3).createCell(1);
        cell.setCellValue(request.getDevice().getNumber());
        cell = sheet.getRow(3).createCell(2);
        cell.setCellValue("系列号");
        cell = sheet.getRow(3).createCell(3);
        cell.setCellValue(request.getDevice().getSerial());


        sheet.createRow(4);
        cell = sheet.getRow(4).createCell(0);
        cell.setCellValue("单位名称");
        cell = sheet.getRow(4).createCell(1);
        cell.setCellValue(customer.getCompanyName());
        cell = sheet.getRow(4).createCell(2);
        cell.setCellValue("联系人");
        cell = sheet.getRow(4).createCell(3);
        cell.setCellValue(customer.getContactName());

        sheet.createRow(5);

        cell = sheet.getRow(5).createCell(0);
        cell.setCellValue("机器故障现象");
        cell.setCellStyle(centerCellStyle);

        sheet.createRow(6);
        cell = sheet.getRow(6).createCell(0);
        cell.setCellValue(request.getDevice().getError());
        cell.setCellStyle(centerCellStyle);


        sheet.createRow(7);
        cell = sheet.getRow(7).createCell(0);
        cell.setCellValue("缺少零件");
        cell.setCellStyle(centerCellStyle);

        sheet.getRow(7).createCell(1);

        cell = sheet.getRow(7).createCell(2);
        cell.setCellValue("随机零件");
        cell.setCellStyle(centerCellStyle);

        sheet.createRow(8);
        cell = sheet.getRow(8).createCell(0);
        cell.setCellValue(request.getDevice().getComponents());

        sheet.getRow(8).createCell(1);
        cell = sheet.getRow(8).createCell(2);
        cell.setCellValue(convertPartOfDevice(request.getDevice()));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 2, 3));
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 2, 3));
        return workbook;
    }

    private static String convertPartOfDevice(Device device) {
        return String.format("HDD:%s、内存:%s、外置PC卡:%s、AC适配器:%s、电池:%s、外置光驱:%s",
                device.getHDD(), device.getMemory(), device.getPCoutside(), device.getAdapter(), device.getBattery(), device.getCDoutside());
    }


    private static String getType(int type) {
        switch (type) {
            case 1:
                return "台式机";
            case 2:
                return "笔记本";
            case 3:
                return "投影仪";
            case 4:
                return "打印机";
            case 5:
                return "其他";
            default:
                throw new IllegalArgumentException("Unknown type " + type);
        }
    }

    private static String getDate(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return String.format("%d-%d-%d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));

    }

    public static HSSFWorkbook saveCheckoutBill(Request request, Customer customer, List<ComponentRequest> componentRequestList) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sample sheet");
        sheet.createRow(0);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        Cell cell = sheet.getRow(0).createCell(0);
        cell.setCellValue("***沈阳市计算机服务有限公司结算清单***");
        cell.setCellStyle(cellStyle);
        sheet.createRow(1);

        cell = sheet.getRow(1).createCell(0);
        cell.setCellValue("维修单号:");
        cell = sheet.getRow(1).createCell(2);
        cell.setCellValue("No." + request.getRepairHistory().getId());

        sheet.createRow(2);
        cell = sheet.getRow(2).createCell(0);
        cell.setCellValue("接修日期:");
        cell = sheet.getRow(2).createCell(2);
        cell.setCellValue(getDate(request.getRepairHistory().getCreated()));
        cell = sheet.getRow(2).createCell(4);
        cell.setCellValue("修复日期:");
        cell = sheet.getRow(2).createCell(6);
        cell.setCellValue(request.getRepairHistory().getRepairTime() + "");

        sheet.createRow(3);
        cell = sheet.getRow(3).createCell(0);
        cell.setCellValue("产品类型:");
        cell = sheet.getRow(3).createCell(2);
        cell.setCellValue(getType(request.getDevice().getType()));

        cell = sheet.getRow(3).createCell(4);
        cell.setCellValue("机器品牌:");
        cell = sheet.getRow(3).createCell(6);
        cell.setCellValue(request.getDevice().getBrand() + "");

        sheet.createRow(4);
        cell = sheet.getRow(4).createCell(0);
        cell.setCellValue("机器型号:");
        cell = sheet.getRow(4).createCell(2);
        cell.setCellValue(request.getDevice().getNumber() + "");
        cell = sheet.getRow(4).createCell(4);
        cell.setCellValue("系列号:");
        cell = sheet.getRow(4).createCell(6);
        cell.setCellValue(request.getDevice().getSerial() + "");

        sheet.createRow(5);
        cell = sheet.getRow(5).createCell(0);
        cell.setCellValue("单位名称:");
        cell = sheet.getRow(5).createCell(2);
        cell.setCellValue(customer.getCompanyName() + "");
        cell = sheet.getRow(5).createCell(4);
        cell.setCellValue("联系人:");
        cell = sheet.getRow(5).createCell(6);
        cell.setCellValue(customer.getContactName() + "");

        sheet.createRow(6);
        cell = sheet.getRow(6).createCell(0);
        cell.setCellValue("合计金额:");
        cell = sheet.getRow(6).createCell(2);
        cell.setCellValue(request.getRepairHistory().getMaterialPrice() + request.getRepairHistory().getManPrice());
        cell = sheet.getRow(6).createCell(4);
        cell.setCellValue("维修费:");
        cell = sheet.getRow(6).createCell(5);
        cell.setCellValue(request.getRepairHistory().getManPrice());
        cell = sheet.getRow(6).createCell(6);
        cell.setCellValue("材料费:");
        cell = sheet.getRow(6).createCell(7);
        cell.setCellValue(request.getRepairHistory().getMaterialPrice());

        sheet.createRow(7);
        cell = sheet.getRow(7).createCell(0);
        cell.setCellValue("机器故障现象");
        cell.setCellStyle(cellStyle);

        sheet.createRow(8);
        cell = sheet.getRow(8).createCell(0);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(request.getDevice().getError() + "");

        sheet.createRow(9);
        cell = sheet.getRow(9).createCell(0);
        cell.setCellValue("报修承诺");
        cell.setCellStyle(cellStyle);

        cell = sheet.getRow(9).createCell(4);
        cell.setCellValue("注意事项");
        cell.setCellStyle(cellStyle);

        sheet.createRow(10);
        cell = sheet.getRow(10).createCell(0);
        cell.setCellValue(request.getRepairHistory().getPromise() + "");
        cell.setCellStyle(cellStyle);

        cell = sheet.getRow(10).createCell(4);
        cell.setCellValue(request.getRepairHistory().getWarning() + "");
        cell.setCellStyle(cellStyle);

        sheet.createRow(11);
        cell = sheet.getRow(11).createCell(0);
        cell.setCellValue("部件名称");
        cell = sheet.getRow(11).createCell(2);
        cell.setCellValue("型号");
        cell = sheet.getRow(11).createCell(4);
        cell.setCellValue("数量");
        cell = sheet.getRow(11).createCell(6);
        cell.setCellValue("单价");
        for (int i = 0; i < componentRequestList.size(); i++) {
            sheet.createRow(12 + i);
            cell = sheet.getRow(12 + i).createCell(0);
            cell.setCellValue(componentRequestList.get(i).getName());
            cell = sheet.getRow(12 + i).createCell(2);
            cell.setCellValue(componentRequestList.get(i).getSerial() + "");
            cell = sheet.getRow(12 + i).createCell(4);
            cell.setCellValue(componentRequestList.get(i).getSize());
            cell = sheet.getRow(12 + i).createCell(6);
            cell.setCellValue(componentRequestList.get(i).getPrice());
        }
        //TODO: list components here
        //TODO: merge some cells
        for (int i = 1; i < 6; i++) {
            for (int j = 0; j < 8; j+=2) {
                sheet.addMergedRegion(new CellRangeAddress(i, i, j, j + 1));
            }
        }
        for (int i = 0; i <= componentRequestList.size(); i++) {
            for (int j = 0; j < 8; j+=2) {
                sheet.addMergedRegion(new CellRangeAddress(i + 11, i + 11, j, j + 1));
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 2, 3));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 7));
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, 7));
        sheet.addMergedRegion(new CellRangeAddress(9, 9, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(9, 9, 4, 7));
        sheet.addMergedRegion(new CellRangeAddress(10, 10, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(10, 10, 4, 7));
        for (Row cells : sheet) {
            for (Cell tmp: cells) {
                tmp.setCellStyle(cellStyle);
            }
        }
        return workbook;

    }
}
