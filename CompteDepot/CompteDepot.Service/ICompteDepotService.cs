using System;
using System.Collections.Generic;
using System.ServiceModel;

namespace CompteDepot.Service
{
    [ServiceContract]
    public interface ICompteDepotService
    {
        [OperationContract]
        decimal ConsulterSolde(string numeroCompte);
        
        [OperationContract]
        bool CreerCompteDepot(string numeroCompte, string proprietaire, 
                            decimal tauxInteret, int dureeEnMois);
        
        [OperationContract]
        bool EffectuerDepot(string numeroCompte, decimal montant);
        
        [OperationContract]
        bool EffectuerRetrait(string numeroCompte, decimal montant);
        
        [OperationContract]
        decimal CalculerInterets(string numeroCompte);
        
        [OperationContract]
        bool PeutRetirer(string numeroCompte, decimal montant);
        
        [OperationContract]
        List<OperationDepot> GetHistorique(string numeroCompte);
    }
}