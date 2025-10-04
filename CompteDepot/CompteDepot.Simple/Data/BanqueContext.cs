using System;
using Microsoft.EntityFrameworkCore;
using CompteDepot.Simple.Models;

namespace CompteDepot.Simple.Data
{
    public class BanqueContext : DbContext
    {
        public BanqueContext(DbContextOptions<BanqueContext> options) : base(options)
        {
        }

        // Tables
        public DbSet<CompteDepot.Simple.Models.CompteDepot> ComptesDepot { get; set; }
        public DbSet<OperationDepot> Operations { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<CompteDepot.Simple.Models.CompteDepot>()
                .Property(c => c.DateCreation)
                .HasColumnType("timestamp without time zone");

            modelBuilder.Entity<CompteDepot.Simple.Models.CompteDepot>()
                .Property(c => c.DateEcheance)
                .HasColumnType("timestamp without time zone");

            modelBuilder.Entity<CompteDepot.Simple.Models.CompteDepot>().HasData(
                new CompteDepot.Simple.Models.CompteDepot 
                { 
                    NumeroCompte = "DEP001", 
                    Proprietaire = "Jean Dupont", 
                    Solde = 5000m,
                    TauxInteret = 2.5m,
                    DateCreation = new DateTime(2024, 01, 01, 0, 0, 0, DateTimeKind.Unspecified),
                    DateEcheance = new DateTime(2024, 07, 01, 0, 0, 0, DateTimeKind.Unspecified)
                },
                new CompteDepot.Simple.Models.CompteDepot 
                { 
                    NumeroCompte = "DEP002", 
                    Proprietaire = "Marie Martin", 
                    Solde = 10000m,
                    TauxInteret = 3.0m,
                    DateCreation = new DateTime(2024, 04, 01, 0, 0, 0, DateTimeKind.Unspecified),
                    DateEcheance = new DateTime(2025, 01, 01, 0, 0, 0, DateTimeKind.Unspecified)
                }
            );
        }

    }
}
