/*
package com.nissin.daily.test;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WriteExcel {

    static int j = 0;
    static int q = 0;
    static int m = 0;

    public void writeExcel(List<Map<String, Object>> dataList, String finalXlsxPath) {

        OutputStream out = null;
        FileInputStream fileInputStream = null;
        try {
            // 读取Excel文档
            fileInputStream = new FileInputStream(finalXlsxPath);
            XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);
            // sheet 对应一个工作页
            Sheet sheet = workBook.getSheetAt(0);
            if (sheet == null) {//如果不存在sheet1，建立sheet1
                sheet = workBook.createSheet("sheet1");
            }

            */
/**
             * 往Excel中写新数据
             *//*

//			for (int j = 0; j < dataList.size(); j++) {
            // 创建一行：从第二行开始，跳过属性列
            // 得到要插入的每一条记录
            Map<String, Object> dataMap = dataList.get(0);
            String ObjectId = dataMap.get("Object Id") == null ? "" : dataMap.get("Object Id").toString().trim();
            String TIFPath = dataMap.get("TIF File Path") == null ? "" : dataMap.get("TIF File Path").toString().trim();
            String PDFPath = dataMap.get("PDF File Path") == null ? "" : dataMap.get("PDF File Path").toString().trim();
            String pass = dataMap.get("Pass(Y/N)") == null ? "" : dataMap.get("Pass(Y/N)").toString().trim();
            if (!"".equals(ObjectId)) {
                Row row = sheet.createRow(++j);
                Cell A = row.createCell(0);
                A.setCellValue(ObjectId);
                System.out.println("log写入：" + ObjectId + "j=======" + j);

            }
//				String stringCellValue = row.getCell(1).getStringCellValue();

//				for (int p=++q; p <= sheet.getLastRowNum(); p++) {
//					int p = ++q;
//					Row row1 = sheet.getRow(p); // 获取指定行
            // 遍历该行，获取每个cell元素
////					Cell cell2 = row1.getCell(p); // 获取指定列
//					System.out.println("q========"+q);
//					System.out.println("p========"+p);

            // 在一行内循环
            if (!"".equals(TIFPath) && !"".equals(pass)) {
                int p = ++q;
                Row row1 = sheet.getRow(p);
                System.out.println("q========" + q);
                System.out.println("p========" + p);

                Cell B = row1.createCell(1);
                Cell C = row1.createCell(2);
                B.setCellValue(TIFPath);
                C.setCellValue(pass);
                System.out.println("log写入：" + TIFPath + "---" + pass);
//							break;
            }
//						break;
//					}
            if (!"".equals(PDFPath) && !"".equals(pass)) {
                int n = ++m;
                Row row2 = sheet.getRow(n);
                System.out.println("m========" + m);
                System.out.println("n========" + n);
                Cell D = row2.createCell(3);
                Cell E = row2.createCell(4);
                D.setCellValue(PDFPath);
                E.setCellValue(pass);
                System.out.println("log写入：" + PDFPath + "---" + pass);
            }

//			}
            // 创建文件输出流，准备输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out = new FileOutputStream(finalXlsxPath);
            workBook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("数据导出成功");
    }

    public boolean copyExcel(String fromPath, String toPath) throws IOException {
        //String fromPath = "D:\\share\\jiemu_new\\"; excel存放路径
        //String toPath = "c:\\ok\\"; 保存新EXCEL路径
        // 创建新的excel
        Workbook wbCreat = new XSSFWorkbook();
        File file = new File(fromPath);
        // 打开已有的excel
        InputStream in = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(in);
        Sheet sheet = wb.getSheetAt(0);
        Sheet sheetCreat = wbCreat.createSheet(sheet.getSheetName());
        int firstRow = sheet.getFirstRowNum();
        int lastRow = sheet.getLastRowNum();
        for (int i = firstRow; i <= lastRow; i++) {
            // 创建新建excel Sheet的行
            Row rowCreat = sheetCreat.createRow(i);
            // 取得源有excel Sheet的行
            Row row = sheet.getRow(i);
            // 单元格式样
            int firstCell = row.getFirstCellNum();
            int lastCell = row.getLastCellNum();
            for (int j = firstCell; j < lastCell; j++) {
                System.out.println(row.getCell(j));
                rowCreat.createCell(j);
                String strVal = "";
                if (row.getCell(j) == null) {

                } else {
                    strVal = row.getCell(j).getStringCellValue();
                }
                rowCreat.getCell(j).setCellValue(strVal);
            }
        }
        FileOutputStream fileOut = new FileOutputStream(toPath);
        wbCreat.write(fileOut);
        fileOut.close();
        return false;
    }

    public boolean copyfile(String fromPath, String toPath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fromPath); //文件全路径

            File file = new File(fromPath);
            String absolutePath = file.getAbsolutePath();
            int index = absolutePath.lastIndexOf("\\");
            String filename = absolutePath.substring(index);
            String toPath1 = toPath + "\\" + filename;
            File tofile = new File(toPath1);
            WriteExcel.judeFileExists(tofile);

            FileOutputStream fileOutputStream = new FileOutputStream(toPath + "\\" + filename); //路径
            int len = 0;
            byte temp[] = new byte[1024 * 8];
            ;
            while ((len = fileInputStream.read(temp)) != -1) {
                System.out.println("len=" + len);
                //It is right
                fileOutputStream.write(temp, 0, len);
                //It is wrong
                //fileOutputStream.write(temp);
            }
            fileOutputStream.close();
            fileInputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }


    public void setExcelHeader(String filePath) {
        try {


            // 创建Excel的工作书册 Workbook,对应到一个excel文档
            System.out.println("开始执行test。");
            XSSFWorkbook wb = new XSSFWorkbook();

            Sheet sheet = wb.createSheet("sheet1");
            */
/*Sheet sheet = wb.getSheetAt(0);
			if(sheet==null){//如果不存在sheet1，建立sheet1
				sheet=wb.createSheet("sheet1");
			}*//*

            FileOutputStream out = null;

            */
/**
             * 往Excel中写新数据
             *//*

            Row row = sheet.getRow(0);
            if (row == null) {//如果行不存在，建立行
                row = sheet.createRow(0);
            }
            for (int i = 0; i < 5; i++) {
                Cell cell = row.getCell((short) i);
                if (cell == null) {
                    cell = row.createCell(i);
                }
                switch (i) {
                    case 0:
                        cell.setCellValue("Object Id");
                        break;
                    case 1:
                        cell.setCellValue("TIF File Path");
                        break;
                    case 2:
                        cell.setCellValue("Pass(Y/N)");
                        break;
                    case 3:
                        cell.setCellValue("PDF File Path");
                        break;
                    case 4:
                        cell.setCellValue("Pass(Y/N)");
                        break;
                }
            }

            out = new FileOutputStream(filePath);
            System.out.println("数据写入excel。");
            wb.write(out);
            out.close();
            System.out.println("end。。");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 判断文件是否存在
    public static void judeFileExists(File file) {

        if (file.exists()) {
            System.out.println("file exists");
        } else {
            System.out.println("file not exists, create it ...");
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    // 判断文件夹是否存在
    public static void judeDirExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                System.out.println("dir exists");
            } else {
                System.out.println("the same name file exists, can not create dir");
            }
        } else {
            System.out.println("dir not exists, create it ...");
            file.mkdirs();
        }

    }
}

*/
