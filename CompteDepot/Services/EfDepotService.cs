using CompteDepot.Data;
using CompteDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace CompteDepot.Services
{
    public class EfDepotService : IDepotService
    {
        private readonly BanqueContext _db;
        private readonly ILogger<EfDepotService> _logger;

        public EfDepotService(BanqueContext db, ILogger<EfDepotService> logger)
        {
            _db = db;
            _logger = logger;
        }

        public async Task<ApiResponse<CompteDepotModel>> GetCompteDepot(int userId)
        {
            try
            {
                var c = await _db.CompteDepots.AsNoTracking()
                    .FirstOrDefaultAsync(x => x.IdUser == userId);

                if (c == null)
                {
                    return new ApiResponse<CompteDepotModel>
                    {
                        Success = true,
                        Message = "Aucun compte dépôt",
                        Data = null
                    };
                }

                var model = new CompteDepotModel
                {
                    IdCompteDepot = c.IdCompteDepot,
                    MontantInitial = c.MontantInitial ?? 0m,
                    TauxInteret = c.TauxInteret ?? 0m,
                    DateDebut = c.DateDebut ?? DateTime.MinValue,
                    DateFin = c.DateFin,
                    IdUser = c.IdUser,
                    Status = null // libellé status non mappé ici
                };

                return new ApiResponse<CompteDepotModel>
                {
                    Success = true,
                    Message = "OK",
                    Data = model
                };
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur GetCompteDepot");
                return new ApiResponse<CompteDepotModel> { Success = false, Message = ex.Message };
            }
        }

        public async Task<ApiResponse<List<OperationModel>>> GetOperations(int compteDepotId)
        {
            try
            {
                // join with type to get libelle
                var ops = await (from o in _db.DepotOperations.AsNoTracking()
                                 join t in _db.Types.AsNoTracking() on o.IdType equals t.IdType
                                 where o.IdCompteDepot == compteDepotId
                                 orderby o.DateOp descending
                                 select new OperationModel
                                 {
                                     IdOp = o.IdOp,
                                     Montant = o.Montant ?? 0m,
                                     DateOp = o.DateOp ?? DateTime.MinValue,
                                     IdCompteDepot = o.IdCompteDepot,
                                     IdType = o.IdType,
                                     TypeLibelle = t.Libelle
                                 }).ToListAsync();

                return new ApiResponse<List<OperationModel>>
                {
                    Success = true,
                    Message = "OK",
                    Data = ops
                };
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur GetOperations");
                return new ApiResponse<List<OperationModel>> { Success = false, Message = ex.Message, Data = new List<OperationModel>() };
            }
        }

        public async Task<ApiResponse<bool>> InsertOperation(InsertOperationRequest request)
        {
            try
            {
                if (request.Montant <= 0) return new ApiResponse<bool> { Success = false, Message = "Montant invalide", Data = false };

                // Map type libelle -> id_type
                var lib = (request.TypeOperation ?? "").Trim().ToUpperInvariant();
                var type = await _db.Types.FirstOrDefaultAsync(t => t.Libelle.ToUpper() == lib);
                if (type == null)
                {
                    return new ApiResponse<bool> { Success = false, Message = $"Type introuvable: {request.TypeOperation}", Data = false };
                }

                var op = new DepotOperationEntity
                {
                    IdCompteDepot = request.CompteDepotId,
                    Montant = request.Montant,
                    DateOp = DateTime.UtcNow.Date,
                    IdType = type.IdType
                };

                _db.DepotOperations.Add(op);
                await _db.SaveChangesAsync();

                return new ApiResponse<bool> { Success = true, Message = "Inséré", Data = true };
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur InsertOperation");
                return new ApiResponse<bool> { Success = false, Message = ex.Message, Data = false };
            }
        }

        public async Task<ApiResponse<List<ProduitEpargneModel>>> GetDetailsProduits(int userId)
        {
            try
            {
                var c = await _db.CompteDepots.AsNoTracking()
                    .FirstOrDefaultAsync(x => x.IdUser == userId);

                var list = new List<ProduitEpargneModel>();
                if (c == null)
                {
                    return new ApiResponse<List<ProduitEpargneModel>>
                    {
                        Success = true,
                        Message = "Aucun produit",
                        Data = list
                    };
                }

                // Compute solde from initial + ops (DEPOT - RETRAIT)
                var ops = await (from o in _db.DepotOperations.AsNoTracking()
                                 join t in _db.Types.AsNoTracking() on o.IdType equals t.IdType
                                 where o.IdCompteDepot == c.IdCompteDepot
                                 select new { o.Montant, t.Libelle }).ToListAsync();

                decimal depot = ops.Where(x => x.Libelle.ToUpper() == "DEPOT").Sum(x => x.Montant ?? 0m);
                decimal retrait = ops.Where(x => x.Libelle.ToUpper() == "RETRAIT").Sum(x => x.Montant ?? 0m);
                decimal solde = (c.MontantInitial ?? 0m) + depot - retrait;

                // Interest simple estimation
                decimal interets = 0m;
                if (c.DateDebut.HasValue && (c.TauxInteret ?? 0m) > 0)
                {
                    var years = (decimal)((DateTime.UtcNow.Date - c.DateDebut.Value.Date).TotalDays / 365.0);
                    interets = solde * ((c.TauxInteret ?? 0m) / 100m) * years;
                    if (interets < 0) interets = 0;
                }

                var produit = new ProduitEpargneModel
                {
                    Id = c.IdCompteDepot,
                    Nom = "Compte Dépôt",
                    Solde = solde,
                    Taux = c.TauxInteret ?? 0m,
                    Terme = c.DateFin.HasValue ? $"Jusqu'au {c.DateFin:dd/MM/yyyy}" : "Illimité",
                    InteretsCumules = Math.Round(interets, 2)
                };
                list.Add(produit);

                return new ApiResponse<List<ProduitEpargneModel>>
                {
                    Success = true,
                    Message = "OK",
                    Data = list
                };
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur GetDetailsProduits");
                return new ApiResponse<List<ProduitEpargneModel>> { Success = false, Message = ex.Message, Data = new List<ProduitEpargneModel>() };
            }
        }
    }
}
