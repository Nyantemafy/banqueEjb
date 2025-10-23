using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PretService.Data;
using PretService.Models;

namespace PretService.Controllers
{
    [ApiController]
    [Route("api/pret")] 
    public class PretController : ControllerBase
    {
        private readonly BanqueContext _db;
        private readonly ILogger<PretController> _logger;
        public PretController(BanqueContext db, ILogger<PretController> logger)
        {
            _db = db; _logger = logger;
        }

        // GET /api/pret/credits?userId=1
        [HttpGet("credits")]
        public async Task<ActionResult<ApiResponse<List<CreditSimpleDto>>>> GetCredits([FromQuery] int userId)
        {
            try
            {
                var list = await _db.Credits.AsNoTracking()
                    .Where(c => c.IdUser == userId)
                    .Select(c => new CreditSimpleDto{
                        IdCredit = c.IdCredit,
                        MontantInitial = c.MontantInitial,
                        Taux = c.Taux,
                        DureeMois = c.DureeMois
                    }).ToListAsync();
                return Ok(new ApiResponse<List<CreditSimpleDto>>{ Success = true, Message = "OK", Data = list });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur GetCredits");
                return StatusCode(500, new ApiResponse<List<CreditSimpleDto>>{ Success = false, Message = ex.Message, Data = new List<CreditSimpleDto>() });
            }
        }

        // GET /api/pret/echeances?creditId=1
        [HttpGet("echeances")]
        public async Task<ActionResult<ApiResponse<List<EcheanceDto>>>> GetEcheances([FromQuery] int creditId)
        {
            try
            {
                var list = await _db.CreditEcheances.AsNoTracking()
                    .Where(x => x.IdCredit == creditId)
                    .OrderBy(x => x.NumeroEcheance).ThenBy(x => x.DateEcheance)
                    .Select(x => new EcheanceDto{
                        IdEcheance = x.IdEcheance,
                        NumeroEcheance = x.NumeroEcheance,
                        DateEcheance = x.DateEcheance,
                        MontantTotal = x.MontantTotal,
                        Capital = x.Capital,
                        Interet = x.Interet,
                        IdStatus = x.IdStatus,
                        IdCredit = x.IdCredit
                    }).ToListAsync();
                return Ok(new ApiResponse<List<EcheanceDto>>{ Success = true, Message = "OK", Data = list });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur GetEcheances");
                return StatusCode(500, new ApiResponse<List<EcheanceDto>>{ Success = false, Message = ex.Message, Data = new List<EcheanceDto>() });
            }
        }

        // POST /api/pret/echeances
        [HttpPost("echeances")]
        public async Task<ActionResult<ApiResponse<int>>> InsertEcheance([FromBody] InsertEcheanceRequest req)
        {
            try
            {
                var entity = new CreditEcheanceEntity
                {
                    IdCredit = req.IdCredit,
                    NumeroEcheance = req.NumeroEcheance,
                    DateEcheance = req.DateEcheance,
                    MontantTotal = req.MontantTotal,
                    Capital = req.Capital,
                    Interet = req.Interet,
                    IdStatus = req.IdStatus
                };
                _db.CreditEcheances.Add(entity);
                await _db.SaveChangesAsync();
                return Ok(new ApiResponse<int>{ Success = true, Message = "Inséré", Data = entity.IdEcheance });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur InsertEcheance");
                return StatusCode(500, new ApiResponse<int>{ Success = false, Message = ex.Message });
            }
        }

        // PUT /api/pret/echeances/{id}/status
        [HttpPut("echeances/{id}/status")]
        public async Task<ActionResult<ApiResponse<bool>>> UpdateEcheanceStatus([FromRoute] int id, [FromBody] UpdateEcheanceStatusRequest req)
        {
            try
            {
                var entity = await _db.CreditEcheances.FirstOrDefaultAsync(x => x.IdEcheance == id);
                if(entity == null) return NotFound(new ApiResponse<bool>{ Success = false, Message = "Introuvable", Data = false });
                entity.IdStatus = req.IdStatus;
                await _db.SaveChangesAsync();
                return Ok(new ApiResponse<bool>{ Success = true, Message = "Mis à jour", Data = true });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur UpdateEcheanceStatus");
                return StatusCode(500, new ApiResponse<bool>{ Success = false, Message = ex.Message, Data = false });
            }
        }
    }
}
