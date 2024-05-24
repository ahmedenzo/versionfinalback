package tn.monetique.cardmanagment.repportandstatistique;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.PBFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;
import tn.monetique.cardmanagment.repository.ApplicationDataRecord.PBFApplicationDataRecordRepository;
import tn.monetique.cardmanagment.repository.DataInputCard.CardHolderRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.security.services.UserDetailsImpl;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import tn.monetique.cardmanagment.service.Interface.Card.IEncryptDecryptservi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class Cardstatistique implements  IcardStat {

    @Autowired
    private CardHolderRepository cardHolderRepository;
    @Autowired
    private PBFApplicationDataRecordRepository pbfApplicationDataRecordRepository;
    @Autowired
    private AdminBankRepository adminBankRepository;
    @Autowired
    IEncryptDecryptservi iEncryptDecryptservi;


    @Override
    public List<CardHolder> getCardHoldersByDateIntervalAndBank(LocalDate startDate, LocalDate endDate, Authentication authentication) {


        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(LocalTime.MAX));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);

        if (adminBank == null) {
            throw new AccessDeniedException("User is not allowed to access this resource");
        }

        Long bankId = adminBank.getBank().getBankId();
        List<CardHolder> cardHolders = cardHolderRepository.findCardHoldersByDateIntervalAndBank(startTimestamp, endTimestamp, bankId);
        for (CardHolder cardHolder : cardHolders) {
            // cardHolder.setCardholderNumber(iEncryptDecryptservi.decrypt(cardHolder.getCardholderNumber()));
            cardHolder.setPassportId(cardHolder.getPassportId());
            cardHolder.setName(cardHolder.getName());
            cardHolder.setDate2(cardHolder.getDate2());
            cardHolder.setBranchcode(cardHolder.getBranchcode());
            cardHolder.setCreatedAt(cardHolder.getCreatedAt());
            cardHolder.setCreatedBy(cardHolder.getCreatedBy());
            cardHolder.setCardtype(cardHolder.getCardtype());
            cardHolder.setStatuscard(cardHolder.getStatuscard());

        }
        return cardHolders;

    }


    @Override
    public byte[] generatePdf(List<CardHolder> cardHolders, LocalDate startDate, LocalDate endDate) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, PageSize.A4);

        Paragraph title = new Paragraph("Card Report - " + startDate.toString() + " to " + endDate.toString())
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontColor(ColorConstants.RED)
                .setFontSize(16);
        doc.add(title);

        // Add a new line for separation
        doc.add(new Paragraph("\n"));

        // Create and add the table with card holder data
        Table table = createCardHolderTable(cardHolders);
        doc.add(table);
        doc.add(new Paragraph("\n"));

        // Add the counters in the footer
        int totalGeneratedCards = cardHolders.size();
        Map<String, Integer> typeCounts = new HashMap<>();
        Map<String, Integer> statusCounts = new HashMap<>();

        // Calculate counts
        for (CardHolder cardHolder : cardHolders) {
            typeCounts.put(cardHolder.getCardtype(), typeCounts.getOrDefault(cardHolder.getCardtype(), 0) + 1);
            statusCounts.put(cardHolder.getStatuscard(), statusCounts.getOrDefault(cardHolder.getStatuscard(), 0) + 1);
        }

        // Add the total generated cards
        doc.add(createStyledParagraph("Total generated cards: " + totalGeneratedCards, ColorConstants.LIGHT_GRAY, ColorConstants.BLACK));

        // Add the counts by card type
        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            doc.add(createStyledParagraph("Total cards of type " + entry.getKey() + ": " + entry.getValue(), ColorConstants.LIGHT_GRAY, ColorConstants.BLACK));
        }

        // Add the counts by card status
        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            doc.add(createStyledParagraph("Total cards with status " + entry.getKey() + ": " + entry.getValue(), ColorConstants.LIGHT_GRAY, ColorConstants.BLACK));
        }

        // Close the document
        doc.close();

        // Convert the output stream to a byte array
        return outputStream.toByteArray();
    }

///////////////////EXCEL////////////////////////////////////////
@Override
public byte[] generateExcel(List<CardHolder> cardHolders, LocalDate startDate, LocalDate endDate) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Card Report");

    // Create header row
    Row headerRow = sheet.createRow(0);
    createExcelHeaderCell(headerRow, 0, "Cardholder Name");
    createExcelHeaderCell(headerRow, 1, "Passport ID");
    createExcelHeaderCell(headerRow, 2, "Cardholder Number");
    createExcelHeaderCell(headerRow, 3, "Expiration Date");
    createExcelHeaderCell(headerRow, 4, "Card Type");
    createExcelHeaderCell(headerRow, 5, "Branch Code");
    createExcelHeaderCell(headerRow, 6, "Card Status");
    createExcelHeaderCell(headerRow, 7, "Created By");
    createExcelHeaderCell(headerRow, 8, "Created At");

    // Add cardholders data to the sheet
    int rowNum = 1;
    for (CardHolder cardHolder : cardHolders) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(cardHolder.getName());
        row.createCell(1).setCellValue(cardHolder.getPassportId());
        row.createCell(2).setCellValue(cardHolder.getCardholderNumber());
        row.createCell(3).setCellValue(cardHolder.getDate2());
        row.createCell(4).setCellValue(cardHolder.getCardtype());
        row.createCell(5).setCellValue(cardHolder.getBranchcode());
        row.createCell(6).setCellValue(cardHolder.getStatuscard());
        row.createCell(7).setCellValue(cardHolder.getCreatedBy());
        row.createCell(8).setCellValue(cardHolder.getCreatedAt() != null ? cardHolder.getCreatedAt().toString() : "N/A");
    }

    // Write totals and counts at the end of the sheet
    int totalGeneratedCards = cardHolders.size();
    Map<String, Integer> typeCounts = new HashMap<>();
    Map<String, Integer> statusCounts = new HashMap<>();

    for (CardHolder cardHolder : cardHolders) {
        typeCounts.put(cardHolder.getCardtype(), typeCounts.getOrDefault(cardHolder.getCardtype(), 0) + 1);
        statusCounts.put(cardHolder.getStatuscard(), statusCounts.getOrDefault(cardHolder.getStatuscard(), 0) + 1);
    }

    int footerStartRow = rowNum + 2; // Leave some space between data and footer

    Row totalCardsRow = sheet.createRow(footerStartRow);
    totalCardsRow.createCell(0).setCellValue("Total generated cards: " + totalGeneratedCards);

    int typeCountRowStart = footerStartRow + 1;
    for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
        Row row = sheet.createRow(typeCountRowStart++);
        row.createCell(0).setCellValue("Total cards of type " + entry.getKey() + ": " + entry.getValue());
    }

    int statusCountRowStart = typeCountRowStart + 1;
    for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
        Row row = sheet.createRow(statusCountRowStart++);
        row.createCell(0).setCellValue("Total cards with status " + entry.getKey() + ": " + entry.getValue());
    }

    // Auto-size columns
    for (int i = 0; i < 9; i++) {
        sheet.autoSizeColumn(i);
    }

    // Write to the output stream
    workbook.write(outputStream);
    workbook.close();

    // Convert the output stream to a byte array
    return outputStream.toByteArray();
}

    private void createExcelHeaderCell(Row row, int column, String value) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(column);
        cell.setCellValue(value);
        CellStyle style = row.getSheet().getWorkbook().createCellStyle();
        Font font = row.getSheet().getWorkbook().createFont();
        font.setBold(true);
        style.setFont(font);
        cell.setCellStyle(style);
    }



    // Method to create the table with cardholder data
    private Table createCardHolderTable(List<CardHolder> cardHolders) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 3}))
                .useAllAvailableWidth();

        // Add table headers
        table.addHeaderCell(createpdfHeaderCell("Cardholder Name"));
        table.addHeaderCell(createpdfHeaderCell("Passport ID"));
        table.addHeaderCell(createpdfHeaderCell("Cardholder Number"));
        table.addHeaderCell(createpdfHeaderCell("Expiration Date"));
        table.addHeaderCell(createpdfHeaderCell("Card Type"));
        table.addHeaderCell(createpdfHeaderCell("Branch Code"));
        table.addHeaderCell(createpdfHeaderCell("Card Status"));
        table.addHeaderCell(createpdfHeaderCell("Created By"));
        table.addHeaderCell(createpdfHeaderCell("Created At"));

        // Add cardholders to the table
        for (CardHolder cardHolder : cardHolders) {
            table.addCell(createpdfBodyCell(cardHolder.getName()));
            table.addCell(createpdfBodyCell(cardHolder.getPassportId()));
            table.addCell(createpdfBodyCell(cardHolder.getCardholderNumber()));
            table.addCell(createpdfBodyCell(cardHolder.getDate2()));
            table.addCell(createpdfBodyCell(cardHolder.getCardtype()));
            table.addCell(createpdfBodyCell(cardHolder.getBranchcode()));
            table.addCell(createpdfBodyCell(cardHolder.getStatuscard()));
            table.addCell(createpdfBodyCell(cardHolder.getCreatedBy()));
            table.addCell(createpdfBodyCell(cardHolder.getCreatedAt() != null ? cardHolder.getCreatedAt().toString() : "N/A"));
        }

        return table;
    }

    // Method to create header cells with standard style
    private Cell createpdfHeaderCell(String content) {
        return new Cell()
                .add(new Paragraph(content)
                        .setBold()
                        .setTextAlignment(TextAlignment.LEFT))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(3);
    }

    // Method to create body cells with standard style
    private Cell createpdfBodyCell(String content) {
        Paragraph paragraph = new Paragraph(content != null ? truncateText(content, 26) : "N/A")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(7) // Smaller font size
                .setFixedLeading(8); // Fixed leading for better spacing
        Cell cell = new Cell().add(paragraph)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(3); // Smaller padding
        cell.setKeepTogether(true); // Ensure the content stays together

        return cell;
    }

    // Utility method to truncate text if it's too long
    private String truncateText(String text, int maxLength) {
        if (text.length() > maxLength) {
            return text.substring(0, maxLength) + "...";
        }
        return text;
    }

    // Method to create styled paragraph with border and background color
    private Paragraph createStyledParagraph(String content, Color
            backgroundColor, Color textColor) {
        Paragraph paragraph = new Paragraph(content)
                .setBackgroundColor(backgroundColor)
                .setFontColor(textColor)
                .setPadding(5)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                .setTextAlignment(TextAlignment.LEFT);
        return paragraph;
    }
@Override
    public byte[] generatePdfAndExcel(List<CardHolder> cardHolders, LocalDate startDate, LocalDate endDate) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        // Generate PDF
        byte[] pdfData = generatePdf(cardHolders, startDate, endDate);
        ZipEntry pdfEntry = new ZipEntry("CardReport.pdf");
        zipOutputStream.putNextEntry(pdfEntry);
        zipOutputStream.write(pdfData);
        zipOutputStream.closeEntry();

        // Generate Excel
        byte[] excelData = generateExcel(cardHolders, startDate, endDate);
        ZipEntry excelEntry = new ZipEntry("CardReport.xlsx");
        zipOutputStream.putNextEntry(excelEntry);
        zipOutputStream.write(excelData);
        zipOutputStream.closeEntry();

        zipOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }




    /////////////////////////////////////////////////////////////////PBALANCE///////////////////////////////////////////
    @Override
    public List<PBFApplicationDataRecord> getPBFApplicationDataRecordsByDateIntervalAndBank(LocalDate startDate, LocalDate endDate, Authentication authentication) {
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(LocalTime.MAX));


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);

        if (adminBank == null) {
            throw new AccessDeniedException("User is not allowed to access this resource");
        }

        Long bankId = adminBank.getBank().getBankId();
        return pbfApplicationDataRecordRepository.findByUpdatedAtBetweenAndPbfCardHolder_Bank_BankIdAndPBFgeneratedTrue(startTimestamp, endTimestamp, bankId);
    }

    @Override
    public byte[] generatePdfpbf(List<PBFApplicationDataRecord> pbfApplicationDataRecords, LocalDate startDate, LocalDate endDate) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, PageSize.A4);

        // Add title
        Paragraph title = new Paragraph("PBF Application Data Report - " + startDate.toString() + " to " + endDate.toString())
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontColor(ColorConstants.RED)
                .setFontSize(16);
        doc.add(title);

        // Add a new line for separation
        doc.add(new Paragraph("\n"));

        // Create and add the table with PBF application data records
        Table table = createPBFApplicationDataRecordTable(pbfApplicationDataRecords);
        doc.add(table);

        // Add a new line for separation
        doc.add(new Paragraph("\n"));

        long totalAvailableBalance = pbfApplicationDataRecords.stream().mapToLong(PBFApplicationDataRecord::getAvailBal).sum();
        long totalLedgerBalance = pbfApplicationDataRecords.stream().mapToLong(PBFApplicationDataRecord::getLedgBal).sum();
        int totalRechargeRequests = pbfApplicationDataRecords.size();

        // Add footer with the calculated values
        doc.add(createFooterParagraph("Total number of recharge requests: " + totalRechargeRequests));
        doc.add(createFooterParagraph("Total available balance of charge: " + totalAvailableBalance));
        doc.add(createFooterParagraph("Total ledger balance of charge: " + totalLedgerBalance));


        // Close the document
        doc.close();

        // Convert the output stream to a byte array
        return outputStream.toByteArray();
    }

    @Override
    public byte[] generateExcelpbf(List<PBFApplicationDataRecord> pbfApplicationDataRecords, LocalDate startDate, LocalDate endDate) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("PBF Application Data Report");

            Row headerRow = sheet.createRow(0);
            createExcelHeaderCellpbf(headerRow, 0, "Cardholder Name");
            createExcelHeaderCellpbf(headerRow, 1, "Cardholder Number");
            createExcelHeaderCellpbf(headerRow, 2, "Available Balance");
            createExcelHeaderCellpbf(headerRow, 3, "Ledger Balance");
            createExcelHeaderCellpbf(headerRow, 4, "Updated At");

            int rowNum = 1;
            long totalAvailableBalance = 0;
            long totalLedgerBalance = 0;

            for (PBFApplicationDataRecord record : pbfApplicationDataRecords) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.getPbfCardHolder().getName());
                row.createCell(1).setCellValue(record.getPbfCardHolder().getCardholderNumber());
                row.createCell(2).setCellValue(record.getAvailBal());
                row.createCell(3).setCellValue(record.getLedgBal());
                row.createCell(4).setCellValue(record.getUpdatedAt().toString());

                // Calculate total available balance and ledger balance
                totalAvailableBalance += record.getAvailBal();
                totalLedgerBalance += record.getLedgBal();
            }

            Row blankRow = sheet.createRow(rowNum++);

            // Add total calculation rows
            Row totalRow1 = sheet.createRow(rowNum++);
            totalRow1.createCell(0).setCellValue("Total number of recharge requests:");
            totalRow1.createCell(1).setCellValue(pbfApplicationDataRecords.size());

            Row totalRow2 = sheet.createRow(rowNum++);
            totalRow2.createCell(0).setCellValue("Total available balance of charge:");
            totalRow2.createCell(1).setCellValue(totalAvailableBalance);

            Row totalRow3 = sheet.createRow(rowNum++);
            totalRow3.createCell(0).setCellValue("Total ledger balance of charge:");
            totalRow3.createCell(1).setCellValue(totalLedgerBalance);


            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        }
    }

    private void createExcelHeaderCellpbf(Row row, int column, String value) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(column);
        cell.setCellValue(value);
        CellStyle style = row.getSheet().getWorkbook().createCellStyle();
        Font font = row.getSheet().getWorkbook().createFont();
        font.setBold(true);
        style.setFont(font);
        cell.setCellStyle(style);
    }

    @Override
    public byte[] generatePdfAndExcelPbf(List<PBFApplicationDataRecord> pbfApplicationDataRecords, LocalDate startDate, LocalDate endDate) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        byte[] pdfData = generatePdfpbf(pbfApplicationDataRecords, startDate, endDate);
        ZipEntry pdfEntry = new ZipEntry("PBFApplicationDataReport.pdf");
        zipOutputStream.putNextEntry(pdfEntry);
        zipOutputStream.write(pdfData);
        zipOutputStream.closeEntry();

        byte[] excelData = generateExcelpbf(pbfApplicationDataRecords, startDate, endDate);
        ZipEntry excelEntry = new ZipEntry("PBFApplicationDataReport.xlsx");
        zipOutputStream.putNextEntry(excelEntry);
        zipOutputStream.write(excelData);
        zipOutputStream.closeEntry();

        zipOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }






    private Table createPBFApplicationDataRecordTable(List<PBFApplicationDataRecord> pfbrecords) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 2}))
                .useAllAvailableWidth();

        // Add table headers
        table.addHeaderCell(createHeaderCellpbf("Cardholder Name"));
        table.addHeaderCell(createHeaderCellpbf("Cardholder Number"));
        table.addHeaderCell(createHeaderCellpbf("Available Balance"));
        table.addHeaderCell(createHeaderCellpbf("Ledger Balance"));
        table.addHeaderCell(createHeaderCellpbf("Updated At"));

        for (PBFApplicationDataRecord record : pfbrecords) {
            table.addCell(createBodyCellpbf(record.getPbfCardHolder().getName()));
            table.addCell(createBodyCellpbf(record.getPbfCardHolder().getCardholderNumber()));
            table.addCell(createBodyCellpbf(record.getAvailBal().toString()));
            table.addCell(createBodyCellpbf(record.getLedgBal().toString()));
            table.addCell(createBodyCellpbf(record.getUpdatedAt().toString()));
        }

        return table;
    }

    private Cell createHeaderCellpbf(String content) {
        return new Cell()
                .add(new Paragraph(content)
                        .setBold()
                        .setTextAlignment(TextAlignment.LEFT))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(3);
    }

    private Cell createBodyCellpbf(String content) {
        Paragraph paragraph = new Paragraph(content != null ? truncateText(content, 26) : "N/A")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(7) // Smaller font size
                .setFixedLeading(8); // Fixed leading for better spacing
        Cell cell = new Cell().add(paragraph)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(3) // Smaller padding
                .setBackgroundColor(ColorConstants.LIGHT_GRAY) // Light gray background
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1)); // Border
        cell.setKeepTogether(true); // Ensure the content stays together

        return cell;
    }


    private Paragraph createFooterParagraph(String content) {
        return new Paragraph(content)
                .setTextAlignment(TextAlignment.LEFT)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(5);
    }

}




