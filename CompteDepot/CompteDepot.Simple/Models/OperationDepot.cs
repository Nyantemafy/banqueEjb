using System.ComponentModel.DataAnnotations;

namespace CompteDepot.Simple.Models
{
    public class OperationDepot
    {
        [Key]
        public int Id { get; set; }
        public string NumeroCompte { get; set; } = "";
        public decimal Montant { get; set; }
        public string Type { get; set; } = "";  // "DEPOT" ou "RETRAIT"
        public DateTime DateOperation { get; set; }
        public string Description { get; set; } = "";
        
        public OperationDepot()
        {
            DateOperation = DateTime.Now;
        }
        
        public OperationDepot(string numeroCompte, decimal montant, string type, string description)
        {
            NumeroCompte = numeroCompte;
            Montant = montant;
            Type = type;
            Description = description;
            DateOperation = DateTime.Now;
        }
        
        public override string ToString()
        {
            return $"{Type} de {Math.Abs(Montant):C} le {DateOperation:dd/MM/yyyy}";
        }
    }
}