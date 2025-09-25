using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Microsoft.EntityFrameworkCore;
using CompteDepot.Service;
using CompteDepot.Data;
using System.ServiceModel;

namespace CompteDepot.Host
{
    public class Startup
    {
        public IConfiguration Configuration { get; }

        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public void ConfigureServices(IServiceCollection services)
        {
            // Configuration de la base de données
            ConfigureDatabase(services);

            // Configuration des services métier
            ConfigureBusinessServices(services);

            // Configuration du logging
            ConfigureLogging(services);

            // Configuration WCF
            ConfigureWcfServices(services);
        }

        private void ConfigureDatabase(IServiceCollection services)
        {
            var connectionString = Configuration.GetConnectionString("DefaultConnection") 
                ?? Configuration.GetConnectionString("BanqueConnection")
                ?? "Server=(localdb)\\MSSQLLocalDB;Database=BanqueCompteDepot;Trusted_Connection=True;MultipleActiveResultSets=true";

            services.AddDbContext<CompteDepotContext>(options =>
            {
                options.UseSqlServer(connectionString, sqlOptions =>
                {
                    sqlOptions.EnableRetryOnFailure(
                        maxRetryCount: 3,
                        maxRetryDelay: TimeSpan.FromSeconds(5),
                        errorNumbersToAdd: null);
                    
                    sqlOptions.CommandTimeout(30);
                });

                // Configuration pour l'environnement
                var environment = Configuration["ASPNETCORE_ENVIRONMENT"] ?? "Production";
                if (environment == "Development")
                {
                    options.EnableSensitiveDataLogging();
                    options.LogTo(Console.WriteLine, LogLevel.Information);
                }
            });

            // Pool de connexions pour les performances
            services.AddDbContextPool<CompteDepotContext>(options =>
            {
                options.UseSqlServer(connectionString);
            }, poolSize: 32);
        }

        private void ConfigureBusinessServices(IServiceCollection services)
        {
            // Services métier
            services.AddScoped<ICompteDepotService, CompteDepotService>();
            
            // Services utilitaires
            services.AddSingleton<IDateTimeProvider, DateTimeProvider>();
            services.AddSingleton<ICalculateurInterets, CalculateurInterets>();
            
            // Configuration des options
            services.Configure<CompteDepotOptions>(Configuration.GetSection("CompteDepot"));
            services.Configure<LimitesRetraitOptions>(Configuration.GetSection("LimitesRetrait"));
        }

        private void ConfigureLogging(IServiceCollection services)
        {
            services.AddLogging(builder =>
            {
                builder.AddConfiguration(Configuration.GetSection("Logging"));
                builder.AddConsole(options =>
                {
                    options.TimestampFormat = "[yyyy-MM-dd HH:mm:ss] ";
                });
                builder.AddDebug();
                
                // Ajout d'un logger pour fichier si configuré
                var logFilePath = Configuration["Logging:FilePath"];
                if (!string.IsNullOrEmpty(logFilePath))
                {
                    // services.AddFile(logFilePath); // Si vous utilisez Serilog ou similar
                }
            });
        }

        private void ConfigureWcfServices(IServiceCollection services)
        {
            // Configuration WCF spécifique
            services.Configure<WcfServiceOptions>(Configuration.GetSection("WcfService"));
            
            // Intercepteurs WCF
            services.AddSingleton<IWcfLoggingInterceptor, WcfLoggingInterceptor>();
            services.AddSingleton<IWcfErrorHandler, WcfErrorHandler>();
        }
    }

    // Classes de configuration
    public class CompteDepotOptions
    {
        public decimal TauxInteretDefaut { get; set; } = 2.5m;
        public int DureeDefautEnMois { get; set; } = 12;
        public decimal FraisOperationDefaut { get; set; } = 2.00m;
        public bool AutoCalculerInterets { get; set; } = true;
        public int PeriodeCalculInteretsEnJours { get; set; } = 30;
    }

    public class LimitesRetraitOptions
    {
        public decimal PourcentageLimiteMensuelle { get; set; } = 0.10m; // 10%
        public decimal PourcentageLimiteAnnuelle { get; set; } = 0.50m;  // 50%
        public bool ActiverLimites { get; set; } = true;
        public decimal MontantMaximumRetrait { get; set; } = 50000.00m;
    }

    public class WcfServiceOptions
    {
        public string HttpEndpointUrl { get; set; } = "http://localhost:8081/CompteDepot";
        public string TcpEndpointUrl { get; set; } = "net.tcp://localhost:8082/CompteDepot";
        public string MetadataUrl { get; set; } = "http://localhost:8081/CompteDepot/mex";
        public int MaxReceivedMessageSize { get; set; } = 1024 * 1024; // 1MB
        public bool EnableHttps { get; set; } = false;
    }

    // Services utilitaires
    public interface IDateTimeProvider
    {
        DateTime Now { get; }
        DateTime Today { get; }
    }

    public class DateTimeProvider : IDateTimeProvider
    {
        public DateTime Now => DateTime.Now;
        public DateTime Today => DateTime.Today;
    }

    public interface ICalculateurInterets
    {
        decimal CalculerInterets(decimal capital, decimal tauxAnnuel, int nombreJours);
        decimal CalculerInteretsComposes(decimal capital, decimal tauxAnnuel, int nombreMois);
    }

    public class CalculateurInterets : ICalculateurInterets
    {
        public decimal CalculerInterets(decimal capital, decimal tauxAnnuel, int nombreJours)
        {
            if (capital <= 0 || tauxAnnuel <= 0 || nombreJours <= 0)
                return 0;

            var tauxJournalier = tauxAnnuel / 100 / 365;
            return Math.Round(capital * (decimal)tauxJournalier * nombreJours, 2);
        }

        public decimal CalculerInteretsComposes(decimal capital, decimal tauxAnnuel, int nombreMois)
        {
            if (capital <= 0 || tauxAnnuel <= 0 || nombreMois <= 0)
                return 0;

            var tauxMensuel = (double)(tauxAnnuel / 100 / 12);
            var facteur = Math.Pow(1 + tauxMensuel, nombreMois);
            var montantFinal = (decimal)((double)capital * facteur);
            
            return Math.Round(montantFinal - capital, 2);
        }
    }

    // Intercepteurs WCF
    public interface IWcfLoggingInterceptor
    {
        void LogRequest(string operation, object[] parameters);
        void LogResponse(string operation, object result);
    }

    public class WcfLoggingInterceptor : IWcfLoggingInterceptor
    {
        private readonly ILogger<WcfLoggingInterceptor> _logger;

        public WcfLoggingInterceptor(ILogger<WcfLoggingInterceptor> logger)
        {
            _logger = logger;
        }

        public void LogRequest(string operation, object[] parameters)
        {
            _logger.LogInformation("WCF Request - Operation: {Operation}, Parameters: {Parameters}", 
                operation, string.Join(", ", parameters ?? new object[0]));
        }

        public void LogResponse(string operation, object result)
        {
            _logger.LogInformation("WCF Response - Operation: {Operation}, Result: {Result}", 
                operation, result?.ToString() ?? "null");
        }
    }

    public interface IWcfErrorHandler
    {
        void HandleError(Exception error, string operation);
    }

    public class WcfErrorHandler : IWcfErrorHandler
    {
        private readonly ILogger<WcfErrorHandler> _logger;

        public WcfErrorHandler(ILogger<WcfErrorHandler> logger)
        {
            _logger = logger;
        }

        public void HandleError(Exception error, string operation)
        {
            _logger.LogError(error, "Erreur WCF dans l'opération {Operation}: {Message}", 
                operation, error.Message);
        }
    }
}