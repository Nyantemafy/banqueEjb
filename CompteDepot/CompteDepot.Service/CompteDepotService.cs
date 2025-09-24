using System;
using System.Collections.Generic;
using System.Linq;

namespace CompteDepot.Service
{
    public class CompteDepotService : ICompteDepotService
    {
        private readonly CompteDepotContext _context;
        
        public CompteDepotService()
        {
            _context = new CompteDepotContext();
        }
        
        public decimal ConsulterSolde(string numeroCompte)
        {
            try
            {
                var compte = _context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
                return compte?.Solde ?? 0;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Erreur consultation solde: {ex.Message}");
                return 0;
            }
        }
        
        public bool CreerCompteDepot(string numeroCompte, string proprietaire, 
                                   decimal tauxInteret, int dureeEnMois)
        {
            try
            {
                if (_context.ComptesDepot.Any(c => c.NumeroCompte == numeroCompte))
                    return false;
                
                var compte = new Models.CompteDepot
                {
                    NumeroCompte = numeroCompte,
                    Proprietaire = proprietaire,
                    Solde = 0,
                    TauxInteret = tauxInteret,
                    DureeEnMois = dureeEnMois,
                    DateCreation = DateTime.Now,
                    DateEcheance = DateTime.Now.AddMonths(dureeEnMois),
                    MontantRetireAnnee = 0,
                    MontantRetireMois = 0
                };
                
                _context.ComptesDepot.Add(compte);
                _context.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Erreur création compte: {ex.Message}");
                return false;
            }
        }
        
        public bool EffectuerDepot(string numeroCompte, decimal montant)
        {
            try
            {
                var compte = _context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
                if (compte == null || montant <= 0) return false;
                
                compte.Solde += montant;
                
                var operation = new OperationDepot
                {
                    NumeroCompte = numeroCompte,
                    Montant = montant,
                    TypeOperation = "DEPOT",
                    DateOperation = DateTime.Now,
                    Description = "Dépôt sur compte de dépôt"
                };
                
                _context.Operations.Add(operation);
                _context.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Erreur dépôt: {ex.Message}");
                return false;
            }
        }
        
        public bool EffectuerRetrait(string numeroCompte, decimal montant)
        {
            try
            {
                var compte = _context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
                if (compte == null || montant <= 0 || !PeutRetirer(numeroCompte, montant))
                    return false;
                
                compte.Solde -= montant;
                
                // Mise à jour des limites de retrait
                var maintenant = DateTime.Now;
                if (compte.DernierRetraitMois?.Month != maintenant.Month)
                {
                    compte.MontantRetireMois = 0;
                    compte.DernierRetraitMois = maintenant;
                }
                if (compte.DernierRetraitAnnee?.Year != maintenant.Year)
                {
                    compte.MontantRetireAnnee = 0;
                    compte.DernierRetraitAnnee = maintenant;
                }
                
                compte.MontantRetireMois += montant;
                compte.MontantRetireAnnee += montant;
                
                var operation = new OperationDepot
                {
                    NumeroCompte = numeroCompte,
                    Montant = -montant,
                    TypeOperation = "RETRAIT",
                    DateOperation = DateTime.Now,
                    Description = "Retrait sur compte de dépôt"
                };
                
                _context.Operations.Add(operation);
                _context.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Erreur retrait: {ex.Message}");
                return false;
            }
        }
        
        public bool PeutRetirer(string numeroCompte, decimal montant)
        {
            try
            {
                var compte = _context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
                if (compte == null) return false;
                
                // Vérification solde suffisant
                if (compte.Solde < montant) return false;
                
                // Limites de retrait (exemple: 10% par mois, 50% par an)
                decimal limiteMensuelle = compte.Solde * 0.10m;
                decimal limiteAnnuelle = compte.Solde * 0.50m;
                
                var maintenant = DateTime.Now;
                decimal retraitMoisCourant = compte.MontantRetireMois;
                decimal retraitAnneeCourante = compte.MontantRetireAnnee;
                
                // Reset si nouveau mois/année
                if (compte.DernierRetraitMois?.Month != maintenant.Month)
                    retraitMoisCourant = 0;
                if (compte.DernierRetraitAnnee?.Year != maintenant.Year)
                    retraitAnneeCourante = 0;
                
                return (retraitMoisCourant + montant <= limiteMensuelle) &&
                       (retraitAnneeCourante + montant <= limiteAnnuelle);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Erreur vérification retrait: {ex.Message}");
                return false;
            }
        }
        
        public decimal CalculerInterets(string numeroCompte)
        {
            try
            {
                var compte = _context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
                if (compte == null) return 0;
                
                var moisEcoules = (DateTime.Now - compte.DateCreation).Days / 30;
                if (moisEcoules <= 0) return 0;
                
                // Calcul intérêts composés mensuels
                decimal tauxMensuel = compte.TauxInteret / 12 / 100;
                decimal interets = compte.Solde * (decimal)Math.Pow((double)(1 + tauxMensuel), moisEcoules) - compte.Solde;
                
                return Math.Round(interets, 2);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Erreur calcul intérêts: {ex.Message}");
                return 0;
            }
        }
        
        public List<OperationDepot> GetHistorique(string numeroCompte)
        {
            try
            {
                return _context.Operations
                    .Where(o => o.NumeroCompte == numeroCompte)
                    .OrderByDescending(o => o.DateOperation)
                    .ToList();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Erreur historique: {ex.Message}");
                return new List<OperationDepot>();
            }
        }
    }
}