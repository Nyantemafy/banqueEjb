namespace CompteDepot.Models
{
    public class OperationModel
    {
        public int? IdOp { get; set; }
        public decimal Montant { get; set; }
        public DateTime DateOp { get; set; }
        public int IdCompteDepot { get; set; }
        public int IdType { get; set; }
        public string? TypeLibelle { get; set; }
    }
}
