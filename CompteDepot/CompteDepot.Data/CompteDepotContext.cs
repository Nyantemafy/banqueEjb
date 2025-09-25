using Microsoft.EntityFrameworkCore;
using CompteDepot.Service.Models;
using Microsoft.Extensions.Configuration;

namespace CompteDepot.Data
{
    public class CompteDepotContext : DbContext
    {
        public DbSet<Service.Models.CompteDepot> ComptesDepot { get; set; } = null!;
        public DbSet<OperationDepot> Operations { get; set; } = null!;

        // Constructeur par défaut pour les migrations
        public CompteDepotContext() { }

        // Constructeur avec options
        public CompteDepotContext(DbContextOptions<CompteDepotContext> options) : base(options) { }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (!optionsBuilder.IsConfigured)
            {
                // Configuration par défaut si pas déjà configuré
                var configuration = new ConfigurationBuilder()
                    .SetBasePath(Directory.GetCurrentDirectory())
                    .AddJsonFile("appsettings.json", optional: true)
                    .Build();

                var connectionString = configuration.GetConnectionString("DefaultConnection") 
                    ?? "Server=(localdb)\\MSSQLLocalDB;Database=BanqueCompteDepot;Trusted_Connection=True;MultipleActiveResultSets=true";

                optionsBuilder.UseSqlServer(connectionString);
                
                // Logging pour le développement
                optionsBuilder.EnableSensitiveDataLogging();
                optionsBuilder.LogTo(Console.WriteLine);
            }
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Configuration de l'entité CompteDepot
            modelBuilder.Entity<Service.Models.CompteDepot>(entity =>
            {
                entity.HasKey(e => e.NumeroCompte);
                
                entity.Property(e => e.NumeroCompte)
                    .HasMaxLength(20)
                    .IsRequired();

                entity.Property(e => e.Proprietaire)
                    .HasMaxLength(100)
                    .IsRequired();

                entity.Property(e => e.Solde)
                    .HasColumnType("decimal(15,2)")
                    .HasDefaultValue(0);

                entity.Property(e => e.TauxInteret)
                    .HasColumnType("decimal(5,2)")
                    .HasDefaultValue(0);

                entity.Property(e => e.MontantRetireAnnee)
                    .HasColumnType("decimal(15,2)")
                    .HasDefaultValue(0);

                entity.Property(e => e.MontantRetireMois)
                    .HasColumnType("decimal(15,2)")
                    .HasDefaultValue(0);

                entity.Property(e => e.Actif)
                    .HasDefaultValue(true);

                entity.Property(e => e.DateCreation)
                    .HasDefaultValueSql("GETDATE()");

                entity.HasIndex(e => e.Proprietaire)
                    .HasDatabaseName("IX_ComptesDepot_Proprietaire");

                entity.HasIndex(e => e.DateEcheance)
                    .HasDatabaseName("IX_ComptesDepot_DateEcheance");
            });

            // Configuration de l'entité OperationDepot
            modelBuilder.Entity<OperationDepot>(entity =>
            {
                entity.HasKey(e => e.IdOperation);

                entity.Property(e => e.IdOperation)
                    .ValueGeneratedOnAdd();

                entity.Property(e => e.NumeroCompte)
                    .HasMaxLength(20)
                    .IsRequired();

                entity.Property(e => e.Montant)
                    .HasColumnType("decimal(15,2)")
                    .IsRequired();

                entity.Property(e => e.TypeOperation)
                    .HasMaxLength(20)
                    .IsRequired();

                entity.Property(e => e.SoldeApresOperation)
                    .HasColumnType("decimal(15,2)");

                entity.Property(e => e.Frais)
                    .HasColumnType("decimal(10,2)")
                    .HasDefaultValue(0);

                entity.Property(e => e.DateOperation)
                    .HasDefaultValueSql("GETDATE()");

                entity.Property(e => e.Statut)
                    .HasMaxLength(20)
                    .HasDefaultValue("VALIDEE");

                entity.HasIndex(e => e.NumeroCompte)
                    .HasDatabaseName("IX_OperationsDepot_NumeroCompte");

                entity.HasIndex(e => e.DateOperation)
                    .HasDatabaseName("IX_OperationsDepot_DateOperation");

                entity.HasIndex(e => new { e.NumeroCompte, e.DateOperation })
                    .HasDatabaseName("IX_OperationsDepot_Compte_Date");
            });

            // Données de test (seed)
            SeedData(modelBuilder);
        }

        private static void SeedData(ModelBuilder modelBuilder)
        {
            // Comptes de test
            var comptesTest = new[]
            {
                new Service.Models.CompteDepot
                {
                    NumeroCompte = "DEP001",
                    Proprietaire = "Jean Dupont",
                    Solde = 10000.00m,
                    TauxInteret = 3.5m,
                    DureeEnMois = 12,
                    DateCreation = DateTime.Now.AddMonths(-3),
                    DateEcheance = DateTime.Now.AddMonths(9),
                    MontantRetireAnnee = 0,
                    MontantRetireMois = 0,
                    Actif = true,
                    Notes = "Compte de test principal"
                },
                new Service.Models.CompteDepot
                {
                    NumeroCompte = "DEP002",
                    Proprietaire = "Marie Martin",
                    Solde = 25000.00m,
                    TauxInteret = 4.0m,
                    DureeEnMois = 24,
                    DateCreation = DateTime.Now.AddMonths(-6),
                    DateEcheance = DateTime.Now.AddMonths(18),
                    MontantRetireAnnee = 1000.00m,
                    MontantRetireMois = 500.00m,
                    Actif = true,
                    Notes = "Compte de test secondaire"
                }
            };

            modelBuilder.Entity<Service.Models.CompteDepot>().HasData(comptesTest);
        }

        // Méthodes utilitaires
        public async Task<bool> CompteExisteAsync(string numeroCompte)
        {
            return await ComptesDepot.AnyAsync(c => c.NumeroCompte == numeroCompte);
        }

        public async Task<Service.Models.CompteDepot?> GetCompteAsync(string numeroCompte)
        {
            return await ComptesDepot.FirstOrDefaultAsync(c => c.NumeroCompte == numeroCompte);
        }

        public async Task<List<OperationDepot>> GetHistoriqueAsync(string numeroCompte, int? limit = null)
        {
            var query = Operations
                .Where(o => o.NumeroCompte == numeroCompte)
                .OrderByDescending(o => o.DateOperation);

            if (limit.HasValue)
            {
                query = (IOrderedQueryable<OperationDepot>)query.Take(limit.Value);
            }

            return await query.ToListAsync();
        }

        public async Task<List<Service.Models.CompteDepot>> GetComptesEchusAsync()
        {
            var aujourd_hui = DateTime.Now.Date;
            return await ComptesDepot
                .Where(c => c.Actif && c.DateEcheance <= aujourd_hui)
                .OrderBy(c => c.DateEcheance)
                .ToListAsync();
        }

        public override async Task<int> SaveChangesAsync(CancellationToken cancellationToken = default)
        {
            try
            {
                return await base.SaveChangesAsync(cancellationToken);
            }
            catch (DbUpdateException ex)
            {
                // Log l'erreur et la transformer en exception plus claire
                Console.WriteLine($"Erreur lors de la sauvegarde: {ex.Message}");
                throw new InvalidOperationException("Erreur lors de la sauvegarde des données", ex);
            }
        }
    }
}