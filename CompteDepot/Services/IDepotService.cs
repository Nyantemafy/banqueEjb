using CompteDepot.Models;

namespace CompteDepot.Services
{
    public interface IDepotService
    {
        Task<ApiResponse<List<ProduitEpargneModel>>> GetDetailsProduits(int userId);
        Task<ApiResponse<bool>> InsertOperation(InsertOperationRequest request);
        Task<ApiResponse<List<OperationModel>>> GetOperations(int compteDepotId);
        Task<ApiResponse<CompteDepotModel>> GetCompteDepot(int userId);
    }
}