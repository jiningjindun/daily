package com.nissin.daily.entity;

import java.util.List;

public class SheetData {
    private EachMonthData monthData ;
    private List<EachMonthData> sixMonthData;
    private int rowNo;
    private int columnNo;

    public EachMonthData getMonthData() {
        return monthData;
    }

    public void setMonthData(EachMonthData monthData) {
        this.monthData = monthData;
    }

    public List<EachMonthData> getSixMonthData() {
        return sixMonthData;
    }

    public void setSixMonthData(List<EachMonthData> sixMonthData) {
        this.sixMonthData = sixMonthData;
    }

    public int getRowNo() {
        return rowNo;
    }

    public void setRowNo(int rowNo) {
        this.rowNo = rowNo;
    }

    public int getColumnNo() {
        return columnNo;
    }

    public void setColumnNo(int columnNo) {
        this.columnNo = columnNo;
    }
}
