using Microsoft.EntityFrameworkCore;

namespace CompteDepot.Data
{
    public class BanqueContext : DbContext
    {
        public BanqueContext(DbContextOptions<BanqueContext> options) : base(options) { }

        public DbSet<CompteDepotEntity> CompteDepots => Set<CompteDepotEntity>();
        public DbSet<DepotOperationEntity> DepotOperations => Set<DepotOperationEntity>();
        public DbSet<TypeEntity> Types => Set<TypeEntity>();

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // Table: comptedepot
            modelBuilder.Entity<CompteDepotEntity>(e =>
            {
                e.ToTable("comptedepot");
                e.HasKey(x => x.IdCompteDepot);
                e.Property(x => x.IdCompteDepot).HasColumnName("id_comptedepot");
                e.Property(x => x.MontantInitial).HasColumnName("montant_initial");
                e.Property(x => x.TauxInteret).HasColumnName("taux_interet");
                e.Property(x => x.DateDebut).HasColumnName("date_debut");
                e.Property(x => x.DateFin).HasColumnName("date_fin");
                e.Property(x => x.IdUser).HasColumnName("id_user");
                e.Property(x => x.IdStatus).HasColumnName("id_status");
            });

            // Table: depot_operation
            modelBuilder.Entity<DepotOperationEntity>(e =>
            {
                e.ToTable("depot_operation");
                e.HasKey(x => x.IdOp);
                e.Property(x => x.IdOp).HasColumnName("id_op");
                e.Property(x => x.Montant).HasColumnName("montant");
                e.Property(x => x.DateOp).HasColumnName("date_op");
                e.Property(x => x.IdCompteDepot).HasColumnName("id_comptedepot");
                e.Property(x => x.IdType).HasColumnName("id_type");
            });

            // Table: type
            modelBuilder.Entity<TypeEntity>(e =>
            {
                e.ToTable("type");
                e.HasKey(x => x.IdType);
                e.Property(x => x.IdType).HasColumnName("id_type");
                e.Property(x => x.Libelle).HasColumnName("libelle");
            });
        }
    }

    public class CompteDepotEntity
    {
        public int IdCompteDepot { get; set; }
        public decimal? MontantInitial { get; set; }
        public decimal? TauxInteret { get; set; }
        public DateTime? DateDebut { get; set; }
        public DateTime? DateFin { get; set; }
        public int IdUser { get; set; }
        public int? IdStatus { get; set; }
    }

    public class DepotOperationEntity
    {
        public int IdOp { get; set; }
        public decimal? Montant { get; set; }
        public DateTime? DateOp { get; set; }
        public int IdCompteDepot { get; set; }
        public int IdType { get; set; }
    }

    public class TypeEntity
    {
        public int IdType { get; set; }
        public string Libelle { get; set; } = string.Empty;
    }
}
