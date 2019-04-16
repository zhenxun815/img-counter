package cn.yiheng.task;

import cn.yiheng.model.ImgInfo;
import cn.yiheng.utils.FileUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Yiheng
 * @create 4/16/2019
 * @since 1.0.0
 */
@Getter
@Setter
@RequiredArgsConstructor(staticName = "with")
public class ExcelTask implements Callable<Boolean> {

    @NonNull
    List<ImgInfo> imgInfos;

    @NonNull
    File excelDir;

    @Override
    public Boolean call() {
        return createExcelFile();
    }

    private Boolean createExcelFile() {
        String[] columns = {"Name", "Size(MB)", "Resolution"};

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;

        for (ImgInfo imgInfo : imgInfos) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
               .setCellValue(imgInfo.getFileName());


            row.createCell(1)
               .setCellValue(imgInfo.getSize() / 1048576.0D);

            row.createCell(2)
               .setCellValue(imgInfo.getResolution().width + "*" + imgInfo.getResolution().height);

        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        File excelFile = new File(excelDir, "imgInfos.xlsx");
        if (FileUtils.createNewFile(excelFile)) {
            try (FileOutputStream fos = new FileOutputStream(excelFile)) {
                workbook.write(fos);
                fos.close();
                workbook.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
}
