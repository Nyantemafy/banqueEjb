using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Configuration;
using Microsoft.EntityFrameworkCore;
using CompteDepot.Service;
using CompteDepot.Data;
using System.ServiceModel;

namespace CompteDepot.Host
{
    class Program
    {
        static async Task Main(string[] args)
        {
            Console.WriteLine("=== Démarrage du service CompteDepot ===");

            try
            {
                // Configuration
                var configuration = new ConfigurationBuilder()
                    .SetBasePath(Directory.GetCurrentDirectory())
                    .AddJsonFile("appsettings.json", optional: true)
                    .AddEnvironmentVariables()
                    .AddCommandLine(args)
                    .Build();

                // Création du host
                var host = CreateHostBuilder(args, configuration).Build();

                // Initialisation de la base de données
                await InitializeDatabaseAsync(host.Services);

                // Démarrage du service WCF
                await StartWcfServiceAsync(host.Services);

                // Démarrage du host
                Console.WriteLine("Service démarré. Appuyez sur Ctrl+C pour arrêter.");
                await host.RunAsync();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Erreur fatale: {ex.Message}");
                Console.WriteLine($"StackTrace: {ex.StackTrace}");
                Environment.Exit(1);
            }
        }

        static IHostBuilder CreateHostBuilder(string[] args, IConfiguration configuration)
        {
            return Microsoft.Extensions.Hosting.Host.CreateDefaultBuilder(args)
                .ConfigureServices((context, services) =>
                {
                    // Configuration Entity Framework
                    services.AddDbContext<CompteDepotContext>(options =>
                    {
                        var connectionString = configuration.GetConnectionString("DefaultConnection")
                            ?? "Server=(localdb)\\MSSQLLocalDB;Database=BanqueCompteDepot;Trusted_Connection=True;MultipleActiveResultSets=true";
                        
                        options.UseSqlServer(connectionString);
                        options.EnableSensitiveDataLogging();
                        options.LogTo(Console.WriteLine, LogLevel.Information);
                    });

                    // Services métier
                    services.AddScoped<ICompteDepotService, CompteDepotService>();
                    
                    // Logging
                    services.AddLogging(builder =>
                    {
                        builder.AddConsole();
                        builder.AddDebug();
                        builder.SetMinimumLevel(LogLevel.Information);
                    });

                    // Configuration
                    services.AddSingleton(configuration);
                });
        }

        static async Task InitializeDatabaseAsync(IServiceProvider services)
        {
            Console.WriteLine("Initialisation de la base de données...");
            
            using var scope = services.CreateScope();
            var context = scope.ServiceProvider.GetRequiredService<CompteDepotContext>();
            var logger = scope.ServiceProvider.GetRequiredService<ILogger<Program>>();

            try
            {
                // Création/migration de la base de données
                await context.Database.EnsureCreatedAsync();
                logger.LogInformation("Base de données initialisée avec succès");
                
                // Vérification des données de test
                var comptesCount = await context.ComptesDepot.CountAsync();
                logger.LogInformation($"Nombre de comptes en base: {comptesCount}");
            }
            catch (Exception ex)
            {
                logger.LogError(ex, "Erreur lors de l'initialisation de la base de données");
                throw;
            }
        }

        static async Task StartWcfServiceAsync(IServiceProvider services)
        {
            Console.WriteLine("Démarrage du service WCF...");
            
            try
            {
                // Configuration du service WCF
                var serviceHost = new ServiceHost(typeof(CompteDepotService));
                
                // Endpoint HTTP
                var httpBinding = new BasicHttpBinding();
                httpBinding.Security.Mode = BasicHttpSecurityMode.None;
                httpBinding.MaxReceivedMessageSize = 1024 * 1024; // 1MB
                
                serviceHost.AddServiceEndpoint(
                    typeof(ICompteDepotService),
                    httpBinding,
                    "http://localhost:8081/CompteDepot");

                // Endpoint TCP (pour de meilleures performances)
                var tcpBinding = new NetTcpBinding();
                tcpBinding.Security.Mode = SecurityMode.None;
                
                serviceHost.AddServiceEndpoint(
                    typeof(ICompteDepotService),
                    tcpBinding,
                    "net.tcp://localhost:8082/CompteDepot");

                // Métadonnées
                var behavior = new System.ServiceModel.Description.ServiceMetadataBehavior();
                behavior.HttpGetEnabled = true;
                behavior.HttpGetUrl = new Uri("http://localhost:8081/CompteDepot/mex");
                serviceHost.Description.Behaviors.Add(behavior);

                // Démarrage
                serviceHost.Open();
                
                Console.WriteLine("Service WCF démarré:");
                Console.WriteLine("  - HTTP: http://localhost:8081/CompteDepot");
                Console.WriteLine("  - TCP:  net.tcp://localhost:8082/CompteDepot");
                Console.WriteLine("  - WSDL: http://localhost:8081/CompteDepot/mex");
                
                // Attendre l'arrêt du programme
                Console.CancelKeyPress += (sender, e) =>
                {
                    e.Cancel = true;
                    Console.WriteLine("Arrêt du service WCF...");
                    serviceHost.Close();
                };
                
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Erreur lors du démarrage WCF: {ex.Message}");
                throw;
            }
        }
    }
}