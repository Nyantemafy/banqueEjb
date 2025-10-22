using Microsoft.EntityFrameworkCore;

namespace PretService.Data
{
    public class BanqueContext : DbContext
    {
        public BanqueContext(DbContextOptions<BanqueContext> options) : base(options) { }

        public DbSet<CreditEntity> Credits => Set<CreditEntity>();
        public DbSet<CreditEcheanceEntity> CreditEcheances => Set<CreditEcheanceEntity>();

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<CreditEntity>(e =>
            {
                e.ToTable("credit");
                e.HasKey(x => x.IdCredit);
                e.Property(x => x.IdCredit).HasColumnName("id_credit");
                e.Property(x => x.MontantInitial).HasColumnName("montant_initial");
                e.Property(x => x.Taux).HasColumnName("taux");
                e.Property(x => x.DureeMois).HasColumnName("duree_mois");
                e.Property(x => x.Mensualite).HasColumnName("mensualite");
                e.Property(x => x.SoldeRest).HasColumnName("solde_rest");
                e.Property(x => x.DateDebut).HasColumnName("date_debut");
                e.Property(x => x.IdStatus).HasColumnName("id_status");
                e.Property(x => x.IdUser).HasColumnName("id_user");
            });

            modelBuilder.Entity<CreditEcheanceEntity>(e =>
            {
                e.ToTable("credit_echeance");
                e.HasKey(x => x.IdEcheance);
                e.Property(x => x.IdEcheance).HasColumnName("id_echeance");
                e.Property(x => x.NumeroEcheance).HasColumnName("numero_echeance");
                e.Property(x => x.DateEcheance).HasColumnName("date_echeance");
                e.Property(x => x.MontantTotal).HasColumnName("montant_total");
                e.Property(x => x.Capital).HasColumnName("capital");
                e.Property(x => x.Interet).HasColumnName("interet");
                e.Property(x => x.SoldeRest).HasColumnName("solde_rest");
                e.Property(x => x.IdStatus).HasColumnName("id_status");
                e.Property(x => x.IdCredit).HasColumnName("id_credit");
            });
        }
    }

    public class CreditEntity
    {
        public int IdCredit { get; set; }
        public decimal? MontantInitial { get; set; }
        public decimal? Taux { get; set; }
        public int? DureeMois { get; set; }
        public decimal? Mensualite { get; set; }
        public decimal? SoldeRest { get; set; }
        public DateTime? DateDebut { get; set; }
        public int? IdStatus { get; set; }
        public int IdUser { get; set; }
    }

    public class CreditEcheanceEntity
    {
        public int IdEcheance { get; set; }
        public int? NumeroEcheance { get; set; }
        public DateTime? DateEcheance { get; set; }
        public decimal? MontantTotal { get; set; }
        public decimal? Capital { get; set; }
        public decimal? Interet { get; set; }
        public string? SoldeRest { get; set; }
        public int? IdStatus { get; set; }
        public int IdCredit { get; set; }
    }
}
