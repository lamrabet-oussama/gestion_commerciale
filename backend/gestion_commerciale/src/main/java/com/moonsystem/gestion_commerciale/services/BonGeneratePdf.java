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

import com.moonsystem.gestion_commerciale.dto.ArticleAddBonDto;
import com.moonsystem.gestion_commerciale.dto.BonAchatVenteDto;
import com.moonsystem.gestion_commerciale.dto.ArticleDto;
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
import java.awt.Color;

@Service
@RequiredArgsConstructor
public class BonGeneratePdf {

    private String logo;
    private String nomEntreprise;
    private String adresse;
    private String activite;
    private String ice;
    private final MesInfoxService mesInfoxService;

    @PostConstruct
    public void init() {
//        MesInfoxDto dto = new MesInfoxDto(
//                1,
//                "Ma Société",
//                "Commerce",
//                "123 Rue Exemple",
//                "Pied de page",
//                "SER123",
//                "logo.png",
//                "codeF",
//                "Banque Nom",
//                "B123",
//                "Activité B",
//                "Adresse B",
//                "logoB.png",
//                "codeB",
//                "Note 1",
//                "Note 2"
//        );
        MesInfoxDto dto=this.mesInfoxService.findById(1);
        this.nomEntreprise = dto.getNomSociete();
        this.logo = dto.getBLogo();
        // Adaptez ces champs selon votre DTO MesInfoxDto
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
                // Position du footer avec plus de padding (80 points du bas au lieu de 50)
                float footerPosition = document.bottom() + 30; // Augmenté de 50 à 80 (30 + 50 de marge)

                // Table pour le footer
                PdfPTable footerTable = new PdfPTable(3);
                footerTable.setTotalWidth(document.right() - document.left());
                footerTable.setWidths(new float[]{2f, 2f, 1f});

                // Colonne gauche - Informations de contact
                PdfPCell leftCell = createFooterCell();
                leftCell.addElement(new Paragraph(nomEntreprise, footerBoldFont));
                leftCell.addElement(new Paragraph(adresse, footerFont));
                leftCell.addElement(new Paragraph("Activité: " + activite, footerFont));
                //leftCell.addElement(new Paragraph("Email: " + email, footerFont));

                // Colonne centrale - Informations légales
                PdfPCell centerCell = createFooterCell();
                centerCell.addElement(new Paragraph(ice, footerFont));
               // centerCell.addElement(new Paragraph(registreCommerce, footerFont));
              //  centerCell.addElement(new Paragraph("IF: En cours", footerFont));
               // centerCell.addElement(new Paragraph("CNSS: En cours", footerFont));

                // Colonne droite - Numéro de page et site web
                PdfPCell rightCell = createFooterCell();
                rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                rightCell.addElement(new Paragraph("Page " + writer.getPageNumber(), footerFont));
                rightCell.addElement(new Paragraph("", footerFont)); // Espace
                rightCell.addElement(new Paragraph("Merci pour votre confiance", footerBoldFont));

                footerTable.addCell(leftCell);
                footerTable.addCell(centerCell);
                footerTable.addCell(rightCell);

                // Ligne de séparation - positionnée plus haut
                PdfPTable separatorTable = new PdfPTable(1);
                separatorTable.setTotalWidth(document.right() - document.left());
                PdfPCell separatorCell = new PdfPCell();
                separatorCell.setBorder(Rectangle.TOP);
                separatorCell.setBorderColorTop(Color.LIGHT_GRAY);
                separatorCell.setFixedHeight(1f);
                separatorTable.addCell(separatorCell);

                // Positionnement et ajout du footer avec plus d'espacement
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

    public byte[] generatePdf(BonAchatVenteDto bon) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 50, 50, 50, 80); // Marge bottom augmentée pour le footer
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = PdfWriter.getInstance(document, out);

        // Ajout du gestionnaire d'événements pour le footer
        writer.setPageEvent(new FooterEvent());

        document.open();

        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        // Header avec logo et informations entreprise
        addHeader(document, titleFont, normalFont, bon, logo);

        // Informations client
        addClientInfo(document, normalFont, bon);

        // Tableau des articles
        addItemsTable(document, headerFont, normalFont, bon);

        // Totaux et paiement
        addTotalsAndPayment(document, normalFont, bon);

        // Conditions de vente et notes
        addConditionsAndNotes(document, smallFont);

        document.close();
        return out.toByteArray();
    }

    private void addHeader(Document document, Font titleFont, Font normalFont, BonAchatVenteDto bon, String logoUrl) throws DocumentException {
        // Table pour l'en-tête avec 3 colonnes
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{2f, 3f, 2f});

        // Logo depuis URL cloud
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.BOX);
        logoCell.setPadding(10);

        try {
            if (logoUrl != null && !logoUrl.trim().isEmpty()) {
                URL url = new URL(logoUrl);
                try (InputStream inputStream = url.openStream()) {
                    byte[] imageBytes = inputStream.readAllBytes();
                    Image logoImage = Image.getInstance(imageBytes);

                    float maxWidth = 80f;
                    float maxHeight = 60f;
                    logoImage.scaleToFit(maxWidth, maxHeight);
                    logoImage.setAlignment(Element.ALIGN_CENTER);

                    logoCell.addElement(logoImage);
                    Paragraph logoPara = new Paragraph(bon.getNomUser(), titleFont);
                    logoPara.setAlignment(Element.ALIGN_CENTER);
                    logoCell.addElement(logoPara);
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

        Paragraph companyDesc = new Paragraph(activite!=null ? activite :"Activité", normalFont);
        companyDesc.setAlignment(Element.ALIGN_CENTER);
        companyCell.addElement(companyDesc);

        Paragraph clientName = new Paragraph((bon.getMvt().equals("VENTE") ? "Client :" :"Frn :") + bon.getNomTier(), normalFont);
        clientName.setAlignment(Element.ALIGN_LEFT);
        clientName.setSpacingBefore(10);
        companyCell.addElement(clientName);
        headerTable.addCell(companyCell);

        // Date et numéro de bon
        PdfPCell dateCell = new PdfPCell();
        dateCell.setBorder(Rectangle.BOX);
        dateCell.setPadding(10);

        String formattedDate = bon.getDatBon().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Paragraph datePara = new Paragraph(formattedDate, normalFont);
        datePara.setAlignment(Element.ALIGN_CENTER);
        dateCell.addElement(datePara);

        Paragraph bonNumber = new Paragraph("BL n°: " + bon.getSerie(), normalFont);
        bonNumber.setAlignment(Element.ALIGN_CENTER);
        bonNumber.setSpacingBefore(10);
        dateCell.addElement(bonNumber);
        headerTable.addCell(dateCell);

        document.add(headerTable);
        document.add(new Paragraph(" "));
    }

    private void addClientInfo(Document document, Font normalFont, BonAchatVenteDto bon) throws DocumentException {
        document.add(new Paragraph(" "));
    }

    private void addItemsTable(Document document, Font headerFont, Font normalFont, BonAchatVenteDto bon) throws DocumentException {
        // Modification: passage de 4 à 5 colonnes pour inclure la remise unitaire
        PdfPTable itemsTable = new PdfPTable(5);
        itemsTable.setWidthPercentage(100);
        // Ajustement des largeurs des colonnes
        itemsTable.setWidths(new float[]{1f, 4f, 1.5f, 1.2f, 1.5f});

        // En-têtes du tableau
        addTableHeader(itemsTable, "Qté", headerFont);
        addTableHeader(itemsTable, "Désignation", headerFont);
        addTableHeader(itemsTable, "Prix unit", headerFont);
        addTableHeader(itemsTable, "Remise", headerFont); // Nouvelle colonne
        addTableHeader(itemsTable, "Total", headerFont);

        // Lignes des articles
        BigDecimal totalHT = BigDecimal.ZERO;
        for (ArticleAddBonDto article : bon.getArticles()) {
            BigDecimal totalLine = article.getPrix().multiply(article.getQuantite());
            totalHT = totalHT.add(totalLine);

            addTableCell(itemsTable, article.getQuantite().toString(), normalFont, Element.ALIGN_CENTER);
            addTableCell(itemsTable, article.getDesignation()+" ("+article.getChoix()+")", normalFont, Element.ALIGN_LEFT);
            addTableCell(itemsTable, String.format("%.2f", article.getPrix())+" DH", normalFont, Element.ALIGN_RIGHT);

            // Nouvelle cellule pour la remise unitaire
            String remiseText = article.getRemisUni() != null ?
                    String.format("%.2f", article.getRemisUni()) + " DH" :
                    "0.00 DH";
            addTableCell(itemsTable, remiseText, normalFont, Element.ALIGN_RIGHT);

            addTableCell(itemsTable, String.format("%.2f", totalLine)+" DH", normalFont, Element.ALIGN_RIGHT);
        }

        document.add(itemsTable);
    }
    private void addTotalsAndPayment(Document document, Font normalFont, BonAchatVenteDto bon) throws DocumentException {
        document.add(new Paragraph(" "));

        // Table pour les totaux
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(100);
        totalsTable.setWidths(new float[]{3f, 1f});

        // Colonne vide à gauche
        PdfPCell emptyCell = new PdfPCell(new Phrase(" "));
        emptyCell.setBorder(Rectangle.NO_BORDER);
        totalsTable.addCell(emptyCell);

        // Colonne des totaux à droite
        PdfPTable rightTable = new PdfPTable(2);
        rightTable.setWidthPercentage(100);

        // Total
        addTotalRow(rightTable, "Total:", String.format("%.2f", bon.getMontantSansRemise() != null ? bon.getMontantSansRemise() : bon.getMontant()), normalFont);

        // Remise si applicable
        if (bon.getRemis() != null && bon.getRemis().compareTo(BigDecimal.ZERO) > 0) {
            addTotalRow(rightTable, "Remise Globale " , String.format("%.2f", bon.getRemis()), normalFont);
        }

        // Net à payer
        addTotalRow(rightTable, "Net à Payer:", String.format("%.2f", bon.getMontant()), normalFont);

        PdfPCell rightCell = new PdfPCell(rightTable);
        rightCell.setBorder(Rectangle.NO_BORDER);
        totalsTable.addCell(rightCell);

        document.add(totalsTable);

        // Section paiement
        document.add(new Paragraph(" "));
        addPaymentSection(document, normalFont, bon);
    }

    private void addPaymentSection(Document document, Font normalFont, BonAchatVenteDto bon) throws DocumentException {
        PdfPTable paymentTable = new PdfPTable(4);
        paymentTable.setWidthPercentage(100);
        paymentTable.setWidths(new float[]{1f, 1f, 1f, 1f});

        // Headers
        addPaymentHeader(paymentTable, "Espèce", normalFont);
        addPaymentHeader(paymentTable, "Chèques", normalFont);
        addPaymentHeader(paymentTable, "Crédits", normalFont);
        addPaymentHeader(paymentTable, "Remise", normalFont);

        // Values
        addPaymentCell(paymentTable, String.format("%.2f", bon.getEspece() != null ? bon.getEspece() : BigDecimal.ZERO), normalFont);
        addPaymentCell(paymentTable, String.format("%.2f", bon.getCheque() != null ? bon.getCheque() : BigDecimal.ZERO), normalFont);
        addPaymentCell(paymentTable, String.format("%.2f", bon.getCredit() != null ? bon.getCredit() : BigDecimal.ZERO), normalFont);
        addPaymentCell(paymentTable, String.format("%.2f", bon.getRemis() != null ? bon.getRemis() : BigDecimal.ZERO), normalFont);

        document.add(paymentTable);
    }
    private void addConditionsAndNotes(Document document, Font smallFont) throws DocumentException {
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // Conditions de vente
        Paragraph conditions = new Paragraph("Conditions de vente:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK));
        document.add(conditions);

        Paragraph conditionsText = new Paragraph(
                "• Les marchandises vendues restent la propriété de l'entreprise jusqu'au paiement intégral.\n" +
                        "• Tout retard de paiement entraîne des pénalités de retard.\n" +
                        "• Les réclamations doivent être formulées dans les 8 jours suivant la livraison.",
                smallFont
        );
        document.add(conditionsText);
    }

    // Méthodes utilitaires inchangées
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
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(8);
        cell.setBorder(Rectangle.BOX);
        table.addCell(cell);
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.BOX);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addPaymentHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(240, 240, 240));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        cell.setBorder(Rectangle.BOX);
        table.addCell(cell);
    }

    private void addPaymentCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text+ " DH", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        cell.setBorder(Rectangle.BOX);
        table.addCell(cell);
    }
}