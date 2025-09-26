package com.banque.principale.ui;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import com.banque.comptecourant.model.Transaction;
import com.banque.pret.entity.DemandePret;
import com.banque.pret.entity.Pret;
import com.banque.principale.model.Client;
import com.banque.principale.service.BanqueService;
import com.banque.principale.service.ClientService;

public class MenuConsole {

    private BanqueService banqueService;
    private Scanner scanner;

    public MenuConsole() {
        // Injection de dépendance correcte
        ClientService clientService = new ClientService();
        this.banqueService = new BanqueService(clientService);
        this.scanner = new Scanner(System.in);
    }

    // ==================== UTILITAIRES ====================

    private int lireChoix() {
        try {
            String line = scanner.nextLine();
            return Integer.parseInt(line.trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private String repeatChar(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++)
            sb.append(c);
        return sb.toString();
    }

    private void pause() {
        System.out.println("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }

    // ==================== MENU PRINCIPAL ====================

    public void demarrer() {
        System.out.println("🏦 BIENVENUE DANS LE SYSTÈME BANCAIRE");

        while (true) {
            System.out.println("\n" + repeatChar('=', 50));
            System.out.println("1. Gestion Clients");
            System.out.println("2. Compte Courant (Java/EJB)");
            System.out.println("3. Compte Dépôt (.NET/WCF)");
            System.out.println("4. Prêts");
            System.out.println("0. Quitter");
            System.out.print("\nVotre choix: ");

            int choix = lireChoix();
            switch (choix) {
                case 1:
                    menuClients();
                    break;
                case 2:
                    menuCompteCourant();
                    break;
                case 3:
                    menuCompteDepot();
                    break;
                case 4:
                    menuPrets();
                    break;
                case 0:
                    System.out.println("👋 Au revoir!");
                    return;
                default:
                    System.out.println("❌ Choix invalide");
            }
        }
    }

    // ==================== MENU CLIENTS ====================

    private void menuClients() {
        while (true) {
            System.out.println("\n" + repeatChar('=', 30));
            System.out.println("👤 GESTION CLIENTS");
            System.out.println("1. Créer client");
            System.out.println("2. Synthèse client");
            System.out.println("0. Retour");
            System.out.print("\nVotre choix: ");

            int choix = lireChoix();
            switch (choix) {
                case 1:
                    creerClient();
                    break;
                case 2:
                    voirSyntheseClient();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Choix invalide");
            }
        }
    }

    private void creerClient() {
        System.out.print("Numéro client: ");
        String num = scanner.nextLine();
        System.out.print("Nom: ");
        String nom = scanner.nextLine();
        System.out.print("Prénom: ");
        String prenom = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        Client client = new Client(num, nom, prenom);
        client.setEmail(email);

        boolean ok = banqueService.creerClient(client);
        System.out.println(ok ? "✅ Client créé avec succès" : "❌ Échec création client");
        pause();
    }

    private void voirSyntheseClient() {
        System.out.print("Numéro client: ");
        String num = scanner.nextLine();
        Client client = banqueService.getClient(num);

        if (client != null) {
            System.out.println("\n📋 Synthèse client:");
            System.out.println("Nom complet: " + client.getNomComplet());
            System.out.println("Solde compte courant: " + client.getSoldeCompteCourant() + " €");
            System.out.println("Solde compte dépôt: " + client.getSoldeCompteDepot() + " €");
        } else {
            System.out.println("❌ Client introuvable");
        }
        pause();
    }

    // ==================== MENU COMPTE COURANT ====================

    private void menuCompteCourant() {
        System.out.print("Numéro compte courant: ");
        String num = scanner.nextLine();

        while (true) {
            System.out.println("\n💳 COMPTE COURANT");
            System.out.println("1. Consulter solde");
            System.out.println("2. Déposer");
            System.out.println("3. Retirer");
            System.out.println("4. Historique");
            System.out.println("0. Retour");
            System.out.print("\nVotre choix: ");

            int choix = lireChoix();
            switch (choix) {
                case 1:
                    BigDecimal solde = banqueService.consulterSoldeCompteCourant(num);
                    System.out.println("💰 Solde: " + solde + " €");
                    pause();
                    break;
                case 2:
                    System.out.print("Montant à déposer: ");
                    BigDecimal dep = new BigDecimal(scanner.nextLine());
                    System.out.println(
                            banqueService.deposerCompteCourant(num, dep) ? "✅ Dépôt effectué" : "❌ Échec dépôt");
                    pause();
                    break;
                case 3:
                    System.out.print("Montant à retirer: ");
                    BigDecimal ret = new BigDecimal(scanner.nextLine());
                    System.out.println(
                            banqueService.retirerCompteCourant(num, ret) ? "✅ Retrait effectué" : "❌ Échec retrait");
                    pause();
                    break;
                case 4:
                    List<Transaction> txs = banqueService.getHistoriqueCompteCourant(num);
                    if (txs != null && !txs.isEmpty())
                        txs.forEach(System.out::println);
                    else
                        System.out.println("📭 Aucun historique");
                    pause();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Choix invalide");
            }
        }
    }

    // ==================== MENU COMPTE DÉPÔT ====================

    private void menuCompteDepot() {
        System.out.print("Numéro compte dépôt: ");
        String num = scanner.nextLine();

        while (true) {
            System.out.println("\n💰 COMPTE DÉPÔT");
            System.out.println("1. Consulter solde");
            System.out.println("2. Déposer");
            System.out.println("3. Retirer");
            System.out.println("0. Retour");
            System.out.print("\nVotre choix: ");

            int choix = lireChoix();
            switch (choix) {
                case 1:
                    BigDecimal solde = banqueService.consulterSoldeCompteDepot(num);
                    System.out.println("💰 Solde: " + solde + " €");
                    pause();
                    break;
                case 2:
                    System.out.print("Montant à déposer: ");
                    BigDecimal dep = new BigDecimal(scanner.nextLine());
                    System.out
                            .println(banqueService.deposerCompteDepot(num, dep) ? "✅ Dépôt effectué" : "❌ Échec dépôt");
                    pause();
                    break;
                case 3:
                    System.out.print("Montant à retirer: ");
                    BigDecimal ret = new BigDecimal(scanner.nextLine());
                    System.out.println(
                            banqueService.retirerCompteDepot(num, ret) ? "✅ Retrait effectué" : "❌ Échec retrait");
                    pause();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Choix invalide");
            }
        }
    }

    // ==================== MENU PRÊTS ====================

    private void menuPrets() {
        System.out.print("Numéro client: ");
        String numClient = scanner.nextLine();

        while (true) {
            System.out.println("\n🏠 PRÊTS");
            System.out.println("1. Demander un prêt");
            System.out.println("2. Calculer mensualité");
            System.out.println("3. Voir demandes en attente");
            System.out.println("4. Approuver une demande");
            System.out.println("5. Rejeter une demande");
            System.out.println("6. Voir prêts client");
            System.out.println("0. Retour");
            System.out.print("\nVotre choix: ");

            int choix = lireChoix();
            switch (choix) {
                case 1:
                    System.out.print("Montant prêt: ");
                    BigDecimal montant = new BigDecimal(scanner.nextLine());
                    System.out.print("Durée (mois): ");
                    int duree = Integer.parseInt(scanner.nextLine());
                    System.out.print("Objet: ");
                    String objet = scanner.nextLine();
                    String numDem = banqueService.demanderPret(numClient, montant, duree, objet);
                    System.out.println("✅ Numéro demande: " + numDem);
                    pause();
                    break;
                case 2:
                    System.out.print("Montant: ");
                    BigDecimal m = new BigDecimal(scanner.nextLine());
                    System.out.print("Durée (mois): ");
                    int d = Integer.parseInt(scanner.nextLine());
                    BigDecimal mens = banqueService.calculerMensualitePret(m, d);
                    System.out.println("💰 Mensualité: " + mens + " €");
                    pause();
                    break;
                case 3:
                    List<DemandePret> demandes = banqueService.getDemandesEnAttente();
                    if (demandes != null && !demandes.isEmpty())
                        demandes.forEach(System.out::println);
                    else
                        System.out.println("📭 Aucune demande en attente");
                    pause();
                    break;
                case 4:
                    System.out.print("Numéro demande à approuver: ");
                    String appr = scanner.nextLine();
                    System.out.println(banqueService.approuverDemandePret(appr) ? "✅ Demande approuvée" : "❌ Échec");
                    pause();
                    break;
                case 5:
                    System.out.print("Numéro demande à rejeter: ");
                    String rej = scanner.nextLine();
                    System.out.print("Motif rejet: ");
                    String motif = scanner.nextLine();
                    System.out.println(banqueService.rejeterDemandePret(rej, motif) ? "✅ Demande rejetée" : "❌ Échec");
                    pause();
                    break;
                case 6:
                    List<Pret> prets = banqueService.getPretsClient(numClient);
                    if (prets != null && !prets.isEmpty())
                        prets.forEach(System.out::println);
                    else
                        System.out.println("📭 Aucun prêt pour ce client");
                    pause();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Choix invalide");
            }
        }
    }

    // ==================== MAIN ====================

    public static void main(String[] args) {
        MenuConsole menu = new MenuConsole();
        menu.demarrer();
    }
}
