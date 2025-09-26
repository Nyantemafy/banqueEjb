using System;
using System.Collections.Generic;
using System.Linq;
using CompteDepot.Simple.Data;
using CompteDepot.Simple.Models;
using Microsoft.EntityFrameworkCore;
using CompteDepotModel = CompteDepot.Simple.Models.CompteDepot;

namespace CompteDepot.Simple.Services
{
    public class CompteDepotService : ICompteDepotService
    {
        private readonly BanqueContext _context;

        public CompteDepotService(BanqueContext context)
        {
            _context = context;
        }

        public decimal ConsulterSolde(string numeroCompte)
        {
            Console.WriteLine($"🔍 Consultation solde CompteDepot : {numeroCompte}");
            
            var compte = _context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
            
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
            
            if (_context.ComptesDepot.Any(c => c.NumeroCompte == numeroCompte))
            {
                Console.WriteLine("❌ Compte déjà existant");
                return false;
            }
            
            var compte = new CompteDepotModel
            {
                NumeroCompte = numeroCompte,
                Proprietaire = proprietaire,
                TauxInteret = tauxInteret,
                DateCreation = DateTime.Now,
                DateEcheance = DateTime.Now.AddMonths(12),
                Solde = 0
            };
            
            _context.ComptesDepot.Add(compte);
            _context.SaveChanges();
            
            Console.WriteLine($"✅ CompteDepot créé avec taux {tauxInteret}%");
            return true;
        }
        
        public bool Deposer(string numeroCompte, decimal montant)
        {
            Console.WriteLine($"💰 Dépôt CompteDepot de {montant:C} sur {numeroCompte}");
            if (montant <= 0) return false;

            var compte = _context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
            if (compte == null) return false;

            compte.Solde += montant;

            var operation = new OperationDepot(numeroCompte, montant, "DEPOT", $"Dépôt de {montant:C}");
            _context.Operations.Add(operation);
            _context.SaveChanges();

            return true;
        }
        
        public bool Retirer(string numeroCompte, decimal montant)
        {
            var compte = _context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
            if (compte == null || montant <= 0 || !compte.PeutRetirer(montant)) return false;

            compte.Solde -= montant;
            compte.MontantRetireMois += montant;
            compte.MontantRetireAnnee += montant;

            var operation = new OperationDepot(numeroCompte, -montant, "RETRAIT", $"Retrait de {montant:C}");
            _context.Operations.Add(operation);
            _context.SaveChanges();

            return true;
        }
        
        public decimal CalculerInterets(string numeroCompte)
        {
            var compte = _context.ComptesDepot.FirstOrDefault(c => c.NumeroCompte == numeroCompte);
            if (compte == null) return 0;

            return compte.CalculerInterets();
        }
        
        public List<OperationDepot> GetHistorique(string numeroCompte)
        {
            return _context.Operations
                .Where(o => o.NumeroCompte == numeroCompte)
                .OrderByDescending(o => o.DateOperation)
                .ToList();
        }
    }
}