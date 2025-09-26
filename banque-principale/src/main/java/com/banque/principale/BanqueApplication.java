package com.banque.principale;

import com.banque.principale.ui.MenuConsole;

public class BanqueApplication {

    public static void main(String[] args) {
        System.out.println("🚀 === DÉMARRAGE SYSTÈME BANCAIRE HÉTÉROGÈNE ===");
        System.out.println("   Architecture: Java EJB + .NET WCF + Interface Unifiée");
        System.out.println("   Date: " + new java.util.Date());
        System.out.println();

        try {
            // Lancer l'interface console
            MenuConsole menu = new MenuConsole();
            menu.demarrer();

        } catch (Exception e) {
            System.err.println("❌ Erreur fatale: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n🛑 Application fermée.");
    }
}