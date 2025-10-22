namespace CompteDepot.Models
{
    public class InsertOperationRequest
    {
        public int CompteDepotId { get; set; }
        public decimal Montant { get; set; }
        public string TypeOperation { get; set; } = "DEPOT"; // DEPOT ou RETRAIT
        public string? Description { get; set; }
    }
}