using System.ServiceModel;
using CompteDepot.Simple.Models;

namespace CompteDepot.Simple.Services
{
    // [ServiceContract] = Interface accessible à distance (comme @Remote en EJB)
    [ServiceContract]
    public interface ICompteDepotService
    {
        // [OperationContract] = Méthode qu'on peut appeler à distance
        [OperationContract]
        decimal ConsulterSolde(string numeroCompte);
        
        [OperationContract]
        bool CreerCompte(string numeroCompte, string proprietaire, decimal tauxInteret);
        
        [OperationContract]
        bool Deposer(string numeroCompte, decimal montant);
        
        [OperationContract]
        bool Retirer(string numeroCompte, decimal montant);
        
        [OperationContract]
        decimal CalculerInterets(string numeroCompte);
        
        [OperationContract]
        List<OperationDepot> GetHistorique(string numeroCompte);
    }
}