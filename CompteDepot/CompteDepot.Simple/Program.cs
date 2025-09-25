using System.ServiceModel;
using CompteDepot.Simple.Services;
using CompteDepot.Simple.Data;
using Microsoft.EntityFrameworkCore;

namespace CompteDepot.Simple
{
    class Program
    {
        static async Task Main(string[] args)
        {
            Console.WriteLine("🏦 === Service CompteDepot Simple ===");
            
            // Initialiser la base de données
            using (var context = new BanqueContext())
            {
                await context.Database.EnsureCreatedAsync();
                Console.WriteLine("✅ Base de données initialisée");
                
                // Afficher les comptes de test
                var comptes = context.ComptesDepot.ToList();
                Console.WriteLine($"📊 {comptes.Count} comptes de test chargés:");
                foreach (var compte in comptes)
                {
                    Console.WriteLine($"   - {compte}");
                }
            }
            
            // Démarrer le service WCF
            Console.WriteLine("\n🚀 Démarrage du service WCF...");
            
            var serviceHost = new ServiceHost(typeof(CompteDepotService));
            
            // Configuration simple HTTP
            var binding = new BasicHttpBinding();
            var address = "http://localhost:8081/CompteDepot";
            
            serviceHost.AddServiceEndpoint(typeof(ICompteDepotService), binding, address);
            
            // Métadonnées (pour voir le WSDL)
            var behavior = new System.ServiceModel.Description.ServiceMetadataBehavior
            {
                HttpGetEnabled = true,
                HttpGetUrl = new Uri(address + "/wsdl")
            };
            serviceHost.Description.Behaviors.Add(behavior);
            
            try
            {
                serviceHost.Open();
                
                Console.WriteLine("✅ Service WCF démarré !");
                Console.WriteLine($"📍 Adresse: {address}");
                Console.WriteLine($"📄 WSDL: {address}/wsdl");
                Console.WriteLine("\n🧪 === TEST RAPIDE ===");
                await TestService();
                
                Console.WriteLine("\n⌨️  Appuyez sur [ENTRÉE] pour arrêter le service...");
                Console.ReadLine();
                
                serviceHost.Close();
                Console.WriteLine("🛑 Service arrêté");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"❌ Erreur : {ex.Message}");
            }
        }
        
        // Test rapide du service
        static async Task TestService()
        {
            var service = new CompteDepotService();
            
            Console.WriteLine("\n1. Test consultation solde DEP001:");
            var solde = service.ConsulterSolde("DEP001");
            Console.WriteLine($"   Résultat: {solde:C}");
            
            Console.WriteLine("\n2. Test dépôt 1000€ sur DEP001:");
            var depotOk = service.Deposer("DEP001", 1000m);
            Console.WriteLine($"   Résultat: {(depotOk ? "✅ Réussi" : "❌ Échec")}");
            
            Console.WriteLine("\n3. Test nouveau solde:");
            var nouveauSolde = service.ConsulterSolde("DEP001");
            Console.WriteLine($"   Résultat: {nouveauSolde:C}");
            
            Console.WriteLine("\n4. Test calcul intérêts:");
            var interets = service.CalculerInterets("DEP001");
            Console.WriteLine($"   Résultat: {interets:C}");
            
            Console.WriteLine("\n5. Test retrait 500€:");
            var retraitOk = service.Retirer("DEP001", 500m);
            Console.WriteLine($"   Résultat: {(retraitOk ? "✅ Réussi" : "❌ Échec")}");
        }
    }
}