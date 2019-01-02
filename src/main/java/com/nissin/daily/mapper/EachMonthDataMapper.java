package com.nissin.daily.mapper;

import com.nissin.daily.entity.EachMonthData;
import com.nissin.daily.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Mapper
public interface EachMonthDataMapper extends MyMapper<EachMonthData> {

    List<EachMonthData> getSixMonthData(@Param("companyId")Integer companyId, @Param("monthNo")Integer monthNo, @Param("yearNo")Integer yearNo);

    EachMonthData getNowMonthData(@Param("companyId")Integer companyId, @Param("monthNo")Integer monthNo, @Param("yearNo")Integer yearNo);

    List<Map> selectTraineeLimit(Map paraMap);

    long getTraineeTotal(Map paraMap);

    int delSelectData(List ids);
}