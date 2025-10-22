namespace PretService.Models
{
    public class ApiResponse<T>
    {
        public bool Success { get; set; }
        public string Message { get; set; } = string.Empty;
        public T? Data { get; set; }
    }

    public class EcheanceDto
    {
        public int IdEcheance { get; set; }
        public int? NumeroEcheance { get; set; }
        public DateTime? DateEcheance { get; set; }
        public decimal? MontantTotal { get; set; }
        public decimal? Capital { get; set; }
        public decimal? Interet { get; set; }
        public int? IdStatus { get; set; }
        public int IdCredit { get; set; }
    }

    public class InsertEcheanceRequest
    {
        public int IdCredit { get; set; }
        public int? NumeroEcheance { get; set; }
        public DateTime? DateEcheance { get; set; }
        public decimal? MontantTotal { get; set; }
        public decimal? Capital { get; set; }
        public decimal? Interet { get; set; }
        public int? IdStatus { get; set; }
    }

    public class UpdateEcheanceStatusRequest
    {
        public int IdStatus { get; set; }
    }

    public class CreditSimpleDto
    {
        public int IdCredit { get; set; }
        public decimal? MontantInitial { get; set; }
        public decimal? Taux { get; set; }
        public int? DureeMois { get; set; }
    }
}
