using System;
using System.Collections.Generic;
using CompteDepot.Simple.Models;

namespace CompteDepot.Simple.Services
{
    public interface ICompteDepotService
    {
        decimal ConsulterSolde(string numeroCompte);
        bool CreerCompte(string numeroCompte, string proprietaire, decimal tauxInteret);
        bool Deposer(string numeroCompte, decimal montant);
        bool Retirer(string numeroCompte, decimal montant);
        decimal CalculerInterets(string numeroCompte);
        List<OperationDepot> GetHistorique(string numeroCompte);
    }

}