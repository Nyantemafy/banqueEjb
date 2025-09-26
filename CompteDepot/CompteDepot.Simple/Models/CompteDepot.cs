using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace CompteDepot.Simple.Models
{
    // Table des comptes de dépôt
    public class CompteDepot
    {
        [Key]  // Clé primaire
        public string NumeroCompte { get; set; } = "";
        
        public string Proprietaire { get; set; } = "";
        public decimal Solde { get; set; }
        public decimal TauxInteret { get; set; }  // Ex: 2.5 pour 2.5%
        public DateTime DateCreation { get; set; }
        public DateTime DateEcheance { get; set; }
        
        // Limites de retrait simples
        public decimal MontantRetireMois { get; set; }    // Ce qui a été retiré ce mois
        public decimal MontantRetireAnnee { get; set; }   // Ce qui a été retiré cette année
        
        public CompteDepot()
        {
            DateCreation = DateTime.Now;
            Solde = 0;
            MontantRetireMois = 0;
            MontantRetireAnnee = 0;
        }
        
        // Méthodes simples
        public bool PeutRetirer(decimal montant)
        {
            // Règles simples : 10% par mois max, 50% par an max
            decimal limiteMensuelle = Solde * 0.10m;
            decimal limiteAnnuelle = Solde * 0.50m;
            
            return (Solde >= montant) && 
                (MontantRetireMois + montant <= limiteMensuelle) && 
                (MontantRetireAnnee + montant <= limiteAnnuelle);
        }
        
        public decimal CalculerInterets()
        {
            // Calcul simple d'intérêts
            int moisEcoules = (int)(DateTime.Now - DateCreation).TotalDays / 30;
            if (moisEcoules <= 0) return 0;
            
            decimal interetsSimples = Solde * (TauxInteret / 100) * (moisEcoules / 12m);
            return Math.Round(interetsSimples, 2);
        }
        
        public override string ToString()
        {
            return $"CompteDepot {NumeroCompte} - {Proprietaire} - {Solde:C}";
        }
    }
}