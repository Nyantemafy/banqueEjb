using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace CompteDepot.Service.Models
{
    public enum TypeOperationDepot
    {
        DEPOT,
        RETRAIT,
        CALCUL_INTERETS,
        FRAIS,
        CLOTURE
    }

    public enum StatutOperationDepot
    {
        EN_ATTENTE,
        VALIDEE,
        REJETEE,
        ANNULEE
    }

    [Table("OperationsDepot")]
    public class OperationDepot
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public long IdOperation { get; set; }

        [Required]
        [StringLength(20)]
        public string NumeroCompte { get; set; } = string.Empty;

        [Column(TypeName = "decimal(15,2)")]
        public decimal Montant { get; set; }

        [Required]
        [StringLength(20)]
        public string TypeOperation { get; set; } = string.Empty;

        [StringLength(255)]
        public string? Description { get; set; }

        public DateTime DateOperation { get; set; }

        [Column(TypeName = "decimal(15,2)")]
        public decimal SoldeApresOperation { get; set; }

        [StringLength(50)]
        public string? ReferenceExterne { get; set; }

        [Column(TypeName = "decimal(10,2)")]
        public decimal Frais { get; set; } = 0;

        [StringLength(20)]
        public string Statut { get; set; } = "VALIDEE";

        [StringLength(100)]
        public string? UtilisateurOperation { get; set; }

        public DateTime? DateValidation { get; set; }

        [StringLength(500)]
        public string? Commentaires { get; set; }

        // Propriétés de navigation et utilitaires
        [NotMapped]
        public TypeOperationDepot TypeEnum
        {
            get => Enum.TryParse<TypeOperationDepot>(TypeOperation, out var result) ? result : TypeOperationDepot.DEPOT;
            set => TypeOperation = value.ToString();
        }

        [NotMapped]
        public StatutOperationDepot StatutEnum
        {
            get => Enum.TryParse<StatutOperationDepot>(Statut, out var result) ? result : StatutOperationDepot.VALIDEE;
            set => Statut = value.ToString();
        }

        [NotMapped]
        public bool EstCredit => Montant > 0;

        [NotMapped]
        public bool EstDebit => Montant < 0;

        [NotMapped]
        public decimal MontantAbsolu => Math.Abs(Montant);

        [NotMapped]
        public string TypeLibelle
        {
            get => TypeOperation switch
            {
                "DEPOT" => "Dépôt",
                "RETRAIT" => "Retrait",
                "CALCUL_INTERETS" => "Calcul d'intérêts",
                "FRAIS" => "Frais bancaires",
                "CLOTURE" => "Clôture de compte",
                _ => TypeOperation
            };
        }

        [NotMapped]
        public string StatutLibelle
        {
            get => Statut switch
            {
                "EN_ATTENTE" => "En attente",
                "VALIDEE" => "Validée",
                "REJETEE" => "Rejetée",
                "ANNULEE" => "Annulée",
                _ => Statut
            };
        }

        // Constructeurs
        public OperationDepot()
        {
            DateOperation = DateTime.Now;
            Statut = "VALIDEE";
            Frais = 0;
        }

        public OperationDepot(string numeroCompte, decimal montant, TypeOperationDepot type, string? description = null)
            : this()
        {
            NumeroCompte = numeroCompte;
            Montant = montant;
            TypeEnum = type;
            Description = description ?? GetDefaultDescription(type, montant);
        }

        private static string GetDefaultDescription(TypeOperationDepot type, decimal montant)
        {
            return type switch
            {
                TypeOperationDepot.DEPOT => $"Dépôt de {montant:C}",
                TypeOperationDepot.RETRAIT => $"Retrait de {Math.Abs(montant):C}",
                TypeOperationDepot.CALCUL_INTERETS => $"Intérêts calculés: {montant:C}",
                TypeOperationDepot.FRAIS => $"Frais bancaires: {Math.Abs(montant):C}",
                TypeOperationDepot.CLOTURE => "Clôture du compte de dépôt",
                _ => "Opération sur compte de dépôt"
            };
        }

        // Méthodes de validation
        public bool EstValide()
        {
            if (string.IsNullOrWhiteSpace(NumeroCompte)) return false;
            if (string.IsNullOrWhiteSpace(TypeOperation)) return false;
            if (Montant == 0 && TypeEnum != TypeOperationDepot.CLOTURE) return false;
            
            return true;
        }

        public void Valider(string? utilisateur = null)
        {
            StatutEnum = StatutOperationDepot.VALIDEE;
            DateValidation = DateTime.Now;
            UtilisateurOperation = utilisateur ?? "SYSTEM";
        }

        public void Rejeter(string motif)
        {
            StatutEnum = StatutOperationDepot.REJETEE;
            DateValidation = DateTime.Now;
            Commentaires = motif;
        }

        public override string ToString()
        {
            return $"Operation[{IdOperation}] - {NumeroCompte} - {TypeLibelle}: {Montant:C} - {StatutLibelle}";
        }
    }
}