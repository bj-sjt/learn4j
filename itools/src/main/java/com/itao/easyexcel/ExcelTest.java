package com.itao.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.itao.easyexcel.bean.ReadData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExcelTest {

    public static void main(String[] args) {
        String filePath = Objects.requireNonNull(ExcelTest.class.getResource("/excel/yg.xlsx")).getPath();
        List<ReadData> datas = new ArrayList<>();
        EasyExcel.read(filePath, ReadData.class, new ReadListener<ReadData>() {
            @Override
            public void invoke(ReadData data, AnalysisContext context) {
                datas.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {

            }

            // 读取头数据
            @Override
            public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                for (Map.Entry<Integer, ReadCellData<?>> entry :headMap.entrySet()) {
                    System.out.println(entry.getKey() + "--" + entry.getValue());
                }
            }
        }).sheet().headRowNumber(2).doRead(); // headRowNumber() 从第几行开始读（默认为第一行）

        EasyExcel.write("C:\\Users\\32088\\Desktop\\yg.xlsx", ReadData.class).sheet().doWrite(datas);
    }

}
