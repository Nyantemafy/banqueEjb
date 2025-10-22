using CompteDepot.Models;
using Newtonsoft.Json;
using System.Text;

namespace CompteDepot.Services
{
    public class DepotServiceClient : IDepotService
    {
        private readonly HttpClient _httpClient;
        private readonly ILogger<DepotServiceClient> _logger;
        private const string API_BASE_PATH = "/BanqueCentral/api/depot";

        public DepotServiceClient(HttpClient httpClient, ILogger<DepotServiceClient> logger)
        {
            _httpClient = httpClient;
            _logger = logger;
        }

        public async Task<ApiResponse<List<ProduitEpargneModel>>> GetDetailsProduits(int userId)
        {
            try
            {
                _logger.LogInformation($"Appel GetDetailsProduits pour userId: {userId}");
                
                var response = await _httpClient.GetAsync($"{API_BASE_PATH}/produits?userId={userId}");
                
                if (response.IsSuccessStatusCode)
                {
                    var content = await response.Content.ReadAsStringAsync();
                    var produits = JsonConvert.DeserializeObject<List<ProduitEpargneModel>>(content);
                    
                    return new ApiResponse<List<ProduitEpargneModel>>
                    {
                        Success = true,
                        Message = "Produits récupérés avec succès",
                        Data = produits ?? new List<ProduitEpargneModel>()
                    };
                }
                else
                {
                    _logger.LogError($"Erreur HTTP: {response.StatusCode}");
                    return new ApiResponse<List<ProduitEpargneModel>>
                    {
                        Success = false,
                        Message = $"Erreur serveur: {response.StatusCode}",
                        Data = new List<ProduitEpargneModel>()
                    };
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de l'appel GetDetailsProduits");
                return new ApiResponse<List<ProduitEpargneModel>>
                {
                    Success = false,
                    Message = $"Erreur: {ex.Message}",
                    Data = new List<ProduitEpargneModel>()
                };
            }
        }

        public async Task<ApiResponse<bool>> InsertOperation(InsertOperationRequest request)
        {
            try
            {
                _logger.LogInformation($"Appel InsertOperation pour compte: {request.CompteDepotId}");
                
                var jsonContent = JsonConvert.SerializeObject(request);
                var content = new StringContent(jsonContent, Encoding.UTF8, "application/json");
                
                var response = await _httpClient.PostAsync($"{API_BASE_PATH}/operation", content);
                
                if (response.IsSuccessStatusCode)
                {
                    var resultContent = await response.Content.ReadAsStringAsync();
                    var result = JsonConvert.DeserializeObject<Dictionary<string, bool>>(resultContent);
                    
                    bool success = result != null && result.ContainsKey("success") && result["success"];
                    
                    return new ApiResponse<bool>
                    {
                        Success = success,
                        Message = success ? "Opération enregistrée avec succès" : "Échec de l'opération",
                        Data = success
                    };
                }
                else
                {
                    _logger.LogError($"Erreur HTTP: {response.StatusCode}");
                    return new ApiResponse<bool>
                    {
                        Success = false,
                        Message = $"Erreur serveur: {response.StatusCode}",
                        Data = false
                    };
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de l'appel InsertOperation");
                return new ApiResponse<bool>
                {
                    Success = false,
                    Message = $"Erreur: {ex.Message}",
                    Data = false
                };
            }
        }

        public async Task<ApiResponse<List<OperationModel>>> GetOperations(int compteDepotId)
        {
            try
            {
                _logger.LogInformation($"Appel GetOperations pour compte: {compteDepotId}");
                
                var response = await _httpClient.GetAsync($"{API_BASE_PATH}/operations?compteId={compteDepotId}");
                
                if (response.IsSuccessStatusCode)
                {
                    var content = await response.Content.ReadAsStringAsync();
                    var operations = JsonConvert.DeserializeObject<List<OperationModel>>(content);
                    
                    return new ApiResponse<List<OperationModel>>
                    {
                        Success = true,
                        Message = "Opérations récupérées avec succès",
                        Data = operations ?? new List<OperationModel>()
                    };
                }
                else
                {
                    return new ApiResponse<List<OperationModel>>
                    {
                        Success = false,
                        Message = $"Erreur serveur: {response.StatusCode}",
                        Data = new List<OperationModel>()
                    };
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de l'appel GetOperations");
                return new ApiResponse<List<OperationModel>>
                {
                    Success = false,
                    Message = $"Erreur: {ex.Message}",
                    Data = new List<OperationModel>()
                };
            }
        }

        public async Task<ApiResponse<CompteDepotModel>> GetCompteDepot(int userId)
        {
            try
            {
                _logger.LogInformation($"Appel GetCompteDepot pour userId: {userId}");
                
                var response = await _httpClient.GetAsync($"{API_BASE_PATH}/info?userId={userId}");
                
                if (response.IsSuccessStatusCode)
                {
                    var content = await response.Content.ReadAsStringAsync();
                    var compte = JsonConvert.DeserializeObject<CompteDepotModel>(content);
                    
                    return new ApiResponse<CompteDepotModel>
                    {
                        Success = true,
                        Message = "Compte récupéré avec succès",
                        Data = compte
                    };
                }
                else
                {
                    return new ApiResponse<CompteDepotModel>
                    {
                        Success = false,
                        Message = $"Erreur serveur: {response.StatusCode}",
                        Data = null
                    };
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de l'appel GetCompteDepot");
                return new ApiResponse<CompteDepotModel>
                {
                    Success = false,
                    Message = $"Erreur: {ex.Message}",
                    Data = null
                };
            }
        }
    }
}