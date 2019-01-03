package com.nissin.daily.test;

import com.nissin.daily.entity.EachMonthData;
import com.nissin.daily.mapper.EachMonthDataMapper;
import com.nissin.daily.service.PurchaseService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
public class TestReadExcel {
    public String dateFormatStr = "yyyy/MM/dd HH:mm:ss";
    public int rcvNum = 0;//接收时长超标量
    public int outAllNum = 0;//发货单总量

    public int checkNum = 0;//检验时长超标总量
    public int inStockNum = 0;//入库时长超标总量
    public int purchaseAllNum = 0;//采购接收总数

    @Test
    public void testCreateExcel() {
        String xlsPath = "E:测试.xlsx";

        // excel文档对象
        XSSFWorkbook wk = new XSSFWorkbook();
        // sheet对象
        XSSFSheet sheet = wk.createSheet("测试");

        // 字体样式
        XSSFFont xssfFont = wk.createFont();
        // 加粗
        xssfFont.setBold(true);
        // 字体名称
        xssfFont.setFontName("楷体");
        // 字体大小
        xssfFont.setFontHeight(12);

        // 表头样式
        XSSFCellStyle headStyle = wk.createCellStyle();
        // 设置字体css
        headStyle.setFont(xssfFont);
        // 竖向居中
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 横向居中
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        // 边框
        headStyle.setBorderBottom(BorderStyle.THIN);
        headStyle.setBorderLeft(BorderStyle.THIN);
        headStyle.setBorderRight(BorderStyle.THIN);
        headStyle.setBorderTop(BorderStyle.THIN);

        // 内容字体样式
        XSSFFont contFont = wk.createFont();
        // 加粗
        contFont.setBold(false);
        // 字体名称
        contFont.setFontName("楷体");
        // 字体大小
        contFont.setFontHeight(11);
        // 内容样式
        XSSFCellStyle contentStyle = wk.createCellStyle();
        // 设置字体css
        contentStyle.setFont(contFont);
        // 竖向居中
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 横向居中
        //contentStyle.setAlignment(HorizontalAlignment.CENTER);
        // 边框
        contentStyle.setBorderBottom(BorderStyle.THIN);
        contentStyle.setBorderLeft(BorderStyle.THIN);
        contentStyle.setBorderRight(BorderStyle.THIN);
        contentStyle.setBorderTop(BorderStyle.THIN);

        // 自动换行
        contentStyle.setWrapText(true);

        // 数字样式
        XSSFCellStyle numStyle = wk.createCellStyle();
        // 设置字体css
        numStyle.setFont(contFont);
        // 竖向居中
        numStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 横向居中
        numStyle.setAlignment(HorizontalAlignment.CENTER);
        // 边框
        numStyle.setBorderBottom(BorderStyle.THIN);
        numStyle.setBorderLeft(BorderStyle.THIN);
        numStyle.setBorderRight(BorderStyle.THIN);
        numStyle.setBorderTop(BorderStyle.THIN);

        // 标题字体样式
        XSSFFont titleFont = wk.createFont();
        // 加粗
        titleFont.setBold(false);
        // 字体名称
        titleFont.setFontName("宋体");
        // 字体大小
        titleFont.setFontHeight(16);

        // 标题样式
        XSSFCellStyle titleStyle = wk.createCellStyle();
        titleStyle.setFont(titleFont);
        // 竖向居中
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 横向居中
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        // 边框
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderLeft(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setBorderTop(BorderStyle.THIN);

        // 合并单元格（第一行、标题）
        CellRangeAddress cAddress = new CellRangeAddress(0, 0, 0, 3);
        sheet.addMergedRegion(cAddress);

        // 合并单元格（第一个分类）
        CellRangeAddress cAddress2 = new CellRangeAddress(2, 3, 0, 0);
        sheet.addMergedRegion(cAddress2);

        // 创建第一行
        XSSFRow row1 = sheet.createRow(0);
        // 创建第一行第一列
        XSSFCell row1Cell1 = row1.createCell(0);
        row1Cell1.setCellValue("title");
        row1Cell1.setCellStyle(titleStyle);
        XSSFCell row1Cell2 = row1.createCell(1);
        // 为了保证合并的单元格能有效追加外框、被合并的单元格、内容要设置为空
        row1Cell2.setCellValue("");
        row1Cell2.setCellStyle(titleStyle);
        XSSFCell row1Cell3 = row1.createCell(2);
        row1Cell3.setCellValue("");
        row1Cell3.setCellStyle(titleStyle);
        XSSFCell row1Cell4 = row1.createCell(3);
        row1Cell4.setCellValue("");
        row1Cell4.setCellStyle(titleStyle);

        // 创建第二行
        XSSFRow row2 = sheet.createRow(1);
        // 创建第二行第一列
        XSSFCell row2Cell1 = row2.createCell(0);
        row2Cell1.setCellValue("分类");
        row2Cell1.setCellStyle(headStyle);
        // 列宽
        sheet.setColumnWidth(row2Cell1.getColumnIndex(), 60 * 50);
        // 创建第二行第二列
        XSSFCell row2Cell2 = row2.createCell(1);
        row2Cell2.setCellValue("内容");
        row2Cell2.setCellStyle(headStyle);
        // 列宽
        sheet.setColumnWidth(row2Cell2.getColumnIndex(), 356 * 50);
        // 创建第二行第三列
        XSSFCell row2Cell3 = row2.createCell(2);
        row2Cell3.setCellValue("标准");
        row2Cell3.setCellStyle(headStyle);
        // 列宽
        sheet.setColumnWidth(row2Cell3.getColumnIndex(), 70 * 50);
        // 创建第二行第四列
        XSSFCell row2Cell4 = row2.createCell(3);
        row2Cell4.setCellValue("备注");
        row2Cell4.setCellStyle(headStyle);
        // 列宽
        sheet.setColumnWidth(row2Cell4.getColumnIndex(), 70 * 50);

        // 创建第三行
        XSSFRow row3 = sheet.createRow(2);
        // 创建第三行第一列
        XSSFCell row3Cell1 = row3.createCell(0);
        row3Cell1.setCellValue("分类1");
        row3Cell1.setCellStyle(contentStyle);
        // 创建第三行第二列
        XSSFCell row3Cell2 = row3.createCell(1);
        row3Cell2.setCellValue("AAAAAAAAAAAAAAAAAAAAAA");
        row3Cell2.setCellStyle(contentStyle);
        // 创建第三行第三列
        XSSFCell row3Cell3 = row3.createCell(2);
        row3Cell3.setCellValue(10);
        row3Cell3.setCellStyle(numStyle);
        // 创建第三行第四列
        XSSFCell row3Cell4 = row3.createCell(3);
        row3Cell4.setCellValue(6);
        row3Cell4.setCellStyle(numStyle);

        // 创建第四行
        XSSFRow row4 = sheet.createRow(3);
        // 创建第四行第一列
        XSSFCell row4Cell1 = row4.createCell(0);
        row4Cell1.setCellValue("");
        row4Cell1.setCellStyle(contentStyle);
        // 创建第四行第二列
        XSSFCell row4Cell2 = row4.createCell(1);
        row4Cell2.setCellValue("BBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        row4Cell2.setCellStyle(contentStyle);

        // 创建第四行第三列
        XSSFCell row4Cell3 = row4.createCell(2);
        row4Cell3.setCellValue(10);
        row4Cell3.setCellStyle(numStyle);
        // 创建第四行第四列
        XSSFCell row4Cell4 = row4.createCell(3);
        row4Cell4.setCellValue(6);
        row4Cell4.setCellStyle(numStyle);

        // 创建第五行
        XSSFRow row5 = sheet.createRow(4);
        // 创建第五行第一列
        XSSFCell row5Cell1 = row5.createCell(0);
        row5Cell1.setCellValue("分类2");
        row5Cell1.setCellStyle(contentStyle);
        // 创建第五行第二列
        XSSFCell row5Cell2 = row5.createCell(1);
        row5Cell2.setCellValue("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
        row5Cell2.setCellStyle(contentStyle);
        // 创建第五行第三列
        XSSFCell row5Cell3 = row5.createCell(2);
        row5Cell3.setCellValue(10);
        row5Cell3.setCellStyle(numStyle);
        // 创建第五行第四列
        XSSFCell row5Cell4 = row5.createCell(3);
        row5Cell4.setCellValue(6);
        row5Cell4.setCellStyle(numStyle);

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(xlsPath);
            wk.write(outputStream);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadExcel() throws Exception {
        //创建输入流
        FileInputStream fis = new FileInputStream(new File("E:\\工作资料\\erp运维\\日清日结\\统计软件\\excel\\发货到接收-dj.xlsx"));
        //通过构造函数传参
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            boolean errorFlag = true;
            StringBuffer sb = new StringBuffer("您的Excel文档存在错误信息，请修改后重新导入：\n");
            //添加一页
            Sheet newSheet = workbook.createSheet();

            Sheet sheet = workbook.getSheetAt(1);
            //总行数
            int trLength = sheet.getLastRowNum();
            //4.得到Excel工作表的行
            Row row = sheet.getRow(3);
            //获取标题对应列的长度
            int tdLength = row.getLastCellNum();
            int count = 0;

            for (int i = 0; i < trLength; i++) {
                row = sheet.getRow(i);
                Cell c3 = row.getCell(3);
                Cell c8 = row.getCell(8);
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
                SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormatStr);
                String c3Str = c3.getStringCellValue() + "";
                if (isValidDate(c3Str)) {
                    Date date1 = sdf.parse(c3Str);
                } else {
                    System.out.println("字符串不合法");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算接收超时的数目
     *
     * @param time

     */
    public void caculateGetTime(int time, File file) {
        //int time = 3;//接收超过的天数
        //int lastDay = 2;//月末的天数
        try {
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            //总行数
            int trLength = sheet.getLastRowNum();
            outAllNum = trLength;
            int count = 0;
            Row row = sheet.getRow(0);
            String creationDate = "";
            String rcvDate = "";
            String shipment = "";
            double value = 0;
            for (int i = 1; i <= trLength; i++) {
                row = sheet.getRow(i);
                creationDate = row.getCell(3).getStringCellValue();
                rcvDate = row.getCell(10).getStringCellValue();
                shipment = row.getCell(5).getStringCellValue();
                if (StringUtils.isEmpty(rcvDate)) {
                    if (!"CANCELLED".equals(shipment)) {
                        count++;
                    }else{
                        row.createCell(14).setCellValue("合格");
                        System.out.println("CANCELLED");
                        //sheet.removeRow(row);
                    }
                } else {
                    if (StringUtils.isEmpty(creationDate)) {
                        //sheet.removeRow(row);
                        row.createCell(14).setCellValue("合格");
                        System.out.println("del");
                        continue;
                    } else {
                        if (!isValidDate(rcvDate)) {
                            rcvDate = convertToValidateDate(rcvDate);
                        }//如果不标准，转标准
                        if (!isValidDate(creationDate)) {
                            creationDate = convertToValidateDate(creationDate);
                        }
                        int days = getDistanOfTime(creationDate, rcvDate);
                        if (days >= time) {
                            count++;
                        }else{
                            //sheet.removeRow(row);
                            row.createCell(14).setCellValue("合格");
                            System.out.println("del2");
                        }
                    }
                }
            }
            rcvNum = count;
            FileOutputStream os = new FileOutputStream(file);
            workbook.write(os);
            fis.close();
            os.close();
            //获取标题对应列的长度
            // int tdLength = row.getLastCellNum();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 计算检验超标的数目
     *
     * @param path
     */
    public void caculateCheckTime(String path,int freestyle) throws Exception {
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
            FileInputStream fis = new FileInputStream(new File(path));
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            //总行数
            int trLength = sheet.getLastRowNum();
            purchaseAllNum = trLength;
            for (int i = 1; i <= trLength; i++) {
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
                    if ("要求检验".equals(receiving_qa) && StringUtils.isEmpty(rejectDate) && StringUtils.isEmpty(returnDate)&&!StringUtils.isEmpty(trancferDate)) {
                        count++;
                        System.out.println("#value");
                    }
                } else {
                    int week = getWeekOfDate(trancferDate);
                    int ouyday = 1;
                    if(freestyle==0&&week==7){ //单休并且周六
                        ouyday = 2;
                    }
                    if(freestyle==1&&week==6){//双休且周五
                        ouyday = 3;
                    }
                    if(freestyle==2&&week==5){//三休且周四
                        ouyday = 4;
                    }
                    disDay = getDistaceTime(trancferDate, acceptDate);
                    if (disDay >= ouyday) {//检验时间大于ouyday
                        count++;
                    }
                }
                /**
                 * 第二步，计算入库超标数
                 */
                if (StringUtils.isEmpty(deliverDate) || StringUtils.isEmpty(acceptDate)) {
                    if ("直接接收".equals(receiving_qa)) {
                        if (!StringUtils.isEmpty(recevDate) && !StringUtils.isEmpty(deliverDate)) {
                            disDay = getDistaceTime(recevDate, deliverDate);
                            if (disDay >= 1) {
                                kuCount++;
                            }
                        }
                    }
                } else {
                    disDay = getDistaceTime(acceptDate, deliverDate);
                    if (disDay >= 1) { //入库时间大于一天
                        kuCount++;
                    }
                }
            }

            System.out.println("过检超标:"+count);
            System.out.println("入库超标:"+kuCount);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取当前日期是周几
     */

    public int getWeekOfDate(String date) throws Exception{
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
        calendar.setTime(sdf.parse(date));
        int i =calendar.get(Calendar.DAY_OF_WEEK);
        return i;
    }
    /**
     * 判断是不是最后几天
     *
     * @param date
     * @return
     */
    public boolean isLastDay(String date, int lastDay) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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
     * 计算时间差
     * param:String start,String end
     */
    public int getDistanOfTime(String start, String end) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
        SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormatStr);
        try {
            Date date1 = sdf.parse(start);
            Date date2 = sdf2.parse(end);
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
     * 计算百分比
     *
     * @param num1
     * @param num2
     * @return
     */
    public String getPercent(int num1, int num2) {
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) num1 / (float) num2 * 100);
        return result + "%";
    }

    /**
     * 获取业务数据比例
     */

    public double getPercentDouble(int num1,int num2){
        DecimalFormat df=new DecimalFormat("0.0000");
        String re = df.format(num1/(float)num2);
        double dr = Double.parseDouble(re);
        return dr;
    }

    /**
     * 获取业务数据比例
     */
    public double getPercentString(String num1,String num2){
        BigDecimal num1Decimal = new BigDecimal(num1);
        BigDecimal num2Decimal = new BigDecimal(num2);
        BigDecimal invPercent = num1Decimal.divide(num2Decimal,4,BigDecimal.ROUND_HALF_UP);
        double percent = Double.valueOf(invPercent.toString());
        return percent;
    }

    /**
     * 生成 Excel
     */

    public void writeExcel(String finalXlsxPath) throws IOException {
        //创建工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();
        //创建表单
        XSSFSheet sheet = workbook.createSheet("0");

        //创建Excel
        //genExcel(sheet,titleStyle,contextStyle);
        XSSFRow row = sheet.createRow(0);//创建第一行，为标题，index从0开始
        XSSFCell cell;
        cell = row.createCell(0);//创建一列
        cell.setCellValue("xxx 幼儿园一年级二班学生信息");//标题
        row = sheet.createRow(1);//创建第二行
        cell = row.createCell(0);//创建第二行第一列
        cell.setCellValue("姓名");//第二行第一列内容

        //将生成的Excel文件保存到本地
        FileOutputStream out = new FileOutputStream(new File(finalXlsxPath));
        //将工作薄写入文件输出流中
        workbook.write(out);
        //文本文件输出流，释放资源
        out.close();

    }

    /**
     * 拷贝文件
     * @param source
     * @param dest
     * @throws IOException
     */
    private  void copyFileUsingFileChannels(File source, File dest) throws IOException {
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

    /**
     * 采购价差
     * @param path
     */
    private void caculateGeneratorPrice(String path) {
        int count = 0;
        Row row;
        BigDecimal purNum = null;//采购入库数量
        BigDecimal purPrice = null;//采购单价
        BigDecimal purGapPrice = null;//采购差异

        BigDecimal gapSum = new BigDecimal("0");
        BigDecimal purSum = new BigDecimal("0");
        try {
            FileInputStream fis = new FileInputStream(new File(path));
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
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

                gapSum = gapSum .add(purGapPrice);
                purSum = purSum.add(purNum.multiply(purPrice));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(gapSum);
        System.out.println(purSum);
    }

    /**
     * 计算发票价差
     * @param path
     */
    private void caculateInvoice(String path) {
        Row row;//行
        int countHandGap = 0;//手工价差数
        int countTenToFive = 0;//10%<=X<50%
        int countFiveToHundrad = 0;//50%<=X<100%
        int countHundrad = 0;//100%<=X

        BigDecimal invPercent = new BigDecimal("0");//价差比
        double invNumD = 0;//开票数量
        double invPriceD = 0;//单价
        double invGapPriceD = 0;//发票价差


        BigDecimal gapSum = new BigDecimal("0");
        BigDecimal purSum = new BigDecimal("0");
        try {
            FileInputStream fis = new FileInputStream(new File(path));
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            //总行数
            int trLength = sheet.getLastRowNum();
            for (int i = 6; i <= trLength; i++) {
                row = sheet.getRow(i);
                invNumD = row.getCell(11).getNumericCellValue();
                invPriceD = row.getCell(10).getNumericCellValue();
                invGapPriceD = row.getCell(12).getNumericCellValue();
                if(invNumD==0||invPriceD==0){
                    if(invGapPriceD>1) countHandGap++;
                }else{
                    BigDecimal invNum = new BigDecimal(Double.toString(invNumD));
                    BigDecimal invPrice = new BigDecimal(Double.toString(invPriceD));
                    BigDecimal invGapPrice = new BigDecimal(Double.toString(invGapPriceD));
                    invPercent = invGapPrice.divide(invNum,8,BigDecimal.ROUND_HALF_UP);
                    invPercent = invPercent.divide(invPrice,10,BigDecimal.ROUND_HALF_UP);
                    double percent = Double.valueOf(invPercent.toString());
                    if(percent>=0.1&&percent<0.5){
                        countTenToFive += 1;
                    }
                    if(percent>=0.5&&percent<1){
                        countFiveToHundrad += 1;
                    }
                    if(percent>=1){
                        countHundrad += 1;
                    }
                }
            }
            System.out.println(countTenToFive);
            System.out.println(countFiveToHundrad);
            System.out.println(countHundrad);
            System.out.println(countHandGap);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<EachMonthData> initSixData() {
        List<EachMonthData> list = new ArrayList<>();
        list.add(initData(102,"山推"));
        list.add(initData(102,"山推"));
        list.add(initData(102,"山推"));
        list.add(initData(102,"山推"));
        list.add(initData(102,"山推"));
        list.add(initData(102,"山推"));
        return list;
    }

    public EachMonthData initData(int cid,String cname){
        EachMonthData data = new EachMonthData();
        data.setMonthno(11);
        data.setInvoicenum(3056);
        data.setOutallnum(11763);
        data.setPurchaseallnum(8205);
        data.setRcvnum(3540);
        data.setInstocknum(355);
        data.setChecknum(665);

        data.setInvoicehand(16);
        data.setInvoiceten(721);
        data.setInvoicefive(67);
        data.setInvoicebai(1);
        data.setPurchasemoney("42886898.4069730");
        data.setGapmoney("3144548.29");
        data.setCompanyname(cname);
        data.setId(cid);
        return data;
    }
    /**
     * 统计所有数据比例总表
     */

    public void caculateBusyExcel(String path,String outPath,int year,int month) {
        File moban = new File(path);
        File outFile = new File(outPath);
        FileOutputStream out = null;
        EachMonthData data = initData(102,"山推");
        Cell cell;
        try{
            copyFileUsingFileChannels(moban,outFile);//拷贝一份模版出来
            FileInputStream fis = new FileInputStream(outFile);//读写拷贝文件
            //创建工作薄
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            //创建表单
            XSSFSheet sheet = workbook.getSheetAt(1);
            XSSFCellStyle cellStyle = workbook.createCellStyle();
            Row row = sheet.getRow(30);
            cell = row.getCell(1);
            String x = "4312.55447115";
            cell.setCellValue(x);

            //创建文件输出流，输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out = new FileOutputStream(outFile);
            workbook.write(out);

        }catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeCompanyDataToExcel(String path,String outPath,int columnNo,int rowNo,EachMonthData record,List<EachMonthData>sixMonthData){
        File moban = new File(path);
        File outFile = new File(outPath);
        FileOutputStream out = null;
        Cell cell;

        double  rcvPercent = 0.0;
        double purPercent = 0.0;
        double checkPercent = 0.0;
        double instockPercent = 0.0;
        double invPercent = 0.0;

        try{
            copyFileUsingFileChannels(moban,outFile);//拷贝一份模版出来
            FileInputStream fis = new FileInputStream(outFile);//读写拷贝文件
            //创建工作薄
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            //创建表单
            XSSFSheet sheet = workbook.getSheetAt(0);
            int differentNo = record.getInvoicehand()+record.getInvoiceten()+record.getInvoicefive()+record.getInvoicebai();
            rcvPercent = getPercentDouble(record.getRcvnum(),record.getOutallnum());
            checkPercent = getPercentDouble(record.getChecknum(),record.getPurchaseallnum());
            instockPercent = getPercentDouble(record.getInstocknum(),record.getPurchaseallnum());
            purPercent = getPercentString(record.getGapmoney(),record.getPurchasemoney());
            invPercent = getPercentDouble(differentNo,record.getInvoicenum());

            //业务数据分析
            Row row = sheet.getRow(2);
            cell = row.getCell( columnNo);
            cell.setCellValue(rcvPercent);

            row = sheet.getRow(3);
            cell = row.getCell( columnNo);
            cell.setCellValue(checkPercent);

            row = sheet.getRow(4);
            cell = row.getCell( columnNo);
            cell.setCellValue(instockPercent);

            row = sheet.getRow(5);
            cell = row.getCell( columnNo);
            cell.setCellValue(purPercent);

            row = sheet.getRow(6);
            cell = row.getCell( columnNo);
            cell.setCellValue(invPercent);

            //接收业务数据
            row  = sheet.getRow(10);
            cell = row.getCell( columnNo);
            cell.setCellValue(record.getOutallnum());

            row  = sheet.getRow(11);
            cell = row.getCell( columnNo);
            cell.setCellValue(record.getRcvnum());

            row  = sheet.getRow(12);
            cell = row.getCell( columnNo);
            cell.setCellValue(rcvPercent);

            row = sheet.getRow(rowNo);
            cell = row.getCell( 8);
            cell.setCellValue(record.getOutallnum());
            cell = row.getCell( 9);
            cell.setCellValue(record.getRcvnum());
            cell = row.getCell( 10);
            cell.setCellValue(rcvPercent);

            //采购价差分析

            row  = sheet.getRow(18);
            cell = row.getCell( columnNo);
            cell.setCellValue(record.getPurchasemoney());

            row  = sheet.getRow(19);
            cell = row.getCell( columnNo);
            cell.setCellValue(record.getGapmoney());

            row  = sheet.getRow(20);
            cell = row.getCell( columnNo);
            cell.setCellValue(purPercent);

            /*创建该事业部的sheet-----------------------------sheet分割线--------------------------------*/
            XSSFSheet sheet2 = workbook.getSheetAt(columnNo);

            //业务分析
            double  rcvPercentD = 0.0;
            double checkPercentD = 0.0;
            double instockPercentD = 0.0;

            for(int i=0;i<sixMonthData.size();i++){
                EachMonthData d = sixMonthData.get(i);
                rcvPercentD = getPercentDouble(d.getRcvnum(),d.getOutallnum());
                checkPercentD = getPercentDouble(d.getChecknum(),d.getPurchaseallnum());
                instockPercentD = getPercentDouble(d.getInstocknum(),d.getPurchaseallnum());

                row  = sheet2.getRow(2);
                cell = row.getCell( i+1);
                cell.setCellValue(record.getMonthno()+"月");

                row  = sheet2.getRow(3);
                cell = row.getCell(i+1);
                cell.setCellValue(rcvPercentD);

                row  = sheet2.getRow(4);
                cell = row.getCell(i+1);
                cell.setCellValue(checkPercentD);

                row  = sheet2.getRow(5);
                cell = row.getCell(i+1);
                cell.setCellValue(instockPercentD);
            }

            row  = sheet2.getRow(8);
            cell = row.getCell(1);
            cell.setCellValue(record.getOutallnum());
            cell = row.getCell(2);
            cell.setCellValue(record.getRcvnum());
            cell = row.getCell(3);
            cell.setCellValue(rcvPercent);

            row  = sheet2.getRow(9);
            cell = row.getCell(1);
            cell.setCellValue(record.getPurchaseallnum());
            cell = row.getCell(2);
            cell.setCellValue(record.getChecknum());
            cell = row.getCell(3);
            cell.setCellValue(checkPercent);

            row  = sheet2.getRow(10);
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
            diffPercent = getPercentDouble(differentNo,invoiceNum);
            tenPercent = getPercentDouble(record.getInvoiceten(),invoiceNum);
            fivePercent = getPercentDouble(record.getInvoicefive(),invoiceNum);
            baiPercent = getPercentDouble(record.getInvoicebai(),invoiceNum);
            handPercent = getPercentDouble(record.getInvoicehand(),invoiceNum);

            row  = sheet2.getRow(15);
            cell = row.getCell(2);
            cell.setCellValue(record.getInvoicenum());


            row  = sheet2.getRow(16);
            cell = row.getCell(1);
            cell.setCellValue(invPercent);
            cell = row.getCell(2);
            cell.setCellValue(differentNo);

            row  = sheet2.getRow(17);
            cell = row.getCell(1);
            cell.setCellValue(tenPercent+fivePercent+baiPercent);
            cell = row.getCell(2);
            cell.setCellValue(differentNo-record.getInvoicehand());

            row  = sheet2.getRow(18);
            cell = row.getCell(1);
            cell.setCellValue(1-diffPercent);
            cell = row.getCell(2);
            cell.setCellValue(record.getInvoicenum()-differentNo);

            row  = sheet2.getRow(19);
            cell = row.getCell(1);
            cell.setCellValue(tenPercent);
            cell = row.getCell(2);
            cell.setCellValue(record.getInvoiceten());

            row  = sheet2.getRow(20);
            cell = row.getCell(1);
            cell.setCellValue(fivePercent);
            cell = row.getCell(2);
            cell.setCellValue(record.getInvoicefive());

            row  = sheet2.getRow(21);
            cell = row.getCell(1);
            cell.setCellValue(baiPercent);
            cell = row.getCell(2);
            cell.setCellValue(record.getInvoicebai());

            row  = sheet2.getRow(22);
            cell = row.getCell(1);
            cell.setCellValue(handPercent);
            cell = row.getCell(2);
            cell.setCellValue(record.getInvoicehand());

            /*采购价差分析*/
            for(int i=0;i<sixMonthData.size();i++){
                EachMonthData d = sixMonthData.get(i);
                row  = sheet2.getRow(28+i);
                cell = row.getCell(0);
                cell.setCellValue(d.getMonthno()+"月");
                cell = row.getCell(1);
                double dd = Double.parseDouble(d.getPurchasemoney());
                dd = dd/10000.0;
                BigDecimal b = new BigDecimal(dd);
                double df = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                cell.setCellValue(df);

                cell = row.getCell(2);
                dd = Double.parseDouble(d.getGapmoney());
                dd = dd/10000.0;
                b = new BigDecimal(dd);
                df = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                cell.setCellValue(df);
            }


            //创建文件输出流，输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out = new FileOutputStream(outFile);
            workbook.write(out);

        }catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testMain() {

        /*String savePath =  "E://home//yulong//bak//"+102+"-"+10+"-"+2018+"//bak-" ;
        String filePath =  "E://home//yulong//bak//"+102+"-"+10+"-"+2018+"//";
        File desFile = null;
        String file1Name = savePath+"发货到接收.xlsx";
        String file1Name2 = filePath+"发货到接收.xlsx";
        desFile = new File(file1Name);
        File file1 = new File(file1Name2);
        try {
            desFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        String path ="E:\\工作资料\\日清日结\\统计测试\\日清日结12月份\\发货到接收\\道机.xlsx";
        File desFile = new File(path);
        caculateGetTime(2,desFile);//接收时长

       /* String path ="E:\\工作资料\\erp运维\\日清日结\\统计软件\\excel\\test.xlsx";
        try {
            delExcelRow(path);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    /**
     * Remove a row by its index
     * @param sheet a Excel sheet
     * @param rowIndex a 0 based index of removing row
     */
    public  void removeRow(XSSFSheet sheet, int rowIndex) {
        int lastRowNum=sheet.getLastRowNum();
        if(rowIndex>=0&&rowIndex<lastRowNum)
            sheet.shiftRows(rowIndex+1,lastRowNum,-1);//将行号为rowIndex+1一直到行号为lastRowNum的单元格全部上移一行，以便删除rowIndex行
        if(rowIndex==lastRowNum){
            XSSFRow removingRow=sheet.getRow(rowIndex);
            if(removingRow!=null)
                sheet.removeRow(removingRow);
        }
    }

    private void delExcelRow(String path) {

            try {
                File file = new File(path);
                FileInputStream is = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                XSSFSheet sheet = workbook.getSheetAt(0);
                //removeRow(sheet,13);
                int lastRowNum = sheet.getLastRowNum();
               /* System.out.println("lastRow:"+lastRowNum);
                sheet.shiftRows(3, lastRowNum,1,true,false);//将行号为rowIndex+1一直到行号为lastRowNum的单元格全部上移一行，以便删除rowIndex行
*/
               for(int i=0;i<lastRowNum;i++){
                   Row r = sheet.getRow(i);
                   r.createCell(16).setCellValue("te22st");
               }



               // sheet.removeRow(r);
                /*Cell c = r.getCell(2);
                c.setCellValue("123445");
*/
                //r.createCell(3).setCellValue("fff");
                FileOutputStream os = new FileOutputStream(file);
                workbook.write(os);
                is.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


}
