using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace CompteDepot.Simple.Migrations
{
    /// <inheritdoc />
    public partial class InitDepot : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "ComptesDepot",
                columns: table => new
                {
                    NumeroCompte = table.Column<string>(type: "text", nullable: false),
                    Proprietaire = table.Column<string>(type: "text", nullable: false),
                    Solde = table.Column<decimal>(type: "numeric", nullable: false),
                    TauxInteret = table.Column<decimal>(type: "numeric", nullable: false),
                    DateCreation = table.Column<DateTime>(type: "timestamp without time zone", nullable: false),
                    DateEcheance = table.Column<DateTime>(type: "timestamp without time zone", nullable: false),
                    MontantRetireMois = table.Column<decimal>(type: "numeric", nullable: false),
                    MontantRetireAnnee = table.Column<decimal>(type: "numeric", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ComptesDepot", x => x.NumeroCompte);
                });

            migrationBuilder.CreateTable(
                name: "Operations",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    NumeroCompte = table.Column<string>(type: "text", nullable: false),
                    Montant = table.Column<decimal>(type: "numeric", nullable: false),
                    Type = table.Column<string>(type: "text", nullable: false),
                    DateOperation = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    Description = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Operations", x => x.Id);
                });

            migrationBuilder.InsertData(
                table: "ComptesDepot",
                columns: new[] { "NumeroCompte", "DateCreation", "DateEcheance", "MontantRetireAnnee", "MontantRetireMois", "Proprietaire", "Solde", "TauxInteret" },
                values: new object[,]
                {
                    { "DEP001", new DateTime(2024, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified), new DateTime(2024, 7, 1, 0, 0, 0, 0, DateTimeKind.Unspecified), 0m, 0m, "Jean Dupont", 5000m, 2.5m },
                    { "DEP002", new DateTime(2024, 4, 1, 0, 0, 0, 0, DateTimeKind.Unspecified), new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified), 0m, 0m, "Marie Martin", 10000m, 3.0m }
                });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "ComptesDepot");

            migrationBuilder.DropTable(
                name: "Operations");
        }
    }
}
