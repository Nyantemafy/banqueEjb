using System;
using System.Runtime.Serialization;

namespace CompteDepot.Service.Exceptions
{
    /// <summary>
    /// Exception spécifique aux opérations sur les comptes de dépôt
    /// </summary>
    [Serializable]
    public class CompteDepotException : Exception
    {
        public string? NumeroCompte { get; }
        public string? CodeErreur { get; }
        public DateTime DateErreur { get; }

        public CompteDepotException() : base()
        {
            DateErreur = DateTime.Now;
        }

        public CompteDepotException(string message) : base(message)
        {
            DateErreur = DateTime.Now;
        }

        public CompteDepotException(string message, Exception innerException) : base(message, innerException)
        {
            DateErreur = DateTime.Now;
        }

        public CompteDepotException(string message, string numeroCompte) : base(message)
        {
            NumeroCompte = numeroCompte;
            DateErreur = DateTime.Now;
        }

        public CompteDepotException(string message, string numeroCompte, string codeErreur) : base(message)
        {
            NumeroCompte = numeroCompte;
            CodeErreur = codeErreur;
            DateErreur = DateTime.Now;
        }

        public CompteDepotException(string message, string numeroCompte, string codeErreur, Exception innerException) 
            : base(message, innerException)
        {
            NumeroCompte = numeroCompte;
            CodeErreur = codeErreur;
            DateErreur = DateTime.Now;
        }

        protected CompteDepotException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
            NumeroCompte = info.GetString(nameof(NumeroCompte));
            CodeErreur = info.GetString(nameof(CodeErreur));
            DateErreur = info.GetDateTime(nameof(DateErreur));
        }

        public override void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            base.GetObjectData(info, context);
            info.AddValue(nameof(NumeroCompte), NumeroCompte);
            info.AddValue(nameof(CodeErreur), CodeErreur);
            info.AddValue(nameof(DateErreur), DateErreur);
        }

        public override string ToString()
        {
            return $"[{DateErreur:yyyy-MM-dd HH:mm:ss}] CompteDepotException: {Message}" +
                   (NumeroCompte != null ? $" (Compte: {NumeroCompte})" : "") +
                   (CodeErreur != null ? $" (Code: {CodeErreur})" : "") +
                   (InnerException != null ? $" -> {InnerException.Message}" : "");
        }
    }

    /// <summary>
    /// Exceptions spécifiques pour les différents cas d'erreur
    /// </summary>
    public class CompteInexistantException : CompteDepotException
    {
        public CompteInexistantException(string numeroCompte) 
            : base($"Le compte {numeroCompte} n'existe pas", numeroCompte, "COMPTE_INEXISTANT")
        {
        }
    }

    public class CompteInactifException : CompteDepotException
    {
        public CompteInactifException(string numeroCompte) 
            : base($"Le compte {numeroCompte} est inactif", numeroCompte, "COMPTE_INACTIF")
        {
        }
    }

    public class SoldeInsuffisantException : CompteDepotException
    {
        public decimal SoldeActuel { get; }
        public decimal MontantDemande { get; }

        public SoldeInsuffisantException(string numeroCompte, decimal soldeActuel, decimal montantDemande)
            : base($"Solde insuffisant sur le compte {numeroCompte}. Solde: {soldeActuel:C}, Demandé: {montantDemande:C}", 
                   numeroCompte, "SOLDE_INSUFFISANT")
        {
            SoldeActuel = soldeActuel;
            MontantDemande = montantDemande;
        }
    }

    public class LimiteRetraitDepasseeException : CompteDepotException
    {
        public decimal LimiteAutorisee { get; }
        public decimal MontantDemande { get; }
        public string TypeLimite { get; }

        public LimiteRetraitDepasseeException(string numeroCompte, decimal limiteAutorisee, decimal montantDemande, string typeLimite)
            : base($"Limite de retrait {typeLimite.ToLower()} dépassée sur le compte {numeroCompte}. Limite: {limiteAutorisee:C}, Demandé: {montantDemande:C}",
                   numeroCompte, "LIMITE_RETRAIT_DEPASSEE")
        {
            LimiteAutorisee = limiteAutorisee;
            MontantDemande = montantDemande;
            TypeLimite = typeLimite;
        }
    }

    public class MontantInvalideException : CompteDepotException
    {
        public decimal MontantFourni { get; }

        public MontantInvalideException(decimal montantFourni)
            : base($"Montant invalide: {montantFourni}", null, "MONTANT_INVALIDE")
        {
            MontantFourni = montantFourni;
        }
    }

    public class CompteEchuException : CompteDepotException
    {
        public DateTime DateEcheance { get; }

        public CompteEchuException(string numeroCompte, DateTime dateEcheance)
            : base($"Le compte {numeroCompte} est échu depuis le {dateEcheance:dd/MM/yyyy}", numeroCompte, "COMPTE_ECHU")
        {
            DateEcheance = dateEcheance;
        }
    }
}