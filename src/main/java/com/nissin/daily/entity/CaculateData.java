package com.nissin.daily.entity;

public class CaculateData {
    private int rcvNum = 0;//接收时长超标量
    private int outAllNum = 0;//发货单总量
    private int outday = 0;//接收超标天数

    private int checkNum = 0;//检验时长超标总量
    private int inStockNum = 0;//入库时长超标总量
    private int purchaseAllNum = 0;//采购接收总数
    private String purchaseMoney ;//采购总金额
    private String gapMoney;//采购差总金额

    private int countHandGap = 0;//手工价差数
    private int countTenToFive = 0;//10%<=X<50%
    private int countFiveToHundrad = 0;//50%<=X<100%
    private int countHundrad = 0;//100%<=X
    private int invoiceNum = 0; // 发票总数

    public int getRcvNum() {
        return rcvNum;
    }

    public void setRcvNum(int rcvNum) {
        this.rcvNum = rcvNum;
    }

    public int getOutAllNum() {
        return outAllNum;
    }

    public void setOutAllNum(int outAllNum) {
        this.outAllNum = outAllNum;
    }

    public int getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(int checkNum) {
        this.checkNum = checkNum;
    }

    public int getInStockNum() {
        return inStockNum;
    }

    public void setInStockNum(int inStockNum) {
        this.inStockNum = inStockNum;
    }

    public int getPurchaseAllNum() {
        return purchaseAllNum;
    }

    public void setPurchaseAllNum(int purchaseAllNum) {
        this.purchaseAllNum = purchaseAllNum;
    }

    public String getPurchaseMoney() {
        return purchaseMoney;
    }

    public void setPurchaseMoney(String purchaseMoney) {
        this.purchaseMoney = purchaseMoney;
    }

    public String getGapMoney() {
        return gapMoney;
    }

    public void setGapMoney(String gapMoney) {
        this.gapMoney = gapMoney;
    }

    public int getCountHandGap() {
        return countHandGap;
    }

    public void setCountHandGap(int countHandGap) {
        this.countHandGap = countHandGap;
    }

    public int getCountTenToFive() {
        return countTenToFive;
    }

    public void setCountTenToFive(int countTenToFive) {
        this.countTenToFive = countTenToFive;
    }

    public int getCountFiveToHundrad() {
        return countFiveToHundrad;
    }

    public void setCountFiveToHundrad(int countFiveToHundrad) {
        this.countFiveToHundrad = countFiveToHundrad;
    }

    public int getCountHundrad() {
        return countHundrad;
    }

    public void setCountHundrad(int countHundrad) {
        this.countHundrad = countHundrad;
    }

    public int getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(int invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public int getOutday() {
        return outday;
    }

    public void setOutday(int outday) {
        this.outday = outday;
    }
}
