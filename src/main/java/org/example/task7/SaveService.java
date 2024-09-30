package org.example.task7;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.task7.model.Item;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

import static org.example.task7.utility.Constants.FORMATTER;

public class SaveService {
    private static final String PATH_TO_DIRECTORY = "src/main/resources/";
    private static final String DELIMITER_FOR_CSV = ",";

    public void saveToCSV(List<Item> items) throws IOException {
        File savedFile = new File(PATH_TO_DIRECTORY + "csvFile.csv");

        try (PrintWriter pw = new PrintWriter(savedFile)) {
            pw.println("№,Наименование,Дата регистрации,Количество,Описание,Изображение");
            items.stream()
                    .map(item -> this.convertToString(item, DELIMITER_FOR_CSV))
                    .forEach(pw::println);
        }
    }

    public void saveToXlsx(List<Item> items) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Items");
        sheet.setColumnWidth(0, 1000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 12000);
        sheet.setColumnWidth(5, 6000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("№");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Наименование");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Дата регистрации");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Количество");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Описание");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Изображение");
        headerCell.setCellStyle(headerStyle);

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        for (int i = 0; i < items.size(); i++) {
            Row row = sheet.createRow(i + 1);
            row.setHeight((short) 3000);

            Cell cell = row.createCell(0);
            cell.setCellValue(items.get(i).getId());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(items.get(i).getName());
            cell.setCellStyle(style);

            cell = row.createCell(2);
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat((short) 14);
            cell.setCellStyle(dateStyle);
            LocalDate actualDate = LocalDate.parse(items.get(i).getRegistrationDate(), FORMATTER);
            cell.setCellValue(actualDate);

            cell = row.createCell(3);
            CellStyle numericStyle = workbook.createCellStyle();
            numericStyle.setDataFormat((short) 2);
            cell.setCellStyle(numericStyle);
            cell.setCellValue(Double.parseDouble(items.get(i).getAmount()));

            cell = row.createCell(4);
            cell.setCellValue(items.get(i).getDescription());
            cell.setCellStyle(style);

            if (items.get(i).getPathToImage() != null) {
                int pictureIdx = workbook.addPicture(getImage(items.get(i).getPathToImage()), Workbook.PICTURE_TYPE_JPEG);
                XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
                XSSFClientAnchor imageAnchor = new XSSFClientAnchor();

                imageAnchor.setCol1(5);
                imageAnchor.setCol2(6);
                imageAnchor.setRow1(i + 1);
                imageAnchor.setRow2(i + 2);

                drawing.createPicture(imageAnchor, pictureIdx);
            }
        }

        File savedFile = new File(PATH_TO_DIRECTORY + "xlsxFile.xlsx");
        FileOutputStream outputStream = new FileOutputStream(savedFile.getAbsolutePath());
        workbook.write(outputStream);
        workbook.close();
    }

    private byte[] getImage(String pathToImage) throws IOException {
        FileInputStream is = new FileInputStream(pathToImage);
        byte[] bytes = IOUtils.toByteArray(is);
        is.close();
        return bytes;
    }

    public String escapeSpecialCharacters(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public String convertToString(Item item, String delimiter) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(item.getId());
        stringBuilder.append(delimiter);
        stringBuilder.append(item.getName());
        stringBuilder.append(delimiter);
        stringBuilder.append(item.getRegistrationDate());
        stringBuilder.append(delimiter);
        stringBuilder.append(item.getAmount());
        stringBuilder.append(delimiter);
        stringBuilder.append(item.getDescription());
        stringBuilder.append(delimiter);
        stringBuilder.append(item.getPathToImage() == null ? "" : item.getPathToImage());
        return stringBuilder.toString();
    }
}
