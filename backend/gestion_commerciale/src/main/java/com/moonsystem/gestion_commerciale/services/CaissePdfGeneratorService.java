package com.moonsystem.gestion_commerciale.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.moonsystem.gestion_commerciale.dto.BonSortieDto;
import com.moonsystem.gestion_commerciale.dto.CaisseJourDto;
import com.moonsystem.gestion_commerciale.dto.MesInfoxDto;

import jakarta.annotation.PostConstruct;

@Service
public class CaissePdfGeneratorService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final MesInfoxService mesInfoxService;

    public CaissePdfGeneratorService(MesInfoxService mesInfoxSrv) {
        this.mesInfoxService = mesInfoxSrv;
    }

    private String nomEntreprise;

    // Couleurs exactes de l'image
    private static final Color HEADER_GRAY = new Color(192, 192, 192);      // Gris en-tête
    private static final Color SECTION_HEADER = new Color(220, 220, 220);   // Gris sections
    private static final Color WHITE = new Color(255, 255, 255);            // Blanc
    private static final Color BLACK = new Color(0, 0, 0);                  // Noir
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);       // Gris clair totaux
    private static final Color BORDER_BLACK = new Color(0, 0, 0);           // Bordures noires

    @PostConstruct
    public void init() {
        MesInfoxDto dto = mesInfoxService.findById(1);
        this.nomEntreprise = dto.getNomSociete();
    }

    public byte[] generateCaissePdf(CaisseJourDto caisseDto) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        document.open();

        // Configuration des polices exactes
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BLACK);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BLACK);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BLACK);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(128, 0, 0)); // Rouge foncé pour sections

        // En-tête exacte de l'image
        addExactHeader(document, caisseDto, titleFont, headerFont);

        // Tableau principal avec toutes les sections
        addCompleteMainTable(document, caisseDto, headerFont, normalFont, smallFont, sectionFont);

        // Totaux finaux avec 4 colonnes comme l'image
        addFinalTotalsGrid(document, caisseDto, headerFont);

        document.close();
        return baos.toByteArray();
    }

    private void addExactHeader(Document document, CaisseJourDto caisseDto, Font titleFont, Font headerFont)
            throws DocumentException {

        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{35f, 30f, 35f});
        headerTable.setSpacingAfter(5);

        // === Cellule gauche ===
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph leftPara = new Paragraph();
        leftPara.add(new Chunk("Caisse du : ", headerFont));
        leftPara.add(new Chunk(caisseDto.getDate().format(DATE_FORMATTER), titleFont));
        leftPara.setAlignment(Element.ALIGN_CENTER); // ✅ Centrer texte
        leftCell.addElement(leftPara);

        // === Cellule centrale ===
        PdfPCell centerCell = new PdfPCell();
        centerCell.setBorder(Rectangle.NO_BORDER);
        centerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        centerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph centerPara = new Paragraph("Caisse journalière", headerFont);
        centerPara.setAlignment(Element.ALIGN_CENTER);
        centerCell.addElement(centerPara);

        // === Cellule droite ===
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph rightPara = new Paragraph(nomEntreprise + ": " + caisseDto.getNomUser(), titleFont);
        rightPara.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(rightPara);

        // === Ajouter les cellules au tableau ===
        headerTable.addCell(leftCell);
        headerTable.addCell(centerCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);
    }

    private void addCompleteMainTable(Document document, CaisseJourDto caisseDto, Font headerFont, Font normalFont, Font smallFont, Font sectionFont)
            throws DocumentException {

        // Tableau principal avec 6 colonnes exactement comme l'image
        PdfPTable mainTable = new PdfPTable(6);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[]{18f, 12f, 12f, 12f, 12f, 34f});

        // En-têtes du tableau
        addMainTableHeaders(mainTable, headerFont);

        // Section 1: Vente Du Jour
        addSectionHeader(mainTable, "Vente Du Jour", sectionFont);
        addVentesData(mainTable, caisseDto, normalFont, smallFont);
        addTotalVentes(mainTable, caisseDto, normalFont);

        // Section 2: Liste Des Achats
//        addSectionHeader(mainTable, "Liste Des Achats", sectionFont);
//        //addAchatsData(mainTable, caisseDto, normalFont, smallFont);
//        addTotalAchats(mainTable, caisseDto, normalFont);
//
//        // Section 3: Charges Divers
//        addSectionHeader(mainTable, "Charges Divers", sectionFont);
//        //addChargesData(mainTable, caisseDto, normalFont, smallFont);
//        addTotalCharges(mainTable, caisseDto, normalFont);
        mainTable.setSpacingAfter(10);
        document.add(mainTable);
    }

    private void addMainTableHeaders(PdfPTable table, Font headerFont) {
        String[] headers = {"Série", "Montant", "Espèce", "Chèque", "Crédit", "Détails"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(HEADER_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            cell.setBorder(Rectangle.BOX);
            cell.setBorderWidth(1);
            cell.setBorderColor(BLACK);
            table.addCell(cell);
        }
    }

    private void addSectionHeader(PdfPTable table, String sectionName, Font sectionFont) {
        PdfPCell sectionCell = new PdfPCell(new Phrase(sectionName, sectionFont));
        sectionCell.setColspan(6);
        sectionCell.setBackgroundColor(SECTION_HEADER);
        sectionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        sectionCell.setPadding(8);
        sectionCell.setBorder(Rectangle.BOX);
        sectionCell.setBorderWidth(1);
        sectionCell.setBorderColor(BLACK);
        table.addCell(sectionCell);
    }

    private void addVentesData(PdfPTable table, CaisseJourDto caisseDto, Font normalFont, Font smallFont) {
        if (caisseDto.getBons() != null && !caisseDto.getBons().isEmpty()) {
            for (BonSortieDto bon : caisseDto.getBons()) {
                addDataRow(table, getBonNumero(bon), getBonMontant(bon),
                        bon.getEspece(), bon.getCheque(), bon.getCredit(),
                        getClientName(bon), normalFont, smallFont);

            }
        }
    }

//    private void addAchatsData(PdfPTable table, CaisseJourDto caisseDto, Font normalFont, Font smallFont) {
//        if (caisseDto.getBons() != null && !caisseDto.getBons().isEmpty()) {
//            for (BonSortieDto bon : caisseDto.getBons()) {
//                if ("ACHAT".equals(bon.getType())) {
//                    addDataRow(table, getBonNumero(bon), getBonMontant(bon),
//                            bon.getEspece(), bon.getCheque(), bon.getCredit(),
//                            getClientName(bon), normalFont, smallFont);
//                }
//            }
//        }
//    }
//    private void addChargesData(PdfPTable table, CaisseJourDto caisseDto, Font normalFont, Font smallFont) {
//        if (caisseDto.getCharges() != null && !caisseDto.getCharges().isEmpty()) {
//            for (Object charge : caisseDto.getCharges()) {
//                // Adapter selon votre DTO de charges
//                addDataRow(table, "CHARGE", new BigDecimal("15.00"),
//                        new BigDecimal("15.00"), null, null,
//                        "LIVRAISON CAMERA IMOU", normalFont, smallFont);
//
//                addDataRow(table, "CHARGE", new BigDecimal("-1000.00"),
//                        new BigDecimal("-1000.00"), null, null,
//                        "HICHAM", normalFont, smallFont);
//            }
//        } else {
//            // Données d'exemple si pas de charges
//            addDataRow(table, "", new BigDecimal("15.00"),
//                    new BigDecimal("15.00"), null, null,
//                    "LIVRAISON CAMERA IMOU", normalFont, smallFont);
//
//            addDataRow(table, "", new BigDecimal("-1000.00"),
//                    new BigDecimal("-1000.00"), null, null,
//                    "HICHAM", normalFont, smallFont);
//        }
//    }
    private void addDataRow(PdfPTable table, String serie, BigDecimal montant,
            BigDecimal espece, BigDecimal cheque, BigDecimal credit,
            String details, Font normalFont, Font smallFont) {

        // Série
        PdfPCell cellSerie = createDataCell(serie, normalFont);
        table.addCell(cellSerie);

        // Montant
        PdfPCell cellMontant = createDataCell(formatMontant(montant), normalFont);
        cellMontant.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellMontant);

        // Espèce
        PdfPCell cellEspece = createDataCell(formatMontant(espece), normalFont);
        cellEspece.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellEspece);

        // Chèque
        PdfPCell cellCheque = createDataCell(formatMontant(cheque), normalFont);
        cellCheque.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellCheque);

        // Crédit
        PdfPCell cellCredit = createDataCell(formatMontant(credit), normalFont);
        cellCredit.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellCredit);

        // Détails
        PdfPCell cellDetails = createDataCell(details, smallFont);
        table.addCell(cellDetails);
    }

    private PdfPCell createDataCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content != null ? content : "", font));
        cell.setPadding(4);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderWidth(1);
        cell.setBorderColor(BLACK);
        cell.setBackgroundColor(WHITE);
        return cell;
    }

    private void addTotalVentes(PdfPTable table, CaisseJourDto caisseDto, Font normalFont) {
        addTotalRow(table, "Total Vte:",
                caisseDto.getTotalMontant(),
                caisseDto.getTotalEspece(),
                caisseDto.getTotalCheque(),
                BigDecimal.ZERO,
                "", normalFont);
    }

    private void addTotalAchats(PdfPTable table, CaisseJourDto caisseDto, Font normalFont) {
        // Calculer les totaux des achats depuis les bons
        BigDecimal totalAchatMontant = BigDecimal.ZERO;
        BigDecimal totalAchatEspece = BigDecimal.ZERO;
        BigDecimal totalAchatCheque = BigDecimal.ZERO;

        if (caisseDto.getBons() != null) {
            for (BonSortieDto bon : caisseDto.getBons()) {
                totalAchatMontant = totalAchatMontant.add(getBonMontant(bon));
                if (bon.getEspece() != null) {
                    totalAchatEspece = totalAchatEspece.add(bon.getEspece());
                }
                if (bon.getCheque() != null) {
                    totalAchatCheque = totalAchatCheque.add(bon.getCheque());
                }

            }
        }

        addTotalRow(table, "Total Achats:", totalAchatMontant, totalAchatEspece, totalAchatCheque, BigDecimal.ZERO, "", normalFont);
    }

    private void addTotalCharges(PdfPTable table, CaisseJourDto caisseDto, Font normalFont) {
        BigDecimal totalCharges = new BigDecimal("-985.00"); // Exemple selon l'image
        addTotalRow(table, "TOTAL des Charges:", totalCharges, totalCharges, BigDecimal.ZERO, BigDecimal.ZERO, "", normalFont);
    }

    private void addTotalRow(PdfPTable table, String label, BigDecimal montant, BigDecimal espece,
            BigDecimal cheque, BigDecimal credit, String details, Font font) {

        // Label
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, font));
        cellLabel.setBackgroundColor(LIGHT_GRAY);
        cellLabel.setPadding(4);
        cellLabel.setBorder(Rectangle.BOX);
        cellLabel.setBorderWidth(1);
        cellLabel.setBorderColor(BLACK);
        table.addCell(cellLabel);

        // Montant
        PdfPCell cellMontant = new PdfPCell(new Phrase(formatMontant(montant), font));
        cellMontant.setBackgroundColor(LIGHT_GRAY);
        cellMontant.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellMontant.setPadding(4);
        cellMontant.setBorder(Rectangle.BOX);
        cellMontant.setBorderWidth(1);
        cellMontant.setBorderColor(BLACK);
        table.addCell(cellMontant);

        // Espèce
        PdfPCell cellEspece = new PdfPCell(new Phrase(formatMontant(espece), font));
        cellEspece.setBackgroundColor(LIGHT_GRAY);
        cellEspece.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellEspece.setPadding(4);
        cellEspece.setBorder(Rectangle.BOX);
        cellEspece.setBorderWidth(1);
        cellEspece.setBorderColor(BLACK);
        table.addCell(cellEspece);

        // Chèque
        PdfPCell cellCheque = new PdfPCell(new Phrase(formatMontant(cheque), font));
        cellCheque.setBackgroundColor(LIGHT_GRAY);
        cellCheque.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellCheque.setPadding(4);
        cellCheque.setBorder(Rectangle.BOX);
        cellCheque.setBorderWidth(1);
        cellCheque.setBorderColor(BLACK);
        table.addCell(cellCheque);

        // Crédit
        PdfPCell cellCredit = new PdfPCell(new Phrase(formatMontant(credit), font));
        cellCredit.setBackgroundColor(LIGHT_GRAY);
        cellCredit.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellCredit.setPadding(4);
        cellCredit.setBorder(Rectangle.BOX);
        cellCredit.setBorderWidth(1);
        cellCredit.setBorderColor(BLACK);
        table.addCell(cellCredit);

        // Détails
        PdfPCell cellDetails = new PdfPCell(new Phrase(details, font));
        cellDetails.setBackgroundColor(LIGHT_GRAY);
        cellDetails.setPadding(4);
        cellDetails.setBorder(Rectangle.BOX);
        cellDetails.setBorderWidth(1);
        cellDetails.setBorderColor(BLACK);
        table.addCell(cellDetails);
    }

    private void addFinalTotalsGrid(Document document, CaisseJourDto caisseDto, Font headerFont)
            throws DocumentException {

        // Grille finale avec 4 colonnes comme dans l'image
        PdfPTable totalGrid = new PdfPTable(4);
        totalGrid.setWidthPercentage(100);
        totalGrid.setWidths(new float[]{25f, 25f, 25f, 25f});

        // Ligne 1: Total Espèce / Ancienne Solde
        addFinalTotalCell(totalGrid, "Total Espèce :", formatMontantVirgule(caisseDto.getTotalEspece()), headerFont);
        //addFinalTotalCell(totalGrid, "Ancienne Solde :", formatMontantVirgule(caisseDto.getAncienneSolde()), headerFont);

        // Ligne 2: Total Chèque / Nouveau Solde
        addFinalTotalCell(totalGrid, "Total Chèque :", formatMontantVirgule(caisseDto.getTotalCheque()), headerFont);
        // addFinalTotalCell(totalGrid, "Nouveau Solde :", formatMontantVirgule(caisseDto.getNouveauSolde()), headerFont);

        totalGrid.setSpacingBefore(10);
        document.add(totalGrid);
    }

    private void addFinalTotalCell(PdfPTable table, String label, String value, Font font) {
        // Label
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBackgroundColor(LIGHT_GRAY);
        labelCell.setPadding(8);
        labelCell.setBorder(Rectangle.BOX);
        labelCell.setBorderWidth(1);
        labelCell.setBorderColor(BLACK);
        table.addCell(labelCell);

        // Value
        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBackgroundColor(LIGHT_GRAY);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(8);
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setBorderWidth(1);
        valueCell.setBorderColor(BLACK);
        table.addCell(valueCell);
    }

    // Méthodes utilitaires
    private String getClientName(BonSortieDto bon) {
        return bon.getNomTier() != null ? bon.getNomTier() : "";
    }

    private String getBonNumero(BonSortieDto bon) {
        return bon.getSerie() != null ? bon.getSerie() : "";
    }

    private BigDecimal getBonMontant(BonSortieDto bon) {
        return bon.getMontant() != null ? bon.getMontant() : BigDecimal.ZERO;
    }

    private String formatMontant(BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) == 0) {
            return "";
        }
        return String.format("%.2f", montant.doubleValue());
    }

    private String formatMontantVirgule(BigDecimal montant) {
        if (montant == null) {
            return "0,00";
        }
        return String.format("%.2f", montant.doubleValue()).replace(".", ",");
    }

    // Méthode pour sauvegarder dans un fichier
    public void saveCaissePdfToFile(CaisseJourDto caisseDto, String filePath)
            throws DocumentException, IOException {
        byte[] pdfBytes = generateCaissePdf(caisseDto);
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfBytes);
        }
    }

}
