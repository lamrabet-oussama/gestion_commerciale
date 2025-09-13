package com.moonsystem.gestion_commerciale.services.impl;

import com.lowagie.text.DocumentException;
import com.moonsystem.gestion_commerciale.dto.*;
import com.moonsystem.gestion_commerciale.exception.EntityNotFoundException;
import com.moonsystem.gestion_commerciale.exception.ErrorCodes;
import com.moonsystem.gestion_commerciale.exception.InvalidEntityException;
import com.moonsystem.gestion_commerciale.exception.InvalidOperationException;
import com.moonsystem.gestion_commerciale.model.*;
import com.moonsystem.gestion_commerciale.model.enums.MvtType;
import com.moonsystem.gestion_commerciale.model.enums.TypeTier;
import com.moonsystem.gestion_commerciale.repository.*;
import com.moonsystem.gestion_commerciale.services.BonAchatVenteService;
import com.moonsystem.gestion_commerciale.services.BonGeneratePdf;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BonAchatVenteServiceImpl implements BonAchatVenteService {

    private final UserRepository userRepository;
    private final BonsortiRepository bonsortiRepository;
    private final TierRepository tierRepository;
    private final ArticleRepository articleRepository;
    private final FluxRepository fluxRepository;
    private final BonGeneratePdf pdfGenerator;

    @Transactional
    public String generateSerie( LocalDateTime dateTime) {
        // Utiliser la dateTime passée en argument, ou maintenant si null
        LocalDateTime targetDateTime = dateTime != null ? dateTime : LocalDateTime.now();

        String datePartie = targetDateTime.format(DateTimeFormatter.ofPattern("ddMMyy"));

        // Déterminer le début et la fin de la journée à partir de dateTime
        LocalDateTime startOfDay = targetDateTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = targetDateTime.toLocalDate().atTime(23, 59, 59);

        // Récupérer la série max du jour correspondant
        String maxSerie = bonsortiRepository.findMaxSerieForDay(startOfDay, endOfDay);

        int numeroSequentiel;
        if (maxSerie != null) {
            numeroSequentiel = Integer.parseInt(maxSerie.substring(0, 2)) + 1;
        } else {
            numeroSequentiel = 1;
        }

        return String.format("%02d", numeroSequentiel) + "-" + datePartie;
    }

    // **MÉTHODE HELPER POUR CRÉER LA LISTE DES ARTICLES DTO**
    private List<ArticleAddBonDto> createArticlesDto(Bonsorti bon) {
        return bon.getFluxes().stream()
                .map(f -> ArticleAddBonDto.builder()
                        .cod(f.getArticle().getCod())
                        .ref(f.getArticle().getRef())
                        .designation(f.getArticle().getDesignation())
                        .choix(f.getArticle().getChoix())
                        .quantite(BigDecimal.valueOf(bon.getMvt().equals(MvtType.ACHAT) ? f.getEntree() : f.getSortie()))
                        .prix(f.getPrixUni())
                        .prixAchat(f.getArticle().getPrixAchat())
                        .prixMin(f.getArticle().getPrixMin())
                        .remisUni(f.getFRemis())

                        .build()
                )
                .toList();
    }

    @Override
    public BonAchatVenteDto getBonAchat(Integer userId, String serie) {
        if (serie == null || serie.isEmpty()) {
            return new BonAchatVenteDto();
        }
        Bonsorti bon = this.bonsortiRepository.findBySerieAndMvtAndUserCod(serie, MvtType.ACHAT, userId);
        if (bon == null) {
            return new BonAchatVenteDto();
        }


        List<ArticleAddBonDto> articlesDto = createArticlesDto(bon);
        return BonAchatVenteDto.mapToDto(bon, articlesDto);
    }

    @Override
    public BonAchatVenteDto getBonVente(Integer userId, String serie) {
        if (serie == null || serie.isEmpty()) {
            return new BonAchatVenteDto();
        }
        Bonsorti bon = this.bonsortiRepository.findBySerieAndMvtAndUserCod(serie, MvtType.VENTE, userId);
        if (bon == null) {
            return new BonAchatVenteDto();
        }

        List<ArticleAddBonDto> articlesDto = createArticlesDto(bon);
        return BonAchatVenteDto.mapToDto(bon, articlesDto);
    }

    private BigDecimal safe(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }


    @Override
    @Transactional
    public BonAchatVenteDto createBon(BonAchatVenteDto dto, MvtType mvt) {
        if(dto.getArticles()==null || dto.getArticles().isEmpty()){
            throw new InvalidOperationException(
                    "Aucun Article ajouté",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Articles ajoutés=null")
            );
        }
        if ((dto.getDetCheque() != null && !dto.getDetCheque().isEmpty())&& dto.getCheque()==null) {
            throw new InvalidOperationException(
                    "Montant Chèque n'est pas défini",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Montant Chèque n'est pas défini")
            );        }
        if (dto.getCheque() != null && dto.getCheque().signum() > 0
                && !StringUtils.hasText(dto.getDetCheque())) {
            throw new InvalidOperationException(
                    "Détails de chèque sont obligatoires",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Détails Chèque")
            );
        }
        dto.getArticles().forEach(article -> {
            if (article.getQuantite() == null) {
                throw new InvalidOperationException(
                        "Quantité non définie pour l'article: " + article.getRef(),
                        ErrorCodes.BAD_CREDENTIALS,
                        List.of("Quantité null")
                );
            }
        });
        if(dto.getIdUser()==null){
            throw new EntityNotFoundException(
                    "Utilisateur n'est pas spécifié",
                    List.of("idUser =null"),
                    ErrorCodes.BAD_CREDENTIALS
            );
        }
        User user = userRepository.findByCod(dto.getIdUser())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Utilisateur non trouvé",
                        List.of("User not found"),
                        ErrorCodes.USER_NOT_FOUND
                ));

        if(dto.getIdTier()==null){
            throw new EntityNotFoundException(
                    "Tier n'est pas spécifié",
                    List.of("idTier =null"),
                    ErrorCodes.BAD_CREDENTIALS
            );
        }
        Tier tier = tierRepository.findById(dto.getIdTier())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tier non trouvé",
                        List.of("Tier not found"),
                        ErrorCodes.TIER_NOT_FOUND
                ));

        if (tier.getQualite() == TypeTier.CLIENT && !mvt.equals(MvtType.VENTE)) {
            throw new InvalidOperationException(
                    "L'action disponible avec le client est juste le vente",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Tier type=Client mais mvt != VENTE")
            );
        }

        if (tier.getQualite() == TypeTier.FOURNISSEUR && !mvt.equals(MvtType.ACHAT)) {
            throw new InvalidOperationException(
                    "L'action disponible avec le fournisseur est juste l'achat",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Tier type=Fournisseur mais mvt != ACHAT")
            );
        }

        // 1) récupérer tous les articles en une seule fois
        List<Integer> articleIds = dto.getArticles().stream()
                .map(ArticleAddBonDto::getCod)
                .toList();
        List<Article> articles = articleRepository.findAllById(articleIds);

        if (articles.size() != dto.getArticles().size()) {
            throw new EntityNotFoundException(
                    "Certains articles sont introuvables",
                    List.of("Article not found"),
                    ErrorCodes.ARTICLE_NOT_FOUND
            );
        }

        // helpers pour éviter les NPE
        BigDecimal safeEspece = dto.getEspece() == null ? BigDecimal.ZERO : dto.getEspece();
        BigDecimal safeCheque = dto.getCheque() == null ? BigDecimal.ZERO : dto.getCheque();
        BigDecimal safeRemisGlobal = dto.getRemis() == null ? BigDecimal.ZERO : dto.getRemis();

        // 2) Vérif stock (comme avant)
        dto.getArticles().forEach(ar -> {
            if(ar.getQuantite().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidOperationException(
                        "Quantité doit etre strictement positif: Artcile: "+ar.getRef()+":" + ar.getQuantite(),
                        ErrorCodes.STOCK_INSUFFISANT,
                        List.of("Stock insuffisant pour " + ar.getQuantite())
                );
            }
            Article article = articles.stream()
                    .filter(a -> a.getCod().equals(ar.getCod()))
                    .findFirst()
                    .orElseThrow(); // safe

            if (mvt.equals(MvtType.VENTE) && article.getStock().compareTo(ar.getQuantite()) < 0) {
                throw new InvalidOperationException(
                        "Stock insuffisant pour l'article: " + article.getDesignation(),
                        ErrorCodes.STOCK_INSUFFISANT,
                        List.of("Stock insuffisant pour " + article.getDesignation())
                );
            }
        });

        // 3) Calcul montantSansRemise & sommeFremise (remise unitaire multipliée par quantité)
        BigDecimal sommeFremise = BigDecimal.ZERO;
        BigDecimal montantSansRemise = BigDecimal.ZERO;

        for (ArticleAddBonDto a : dto.getArticles()) {
            Article article = articles.stream()
                    .filter(ar -> ar.getCod().equals(a.getCod()))
                    .findFirst()
                    .get();

            // MODIFICATION: Utiliser prixAchat si mvt est ACHAT, sinon prix
            BigDecimal linePrice = mvt.equals(MvtType.ACHAT)
                    ? safe(article.getPrixAchat())
                    : safe(article.getPrix());

            BigDecimal lineMontant = safe(linePrice).multiply(safe(a.getQuantite()));
            montantSansRemise = montantSansRemise.add(lineMontant);

            // **CORRECTION IMPORTANTE** : Vérifier que fRemis n'est pas null
            BigDecimal lineFRemise = safe(a.getRemisUni()).multiply(safe(a.getQuantite()));
            sommeFremise = sommeFremise.add(lineFRemise);
        }

        // 4) Calcul de la remise totale (remise globale + somme des fRemis)
        BigDecimal remiseTotale = safeRemisGlobal.add(sommeFremise);

        // 5) Calcul du montant final après remise (pour le crédit)
        BigDecimal montantAvecRemise = montantSansRemise.subtract(remiseTotale);

        // 6) calcul credit (normalisation pour éviter NPE)
        BigDecimal credit = montantAvecRemise.subtract(safeEspece.add(safeCheque));
        if (mvt.equals(MvtType.ACHAT)) credit = credit.multiply(BigDecimal.valueOf(-1));

        // 7) mise à jour du solde
        // BigDecimal prevSolde = BigDecimal.ZERO;

        BigDecimal prevSolde = tier.getSolde() == null ? BigDecimal.ZERO : tier.getSolde();
        tier.setSolde(prevSolde.add(credit));



        if(dto.getEspece()!=null && dto.getEspece().compareTo(BigDecimal.ZERO)<0){
            throw new InvalidOperationException(
                    "Espèce est négatif",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Espèce négatif")
            );
        }






        // 8) Création et sauvegarde du bon - MONTANT = MONTANT SANS REMISE
        Bonsorti bonsorti = Bonsorti.builder()
                .user(user)
                .tier(tier)
                .detCheq(dto.getDetCheque())
                .serie(generateSerie(dto.getDatBon()))
                .datBon(dto.getDatBon() != null ? dto.getDatBon() : LocalDateTime.now())
                .mvt(mvt)
                .cheque(safeCheque)
                .credit(credit)
                .espece(safeEspece)
                .echeance(LocalDateTime.now())
                .remis(remiseTotale)
                .montant(montantSansRemise)
                .etat(true)
                .build();

        Bonsorti savedBon = bonsortiRepository.save(bonsorti);

        // 9) créer les flux et mettre à jour stocks - ENREGISTRER LES FLUX EN PREMIER
        List<Flux> fluxes = dto.getArticles().stream().map(ar -> {
            Article article = articles.stream()
                    .filter(a -> a.getCod().equals(ar.getCod()))
                    .findFirst().get();

            if (mvt.equals(MvtType.VENTE)) {
                article.setStock(article.getStock().subtract(ar.getQuantite()));
            } else if (mvt.equals(MvtType.ACHAT)) {
                article.setStock(article.getStock().add(ar.getQuantite()));
            }

            articleRepository.save(article);

            // MODIFICATION: Utiliser prixAchat si mvt est ACHAT, sinon prix
            BigDecimal prixUni = mvt.equals(MvtType.ACHAT)
                    ? safe(article.getPrixAchat())
                    : safe(article.getPrix());

            // montant ligne (avant remise)
            BigDecimal montantLigne = prixUni.multiply(ar.getQuantite());

            return Flux.builder()
                    .bonSorti(savedBon)
                    .article(article)
                    .dateFlux(dto.getDatBon()!=null ? dto.getDatBon() : LocalDateTime.now())
                    .libelle(article.getDesignation())
                    .montant(montantLigne)
                    .sortie(mvt.equals(MvtType.VENTE) ? ar.getQuantite().intValue() : 0)
                    .entree(mvt.equals(MvtType.ACHAT) ? ar.getQuantite().intValue() : 0)
                    .prixUni(prixUni)
                    .fRemis(safe(ar.getRemisUni()))
// **IMPORTANT** : sauvegarder la remise unitaire du DTO
                    .build();
        }).toList();

        // **ENREGISTRER LES FLUX EN PREMIER**
        fluxRepository.saveAll(fluxes);
        savedBon.setFluxes(fluxes);
        savedBon.setRemisSurBon(dto.getRemisSurBon()!=null ? dto.getRemisSurBon():BigDecimal.ZERO);

        // **CRÉATION DE LA LISTE DES ARTICLES DTO DANS LE SERVICE**
        List<ArticleAddBonDto> articlesDto = fluxes.stream()
                .map(f -> ArticleAddBonDto.builder()
                        .cod(f.getArticle().getCod())
                        .ref(f.getArticle().getRef())
                        .designation(f.getArticle().getDesignation())
                        .quantite(mvt.equals(MvtType.ACHAT)
                                ? BigDecimal.valueOf(f.getEntree() != null ? f.getEntree() : 0)
                                : BigDecimal.valueOf(f.getSortie() != null ? f.getSortie() : 0))                        .prix(f.getPrixUni())
                        .remisUni(f.getFRemis()) // **IMPORTANT** : récupérer depuis le flux sauvegardé
                        .build()
                )
                .toList();

        tierRepository.saveAndFlush(tier);
        return BonAchatVenteDto.mapToDto(savedBon, articlesDto);
    }
    @Override
    @Transactional

    public BonAchatVenteDto updateBon(String serie, BonAchatVenteDto dto, MvtType mvt) {
        // 1) Vérifier que le bon existe
        Bonsorti existingBon = bonsortiRepository.findBySerie(serie)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bon avec la série " + serie + " introuvable",
                        List.of("Bon non trouvé"),
                        ErrorCodes.BONSOR_NOT_FOUND
                ));

        if(dto.getArticles()==null || dto.getArticles().isEmpty()){
            throw new InvalidOperationException(
                    "Aucun Article ajouté",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Articles ajoutés=null")
            );
        }
        if ((dto.getDetCheque() != null && !dto.getDetCheque().isEmpty()&& dto.getCheque()==null) ) {
            throw new InvalidOperationException(
                    "Montant Chèque n'est pas défini",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Montant Chèque n'est pas défini")
            );
        }
        if (dto.getCheque() != null && dto.getCheque().signum() > 0
                && !StringUtils.hasText(dto.getDetCheque())) {
            throw new InvalidOperationException(
                    "Détails de chèque sont obligatoires",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Détails Chèque")
            );
        }
        // Old tier (pour rollback)
        Tier oldTier = existingBon.getTier();

        // 2) Rollback du solde (null-safe)
        BigDecimal oldTierSolde = oldTier.getSolde() == null ? BigDecimal.ZERO : oldTier.getSolde();
        BigDecimal existingCredit = existingBon.getCredit() == null ? BigDecimal.ZERO : existingBon.getCredit();
        oldTier.setSolde(oldTierSolde.subtract(existingCredit));

        // rollback stock des flux existants (null-safe)
        existingBon.getFluxes().forEach(flux -> {
            Article article = flux.getArticle();
            BigDecimal stock = article.getStock() == null ? BigDecimal.ZERO : article.getStock();
            // flux.getSortie() et getEntree() sont des int
            if (existingBon.getMvt() != null && existingBon.getMvt().equals(MvtType.VENTE)) {

                article.setStock(stock.add(BigDecimal.valueOf(flux.getSortie())));
            } else if (existingBon.getMvt() != null && existingBon.getMvt().equals(MvtType.ACHAT)) {
                article.setStock(stock.subtract(BigDecimal.valueOf(flux.getEntree())));
            }
            articleRepository.save(article);
        });

        // 3) Supprimer anciens flux
        existingBon.getFluxes().clear();

        // 4) Validation articles
        if (dto.getArticles() == null || dto.getArticles().isEmpty()) {
            throw new InvalidOperationException(
                    "Aucun Article ajouté",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Articles ajoutés=null")
            );
        }

        // User
        User user;
        if (dto.getIdUser() != null) {
            user = userRepository.findByCod(dto.getIdUser())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Utilisateur non trouvé",
                            List.of("User not found"),
                            ErrorCodes.USER_NOT_FOUND
                    ));
        } else {
            user = existingBon.getUser();
        }

        // New tier
        Tier newTier = tierRepository.findById(dto.getIdTier())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tier non trouvé",
                        List.of("Tier not found"),
                        ErrorCodes.TIER_NOT_FOUND
                ));

        if (newTier.getQualite() == TypeTier.CLIENT && !mvt.equals(MvtType.VENTE)) {
            throw new InvalidOperationException(
                    "L'action disponible avec le client est juste le vente",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Tier type=Client mais mvt != VENTE")
            );
        }

        if (newTier.getQualite() == TypeTier.FOURNISSEUR && !mvt.equals(MvtType.ACHAT)) {
            throw new InvalidOperationException(
                    "L'action disponible avec le fournisseur est juste l'achat",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Tier type=Fournisseur mais mvt != ACHAT")
            );
        }

        // 5) Recalcul des flux (récupérer articles depuis DB)
        List<Article> articles = articleRepository.findAllById(
                dto.getArticles().stream().map(a -> a.getCod()).toList()
        );

        List<Flux> newFluxes = dto.getArticles().stream().map(ar -> {
            Article article = articles.stream()
                    .filter(a -> a.getCod().equals(ar.getCod()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Article introuvable",
                            List.of("Article not found"),
                            ErrorCodes.ARTICLE_NOT_FOUND
                    ));

            // null-safe stock
            BigDecimal currentStock = article.getStock() == null ? BigDecimal.ZERO : article.getStock();
            BigDecimal quantite = ar.getQuantite() == null ? BigDecimal.ZERO : ar.getQuantite();

            // MODIFICATION: Utiliser prixAchat si mvt est ACHAT, sinon prix
            BigDecimal prix = mvt.equals(MvtType.ACHAT)
                    ? (article.getPrixAchat() == null ? BigDecimal.ZERO : article.getPrixAchat())
                    : (article.getPrix() == null ? BigDecimal.ZERO : article.getPrix());

            BigDecimal fRemis = ar.getRemisUni() == null ? BigDecimal.ZERO : ar.getRemisUni();

            if (mvt.equals(MvtType.VENTE)) {
                article.setStock(currentStock.subtract(quantite));
            } else if (mvt.equals(MvtType.ACHAT)) {
                article.setStock(currentStock.add(quantite));
            }
            articleRepository.save(article);

            BigDecimal montant = prix.multiply(quantite);

            return Flux.builder()
                    .bonSorti(existingBon)
                    .article(article)
                    .dateFlux(dto.getDatBon() != null ? dto.getDatBon() : LocalDateTime.now())
                    .libelle(article.getDesignation())
                    .montant(montant)
                    .sortie(mvt.equals(MvtType.VENTE) ? quantite.intValue() : 0)
                    .entree(mvt.equals(MvtType.ACHAT) ? quantite.intValue() : 0)
                    .prixUni(prix)
                    .fRemis(fRemis)
                    .build();
        }).toList();

        // 6) Attacher les nouveaux flux
        existingBon.getFluxes().addAll(newFluxes);

        // 7) Recalcul montants et solde (null-safe)
        BigDecimal montantSansRemise = newFluxes.stream()
                .map(flux -> flux.getMontant() == null ? BigDecimal.ZERO : flux.getMontant())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remiseTotaleFlux = newFluxes.stream()
                .map(flux -> flux.getFRemis() == null ? BigDecimal.ZERO : flux.getFRemis())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remiseTotale = remiseTotaleFlux.add(dto.getRemis() != null ? dto.getRemis() : BigDecimal.ZERO);

        BigDecimal safeEspece = dto.getEspece() == null ? BigDecimal.ZERO : dto.getEspece();
        BigDecimal safeCheque = dto.getCheque() == null ? BigDecimal.ZERO : dto.getCheque();
        BigDecimal montantAvecRemise = montantSansRemise.subtract(remiseTotale);
        BigDecimal credit = montantAvecRemise.subtract(safeEspece.add(safeCheque));
        if (mvt.equals(MvtType.ACHAT)) credit = credit.multiply(BigDecimal.valueOf(-1));

        // Appliquer le solde sur le bon tier (newTier) — et sauvegarder les deux tiers correctement
        if (newTier.getId().equals(oldTier.getId())) {
            // même tier : on a déjà fait rollback sur oldTier, maintenant appliquer le nouveau crédit
            BigDecimal newSolde = (oldTier.getSolde() == null ? BigDecimal.ZERO : oldTier.getSolde()).add(credit);
            oldTier.setSolde(newSolde);
            tierRepository.saveAndFlush(oldTier);
        } else {
            // tiers différents : oldTier rollback déjà fait, appliquer crédit sur newTier
            BigDecimal newTierSolde = (newTier.getSolde() == null ? BigDecimal.ZERO : newTier.getSolde()).add(credit);
            newTier.setSolde(newTierSolde);
            tierRepository.saveAndFlush(oldTier);   // persister rollback de l'ancien
            tierRepository.saveAndFlush(newTier);   // persister application sur le nouveau
        }

        // 8) Mettre à jour les champs du bon
        existingBon.setUser(user);
        existingBon.setTier(newTier);
        existingBon.setCheque(safeCheque);
        existingBon.setEspece(safeEspece);
        existingBon.setCredit(credit);
        existingBon.setRemis(remiseTotale);
        existingBon.setMontant(montantSansRemise);
        existingBon.setDetCheq(dto.getDetCheque());
        existingBon.setDatBon(dto.getDatBon() != null ? dto.getDatBon() : LocalDateTime.now());
        existingBon.setMvt(mvt);
        existingBon.setEtat(true);
        existingBon.setSerie(this.generateSerie(existingBon.getDatBon()));
        existingBon.setRemisSurBon(dto.getRemisSurBon() !=null ? dto.getRemisSurBon():BigDecimal.ZERO);

        // 9) Sauvegarde finale
        Bonsorti savedBon = bonsortiRepository.save(existingBon);

        return BonAchatVenteDto.mapToDto(savedBon, dto.getArticles());
    }

    @Override
    @Transactional
    public void annulerBon(String serie) {

        Bonsorti bon = bonsortiRepository.findBySerie(serie)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bon introuvable",
                        List.of("Bon introuvable"),
                        ErrorCodes.BONSOR_NOT_FOUND));



        // Récupérer tier
        Tier tier = bon.getTier();
        if (tier == null) {
            throw new EntityNotFoundException("Tier non trouvé",
                    List.of("Tier not found"),
                    ErrorCodes.TIER_NOT_FOUND);
        }

        // 1) Réinjecter les stocks
        List<Flux> fluxList = fluxRepository.findByBonSorti(bon);
        for (Flux flux : fluxList) {
            Article article = flux.getArticle();
            if (article == null) continue;

            // Inverse de createBon
            if (bon.getMvt().equals(MvtType.VENTE)) {
                // Vente annulée → stock augmenté
                article.setStock(article.getStock().add(BigDecimal.valueOf(flux.getSortie())));
            } else if (bon.getMvt().equals(MvtType.ACHAT)) {
                // Achat annulé → stock diminué
                article.setStock(article.getStock().subtract(BigDecimal.valueOf(flux.getEntree())));
            }
            articleRepository.save(article);
        }

        // 2) Corriger le solde du tier
        BigDecimal prevSolde = tier.getSolde() == null ? BigDecimal.ZERO : tier.getSolde();
        BigDecimal credit = bon.getCredit() == null ? BigDecimal.ZERO : bon.getCredit();
        // On fait l’inverse du createBon
        tier.setSolde(prevSolde.subtract(credit));
        tierRepository.save(tier);

        // 3) Marquer le bon comme annulé (etat = false) au lieu de le supprimer
        bonsortiRepository.delete(bon);



    }

    @Override
    public ResponseEntity<byte[]> downloadBon(String serie, String mvt) {

        try {

            if(serie==null || serie.isBlank()){
                throw new InvalidOperationException(
                        "Série non spécifié",
                        ErrorCodes.BAD_CREDENTIALS,
                        List.of("Série indéfinie")
                );
            }
            // Récupérer les données de caisse pour la date
            Bonsorti bonsorti = this.bonsortiRepository.findBySerieAndMvtAndUserCod(serie, MvtType.valueOf(mvt), null);



            if (bonsorti == null) {
                 throw new InvalidOperationException(
                        "Bon n'est pas encore existe clique sur Valider",
                        ErrorCodes.BAD_CREDENTIALS,
                        List.of("Bon non trouvé")
                );
            }

            // **CORRECTION** : Créer la liste des articles DTO
            List<ArticleAddBonDto> articlesDto = createArticlesDto(bonsorti);

            // Générer le PDF
            byte[] pdfBytes = pdfGenerator.generatePdf(BonAchatVenteDto.mapToDto(bonsorti, articlesDto));

            // Créer le nom du fichier
            String fileName = "Bon" + bonsorti.getMvt() + mvt + bonsorti.getSerie() + bonsorti.getDatBon().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";

            // Configurer les headers HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());

            // headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (DocumentException | IOException e) {
            throw new InvalidEntityException("Invalid data", ErrorCodes.BAD_CREDENTIALS, List.of("Une erreur se produit"));
        }
    }

    @Override
    public List<String> getAllBonAchatSeries(Integer userCod) {
        return this.bonsortiRepository.getAllByMvt(userCod, MvtType.ACHAT);
    }

    @Override
    public List<String> getAllBonVenteSeries(Integer userCod) {
        return this.bonsortiRepository.getAllByMvt(userCod, MvtType.VENTE);
    }
}