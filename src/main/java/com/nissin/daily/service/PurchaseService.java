package com.nissin.daily.service;

import com.nissin.daily.entity.CaculateData;
import com.nissin.daily.entity.EachMonthData;
import com.nissin.daily.entity.SheetData;
import com.nissin.daily.mapper.EachMonthDataMapper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.constant.ErrorConstant;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 发货到接收
 * 接收到入库处理类
 */

@Service
public class PurchaseService {
    private String dateFormatStr = "yyyy/MM/dd HH:mm:ss";
    private CaculateData cdata;
    private String tempPath = this.getClass().getResource("/").getPath() + "temp";
    @Value(value = "${file.path}")
    private String path;
    @Autowired
    private EachMonthDataMapper eachMonthDataMapper;

    /**
     * 处理业务
     *
     * @param file1
     * @param file2
     * @param file3
     * @param file4
     * @param cid
     * @param year
     * @param month
     * @param outDay
     */

    public void doBusiness(File file1, File file2, File file3, File file4,Integer cid,Integer year,Integer month, Integer outDay,  Integer freestyle) throws IOException {

        //创建分析文件
       // String savePath =  this.path+cid+"-"+month+"-"+year+"//bak-" ;
        this.cdata = new CaculateData();
        if (!StringUtils.isEmpty(file1.getName())) {
           /* String file1Name = savePath+file1.getName();
            desFile = new File(file1Name);
            desFile.createNewFile();*/
            //copyFileUsingFileChannels(file1,desFile);
            caculateGetTime(file1,outDay);//接收时长
        }
        if (!StringUtils.isEmpty(file2.getName())) {
            /*String file2Name = savePath+file2.getName();
            desFile = new File(file2Name);
            desFile.createNewFile();
            copyFileUsingFileChannels(file2,desFile);*/
          caculateCheckTime(file2, freestyle);//检验时长
        }
        if (!StringUtils.isEmpty(file3.getName())) {
           /* String file3Name = savePath+file3.getName();
            desFile = new File(file3Name);
            desFile.createNewFile();
            copyFileUsingFileChannels(file3,desFile);*/
           caculateGeneratorPrice(file3);//采购价差
        }
        if (!StringUtils.isEmpty(file4.getName())) {
           /* String file4Name = savePath+file4.getName();
            desFile = new File(file4Name);
            desFile.createNewFile();
            copyFileUsingFileChannels(file4,desFile);*/
            caculateInvoice(file4);//发票价差
        }
        saveToDataSource(cid, month, year, freestyle);//写入到数据库

}

    /**
     * 保存到数据库
     *
     * @param cid
     * @param month
     * @param year
     * @return
     */
    public String saveToDataSource(int cid, int month, int year, int freestyle) {
        EachMonthData dataNow = eachMonthDataMapper.getNowMonthData(cid, month, year);
        EachMonthData data = new EachMonthData();
        if (dataNow != null) {
            data = dataNow;
        }
        String cname = "推土机";
        if (cid == 362) cname = "道机";
        if (cid == 181) cname = "履带";
        if (cid == 221) cname = "传动";
        data.setCompanyid(cid);
        data.setCompanyname(cname);
        data.setMonthno(month);
        data.setYearno(year);
        data.setRcvnum(this.cdata.getRcvNum());
        data.setOutallnum(this.cdata.getOutAllNum());
        data.setChecknum(this.cdata.getCheckNum());
        data.setInstocknum(this.cdata.getInStockNum());
        data.setPurchaseallnum(this.cdata.getPurchaseAllNum());
        data.setPurchasemoney(this.cdata.getPurchaseMoney());
        data.setGapmoney(this.cdata.getGapMoney());
        data.setInvoicehand(this.cdata.getCountHandGap());
        data.setInvoiceten(this.cdata.getCountTenToFive());
        data.setInvoicefive(this.cdata.getCountFiveToHundrad());
        data.setInvoicebai(this.cdata.getCountHundrad());
        data.setInvoicenum(this.cdata.getInvoiceNum());
        data.setInvoicediff(this.cdata.getCountHandGap() + cdata.getCountTenToFive() + cdata.getCountFiveToHundrad() + cdata.getCountHundrad());
        data.setFreestyle(freestyle);
        data.setOutday(this.cdata.getOutday());
        int i = 0;
        if (dataNow != null) {
            i = eachMonthDataMapper.updateByPrimaryKeySelective(data);
        } else {
            i = eachMonthDataMapper.insert(data);
        }

        return "i";
    }


    public String transferFile(MultipartFile file, String path,int cid,int month,int year,String fileName) {
        if (file.isEmpty()) {
            return "";
        }
        String realDirectory = path+cid+File.separator+year+"-"+month+File.separator;
        /*String fileName = file.getOriginalFilename();
        String filename = fileName;*/
        //普通上传
        File fi = new File(realDirectory);
        try {
            if (!fi.isDirectory()) { // 如果文件夹不存在就新建
                fi.mkdirs();
            }
            File fie = new File(realDirectory, fileName);
            file.transferTo(fie);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return realDirectory+fileName;
    }

    /**
     * 导出excel表
     *
     * @param path
     * @param outPath
     */
    public String caculateBusyExcel(String path, String outPath, int yearNo, int monthNo) {
        File moban = new File(path);
        File outFile = new File(outPath);
        List<SheetData> sheetDataList = new ArrayList<>();
        try {
            outFile.createNewFile();
            copyFileUsingFileChannels(moban, outFile);//拷贝一份模版出来
        } catch (IOException e) {
            e.printStackTrace();
        }
        //查询本月四个事业部的数据
        int[] cids = {102, 362, 181, 221};
        for (int i = 0; i < cids.length; i++) {
            SheetData sheetData = new SheetData();
            if (cids[i] == 102) {
                sheetData.setColumnNo(1);
                sheetData.setRowNo(10);
            }
            if (cids[i] == 362) {
                sheetData.setColumnNo(2);
                sheetData.setRowNo(11);
            }
            if (cids[i] == 181) {
                sheetData.setColumnNo(3);
                sheetData.setRowNo(12);
            }
            if (cids[i] == 221) {
                sheetData.setColumnNo(4);
                sheetData.setRowNo(13);
            }
            //查询某个事业部近6个月的数据
            List<EachMonthData> sixMonthData = eachMonthDataMapper.getSixMonthData(cids[i], monthNo, yearNo);
            //查询某个事业部本月的数据
            EachMonthData record = eachMonthDataMapper.getNowMonthData(cids[i], monthNo, yearNo);
            if (record != null) {
                sheetData.setMonthData(record);
                sheetData.setSixMonthData(sixMonthData);
                sheetDataList.add(sheetData);
            }
        }
        writeCompanyDataToExcel(outFile, sheetDataList);
        return outPath;
    }

    /**
     * 把某个事业部的数据写入到excel模版里面
     *
     * @param outFile
     */
    public void writeCompanyDataToExcel(File outFile, List<SheetData> sheetDataList) {
        int columnNo, rowNo;
        FileOutputStream out = null;
        Cell cell;
        double rcvPercent = 0.0;
        double purPercent = 0.0;
        double checkPercent = 0.0;
        double instockPercent = 0.0;
        double invPercent = 0.0;
        try {
            FileInputStream fis = new FileInputStream(outFile);//读写拷贝文件
            //创建工作薄
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            //创建表单
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (SheetData sheetData : sheetDataList) {
                rowNo = sheetData.getRowNo();
                columnNo = sheetData.getColumnNo();
                EachMonthData record = sheetData.getMonthData();
                List<EachMonthData> sixMonthData = sheetData.getSixMonthData();
                int differentNo = record.getInvoicehand() + record.getInvoiceten() + record.getInvoicefive() + record.getInvoicebai();
                rcvPercent = getPercentDouble(record.getRcvnum(), record.getOutallnum());
                checkPercent = getPercentDouble(record.getChecknum(), record.getPurchaseallnum());
                instockPercent = getPercentDouble(record.getInstocknum(), record.getPurchaseallnum());
                purPercent = getPercentString(record.getGapmoney(), record.getPurchasemoney());
                invPercent = getPercentDouble(differentNo, record.getInvoicenum());

                //业务数据分析
                Row row = sheet.getRow(2);
                cell = row.getCell(columnNo);
                cell.setCellValue(rcvPercent);

                row = sheet.getRow(3);
                cell = row.getCell(columnNo);
                cell.setCellValue(checkPercent);

                row = sheet.getRow(4);
                cell = row.getCell(columnNo);
                cell.setCellValue(instockPercent);

                row = sheet.getRow(5);
                cell = row.getCell(columnNo);
                cell.setCellValue(purPercent);

                row = sheet.getRow(6);
                cell = row.getCell(columnNo);
                cell.setCellValue(invPercent);

                //接收业务数据
                row = sheet.getRow(10);
                cell = row.getCell(columnNo);
                cell.setCellValue(record.getOutallnum());

                row = sheet.getRow(11);
                cell = row.getCell(columnNo);
                cell.setCellValue(record.getRcvnum());

                row = sheet.getRow(12);
                cell = row.getCell(columnNo);
                cell.setCellValue(rcvPercent);

                row = sheet.getRow(rowNo);
                cell = row.getCell(8);
                cell.setCellValue(record.getOutallnum());
                cell = row.getCell(9);
                cell.setCellValue(record.getRcvnum());
                cell = row.getCell(10);
                cell.setCellValue(rcvPercent);

                //采购价差分析

                row = sheet.getRow(18);
                cell = row.getCell(columnNo);
                cell.setCellValue(record.getPurchasemoney());

                row = sheet.getRow(19);
                cell = row.getCell(columnNo);
                cell.setCellValue(record.getGapmoney());

                row = sheet.getRow(20);
                cell = row.getCell(columnNo);
                cell.setCellValue(purPercent);

            /*读写该事业部的sheet-----------------------------sheet分割线--------------------------------*/
                XSSFSheet sheet2 = workbook.getSheetAt(columnNo);

                //业务分析
                double rcvPercentD = 0.0;
                double checkPercentD = 0.0;
                double instockPercentD = 0.0;

                int length = sixMonthData.size();
                for (int i = 1; i <= length; i++) {
                    EachMonthData d = sixMonthData.get(length - i);
                    rcvPercentD = getPercentDouble(d.getRcvnum(), d.getOutallnum());
                    checkPercentD = getPercentDouble(d.getChecknum(), d.getPurchaseallnum());
                    instockPercentD = getPercentDouble(d.getInstocknum(), d.getPurchaseallnum());

                    row = sheet2.getRow(2);
                    cell = row.getCell(i);
                    cell.setCellValue(d.getMonthno() + "月");

                    row = sheet2.getRow(3);
                    cell = row.getCell(i);
                    cell.setCellValue(rcvPercentD);

                    row = sheet2.getRow(4);
                    cell = row.getCell(i);
                    cell.setCellValue(checkPercentD);

                    row = sheet2.getRow(5);
                    cell = row.getCell(i);
                    cell.setCellValue(instockPercentD);
                }

                row = sheet2.getRow(8);
                cell = row.getCell(1);
                cell.setCellValue(record.getOutallnum());
                cell = row.getCell(2);
                cell.setCellValue(record.getRcvnum());
                cell = row.getCell(3);
                cell.setCellValue(rcvPercent);

                row = sheet2.getRow(9);
                cell = row.getCell(1);
                cell.setCellValue(record.getPurchaseallnum());
                cell = row.getCell(2);
                cell.setCellValue(record.getChecknum());
                cell = row.getCell(3);
                cell.setCellValue(checkPercent);

                row = sheet2.getRow(10);
                cell = row.getCell(1);
                cell.setCellValue(record.getPurchaseallnum());
                cell = row.getCell(2);
                cell.setCellValue(record.getInstocknum());
                cell = row.getCell(3);
                cell.setCellValue(instockPercent);

            /*发票价差分析*/
                double diffPercent = 0.0;
                double tenPercent = 0.0;
                double fivePercent = 0.0;
                double baiPercent = 0.0;
                double handPercent = 0.0;
                int invoiceNum = record.getInvoicenum();
                diffPercent = getPercentDouble(differentNo, invoiceNum);
                tenPercent = getPercentDouble(record.getInvoiceten(), invoiceNum);
                fivePercent = getPercentDouble(record.getInvoicefive(), invoiceNum);
                baiPercent = getPercentDouble(record.getInvoicebai(), invoiceNum);
                handPercent = getPercentDouble(record.getInvoicehand(), invoiceNum);

                row = sheet2.getRow(15);
                cell = row.getCell(2);
                cell.setCellValue(record.getInvoicenum());


                row = sheet2.getRow(16);
                cell = row.getCell(1);
                cell.setCellValue(invPercent);
                cell = row.getCell(2);
                cell.setCellValue(differentNo);

                row = sheet2.getRow(17);
                cell = row.getCell(1);
                cell.setCellValue(tenPercent + fivePercent + baiPercent);
                cell = row.getCell(2);
                cell.setCellValue(differentNo - record.getInvoicehand());

                row = sheet2.getRow(18);
                cell = row.getCell(1);
                cell.setCellValue(1 - diffPercent);
                cell = row.getCell(2);
                cell.setCellValue(record.getInvoicenum() - differentNo);

                row = sheet2.getRow(19);
                cell = row.getCell(1);
                cell.setCellValue(tenPercent);
                cell = row.getCell(2);
                cell.setCellValue(record.getInvoiceten());

                row = sheet2.getRow(20);
                cell = row.getCell(1);
                cell.setCellValue(fivePercent);
                cell = row.getCell(2);
                cell.setCellValue(record.getInvoicefive());

                row = sheet2.getRow(21);
                cell = row.getCell(1);
                cell.setCellValue(baiPercent);
                cell = row.getCell(2);
                cell.setCellValue(record.getInvoicebai());

                row = sheet2.getRow(22);
                cell = row.getCell(1);
                cell.setCellValue(handPercent);
                cell = row.getCell(2);
                cell.setCellValue(record.getInvoicehand());

            /*采购价差分析*/
                for (int i = 0; i < sixMonthData.size(); i++) {
                    EachMonthData d = sixMonthData.get(i);
                    row = sheet2.getRow(28 + i);
                    cell = row.getCell(0);
                    cell.setCellValue(d.getMonthno() + "月");
                    cell = row.getCell(1);
                    double dd = Double.parseDouble(d.getPurchasemoney());
                    dd = dd / 10000.0;
                    BigDecimal b = new BigDecimal(dd);
                    double df = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    cell.setCellValue(df);

                    cell = row.getCell(2);
                    dd = Double.parseDouble(d.getGapmoney());
                    dd = dd / 10000.0;
                    b = new BigDecimal(dd);
                    df = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    cell.setCellValue(df);
                }
            }
            //创建文件输出流，输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out = new FileOutputStream(outFile);
            workbook.write(out);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public EachMonthData queryByid(int id) {
        EachMonthData row = eachMonthDataMapper.selectByPrimaryKey(id);
        return row;
    }

    public List<EachMonthData> queryAllData() {

        return eachMonthDataMapper.selectAll();
    }

    /**
     * 计算接收超时的数目
     *
     * @param outDay
     */

    public void caculateGetTime(File file, int outDay) {

        if (file != null) {
            int count = 0;
            Row row = null;
            String creationDate = "";
            String rcvDate = "";
            String shipment = "";
            try {
                FileInputStream is = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                Sheet sheet = workbook.getSheetAt(0);
                //总行数
                int trLength = sheet.getLastRowNum();
                this.cdata.setOutAllNum(trLength);
                this.cdata.setOutday(outDay);
                boolean isPass;
                row = sheet.getRow(0);
                row.createCell(12).setCellValue("接收超时");
                for (int i = 1; i <= trLength; i++) {
                    isPass = true;
                    row = sheet.getRow(i);
                    creationDate = row.getCell(3).getStringCellValue();
                    rcvDate = row.getCell(8).getStringCellValue();
                    shipment = row.getCell(4).getStringCellValue();
                    if (StringUtils.isEmpty(rcvDate)) {
                        if (!"CANCELLED".equals(shipment)) {
                            count++;
                            isPass = false;
                        }
                    } else {
                        if (StringUtils.isEmpty(creationDate)) {
                            continue;
                        } else {
                            int days = getDistaceTime(creationDate, rcvDate);
                            if (days >= outDay) {
                                count++;
                                isPass = false;
                            }
                        }
                    }
                    /*写入标记合格cell*/
                    if(isPass){
                        row.createCell(12).setCellValue("合格");
                    }else{
                        row.createCell(12).setCellValue("超时");
                    }
                }
                this.cdata.setRcvNum(count);

                FileOutputStream os = new FileOutputStream(file);
                workbook.write(os);
                is.close();
                os.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 计算检验时长超标的数目
     *
     * @param file
     */

    public void caculateCheckTime(File file, int freestyle) {
        if (file != null) {
            int count = 0;//检验超标数
            int kuCount = 0;//入库超标数
            Row row = null;
            String recevDate = "";//接收时间
            String acceptDate = "";//过检时间
            String trancferDate = "";//报检时间
            String rejectDate = "";//拒绝时间
            String returnDate = "";//退货时间
            String deliverDate = "";//入库时间
            String receiving_qa = "";//接收类型
            int disDay = 0;//相差的天数
            try {
                FileInputStream is = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                Sheet sheet = workbook.getSheetAt(0);
                //总行数
                int trLength = sheet.getLastRowNum();
                this.cdata.setPurchaseAllNum(trLength);
                row = sheet.getRow(0);
                row.createCell(14).setCellValue("检验超时");
                row.createCell(15).setCellValue("入库超时");
                boolean isCheckPass = true;
                boolean isKuPass = true;
                for (int i = 1; i <= trLength; i++) {
                    isKuPass = true;
                    isCheckPass = true;
                    row = sheet.getRow(i);
                    receiving_qa = row.getCell(6).getStringCellValue();
                    recevDate = row.getCell(8).getStringCellValue();
                    trancferDate = row.getCell(9).getStringCellValue();
                    acceptDate = row.getCell(10).getStringCellValue();
                    rejectDate = row.getCell(11).getStringCellValue();
                    returnDate = row.getCell(12).getStringCellValue();
                    deliverDate = row.getCell(13).getStringCellValue();
                    /**
                     * 第一步计算检验超标数
                     */
                    if (StringUtils.isEmpty(acceptDate) || StringUtils.isEmpty(trancferDate)) {
                        if ("要求检验".equals(receiving_qa) && StringUtils.isEmpty(rejectDate) && StringUtils.isEmpty(returnDate)) {
                            count++;
                            isCheckPass = false;
                        }
                    } else {
                        int week = getWeekOfDate(trancferDate);
                        int ouyday = 1;
                        if (freestyle == 0 && week == 7) { //单休并且周六
                            ouyday = 2;
                        }
                        if (freestyle == 1 && week == 6) {//双休且周五
                            ouyday = 3;
                        }
                        if (freestyle == 2 && week == 5) {//三休且周四
                            ouyday = 4;
                        }
                        disDay = getDistaceTime(trancferDate, acceptDate);
                        if (disDay >= ouyday) {//检验时间大于ouyday
                            count++;
                            isCheckPass = false;
                        }
                    }
                    /**
                     * 第二步，计算入库超标数
                     */
                    if(StringUtils.isEmpty(deliverDate)||StringUtils.isEmpty(acceptDate)){
                        if (!StringUtils.isEmpty(recevDate) && !StringUtils.isEmpty(deliverDate)) {
                            disDay = getDistaceTime(recevDate, deliverDate);
                            if (disDay >= 1) {
                                kuCount++;
                                isKuPass = false;
                            }
                        }else if(StringUtils.isEmpty(deliverDate)&&StringUtils.isEmpty(rejectDate)&&StringUtils.isEmpty(returnDate)&&!StringUtils.isEmpty(recevDate)){
                            kuCount++;
                            isKuPass = false;
                        }
                    } else {
                        disDay = getDistaceTime(acceptDate, deliverDate);
                        if (disDay >= 1) { //入库时间大于一天
                            kuCount++;
                            isKuPass = false;
                        }
                    }
                     /*写入入库标记cell*/
                    if(isKuPass){
                        row.createCell(15).setCellValue("合格");
                    }else{
                        row.createCell(15).setCellValue("超时");
                    }

                    /*写入检验标记cell*/
                    if(isCheckPass){
                        row.createCell(14).setCellValue("合格");
                    }else{
                        row.createCell(14).setCellValue("超时");
                    }
                }
                this.cdata.setCheckNum(count);
                this.cdata.setInStockNum(kuCount);
                System.out.println(count);
                System.out.println(kuCount);
                FileOutputStream os = new FileOutputStream(file);
                workbook.write(os);
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 采购价差
     *
     * @param file
     */
    private void caculateGeneratorPrice(File file) {
        if (file != null) {
            Row row;
            BigDecimal purNum = null;//采购入库数量
            BigDecimal purPrice = null;//采购单价
            BigDecimal purGapPrice = null;//采购差异

            BigDecimal gapSum = new BigDecimal("0");
            BigDecimal purSum = new BigDecimal("0");
            try {
                FileInputStream is = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                Sheet sheet = workbook.getSheetAt(0);
                //总行数
                int trLength = sheet.getLastRowNum();
                double purNumD = 0;
                double purPriceD = 0;
                double purGapPriceD = 0;

                for (int i = 3; i <= trLength; i++) {
                    row = sheet.getRow(i);
                    purNumD = row.getCell(9).getNumericCellValue();
                    purPriceD = row.getCell(10).getNumericCellValue();
                    purGapPriceD = row.getCell(14).getNumericCellValue();

                    purNum = new BigDecimal(Double.toString(purNumD));
                    purPrice = new BigDecimal(Double.toString(purPriceD));
                    purGapPrice = new BigDecimal(Double.toString(purGapPriceD));
                    purGapPrice = purGapPrice.abs();

                    gapSum = gapSum.add(purGapPrice);
                    purSum = purSum.add(purNum.multiply(purPrice));
                }
                this.cdata.setPurchaseMoney(purSum.toString());
                this.cdata.setGapMoney(gapSum.toString());
                FileOutputStream os = new FileOutputStream(file);
                workbook.write(os);
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 计算发票价差
     *
     * @param file
     */
    private void caculateInvoice(File file) {
        if (file != null) {
            Row row;//行
            BigDecimal invPercent = new BigDecimal("0");//价差比
            double invNumD = 0;//开票数量
            double invPriceD = 0;//单价
            double invGapPriceD = 0;//发票价差
            try {
                FileInputStream is = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                Sheet sheet = workbook.getSheetAt(0);
                //总行数
                int trLength = sheet.getLastRowNum();
                this.cdata.setInvoiceNum(trLength - 5);
                int countHandGap = 0;
                int countTenToFive = 0;
                int countFiveToHundrad = 0;
                int countHundrad = 0;
                boolean isInvPass = true;
                for (int i = 6; i <= trLength; i++) {
                    isInvPass = true;
                    row = sheet.getRow(i);
                    invNumD = row.getCell(11).getNumericCellValue();
                    invPriceD = row.getCell(10).getNumericCellValue();
                    invGapPriceD = row.getCell(12).getNumericCellValue();
                    if (invNumD == 0 || invPriceD == 0) {
                        if (invGapPriceD > 1){
                            countHandGap++;
                            isInvPass = false;
                        }
                    } else {
                        BigDecimal invNum = new BigDecimal(Double.toString(invNumD));
                        BigDecimal invPrice = new BigDecimal(Double.toString(invPriceD));
                        BigDecimal invGapPrice = new BigDecimal(Double.toString(invGapPriceD));
                        invPercent = invGapPrice.divide(invNum, 8, BigDecimal.ROUND_HALF_UP);
                        invPercent = invPercent.divide(invPrice, 10, BigDecimal.ROUND_HALF_UP);
                        double percent = Double.valueOf(invPercent.toString());
                        if (percent >= 0.1 && percent < 0.5) {
                            countTenToFive += 1;
                            isInvPass = false;
                        }
                        if (percent >= 0.5 && percent < 1) {
                            countFiveToHundrad += 1;
                            isInvPass = false;
                        }
                        if (percent >= 1) {
                            countHundrad += 1;
                            isInvPass = false;
                        }
                    }
                    if(!isInvPass){
                        row.createCell(13).setCellValue("超标");
                    }else{
                        row.createCell(13).setCellValue("合格");
                    }
                }

                this.cdata.setCountHandGap(countHandGap);
                this.cdata.setCountTenToFive(countTenToFive);
                this.cdata.setCountFiveToHundrad(countFiveToHundrad);
                this.cdata.setCountHundrad(countHundrad);
                FileOutputStream os = new FileOutputStream(file);
                workbook.write(os);
                is.close();
                os.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取当前日期是周几
     */

    public int getWeekOfDate(String date) throws Exception {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
        calendar.setTime(sdf.parse(date));
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        return i;
    }

    /**
     * 获取业务数据比例
     */
    public double getPercentDouble(int num1, int num2) {
        if (num2 == 0) return 0;
        DecimalFormat df = new DecimalFormat("0.0000");
        String re = df.format(num1 / (float) num2);
        double dr = Double.parseDouble(re);
        return dr;
    }

    /**
     * 获取业务数据比例
     */
    public double getPercentString(String num1, String num2) {
        BigDecimal num1Decimal = new BigDecimal(num1);
        BigDecimal num2Decimal = new BigDecimal(num2);
        BigDecimal invPercent = num1Decimal.divide(num2Decimal, 4, BigDecimal.ROUND_HALF_UP);
        double percent = Double.valueOf(invPercent.toString());
        return percent;
    }

    /**
     * 转换非法日期为标准格式
     * yyyy/MM/dd HH:mm:ss
     *
     * @param str1
     * @return
     */
    public String convertToValidateDate(String str1) {
        if (str1 == null || "".equals(str1)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();//构造一个StringBuilder对象
        String date[] = str1.split(" ");
        String date1[] = date[0].split("/");
        sb.append(date1[0]);
        sb.append("/");
        if (date1[1].length() == 1) {
            sb.append("0");
        }
        sb.append(date1[1]);
        sb.append("/");
        if (date1[2].length() == 1) {
            sb.append("0");
        }
        sb.append(date1[2]);
        sb.append(" ");
        if (date.length == 2) {
            String date2[] = date[1].split(":");
            if (date2[0].length() == 1) {
                sb.append("0");
            }
            sb.append(date2[0]);
            sb.append(":");
            sb.append(date2[1]);
            sb.append(":");
            sb.append(date2[2]);
        } else {
            sb.append("00:00:00");
        }
        str1 = sb.toString();
        return str1;
    }

    /**
     * 判定字符串合法性
     * yyyy/MM/dd HH:mm:ss
     *
     * @param str
     * @return
     */
    public boolean isValidDate(String str) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat(dateFormatStr);
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 计算非标准时间差
     *
     * @param start
     * @param end
     * @return
     */
    public int getDistaceTime(String start, String end) {
        if (!isValidDate(start)) {
            start = convertToValidateDate(start);
        }//如果不标准，转标准
        if (!isValidDate(end)) {
            end = convertToValidateDate(end);
        }
        int days = getDistanOfTime(start, end);
        return days;
    }

    /**
     * 计算合法时间差
     * param:String start,String end
     */
    public int getDistanOfTime(String start, String end) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
        try {
            Date date1 = sdf.parse(start);
            Date date2 = sdf.parse(end);
            int days = differentDaysByMillisecond(date1, date2);
            return days;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param start
     * @param end
     * @return
     */
    public int differentDaysByMillisecond(Date start, Date end) {
        int days = (int) ((end.getTime() - start.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    /**
     * 判断是不是最后几天
     *
     * @param date
     * @return
     */
    public boolean isLastDay(String date, int lastDay) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormatStr);
        Date currdate = null;
        try {
            currdate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar ca = Calendar.getInstance();
        ca.setTime(currdate);
        int nowMonth = ca.get(Calendar.MONTH);
        ca.add(Calendar.DATE, lastDay);// num为增加的天数，可以改变的
        int afterMonth = ca.get(Calendar.MONTH);
        return !(nowMonth == afterMonth);
    }

    /**
     * 生成sheet页
     *
     * @param sheet
     * @param data
     */
    public void createSheet(XSSFSheet sheet, List<List<String>> data) {
        XSSFCell cell;
        for (int i = 0; i < data.size(); i++) {
            XSSFRow row = sheet.createRow(i);//创建第i行
            List<String> rowList = data.get(i);
            for (int j = 0; j < data.get(i).size(); j++) {
                cell = row.createCell(j); //创建第j列
                cell.setCellValue(rowList.get(j));//设置第i行j列的内容
            }
        }
    }

    /**
     * 生成 Excel
     */

    public void writeExcel(String finalXlsxPath, List<List<String>> data) throws IOException {
        //创建工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();
        //创建表单
        XSSFSheet sheet = workbook.createSheet("0");
        createSheet(sheet, data);
        //将生成的Excel文件保存到本地
        FileOutputStream out = new FileOutputStream(new File(finalXlsxPath));
        //将工作薄写入文件输出流中
        workbook.write(out);
        //文本文件输出流，释放资源
        out.close();
    }

    /**
     * 拷贝文件
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    private void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    public List<Map> showAllTrainee(Map paraMap) {
        return eachMonthDataMapper.selectTraineeLimit(paraMap);
    }

    public long getTraineeTotal(Map paraMap) {
        return eachMonthDataMapper.getTraineeTotal(paraMap);
    }

    public boolean updateOneMonth(EachMonthData eachMonthData) {
        if (eachMonthDataMapper.updateByPrimaryKeySelective(eachMonthData)>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean delSelectData(List ids) {
        if (eachMonthDataMapper.delSelectData(ids)>0){
            return true;
        }else{
            return false;
        }
    }
}

