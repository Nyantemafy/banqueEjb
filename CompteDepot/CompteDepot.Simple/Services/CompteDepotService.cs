using CompteDepot.Simple.Data;
using CompteDepot.Simple.Models;
using Microsoft.EntityFrameworkCore;

namespace CompteDepot.Simple.Services
{
    public class CompteDepotService : ICompteDepotService
    {
        public decimal ConsulterSolde(string numeroCompte)
        {
            Console.WriteLine($"🔍 Consultation solde CompteDepot : {numeroCompte}");
            
            using var context = new BanqueContext();
            var compte = context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
            
            if (compte == null)
            {
                Console.WriteLine("❌ Compte de dépôt non trouvé");
                return 0;
            }
            
            Console.WriteLine($"✅ Solde trouvé : {compte.Solde:C}");
            return compte.Solde;
        }
        
        public bool CreerCompte(string numeroCompte, string proprietaire, decimal tauxInteret)
        {
            Console.WriteLine($"🆕 Création CompteDepot {numeroCompte} pour {proprietaire}");
            
            using var context = new BanqueContext();
            
            // Vérifier si existe déjà
            if (context.ComptesDepot.Any(c => c.NumeroCompte == numeroCompte))
            {
                Console.WriteLine("❌ Compte déjà existant");
                return false;
            }
            
            // Créer le compte (durée par défaut 12 mois)
            var compte = new CompteDepot.Simple.Models.CompteDepot
            {
                NumeroCompte = numeroCompte,
                Proprietaire = proprietaire,
                TauxInteret = tauxInteret,
                DateCreation = DateTime.Now,
                DateEcheance = DateTime.Now.AddMonths(12),
                Solde = 0
            };
            
            context.ComptesDepot.Add(compte);
            context.SaveChanges();
            
            Console.WriteLine($"✅ CompteDepot créé avec taux {tauxInteret}%");
            return true;
        }
        
        public bool Deposer(string numeroCompte, decimal montant)
        {
            Console.WriteLine($"💰 Dépôt CompteDepot de {montant:C} sur {numeroCompte}");
            
            if (montant <= 0)
            {
                Console.WriteLine("❌ Montant invalide");
                return false;
            }
            
            using var context = new BanqueContext();
            var compte = context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
            
            if (compte == null)
            {
                Console.WriteLine("❌ Compte non trouvé");
                return false;
            }
            
            // Ajouter l'argent
            compte.Solde += montant;
            
            // Enregistrer l'opération
            var operation = new OperationDepot(numeroCompte, montant, "DEPOT", 
                                             $"Dépôt de {montant:C}");
            context.Operations.Add(operation);
            
            context.SaveChanges();
            
            Console.WriteLine($"✅ Dépôt réussi, nouveau solde : {compte.Solde:C}");
            return true;
        }
        
        public bool Retirer(string numeroCompte, decimal montant)
        {
            Console.WriteLine($"💸 Retrait CompteDepot de {montant:C} sur {numeroCompte}");
            
            if (montant <= 0)
            {
                Console.WriteLine("❌ Montant invalide");
                return false;
            }
            
            using var context = new BanqueContext();
            var compte = context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
            
            if (compte == null)
            {
                Console.WriteLine("❌ Compte non trouvé");
                return false;
            }
            
            // Vérifier les limites de retrait
            if (!compte.PeutRetirer(montant))
            {
                Console.WriteLine($"❌ Limites de retrait dépassées");
                Console.WriteLine($"   Limite mensuelle: {compte.Solde * 0.10m:C} (déjà retiré: {compte.MontantRetireMois:C})");
                Console.WriteLine($"   Limite annuelle: {compte.Solde * 0.50m:C} (déjà retiré: {compte.MontantRetireAnnee:C})");
                return false;
            }
            
            // Retirer l'argent
            compte.Solde -= montant;
            compte.MontantRetireMois += montant;
            compte.MontantRetireAnnee += montant;
            
            // Enregistrer l'opération (montant négatif)
            var operation = new OperationDepot(numeroCompte, -montant, "RETRAIT", 
                                             $"Retrait de {montant:C}");
            context.Operations.Add(operation);
            
            context.SaveChanges();
            
            Console.WriteLine($"✅ Retrait réussi, nouveau solde : {compte.Solde:C}");
            return true;
        }
        
        public decimal CalculerInterets(string numeroCompte)
        {
            Console.WriteLine($"📊 Calcul intérêts pour {numeroCompte}");
            
            using var context = new BanqueContext();
            var compte = context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
            
            if (compte == null)
            {
                Console.WriteLine("❌ Compte non trouvé");
                return 0;
            }
            
            decimal interets = compte.CalculerInterets();
            Console.WriteLine($"✅ Intérêts calculés : {interets:C} (Taux: {compte.TauxInteret}%)");
            
            return interets;
        }
        
        public List<OperationDepot> GetHistorique(string numeroCompte)
        {
            Console.WriteLine($"📋 Historique CompteDepot pour {numeroCompte}");
            
            using var context = new BanqueContext();
            var operations = context.Operations
                .Where(o => o.NumeroCompte == numeroCompte)
                .OrderByDescending(o => o.DateOperation)
                .ToList();
            
            Console.WriteLine($"✅ {operations.Count} opérations trouvées");
            return operations;
        }
    }
}