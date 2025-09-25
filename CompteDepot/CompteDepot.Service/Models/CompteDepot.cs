using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace CompteDepot.Service.Models
{
    [Table("ComptesDepot")]
    public class CompteDepot
    {
        [Key]
        [StringLength(20)]
        public string NumeroCompte { get; set; } = string.Empty;

        [Required]
        [StringLength(100)]
        public string Proprietaire { get; set; } = string.Empty;

        [Column(TypeName = "decimal(15,2)")]
        public decimal Solde { get; set; }

        [Column(TypeName = "decimal(5,2)")]
        public decimal TauxInteret { get; set; }

        public int DureeEnMois { get; set; }

        public DateTime DateCreation { get; set; }

        public DateTime DateEcheance { get; set; }

        [Column(TypeName = "decimal(15,2)")]
        public decimal MontantRetireAnnee { get; set; }

        [Column(TypeName = "decimal(15,2)")]
        public decimal MontantRetireMois { get; set; }

        public DateTime? DernierRetraitMois { get; set; }

        public DateTime? DernierRetraitAnnee { get; set; }

        public bool Actif { get; set; } = true;

        [StringLength(255)]
        public string? Notes { get; set; }

        // Propriétés calculées
        [NotMapped]
        public decimal LimiteMensuelle => Solde * 0.10m; // 10% par mois

        [NotMapped]
        public decimal LimiteAnnuelle => Solde * 0.50m; // 50% par an

        [NotMapped]
        public decimal DisponibleMois 
        { 
            get 
            {
                var maintenant = DateTime.Now;
                if (DernierRetraitMois?.Month != maintenant.Month || DernierRetraitMois?.Year != maintenant.Year)
                {
                    return LimiteMensuelle;
                }
                return Math.Max(0, LimiteMensuelle - MontantRetireMois);
            } 
        }

        [NotMapped]
        public decimal DisponibleAnnee 
        { 
            get 
            {
                var maintenant = DateTime.Now;
                if (DernierRetraitAnnee?.Year != maintenant.Year)
                {
                    return LimiteAnnuelle;
                }
                return Math.Max(0, LimiteAnnuelle - MontantRetireAnnee);
            } 
        }

        [NotMapped]
        public bool EstEchu => DateTime.Now >= DateEcheance;

        [NotMapped]
        public int JoursRestants => EstEchu ? 0 : (int)(DateEcheance - DateTime.Now).TotalDays;

        // Méthodes utilitaires
        public bool PeutRetirer(decimal montant)
        {
            if (montant <= 0 || !Actif) return false;
            if (Solde < montant) return false;

            var maintenant = DateTime.Now;
            decimal retraitMoisCourant = MontantRetireMois;
            decimal retraitAnneeCourante = MontantRetireAnnee;

            // Reset si nouveau mois/année
            if (DernierRetraitMois?.Month != maintenant.Month || DernierRetraitMois?.Year != maintenant.Year)
                retraitMoisCourant = 0;
            if (DernierRetraitAnnee?.Year != maintenant.Year)
                retraitAnneeCourante = 0;

            return (retraitMoisCourant + montant <= LimiteMensuelle) &&
                   (retraitAnneeCourante + montant <= LimiteAnnuelle);
        }

        public decimal CalculerInteretsActuels()
        {
            var moisEcoules = (DateTime.Now - DateCreation).Days / 30.0;
            if (moisEcoules <= 0) return 0;

            // Intérêts composés mensuels
            var tauxMensuel = (double)(TauxInteret / 12 / 100);
            var facteur = Math.Pow(1 + tauxMensuel, moisEcoules);
            var montantAvecInterets = (double)Solde * facteur;
            
            return Math.Round((decimal)(montantAvecInterets - (double)Solde), 2);
        }

        public void ResetLimitesRetrait()
        {
            var maintenant = DateTime.Now;
            
            if (DernierRetraitMois?.Month != maintenant.Month || DernierRetraitMois?.Year != maintenant.Year)
            {
                MontantRetireMois = 0;
            }
            
            if (DernierRetraitAnnee?.Year != maintenant.Year)
            {
                MontantRetireAnnee = 0;
            }
        }

        public override string ToString()
        {
            return $"CompteDepot[{NumeroCompte}] - {Proprietaire} - Solde: {Solde:C} - Actif: {Actif}";
        }
    }
}