package com.nissin.daily.service;

import com.nissin.daily.utils.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DownZipService {
    private static final String BASE_PATH = System.getProperty("java.io.tmpdir") + "Resource" + File.separator;
    public void exportZip(HttpServletRequest request, HttpServletResponse response,int cid,int year,int month,String path){
        ZipOutputStream out = null;
        BufferedInputStream bis =  null;
        InputStream in = null;
        String rdomS = UUID.randomUUID().toString();
        String tip = rdomS + File.separator;
        String filePath = path+cid+File.separator+year+"-"+month;
        try {
            createAllWorkbooks(tip);
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + cid+"-"+ year+"-"+ month+"-"+rdomS+".zip");
            File tempZip = new File(BASE_PATH + tip + "temp.zip");
            FileUtils.createZipFile(new File(filePath), new ZipOutputStream(new FileOutputStream(tempZip)));
            System.out.println("Created ZIP File");
            OutputStream os = response.getOutputStream();
            in = new FileInputStream(tempZip);
            bis = new BufferedInputStream(in);
            byte buff[] = new byte[1024];
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileUtils.deleteDir(BASE_PATH);
        }
    }

    /**
     * create mock data
     *
     * */
    public List<Workbook> createAllWorkbooks(String tip) {
        List<Workbook> workbooks = new ArrayList<>();
        OutputStream out = null;
        try {
            for (int i=0;i<100;i++) {
                File tempFile = new File(BASE_PATH + tip + i + ".xlsx");
                tempFile.getParentFile().mkdirs();
                tempFile.createNewFile();
                out = new FileOutputStream(tempFile);
                Workbook workbook = new XSSFWorkbook();
                workbook.createSheet("summary");
                workbook.getSheetAt(0).createRow(0);
                Row row = workbook.getSheetAt(0).getRow(0);
                Cell cell = row.createCell(0);
                cell.setCellValue("Hello Spring Boot.");
                workbooks.add(workbook);
                workbook.write(out);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out!= null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return workbooks;
    }

}
