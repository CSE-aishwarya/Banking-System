package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    public byte[] generateBankStatement(
            User user,
            List<Account> accounts,
            List<Transaction> transactions)
            throws Exception {

        Document document = new Document(
            PageSize.A4, 40, 40, 60, 60);
        ByteArrayOutputStream out =
            new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Colors
        BaseColor bankBlue =
            new BaseColor(26, 35, 126);
        BaseColor lightBlue =
            new BaseColor(232, 234, 246);
        BaseColor green =
            new BaseColor(46, 125, 50);
        BaseColor red =
            new BaseColor(198, 40, 40);
        BaseColor gray =
            new BaseColor(245, 245, 245);

        // Fonts
        Font titleFont = new Font(
            Font.FontFamily.HELVETICA, 22,
            Font.BOLD, BaseColor.WHITE);
        Font headerFont = new Font(
            Font.FontFamily.HELVETICA, 11,
            Font.BOLD, bankBlue);
        Font normalFont = new Font(
            Font.FontFamily.HELVETICA, 9,
            Font.NORMAL, BaseColor.DARK_GRAY);
        Font boldFont = new Font(
            Font.FontFamily.HELVETICA, 9,
            Font.BOLD, BaseColor.BLACK);
        Font smallFont = new Font(
            Font.FontFamily.HELVETICA, 8,
            Font.NORMAL, BaseColor.GRAY);

        // ===== HEADER =====
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(bankBlue);
        headerCell.setPadding(20);
        headerCell.setBorder(Rectangle.NO_BORDER);

        Paragraph bankName = new Paragraph(
            "🏦 SECUREBANK", titleFont);
        bankName.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(bankName);

        Font subTitleFont = new Font(
            Font.FontFamily.HELVETICA, 11,
            Font.NORMAL, BaseColor.WHITE);
        Paragraph subTitle = new Paragraph(
            "Official Account Statement",
            subTitleFont);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(subTitle);

        header.addCell(headerCell);
        document.add(header);
        document.add(Chunk.NEWLINE);

        // ===== STATEMENT INFO =====
        DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern(
                "dd MMM yyyy HH:mm");
        String generatedOn =
            LocalDateTime.now().format(fmt);

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10);

        // Left - Customer Info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setBackgroundColor(lightBlue);
        leftCell.setPadding(15);

        leftCell.addElement(new Paragraph(
            "CUSTOMER DETAILS", headerFont));
        leftCell.addElement(Chunk.NEWLINE);
        leftCell.addElement(new Paragraph(
            "Name    : " + user.getFullName(),
            boldFont));
        leftCell.addElement(new Paragraph(
            "Email   : " + user.getEmail(),
            normalFont));
        leftCell.addElement(new Paragraph(
            "Phone   : " + (user.getPhone() != null ?
                user.getPhone() : "N/A"),
            normalFont));
        leftCell.addElement(new Paragraph(
            "Address : " + (user.getAddress() != null ?
                user.getAddress() : "N/A"),
            normalFont));

        infoTable.addCell(leftCell);

        // Right - Statement Info
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setBackgroundColor(gray);
        rightCell.setPadding(15);

        rightCell.addElement(new Paragraph(
            "STATEMENT DETAILS", headerFont));
        rightCell.addElement(Chunk.NEWLINE);
        rightCell.addElement(new Paragraph(
            "Generated : " + generatedOn,
            normalFont));
        rightCell.addElement(new Paragraph(
            "Role      : " + user.getRole(),
            normalFont));
        rightCell.addElement(new Paragraph(
            "Accounts  : " + accounts.size(),
            normalFont));
        rightCell.addElement(new Paragraph(
            "Statement : Full History",
            normalFont));

        infoTable.addCell(rightCell);
        document.add(infoTable);
        document.add(Chunk.NEWLINE);

        // ===== ACCOUNTS SECTION =====
        Paragraph accTitle = new Paragraph(
            "ACCOUNT SUMMARY", headerFont);
        accTitle.setSpacingBefore(10);
        accTitle.setSpacingAfter(8);
        document.add(accTitle);

        PdfPTable accTable = new PdfPTable(4);
        accTable.setWidthPercentage(100);
        accTable.setWidths(new float[]{3,2,2,2});

        // Account Table Header
        String[] accHeaders = {
            "Account Number", "Type",
            "Balance", "Status"
        };
        for (String h : accHeaders) {
            PdfPCell cell = new PdfPCell(
                new Phrase(h, new Font(
                    Font.FontFamily.HELVETICA,
                    9, Font.BOLD,
                    BaseColor.WHITE)));
            cell.setBackgroundColor(bankBlue);
            cell.setPadding(8);
            cell.setHorizontalAlignment(
                Element.ALIGN_CENTER);
            accTable.addCell(cell);
        }

        // Account Rows
        for (Account acc : accounts) {
            accTable.addCell(createCell(
                acc.getAccountNumber(),
                normalFont, false));
            accTable.addCell(createCell(
                acc.getAccountType().toString(),
                normalFont, true));
            PdfPCell balCell = createCell(
                "Rs." + acc.getBalance()
                .toPlainString(),
                new Font(Font.FontFamily.HELVETICA,
                    9, Font.BOLD, green), true);
            accTable.addCell(balCell);
            accTable.addCell(createCell(
                acc.getStatus().toString(),
                normalFont, true));
        }

        document.add(accTable);
        document.add(Chunk.NEWLINE);

        // ===== TRANSACTIONS SECTION =====
        Paragraph txTitle = new Paragraph(
            "TRANSACTION HISTORY", headerFont);
        txTitle.setSpacingBefore(10);
        txTitle.setSpacingAfter(8);
        document.add(txTitle);

        if (transactions.isEmpty()) {
            document.add(new Paragraph(
                "No transactions found.",
                normalFont));
        } else {
            PdfPTable txTable = new PdfPTable(5);
            txTable.setWidthPercentage(100);
            txTable.setWidths(
                new float[]{3,2,2,2,2});

            // Transaction Header
            String[] txHeaders = {
                "Transaction ID", "Type",
                "Amount", "Description", "Status"
            };
            for (String h : txHeaders) {
                PdfPCell cell = new PdfPCell(
                    new Phrase(h, new Font(
                        Font.FontFamily.HELVETICA,
                        9, Font.BOLD,
                        BaseColor.WHITE)));
                cell.setBackgroundColor(bankBlue);
                cell.setPadding(8);
                cell.setHorizontalAlignment(
                    Element.ALIGN_CENTER);
                txTable.addCell(cell);
            }

            // Transaction Rows
            boolean alternate = false;
            for (Transaction tx : transactions) {
                BaseColor rowColor = alternate ?
                    gray : BaseColor.WHITE;
                alternate = !alternate;

                // Transaction ID
                PdfPCell idCell = new PdfPCell(
                    new Phrase(
                        tx.getTransactionId()
                        .substring(0, 8) + "...",
                        smallFont));
                idCell.setPadding(7);
                idCell.setBackgroundColor(rowColor);
                txTable.addCell(idCell);

                // Type
                PdfPCell typeCell = new PdfPCell(
                    new Phrase(
                        tx.getTransactionType()
                        .toString(), normalFont));
                typeCell.setPadding(7);
                typeCell.setBackgroundColor(rowColor);
                typeCell.setHorizontalAlignment(
                    Element.ALIGN_CENTER);
                txTable.addCell(typeCell);

                // Amount with color
                boolean isCredit =
                    tx.getTransactionType()
                    .toString().equals("DEPOSIT");
                Font amtFont = new Font(
                    Font.FontFamily.HELVETICA,
                    9, Font.BOLD,
                    isCredit ? green : red);
                PdfPCell amtCell = new PdfPCell(
                    new Phrase("Rs." +
                        tx.getAmount()
                        .toPlainString(), amtFont));
                amtCell.setPadding(7);
                amtCell.setBackgroundColor(rowColor);
                amtCell.setHorizontalAlignment(
                    Element.ALIGN_CENTER);
                txTable.addCell(amtCell);

                // Description
                PdfPCell descCell = new PdfPCell(
                    new Phrase(
                        tx.getDescription() != null ?
                        tx.getDescription() : "-",
                        normalFont));
                descCell.setPadding(7);
                descCell.setBackgroundColor(rowColor);
                txTable.addCell(descCell);

                // Status
                PdfPCell statusCell = new PdfPCell(
                    new Phrase(
                        tx.getStatus().toString(),
                        normalFont));
                statusCell.setPadding(7);
                statusCell.setBackgroundColor(rowColor);
                statusCell.setHorizontalAlignment(
                    Element.ALIGN_CENTER);
                txTable.addCell(statusCell);
            }

            document.add(txTable);
        }

        // ===== FOOTER =====
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);

        PdfPCell footerCell = new PdfPCell();
        footerCell.setBackgroundColor(bankBlue);
        footerCell.setPadding(12);
        footerCell.setBorder(Rectangle.NO_BORDER);

        Font footerFont = new Font(
            Font.FontFamily.HELVETICA, 8,
            Font.NORMAL, BaseColor.WHITE);

        Paragraph footerText = new Paragraph(
            "This is a computer generated statement " +
            "and does not require a signature.\n" +
            "SecureBank | support@securebank.com | " +
            "www.securebank.com",
            footerFont);
        footerText.setAlignment(Element.ALIGN_CENTER);
        footerCell.addElement(footerText);
        footer.addCell(footerCell);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    private PdfPCell createCell(
            String text, Font font,
            boolean center) {
        PdfPCell cell = new PdfPCell(
            new Phrase(text, font));
        cell.setPadding(7);
        if (center) {
            cell.setHorizontalAlignment(
                Element.ALIGN_CENTER);
        }
        return cell;
    }
}