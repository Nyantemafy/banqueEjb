using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.EntityFrameworkCore;
using CompteDepot.Simple.Services;
using CompteDepot.Simple.Data;

var builder = WebApplication.CreateBuilder(args);

// Ajouter le DbContext
builder.Services.AddDbContext<BanqueContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

// Injection du service
builder.Services.AddScoped<ICompteDepotService, CompteDepotService>();

// Ajouter support JSON et controllers si besoin
builder.Services.AddControllers();

var app = builder.Build();

// Endpoints REST minimal
app.MapGet("/solde/{numero}", (string numero, ICompteDepotService service) =>
{
    return service.ConsulterSolde(numero);
});

app.MapPost("/depot", (string numero, decimal montant, ICompteDepotService service) =>
{
    return service.Deposer(numero, montant);
});

app.MapPost("/retrait", (string numero, decimal montant, ICompteDepotService service) =>
{
    return service.Retirer(numero, montant);
});

app.MapGet("/interets/{numero}", (string numero, ICompteDepotService service) =>
{
    return service.CalculerInterets(numero);
});

app.MapGet("/historique/{numero}", (string numero, ICompteDepotService service) =>
{
    return service.GetHistorique(numero);
});

app.Run();
