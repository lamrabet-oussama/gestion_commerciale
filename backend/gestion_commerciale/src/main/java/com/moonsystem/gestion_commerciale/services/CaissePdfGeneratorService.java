package com.moonsystem.gestion_commerciale.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import com.moonsystem.gestion_commerciale.dto.ReglementDto;
import com.moonsystem.gestion_commerciale.model.Reglement;
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

    // Variables pour calculer les totaux globaux
    private BigDecimal totalEspeceGlobal = BigDecimal.ZERO;
    private BigDecimal totalChequeGlobal = BigDecimal.ZERO;

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
    private static final Color FOOTER_BLUE = new Color(173, 216, 230);      // Bleu clair pour le footer

    @PostConstruct
    public void init() {
        try {
            MesInfoxDto dto = mesInfoxService.findById(1);
            this.nomEntreprise = dto != null ? dto.getNomSociete() : "Ma Société";
        } catch (Exception e) {
            // Fallback en cas d'erreur
            this.nomEntreprise = "Ma Société";
        }
    }

    public byte[] generateCaissePdf(CaisseJourDto caisseDto) throws DocumentException, IOException {
        // Validation des données d'entrée
        if (caisseDto == null) {
            throw new IllegalArgumentException("CaisseJourDto ne peut pas être null");
        }

        // Réinitialiser les totaux globaux à chaque génération
        totalEspeceGlobal = BigDecimal.ZERO;
        totalChequeGlobal = BigDecimal.ZERO;

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
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BLACK);

        // En-tête exacte de l'image
        addExactHeader(document, caisseDto, titleFont, headerFont);

        // Tableau principal avec toutes les sections dans l'ordre modifié
        addCompleteMainTable(document, caisseDto, headerFont, normalFont, smallFont, sectionFont);

        // Footer avec totaux finaux
        addFooterTotals(document, footerFont);

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
        String dateStr = caisseDto.getDate() != null ? caisseDto.getDate().format(DATE_FORMATTER) : "";
        leftPara.add(new Chunk(dateStr, titleFont));
        leftPara.setAlignment(Element.ALIGN_CENTER);
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

        String nomUser = caisseDto.getNomUser() != null ? caisseDto.getNomUser() : "";
        Paragraph rightPara = new Paragraph(nomEntreprise + ": " + nomUser, titleFont);
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

        // ORDRE MODIFIÉ : 1. Vente Du Jour (d'abord)
        if (hasBonsVente(caisseDto)) {
            addSectionHeader(mainTable, "Vente Du Jour", sectionFont);
            addBonsVenteData(mainTable, caisseDto, normalFont, smallFont);
            addTotalBonsVente(mainTable, caisseDto, normalFont);
        }

        // 2. Liste Des Achats (ensuite)
        if (hasBonsAchat(caisseDto)) {
            addSectionHeader(mainTable, "Liste Des Achats", sectionFont);
            addBonsAchatData(mainTable, caisseDto, normalFont, smallFont);
            addTotalBonsAchat(mainTable, caisseDto, normalFont);
        }

        // 3. Règlements de Crédits (en dernier)
        if (hasReglements(caisseDto)) {
            addSectionHeader(mainTable, "Règlements de Crédits", sectionFont);
            addReglementsData(mainTable, caisseDto, normalFont, smallFont);
            addTotalReglements(mainTable, caisseDto, normalFont);
        }

        mainTable.setSpacingAfter(10);
        document.add(mainTable);
    }

    private boolean hasBonsVente(CaisseJourDto caisseDto) {
        return caisseDto.getBonsVente() != null && !caisseDto.getBonsVente().isEmpty();
    }

    private boolean hasBonsAchat(CaisseJourDto caisseDto) {
        return caisseDto.getBonsAchat() != null && !caisseDto.getBonsAchat().isEmpty();
    }

    private boolean hasReglements(CaisseJourDto caisseDto) {
        return caisseDto.getReglements() != null && !caisseDto.getReglements().isEmpty();
    }

    private void addBonsVenteData(PdfPTable table, CaisseJourDto caisseDto, Font normalFont, Font smallFont) {
        if (caisseDto.getBonsVente() != null && !caisseDto.getBonsVente().isEmpty()) {
            for (BonSortieDto bon : caisseDto.getBonsVente()) {
                addDataRow(table, getBonNumero(bon), getBonMontant(bon),
                        getBonEspece(bon), getBonCheque(bon), getBonCredit(bon),
                        getClientName(bon), normalFont, smallFont);
            }
        }
    }

    private void addBonsAchatData(PdfPTable table, CaisseJourDto caisseDto, Font normalFont, Font smallFont) {
        if (caisseDto.getBonsAchat() != null && !caisseDto.getBonsAchat().isEmpty()) {
            for (BonSortieDto bon : caisseDto.getBonsAchat()) {
                addDataRow(table, getBonNumero(bon), getBonMontant(bon),
                        getBonEspece(bon), getBonCheque(bon), getBonCredit(bon),
                        getClientName(bon), normalFont, smallFont);
            }
        }
    }

    private void addReglementsData(PdfPTable table, CaisseJourDto caisseDto, Font normalFont, Font smallFont) {
        if (caisseDto.getReglements() != null && !caisseDto.getReglements().isEmpty()) {
            for (ReglementDto reglement : caisseDto.getReglements()) {
                addDataRow(table,
                        getReglementNumeroFormate(reglement), // MODIFIÉ : utilise le format "Reg N°XX"
                        getReglementMontant(reglement),
                        getReglementEspece(reglement),
                        getReglementCheque(reglement),
                        BigDecimal.ZERO, // Les règlements n'ont généralement pas de crédit
                        getReglementDetails(reglement),
                        normalFont,
                        smallFont);
            }
        }
    }

    private void addTotalBonsVente(PdfPTable table, CaisseJourDto caisseDto, Font normalFont) {
        BigDecimal totalVenteMontant = BigDecimal.ZERO;
        BigDecimal totalVenteEspece = BigDecimal.ZERO;
        BigDecimal totalVenteCheque = BigDecimal.ZERO;
        BigDecimal totalVenteCredit = BigDecimal.ZERO;

        if (caisseDto.getBonsVente() != null) {
            for (BonSortieDto bon : caisseDto.getBonsVente()) {
                totalVenteMontant = totalVenteMontant.add(getBonMontant(bon));
                totalVenteEspece = totalVenteEspece.add(getBonEspece(bon));
                totalVenteCheque = totalVenteCheque.add(getBonCheque(bon));
                totalVenteCredit = totalVenteCredit.add(getBonCredit(bon));
            }
        }

        // Ajouter aux totaux globaux
        totalEspeceGlobal = totalEspeceGlobal.add(totalVenteEspece);
        totalChequeGlobal = totalChequeGlobal.add(totalVenteCheque);

        addTotalRow(table, "Total Vte:",
                totalVenteMontant,
                totalVenteEspece,
                totalVenteCheque,
                totalVenteCredit,
                "", normalFont);
    }

    private void addTotalBonsAchat(PdfPTable table, CaisseJourDto caisseDto, Font normalFont) {
        BigDecimal totalAchatMontant = BigDecimal.ZERO;
        BigDecimal totalAchatEspece = BigDecimal.ZERO;
        BigDecimal totalAchatCheque = BigDecimal.ZERO;
        BigDecimal totalAchatCredit = BigDecimal.ZERO;

        if (caisseDto.getBonsAchat() != null) {
            for (BonSortieDto bon : caisseDto.getBonsAchat()) {
                totalAchatMontant = totalAchatMontant.add(getBonMontant(bon));
                totalAchatEspece = totalAchatEspece.add(getBonEspece(bon));
                totalAchatCheque = totalAchatCheque.add(getBonCheque(bon));
                totalAchatCredit = totalAchatCredit.add(getBonCredit(bon));
            }
        }

        addTotalRow(table, "Total Achats:",
                totalAchatMontant,
                totalAchatEspece,
                totalAchatCheque,
                totalAchatCredit,
                "", normalFont);
    }

    private void addTotalReglements(PdfPTable table, CaisseJourDto caisseDto, Font normalFont) {
        BigDecimal totalReglementMontant = BigDecimal.ZERO;
        BigDecimal totalReglementEspece = BigDecimal.ZERO;
        BigDecimal totalReglementCheque = BigDecimal.ZERO;

        if (caisseDto.getReglements() != null) {
            for (ReglementDto reglement : caisseDto.getReglements()) {
                totalReglementMontant = totalReglementMontant.add(getReglementMontant(reglement));
                totalReglementEspece = totalReglementEspece.add(getReglementEspece(reglement));
                totalReglementCheque = totalReglementCheque.add(getReglementCheque(reglement));
            }
        }

        // Ajouter aux totaux globaux
        totalEspeceGlobal = totalEspeceGlobal.add(totalReglementEspece);
        totalChequeGlobal = totalChequeGlobal.add(totalReglementCheque);

        addTotalRow(table, "TOTAL Règlements:",
                totalReglementMontant,
                totalReglementEspece,
                totalReglementCheque,
                BigDecimal.ZERO,
                "", normalFont);
    }

    /**
     * Ajoute un footer avec les totaux globaux d'espèces et de chèques
     */
    private void addFooterTotals(Document document, Font footerFont) throws DocumentException {
        // Espacement avant le footer
        document.add(new Paragraph(" "));

        // Créer un tableau pour le footer avec 2 colonnes
        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(60); // Largeur réduite pour centrer
        footerTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        footerTable.setWidths(new float[]{50f, 50f});
        footerTable.setSpacingBefore(10);

        // Cellule Total Espèces
        PdfPCell cellTotalEspece = new PdfPCell(new Phrase("Total Espèces: " + formatMontantVirgule(totalEspeceGlobal), footerFont));
        cellTotalEspece.setBackgroundColor(FOOTER_BLUE);
        cellTotalEspece.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellTotalEspece.setPadding(8);
        cellTotalEspece.setBorder(Rectangle.BOX);
        cellTotalEspece.setBorderWidth(2);
        cellTotalEspece.setBorderColor(BLACK);

        // Cellule Total Chèques
        PdfPCell cellTotalCheque = new PdfPCell(new Phrase("Total Chèques: " + formatMontantVirgule(totalChequeGlobal), footerFont));
        cellTotalCheque.setBackgroundColor(FOOTER_BLUE);
        cellTotalCheque.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellTotalCheque.setPadding(8);
        cellTotalCheque.setBorder(Rectangle.BOX);
        cellTotalCheque.setBorderWidth(2);
        cellTotalCheque.setBorderColor(BLACK);

        footerTable.addCell(cellTotalEspece);
        footerTable.addCell(cellTotalCheque);

        document.add(footerTable);

        // Ligne de total général
        BigDecimal totalGeneral = totalEspeceGlobal.add(totalChequeGlobal);
        if (totalGeneral.compareTo(BigDecimal.ZERO) > 0) {
            PdfPTable totalGeneralTable = new PdfPTable(1);
            totalGeneralTable.setWidthPercentage(40);
            totalGeneralTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalGeneralTable.setSpacingBefore(5);

            PdfPCell cellTotalGeneral = new PdfPCell(new Phrase("TOTAL GÉNÉRAL: " + formatMontantVirgule(totalGeneral), footerFont));
            cellTotalGeneral.setBackgroundColor(new Color(255, 255, 200)); // Jaune clair
            cellTotalGeneral.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTotalGeneral.setPadding(8);
            cellTotalGeneral.setBorder(Rectangle.BOX);
            cellTotalGeneral.setBorderWidth(2);
            cellTotalGeneral.setBorderColor(BLACK);

            totalGeneralTable.addCell(cellTotalGeneral);
            document.add(totalGeneralTable);
        }
    }

    // === Méthodes utilitaires pour BonSortieDto ===
    private String getBonNumero(BonSortieDto bon) {
        return bon != null && bon.getSerie() != null ? bon.getSerie() : "";
    }

    private BigDecimal getBonMontant(BonSortieDto bon) {
        return bon != null && bon.getMontant() != null ? bon.getMontant() : BigDecimal.ZERO;
    }

    private BigDecimal getBonEspece(BonSortieDto bon) {
        return bon != null && bon.getEspece() != null ? bon.getEspece() : BigDecimal.ZERO;
    }

    private BigDecimal getBonCheque(BonSortieDto bon) {
        return bon != null && bon.getCheque() != null ? bon.getCheque() : BigDecimal.ZERO;
    }

    private BigDecimal getBonCredit(BonSortieDto bon) {
        return bon != null && bon.getCredit() != null ? bon.getCredit() : BigDecimal.ZERO;
    }

    private String getClientName(BonSortieDto bon) {
        return bon != null && bon.getNomTier() != null ? bon.getNomTier() : "";
    }

    // === Méthodes utilitaires pour Reglement ===
    private Integer getReglementNumero(Reglement reglement) {
        return reglement != null && reglement.getIdRegl() != null ? reglement.getIdRegl() : 0;
    }

    // NOUVELLE MÉTHODE : formatage du numéro de règlement
    private String getReglementNumeroFormate(ReglementDto reglement) {
        if (reglement == null || reglement.getId() == null) {
            return "Reg N°0";
        }
        return "Reg N°" + reglement.getId();
    }

    private BigDecimal getReglementMontant(ReglementDto reglement) {
        if (reglement == null) return BigDecimal.ZERO;

        // Si l'attribut reglement est null, prendre la somme de espèce et chèque
        if (reglement.getReglement() == null) {
            BigDecimal montantTotal = BigDecimal.ZERO;

            if (reglement.getEspece() != null) {
                montantTotal = montantTotal.add(reglement.getEspece());
            }
            if (reglement.getCheque() != null) {
                montantTotal = montantTotal.add(reglement.getCheque());
            }

            return montantTotal;
        }

        // Si l'attribut reglement existe, l'utiliser
        return reglement.getReglement();
    }

    private BigDecimal getReglementEspece(ReglementDto reglement) {
        return reglement != null && reglement.getEspece() != null ? reglement.getEspece() : BigDecimal.ZERO;
    }

    private BigDecimal getReglementCheque(ReglementDto reglement) {
        return reglement != null && reglement.getCheque() != null ? reglement.getCheque() : BigDecimal.ZERO;
    }

    private String getReglementDetails(ReglementDto reglement) {
        return reglement != null && reglement.getDetailsCheque() != null ? reglement.getDetailsCheque() : "";
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

    // === Méthodes utilitaires de formatage ===
    private BigDecimal safeGetBigDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String formatMontant(BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) == 0) {
            return "";
        }
        return String.format("%.2f", montant.doubleValue());
    }

    private String formatMontantVirgule(BigDecimal montant) {
        if (montant == null) {
            return "0,00 DH";
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