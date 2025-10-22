using CompteDepot.Models;
using CompteDepot.Services;
using Microsoft.AspNetCore.Mvc;

namespace CompteDepot.Controllers
{
    [ApiController]
    [Route("api/depot")]
    public class DepotController : ControllerBase
    {
        private readonly IDepotService _service;
        private readonly ILogger<DepotController> _logger;

        public DepotController(IDepotService service, ILogger<DepotController> logger)
        {
            _service = service;
            _logger = logger;
        }

        // GET api/depot/produits?userId=123
        [HttpGet("produits")]
        public async Task<IActionResult> GetProduits([FromQuery] int userId)
        {
            var result = await _service.GetDetailsProduits(userId);
            if (result.Success) return Ok(result.Data);
            return StatusCode(500, new { error = result.Message });
        }

        // GET api/depot/info?userId=123
        [HttpGet("info")]
        public async Task<IActionResult> GetCompteDepot([FromQuery] int userId)
        {
            var result = await _service.GetCompteDepot(userId);
            if (result.Success) return Ok(result.Data);
            return StatusCode(500, new { error = result.Message });
        }

        // GET api/depot/operations?compteId=1
        [HttpGet("operations")]
        public async Task<IActionResult> GetOperations([FromQuery] int compteId)
        {
            var result = await _service.GetOperations(compteId);
            if (result.Success) return Ok(result.Data);
            return StatusCode(500, new { error = result.Message });
        }

        // POST api/depot/operation
        [HttpPost("operation")]
        public async Task<IActionResult> InsertOperation([FromBody] InsertOperationRequest request)
        {
            if (request == null) return BadRequest(new { error = "Requête invalide" });
            var result = await _service.InsertOperation(request);
            if (result.Success) return Ok(new { success = true });
            return StatusCode(500, new { success = false, error = result.Message });
        }
    }
}
