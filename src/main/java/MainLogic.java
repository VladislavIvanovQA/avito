import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import models.*;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class MainLogic {
    public static final String USERAGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36";
    final static List<String> headers1 = Arrays.asList("Заголовок", "Тип", "Тип сдачи", "Цена",
            "Кол-во квадаратов", "Площадь земли", "Тип материалов",
            "Кордината (Lat)", "Кордината (Lng)",
            "Полный адресс", "Картинки 208х156", "Картинки 416х312");

    public static void main(String[] args) {
        ItemsList root = new ItemsList();
        for (int i = 1; i < 21; i++) {
            String request = "https://www.avito.ru/js/v2/map/items?" +
                    "categoryId=25&" +
                    "locationId=621540&" +
                    "correctorMode=1&" +
                    "page=" + i + "&" +
                    "map=eyJzZWFyY2hBcmVhIjp7ImxhdEJvdHRvbSI6NDIuMTcxMDYyMTQwNzQ4MjYsImxhdFRvcCI6NjUuMjk3Nzg2MzQ2MTQ1NDcsImxvbkxlZnQiOi00LjQyMjczNDM3NTAwMDA5NSwibG9uUmlnaHQiOjE2NS41NTc3MzQzNzQ5OTk4OH0sInpvb20iOjN9&params%5B202%5D=1065&" +
                    "params%5B557%5D%5B0%5D=5737&" +
                    "params%5B557%5D%5B1%5D=5740&" +
                    "params%5B557%5D%5B2%5D=5739&" +
                    "verticalCategoryId=1&" +
                    "rootCategoryId=4&" +
                    "searchArea%5BlatBottom%5D=42.17106214074826&" +
                    "searchArea%5BlonLeft%5D=-4.422734375000095&" +
                    "searchArea%5BlatTop%5D=65.29778634614547&" +
                    "searchArea%5BlonRight%5D=165.55773437499988&" +
                    "viewPort%5Bwidth%5D=966.602294921875&" +
                    "viewPort%5Bheight%5D=230&" +
                    "limit=50";
            log.info("Start parse page: {}", i);
            root.addedItems(sendRequestAndGetResult(request).getItems());
            Random random = new Random();
            long timeWait = random.nextInt(120000);
            try {
                log.info("Anit-Block system wait: {} sec.", timeWait / 1000);
                Thread.sleep(timeWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("Start generate excel file");
        generateExcelFile(root);
    }

    private static Root sendRequestAndGetResult(String request) {
        Document parse = null;
        try {
            parse = Jsoup.connect(request)
                    .ignoreContentType(true)
                    .userAgent(USERAGENT)
                    .method(Connection.Method.GET)
                    .execute()
                    .parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert parse != null;
        String html = parse.html();
        html = html.substring(html.indexOf("{"));
        html = html.substring(0, html.indexOf("</body>") - 2);

        while (true) {
            if (html.indexOf("<") > 0) {
                html = html.replace(html.substring(html.indexOf("<"), html.indexOf(">") + 1), "");
                break;
            }
        }
        html = html.replace(" \n  ", "");

        ObjectMapper om = new ObjectMapper();
        Root root = null;
        try {
            root = om.readValue(html, Root.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info("Correct parse page!");
        return root;
    }

    private static void generateExcelFile(ItemsList root) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Avito");
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headers1.size() - 1));
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers1.size(); i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers1.get(i));
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headers1.size() - 1));
        sheet.createFreezePane(0, 1);

        assert root != null;
        fillRows(root.getItems(), sheet);
        autoSizeColumn(workbook);
        writeFile(workbook);
    }

    private static void writeFile(Workbook main) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        String path = System.getProperty("user.dir");
        String fileFolder = path + File.separator + "resultFile" + File.separator;
        if (!new File(fileFolder).isDirectory()) {
            try {
                Files.createDirectory(Paths.get(fileFolder));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String fileName = "Avito_" + LocalDateTime.now().format(formatter);
        File mainFile = new File(fileFolder + fileName + ".csv");
        try (FileOutputStream outputStream = new FileOutputStream(mainFile.getPath())) {
            main.write(outputStream);
            main.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Save file to: {}", fileFolder + fileName + ".csv");
    }

    public static void fillRows(List<Item> items, Sheet sheet) {
        CellStyle cs = sheet.getWorkbook().createCellStyle();
        cs.setWrapText(true);
        AtomicInteger rowCount = new AtomicInteger();
        items.forEach(item -> {
            Row row = sheet.createRow(rowCount.incrementAndGet());
            Hyperlink hyperlink = sheet.getWorkbook().getCreationHelper().createHyperlink(HyperlinkType.URL);
            hyperlink.setAddress("http://avito.ru" + item.getUrl());
            hyperlink.setLabel(item.getTitle());
            Cell cell = row.createCell(0);
            cell.setCellStyle(cs);
            cell.setCellValue(String.valueOf(hyperlink));
            Ext ext = item.getExt();
            if (ext.getCommission() != null) {
                createNewCell(row, cs, ext.getCommission().equals("Посредник") ? ext.getCommission() + " " + ext.getCommission_amount() : ext.getCommission());
            } else {
                createNewCell(row, cs, "Не указано");
            }
            if (item.getPriceFormatted().getOld_value() != null) {
                createNewCell(row, cs, item.getPriceFormatted().getOld_string() + " " + item.getPriceFormatted().getValue());
            } else {
                createNewCell(row, cs, item.getPriceFormatted().getValue());
            }
            createNewCell(row, cs, ext.getOffer_type());
            createNewCell(row, cs, ext.getHouse_area());
            createNewCell(row, cs, ext.getSite_area());
            createNewCell(row, cs, ext.getMaterial_sten());

            createNewCell(row, cs, item.getCoords().getLat());
            createNewCell(row, cs, item.getCoords().getLng());
            createNewCell(row, cs, item.getCoords().getAddress_user());
            String image_208x156 = item.getImages()
                    .stream()
                    .map(Image::get_208x156)
                    .collect(Collectors.joining("\n"));
            createNewCell(row, cs, image_208x156);

            String image_416x312 = item.getImages()
                    .stream()
                    .map(Image::get_416x312)
                    .collect(Collectors.joining("\n"));
            createNewCell(row, cs, image_416x312);

        });
    }

    private static void autoSizeColumn(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet rowsSheet = workbook.getSheetAt(i);
            if (rowsSheet.getPhysicalNumberOfRows() > 0) {
                Row row = rowsSheet.getRow(rowsSheet.getFirstRowNum());
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    if (!Arrays.asList(3, 5).contains(columnIndex)) {
                        rowsSheet.autoSizeColumn(columnIndex);
                    }
                }
            }
        }
    }

    public static void createNewCell(Row row, CellStyle cs, Object value) {
        Cell cell = row.createCell(row.getLastCellNum());
        cell.setCellStyle(cs);
        try {
            cell.setCellValue(String.valueOf(value));
        } catch (IllegalArgumentException e) {
            cell.setCellValue(String.valueOf(value).substring(0, 32767));
        }
    }
}
