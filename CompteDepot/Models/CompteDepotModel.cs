namespace CompteDepot.Models
{
    public class CompteDepotModel
    {
        public int IdCompteDepot { get; set; }
        public decimal MontantInitial { get; set; }
        public decimal TauxInteret { get; set; }
        public DateTime DateDebut { get; set; }
        public DateTime? DateFin { get; set; }
        public int IdUser { get; set; }
        public string? Status { get; set; }
    }
}