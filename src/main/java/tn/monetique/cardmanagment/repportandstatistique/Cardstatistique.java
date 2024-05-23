package tn.monetique.cardmanagment.repportandstatistique;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
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
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atStartOfDay());
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

    // Method to create the table with cardholder data
    private Table createCardHolderTable(List<CardHolder> cardHolders) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 3}))
                .useAllAvailableWidth();

        // Add table headers
        table.addHeaderCell(createHeaderCell("Cardholder Name"));
        table.addHeaderCell(createHeaderCell("Passport ID"));
        table.addHeaderCell(createHeaderCell("Cardholder Number"));
        table.addHeaderCell(createHeaderCell("Expiration Date"));
        table.addHeaderCell(createHeaderCell("Card Type"));
        table.addHeaderCell(createHeaderCell("Branch Code"));
        table.addHeaderCell(createHeaderCell("Card Status"));
        table.addHeaderCell(createHeaderCell("Created By"));
        table.addHeaderCell(createHeaderCell("Created At"));

        // Add cardholders to the table
        for (CardHolder cardHolder : cardHolders) {
            table.addCell(createBodyCell(cardHolder.getName()));
            table.addCell(createBodyCell(cardHolder.getPassportId()));
            table.addCell(createBodyCell(cardHolder.getCardholderNumber()));
            table.addCell(createBodyCell(cardHolder.getDate2()));
            table.addCell(createBodyCell(cardHolder.getCardtype()));
            table.addCell(createBodyCell(cardHolder.getBranchcode()));
            table.addCell(createBodyCell(cardHolder.getStatuscard()));
            table.addCell(createBodyCell(cardHolder.getCreatedBy()));
            table.addCell(createBodyCell(cardHolder.getCreatedAt() != null ? cardHolder.getCreatedAt().toString() : "N/A"));
        }

        return table;
    }

    // Method to create header cells with standard style
    private Cell createHeaderCell(String content) {
        return new Cell()
                .add(new Paragraph(content)
                        .setBold()
                        .setTextAlignment(TextAlignment.LEFT))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(3);
    }

    // Method to create body cells with standard style
    private Cell createBodyCell(String content) {
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


    /////////////////////////////////////////////////////////////////
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




