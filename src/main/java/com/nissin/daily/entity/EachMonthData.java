package com.nissin.daily.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
@Table(name = "each_month_data")
public class EachMonthData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer companyid;

    private String companyname;

    private Integer yearno;

    private Integer monthno;

    private Integer rcvnum;

    private Integer outallnum;

    private Integer checknum;

    private Integer instocknum;

    private Integer purchaseallnum;

    private String purchasemoney;

    private String gapmoney;

    private Integer invoicehand;

    private Integer invoiceten;

    private Integer invoicefive;

    private Integer invoicebai;

    private Integer invoicediff;

    private Integer invoicenum;

    private String code;

    private Integer outday;

    private Integer freestyle;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompanyid() {
        return companyid;
    }

    public void setCompanyid(Integer companyid) {
        this.companyid = companyid;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname == null ? null : companyname.trim();
    }

    public Integer getYearno() {
        return yearno;
    }

    public void setYearno(Integer yearno) {
        this.yearno = yearno;
    }

    public Integer getMonthno() {
        return monthno;
    }

    public void setMonthno(Integer monthno) {
        this.monthno = monthno;
    }

    public Integer getRcvnum() {
        return rcvnum;
    }

    public void setRcvnum(Integer rcvnum) {
        this.rcvnum = rcvnum;
    }

    public Integer getOutallnum() {
        return outallnum;
    }

    public void setOutallnum(Integer outallnum) {
        this.outallnum = outallnum;
    }

    public Integer getChecknum() {
        return checknum;
    }

    public void setChecknum(Integer checknum) {
        this.checknum = checknum;
    }

    public Integer getInstocknum() {
        return instocknum;
    }

    public void setInstocknum(Integer instocknum) {
        this.instocknum = instocknum;
    }

    public Integer getPurchaseallnum() {
        return purchaseallnum;
    }

    public void setPurchaseallnum(Integer purchaseallnum) {
        this.purchaseallnum = purchaseallnum;
    }

    public String getPurchasemoney() {
        return purchasemoney;
    }

    public void setPurchasemoney(String purchasemoney) {
        this.purchasemoney = purchasemoney == null ? null : purchasemoney.trim();
    }

    public String getGapmoney() {
        return gapmoney;
    }

    public void setGapmoney(String gapmoney) {
        this.gapmoney = gapmoney == null ? null : gapmoney.trim();
    }

    public Integer getInvoicehand() {
        return invoicehand;
    }

    public void setInvoicehand(Integer invoicehand) {
        this.invoicehand = invoicehand;
    }

    public Integer getInvoiceten() {
        return invoiceten;
    }

    public void setInvoiceten(Integer invoiceten) {
        this.invoiceten = invoiceten;
    }

    public Integer getInvoicefive() {
        return invoicefive;
    }

    public void setInvoicefive(Integer invoicefive) {
        this.invoicefive = invoicefive;
    }

    public Integer getInvoicebai() {
        return invoicebai;
    }

    public void setInvoicebai(Integer invoicebai) {
        this.invoicebai = invoicebai;
    }

    public Integer getInvoicediff() {
        return invoicediff;
    }

    public void setInvoicediff(Integer invoicediff) {
        this.invoicediff = invoicediff;
    }

    public Integer getInvoicenum() {
        return invoicenum;
    }

    public void setInvoicenum(Integer invoicenum) {
        this.invoicenum = invoicenum;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getFreestyle() {
        return freestyle;
    }

    public void setFreestyle(Integer freestyle) {
        this.freestyle = freestyle;
    }

    public Integer getOutday() {
        return outday;
    }

    public void setOutday(Integer outday) {
        this.outday = outday;
    }
}