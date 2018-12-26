package com.nissin.daily.controller;

import com.nissin.daily.entity.CaculateData;
import com.nissin.daily.entity.Page;
import com.nissin.daily.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/busy")
public class BusinessController {
    /**
     * 注入service
     */
    @Autowired
    private PurchaseService purchaseService;
    @Value(value = "${file.path}")
    private String path;
    @RequestMapping(value = {"caculateBusiness"}, method = RequestMethod.POST)
    public Map<String, Object> caculateBusiness(@RequestParam(value = "file1",required = false) MultipartFile file1,//发货到接收
                                                @RequestParam(value ="file2",required = false) MultipartFile file2,//接收到入库
                                                @RequestParam(value ="file3",required = false) MultipartFile file3,//采购价差文件
                                                @RequestParam(value ="file4",required = false) MultipartFile file4,//发票价差文件
                                                @RequestParam(value ="outDay",required = false) Integer outDay,//超出几天
                                                @RequestParam("cid") Integer  cid,//部门id
                                                @RequestParam("month") Integer  month,//月份
                                                @RequestParam("year") Integer  year,//年份
                                                @RequestParam(value ="freestyle" ,required = false) Integer  freestyle,//休息制度
                                                HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String code = "";
        try {
            String savePath =  this.path+cid+"-"+month+"-"+year+"//" ;
            String file1Name = purchaseService.transferFile(file1,savePath);
            String file2Name =  purchaseService.transferFile(file2,savePath);
            String file3Name =  purchaseService.transferFile(file3,savePath);
            String file4Name =  purchaseService.transferFile(file4,savePath);
            //System.out.println("备份完成");
            //创建源文件
            File fileone = new File(file1Name);
            File filetwo = new File(file2Name);
            File filethree = new File(file3Name);
            File filefour = new File(file4Name);

            purchaseService.doBusiness(fileone, filetwo, filethree, filefour, cid, year, month, outDay,freestyle);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value = {"/caculateBusyExcel"}, method = RequestMethod.GET)
    public void caculateBusyExcel(@RequestParam("month") int month,//月份
                                  @RequestParam("year") int year,//年份
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        String path = this.getClass().getResource("/").getPath() + "templates/业务比例分析.xlsx";
        String outPath = this.getClass().getResource("/").getPath() + "temp/" + year + "-" + month + ".xlsx";
        String filePath = "";
        try {
            filePath = purchaseService.caculateBusyExcel(path, outPath, year, month);
            // 下载文件
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + year + "-" + month + ".xlsx");
            InputStream inputStream = new FileInputStream(new File(filePath));
            OutputStream os = response.getOutputStream();
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
            os.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value="/query")
    public Map query(@RequestParam(value="page", required=false) String page,
                     @RequestParam(value="rows", required=false) String rows){

        Page pageBean = new Page(Integer.parseInt(page), Integer.parseInt(rows));
        Map reMap = new HashMap();
        Map paraMap = new HashMap();

        paraMap.put("firstPage", pageBean.getFirstPage());
        paraMap.put("rows", pageBean.getRows());

        try {
            List<Map> list = purchaseService.showAllTrainee(paraMap);
            long total = purchaseService.getTraineeTotal(paraMap);
            reMap.put("rows", list);     //存放每页记录数
            reMap.put("total", total);   //存放总记录数 ，必须的
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reMap;
    }

    @ResponseBody
    @RequestMapping(value="/saveinfo",method = RequestMethod.POST)
    public Map saveinfo(@RequestParam(value="page", required=false) String page,
                     @RequestParam(value="rows", required=false) String rows){

        Page pageBean = new Page(Integer.parseInt(page), Integer.parseInt(rows));
        Map reMap = new HashMap();
        Map paraMap = new HashMap();

        paraMap.put("firstPage", pageBean.getFirstPage());
        paraMap.put("rows", pageBean.getRows());

        try {
            List<Map> list = purchaseService.showAllTrainee(paraMap);
            long total = purchaseService.getTraineeTotal(paraMap);
            reMap.put("rows", list);     //存放每页记录数
            reMap.put("total", total);   //存放总记录数 ，必须的
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reMap;
    }

}
