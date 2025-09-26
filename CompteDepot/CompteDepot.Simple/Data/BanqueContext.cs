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
            // Données de test au démarrage
            modelBuilder.Entity<CompteDepot.Simple.Models.CompteDepot>().HasData(
                new CompteDepot.Simple.Models.CompteDepot 
                { 
                    NumeroCompte = "DEP001", 
                    Proprietaire = "Jean Dupont", 
                    Solde = 5000m,
                    TauxInteret = 2.5m,
                    DateCreation = DateTime.Now.AddMonths(-6),
                    DateEcheance = DateTime.Now.AddMonths(6)
                },
                new CompteDepot.Simple.Models.CompteDepot 
                { 
                    NumeroCompte = "DEP002", 
                    Proprietaire = "Marie Martin", 
                    Solde = 10000m,
                    TauxInteret = 3.0m,
                    DateCreation = DateTime.Now.AddMonths(-3),
                    DateEcheance = DateTime.Now.AddMonths(9)
                }
            );
        }
    }
}
