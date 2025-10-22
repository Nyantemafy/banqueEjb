namespace CompteDepot.Models
{
    public class ProduitEpargneModel
    {
        public int Id { get; set; }
        public string Nom { get; set; } = string.Empty;
        public decimal Solde { get; set; }
        public decimal Taux { get; set; }
        public string Terme { get; set; } = string.Empty;
        public decimal InteretsCumules { get; set; }
    }
}