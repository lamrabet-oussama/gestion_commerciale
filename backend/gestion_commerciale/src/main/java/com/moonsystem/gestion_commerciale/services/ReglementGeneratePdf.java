package com.moonsystem.gestion_commerciale.services;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPageEventHelper;

import com.moonsystem.gestion_commerciale.model.Reglement;
import com.moonsystem.gestion_commerciale.dto.MesInfoxDto;
import com.moonsystem.gestion_commerciale.services.MesInfoxService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.Color;

@Service
@RequiredArgsConstructor
public class ReglementGeneratePdf {

    private String logo;
    private String nomEntreprise;
    private String adresse;
    private String activite;
    private String ice;
    private final MesInfoxService mesInfoxService;

    @PostConstruct
    public void init() {
     MesInfoxDto dto=this.mesInfoxService.findById(1);
        this.nomEntreprise = dto.getNomSociete();
        this.logo = dto.getBLogo();
        this.adresse = dto.getAdresse() != null ? dto.getAdresse() : "Adresse de l'entreprise";
        this.activite = dto.getActivite() != null ? dto.getActivite() : "Activité";
        this.ice = dto.getPiedPage() != null ? dto.getPiedPage() : "ICE: 000000000000000";
    }

    // Classe interne pour gérer le footer
    private class FooterEvent extends PdfPageEventHelper {
        private Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
        private Font footerBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.GRAY);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                float footerPosition = document.bottom() + 30;

                PdfPTable footerTable = new PdfPTable(3);
                footerTable.setTotalWidth(document.right() - document.left());
                footerTable.setWidths(new float[]{2f, 2f, 1f});

                // Colonne gauche - Informations de contact
                PdfPCell leftCell = createFooterCell();
                leftCell.addElement(new Paragraph(nomEntreprise, footerBoldFont));
                leftCell.addElement(new Paragraph(adresse, footerFont));
                leftCell.addElement(new Paragraph("Activité: " + activite, footerFont));

                // Colonne centrale - Informations légales
                PdfPCell centerCell = createFooterCell();
                centerCell.addElement(new Paragraph(ice, footerFont));

                // Colonne droite - Numéro de page
                PdfPCell rightCell = createFooterCell();
                rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                rightCell.addElement(new Paragraph("Page " + writer.getPageNumber(), footerFont));
                rightCell.addElement(new Paragraph("", footerFont));
                rightCell.addElement(new Paragraph("Merci pour votre confiance", footerBoldFont));

                footerTable.addCell(leftCell);
                footerTable.addCell(centerCell);
                footerTable.addCell(rightCell);

                // Ligne de séparation
                PdfPTable separatorTable = new PdfPTable(1);
                separatorTable.setTotalWidth(document.right() - document.left());
                PdfPCell separatorCell = new PdfPCell();
                separatorCell.setBorder(Rectangle.TOP);
                separatorCell.setBorderColorTop(Color.LIGHT_GRAY);
                separatorCell.setFixedHeight(1f);
                separatorTable.addCell(separatorCell);

                separatorTable.writeSelectedRows(0, -1, document.left(), footerPosition + 20, writer.getDirectContent());
                footerTable.writeSelectedRows(0, -1, document.left(), footerPosition, writer.getDirectContent());

            } catch (Exception e) {
                System.err.println("Erreur lors de la création du footer: " + e.getMessage());
            }
        }

        private PdfPCell createFooterCell() {
            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPaddingTop(5);
            cell.setPaddingBottom(5);
            cell.setPaddingLeft(5);
            cell.setPaddingRight(5);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            return cell;
        }
    }

    // Méthode pour un seul règlement (conservée pour compatibilité)
    public byte[] generatePdf(Reglement reglement) throws DocumentException, IOException {
        return generatePdf(List.of(reglement));
    }

    // Nouvelle méthode pour une liste de règlements
    public byte[] generatePdf(List<Reglement> reglements) throws DocumentException, IOException {


        Document document = new Document(PageSize.A4, 50, 50, 50, 80);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = PdfWriter.getInstance(document, out);
        writer.setPageEvent(new FooterEvent());

        document.open();

        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        // Header principal
        addMainHeader(document, titleFont, normalFont, reglements);

        if (reglements.size() == 1) {
            // Pour un seul règlement, utiliser le format détaillé
            generateSingleReglementContent(document, headerFont, normalFont, reglements.get(0));
        } else {
            // Pour plusieurs règlements, utiliser le format tableau
            generateMultipleReglementsContent(document, headerFont, normalFont, smallFont, reglements);
        }

        document.close();
        return out.toByteArray();
    }

    private void addMainHeader(Document document, Font titleFont, Font normalFont, List<Reglement> reglements) throws DocumentException {
        // Table pour l'en-tête avec 3 colonnes
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{2f, 3f, 2f});

        // Logo
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.BOX);
        logoCell.setPadding(10);

        try {
            if (logo != null && !logo.trim().isEmpty()) {
                URL url = new URL(logo);
                try (InputStream inputStream = url.openStream()) {
                    byte[] imageBytes = inputStream.readAllBytes();
                    Image logoImage = Image.getInstance(imageBytes);

                    float maxWidth = 80f;
                    float maxHeight = 60f;
                    logoImage.scaleToFit(maxWidth, maxHeight);
                    logoImage.setAlignment(Element.ALIGN_CENTER);

                    logoCell.addElement(logoImage);
                }
            } else {
                Paragraph logoPara = new Paragraph("LOGO", titleFont);
                logoPara.setAlignment(Element.ALIGN_CENTER);
                logoCell.addElement(logoPara);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du logo: " + e.getMessage());
            Paragraph logoPara = new Paragraph("LOGO", titleFont);
            logoPara.setAlignment(Element.ALIGN_CENTER);
            logoCell.addElement(logoPara);
        }

        headerTable.addCell(logoCell);

        // Informations entreprise
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(Rectangle.BOX);
        companyCell.setPadding(10);

        Paragraph companyName = new Paragraph(nomEntreprise != null ? nomEntreprise : "Nom Entreprise", titleFont);
        companyName.setAlignment(Element.ALIGN_CENTER);
        companyCell.addElement(companyName);

        Paragraph companyDesc = new Paragraph(activite != null ? activite : "Activité", normalFont);
        companyDesc.setAlignment(Element.ALIGN_CENTER);
        companyCell.addElement(companyDesc);

        // Titre du document
        String documentTitle = reglements.size() == 1 ? "RÈGLEMENT" : "LISTE DES RÈGLEMENTS";
        Paragraph titlePara = new Paragraph(documentTitle,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLUE));
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingBefore(10);
        companyCell.addElement(titlePara);

        headerTable.addCell(companyCell);

        // Informations sur la période ou le règlement
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.BOX);
        infoCell.setPadding(10);

        if (reglements.size() == 1) {
            Reglement reglement = reglements.get(0);
            String formattedDate = reglement.getDatRegl().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Paragraph datePara = new Paragraph("Date:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
            datePara.setAlignment(Element.ALIGN_CENTER);
            infoCell.addElement(datePara);

            Paragraph dateValue = new Paragraph(formattedDate, normalFont);
            dateValue.setAlignment(Element.ALIGN_CENTER);
            infoCell.addElement(dateValue);

            Paragraph reglNumber = new Paragraph("Règlement n°: " + reglement.getIdRegl(), normalFont);
            reglNumber.setAlignment(Element.ALIGN_CENTER);
            reglNumber.setSpacingBefore(10);
            infoCell.addElement(reglNumber);
        } else {
            Paragraph countPara = new Paragraph("Nombre de règlements:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
            countPara.setAlignment(Element.ALIGN_CENTER);
            infoCell.addElement(countPara);

            Paragraph countValue = new Paragraph(String.valueOf(reglements.size()), normalFont);
            countValue.setAlignment(Element.ALIGN_CENTER);
            infoCell.addElement(countValue);

            // Total général
            BigDecimal totalGeneral = reglements.stream()
                    .map(Reglement::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Paragraph totalPara = new Paragraph("Montant total: " + String.format("%.2f DH", totalGeneral),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
            totalPara.setAlignment(Element.ALIGN_CENTER);
            totalPara.setSpacingBefore(10);
            infoCell.addElement(totalPara);
        }

        headerTable.addCell(infoCell);
        document.add(headerTable);
        document.add(new Paragraph(" "));
    }

    private void generateSingleReglementContent(Document document, Font headerFont, Font normalFont, Reglement reglement) throws DocumentException {
        // Informations du règlement
        addReglementInfo(document, headerFont, normalFont, reglement);

        // Détails du paiement
        addPaymentDetails(document, headerFont, normalFont, reglement);

        // Section signature
        addSignatureSection(document, normalFont);
    }

    private void generateMultipleReglementsContent(Document document, Font headerFont, Font normalFont, Font smallFont, List<Reglement> reglements) throws DocumentException {
        // Titre de la section
        Paragraph listTitle = new Paragraph("DÉTAIL DES RÈGLEMENTS", headerFont);
        listTitle.setAlignment(Element.ALIGN_CENTER);
        listTitle.setSpacingBefore(15);
        listTitle.setSpacingAfter(10);
        document.add(listTitle);

        // Table des règlements
        PdfPTable reglementsTable = new PdfPTable(7);
        reglementsTable.setWidthPercentage(100);
        reglementsTable.setWidths(new float[]{1f, 2f, 1.5f, 1f, 1f, 1f, 1.5f});

        // En-têtes
        addTableHeader(reglementsTable, "N°", smallFont);
        addTableHeader(reglementsTable, "Tiers", smallFont);
        addTableHeader(reglementsTable, "Date", smallFont);
        addTableHeader(reglementsTable, "Type", smallFont);
        addTableHeader(reglementsTable, "Espèces", smallFont);
        addTableHeader(reglementsTable, "Chèque", smallFont);
        addTableHeader(reglementsTable, "Total", smallFont);

        // Lignes des règlements
        BigDecimal totalEspeces = BigDecimal.ZERO;
        BigDecimal totalCheques = BigDecimal.ZERO;
        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (Reglement reglement : reglements) {
            // N° de règlement
            addTableCell(reglementsTable, String.valueOf(reglement.getIdRegl()), smallFont, Element.ALIGN_CENTER);

            // Nom du tiers
            String nomTiers = reglement.getTier() != null ? reglement.getTier().getNom() : "N/A";
            addTableCell(reglementsTable, nomTiers, smallFont, Element.ALIGN_LEFT);

            // Date
            String dateFormatted = reglement.getDatRegl().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            addTableCell(reglementsTable, dateFormatted, smallFont, Element.ALIGN_CENTER);

            // Type de mouvement
            String typeReglement = reglement.getMouvement() != null ?
                    (reglement.getMouvement().toString().equals("VENTE") ? "ENC" : "DEC") : "N/A";
            addTableCell(reglementsTable, typeReglement, smallFont, Element.ALIGN_CENTER);

            // Espèces
            BigDecimal especes = reglement.getEspece() != null ? reglement.getEspece() : BigDecimal.ZERO;
            addTableCell(reglementsTable, String.format("%.2f", especes), smallFont, Element.ALIGN_RIGHT);
            totalEspeces = totalEspeces.add(especes);

            // Chèque
            BigDecimal cheque = reglement.getCheque() != null ? reglement.getCheque() : BigDecimal.ZERO;
            addTableCell(reglementsTable, String.format("%.2f", cheque), smallFont, Element.ALIGN_RIGHT);
            totalCheques = totalCheques.add(cheque);

            // Total
            addTableCell(reglementsTable, String.format("%.2f", reglement.getTotal()), smallFont, Element.ALIGN_RIGHT);
            totalGeneral = totalGeneral.add(reglement.getTotal());
        }

        document.add(reglementsTable);

        // Ligne de totaux
        document.add(new Paragraph(" "));
        addSummaryTable(document, headerFont, normalFont, totalEspeces, totalCheques, totalGeneral);

        // Détails des chèques s'il y en a
        addChequeDetails(document, headerFont, normalFont, reglements);
    }

    private void addSummaryTable(Document document, Font headerFont, Font normalFont,
                                 BigDecimal totalEspeces, BigDecimal totalCheques, BigDecimal totalGeneral) throws DocumentException {

        Paragraph summaryTitle = new Paragraph("RÉCAPITULATIF", headerFont);
        summaryTitle.setAlignment(Element.ALIGN_CENTER);
        summaryTitle.setSpacingBefore(15);
        summaryTitle.setSpacingAfter(10);
        document.add(summaryTitle);

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(60);
        summaryTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addSummaryRow(summaryTable, "Total Espèces:", totalEspeces, normalFont);
        addSummaryRow(summaryTable, "Total Chèques:", totalCheques, normalFont);

        // Ligne de séparation
        PdfPCell separatorCell1 = new PdfPCell(new Phrase("", normalFont));
        separatorCell1.setBorder(Rectangle.TOP);
        separatorCell1.setBorderColorTop(Color.BLACK);
        separatorCell1.setPadding(5);
        PdfPCell separatorCell2 = new PdfPCell(new Phrase("", normalFont));
        separatorCell2.setBorder(Rectangle.TOP);
        separatorCell2.setBorderColorTop(Color.BLACK);
        separatorCell2.setPadding(5);
        summaryTable.addCell(separatorCell1);
        summaryTable.addCell(separatorCell2);

        // Total général
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("MONTANT TOTAL:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK)));
        totalLabelCell.setBorder(Rectangle.NO_BORDER);
        totalLabelCell.setPadding(8);
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabelCell.setBackgroundColor(new Color(240, 240, 240));

        PdfPCell totalValueCell = new PdfPCell(new Phrase(String.format("%.2f DH", totalGeneral),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK)));
        totalValueCell.setBorder(Rectangle.NO_BORDER);
        totalValueCell.setPadding(8);
        totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalValueCell.setBackgroundColor(new Color(240, 240, 240));

        summaryTable.addCell(totalLabelCell);
        summaryTable.addCell(totalValueCell);

        document.add(summaryTable);
    }

    private void addChequeDetails(Document document, Font headerFont, Font normalFont, List<Reglement> reglements) throws DocumentException {
        List<Reglement> reglementsAvecCheques = reglements.stream()
                .filter(r -> r.getDet_cheque() != null && !r.getDet_cheque().trim().isEmpty())
                .toList();

        if (!reglementsAvecCheques.isEmpty()) {
            document.add(new Paragraph(" "));
            Paragraph chequeTitle = new Paragraph("DÉTAILS DES CHÈQUES", headerFont);
            chequeTitle.setAlignment(Element.ALIGN_CENTER);
            chequeTitle.setSpacingBefore(15);
            chequeTitle.setSpacingAfter(10);
            document.add(chequeTitle);

            for (Reglement reglement : reglementsAvecCheques) {
                Paragraph chequeInfo = new Paragraph("Règlement n° " + reglement.getIdRegl() + ": " +
                        reglement.getDet_cheque(), normalFont);
                chequeInfo.setSpacingBefore(5);
                document.add(chequeInfo);
            }
        }
    }

    private void addReglementInfo(Document document, Font headerFont, Font normalFont, Reglement reglement) throws DocumentException {
        // Section informations du tiers
        Paragraph infoTitle = new Paragraph("INFORMATIONS DU RÈGLEMENT", headerFont);
        infoTitle.setAlignment(Element.ALIGN_CENTER);
        infoTitle.setSpacingBefore(15);
        infoTitle.setSpacingAfter(10);
        document.add(infoTitle);

        // Table des informations
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1f, 2f});

        // Nom du tiers
        addInfoRow(infoTable,
                reglement.getMouvement() != null && reglement.getMouvement().toString().equals("VENTE") ? "Client:" : "Fournisseur:",
                reglement.getTier() != null ? reglement.getTier().getNom() : "Non spécifié",
                headerFont, normalFont);

        // Utilisateur
        addInfoRow(infoTable, "Utilisateur:",
                reglement.getUser() != null ? reglement.getUser().getLogin() : "Non spécifié",
                headerFont, normalFont);

        document.add(infoTable);
        document.add(new Paragraph(" "));
    }

    private void addPaymentDetails(Document document, Font headerFont, Font normalFont, Reglement reglement) throws DocumentException {
        // Titre de la section
        Paragraph paymentTitle = new Paragraph("DÉTAILS DU RÈGLEMENT", headerFont);
        paymentTitle.setAlignment(Element.ALIGN_CENTER);
        paymentTitle.setSpacingBefore(15);
        paymentTitle.setSpacingAfter(10);
        document.add(paymentTitle);

        // Table des montants
        PdfPTable paymentTable = new PdfPTable(2);
        paymentTable.setWidthPercentage(100);
        paymentTable.setWidths(new float[]{1f, 1f});

        // En-têtes
        addTableHeader(paymentTable, "Mode de paiement", headerFont);
        addTableHeader(paymentTable, "Montant", headerFont);

        // Espèces
        if (reglement.getEspece() != null && reglement.getEspece().compareTo(BigDecimal.ZERO) > 0) {
            addPaymentRow(paymentTable, "Espèces", reglement.getEspece(), normalFont);
        }

        // Chèques
        if (reglement.getCheque() != null && reglement.getCheque().compareTo(BigDecimal.ZERO) > 0) {
            addPaymentRow(paymentTable, "Chèque", reglement.getCheque(), normalFont);
        }

        // Si aucun montant spécifique n'est renseigné, afficher le total
        if ((reglement.getEspece() == null || reglement.getEspece().compareTo(BigDecimal.ZERO) == 0) &&
                (reglement.getCheque() == null || reglement.getCheque().compareTo(BigDecimal.ZERO) == 0)) {
            addPaymentRow(paymentTable, "Total", reglement.getTotal(), normalFont);
        }

        document.add(paymentTable);

        // Détails du chèque si présent
        if (reglement.getDet_cheque() != null && !reglement.getDet_cheque().trim().isEmpty()) {
            document.add(new Paragraph(" "));
            Paragraph chequeDetails = new Paragraph("Détails du chèque:", headerFont);
            document.add(chequeDetails);

            Paragraph chequeInfo = new Paragraph(reglement.getDet_cheque(), normalFont);
            chequeInfo.setSpacingBefore(5);
            document.add(chequeInfo);
        }

        // Total général
        document.add(new Paragraph(" "));
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100);
        totalTable.setWidths(new float[]{3f, 1f});

        PdfPCell emptyCell = new PdfPCell(new Phrase(" "));
        emptyCell.setBorder(Rectangle.NO_BORDER);
        totalTable.addCell(emptyCell);

        PdfPCell totalCell = new PdfPCell();
        totalCell.setBorder(Rectangle.BOX);
        totalCell.setBackgroundColor(new Color(240, 240, 240));
        totalCell.setPadding(10);

        Paragraph totalLabel = new Paragraph("MONTANT TOTAL",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK));
        totalLabel.setAlignment(Element.ALIGN_CENTER);
        totalCell.addElement(totalLabel);

        Paragraph totalAmount = new Paragraph(String.format("%.2f DH", reglement.getTotal()),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK));
        totalAmount.setAlignment(Element.ALIGN_CENTER);
        totalCell.addElement(totalAmount);

        totalTable.addCell(totalCell);
        document.add(totalTable);
    }

    private void addSignatureSection(Document document, Font normalFont) throws DocumentException {
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // Table pour les signatures
        PdfPTable signatureTable = new PdfPTable(2);
        signatureTable.setWidthPercentage(100);
        signatureTable.setWidths(new float[]{1f, 1f});

        // Signature du client/fournisseur
        PdfPCell clientSignCell = new PdfPCell();
        clientSignCell.setBorder(Rectangle.NO_BORDER);
        clientSignCell.setPadding(20);
        clientSignCell.setMinimumHeight(80);

        Paragraph clientSignTitle = new Paragraph("Signature du client/fournisseur",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
        clientSignTitle.setAlignment(Element.ALIGN_CENTER);
        clientSignCell.addElement(clientSignTitle);

        Paragraph clientSignLine = new Paragraph("_________________________", normalFont);
        clientSignLine.setAlignment(Element.ALIGN_CENTER);
        clientSignLine.setSpacingBefore(40);
        clientSignCell.addElement(clientSignLine);

        // Signature de l'entreprise
        PdfPCell companySignCell = new PdfPCell();
        companySignCell.setBorder(Rectangle.NO_BORDER);
        companySignCell.setPadding(20);
        companySignCell.setMinimumHeight(80);

        Paragraph companySignTitle = new Paragraph("Signature de l'entreprise",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
        companySignTitle.setAlignment(Element.ALIGN_CENTER);
        companySignCell.addElement(companySignTitle);

        Paragraph companySignLine = new Paragraph("_________________________", normalFont);
        companySignLine.setAlignment(Element.ALIGN_CENTER);
        companySignLine.setSpacingBefore(40);
        companySignCell.addElement(companySignLine);

        signatureTable.addCell(clientSignCell);
        signatureTable.addCell(companySignCell);

        document.add(signatureTable);
    }

    // Méthodes utilitaires
    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.BOX);
        labelCell.setBackgroundColor(new Color(240, 240, 240));
        labelCell.setPadding(8);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setPadding(8);
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(230, 230, 230));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        cell.setBorder(Rectangle.BOX);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    private void addPaymentRow(PdfPTable table, String mode, BigDecimal amount, Font font) {
        PdfPCell modeCell = new PdfPCell(new Phrase(mode, font));
        modeCell.setBorder(Rectangle.BOX);
        modeCell.setPadding(8);
        modeCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(modeCell);

        PdfPCell amountCell = new PdfPCell(new Phrase(String.format("%.2f DH", amount), font));
        amountCell.setBorder(Rectangle.BOX);
        amountCell.setPadding(8);
        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(amountCell);
    }

    private void addSummaryRow(PdfPTable table, String label, BigDecimal amount, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);

        PdfPCell amountCell = new PdfPCell(new Phrase(String.format("%.2f DH", amount), font));
        amountCell.setBorder(Rectangle.NO_BORDER);
        amountCell.setPadding(5);
        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(amountCell);
    }
}