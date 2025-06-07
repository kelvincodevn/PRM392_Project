using BusinessObjects.DTOs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Services.Interfaces;

namespace PCPB_backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize(Roles = "Admin")]
    public class CommissionController : ControllerBase
    {
        private readonly ICommissionService _commissionService;

        public CommissionController(ICommissionService commissionService) => _commissionService = commissionService;

        [HttpPost("calculate/{orderItemId}")]
        public async Task<ActionResult<CommissionDTO>> CalculateCommission(int orderItemId)
        {
            try
            {
                return Ok(await _commissionService.CalculateCommission(orderItemId));
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "Error calculating commission", error = ex.Message });
            }
        }

        [HttpGet("third-party/{thirdPartyId}")]
        public async Task<ActionResult<List<CommissionDTO>>> GetCommissionsByThirdParty(int thirdPartyId)
        {
            try
            {
                return Ok(await _commissionService.GetCommissionsByThirdParty(thirdPartyId));
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "Error retrieving commissions", error = ex.Message });
            }
        }

        [HttpGet("third-party/{thirdPartyId}/total")]
        public async Task<ActionResult<decimal>> GetTotalCommissionsByThirdParty(int thirdPartyId)
        {
            try
            {
                return Ok(await _commissionService.GetTotalCommissionsByThirdParty(thirdPartyId));
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "Error calculating total commissions", error = ex.Message });
            }
        }

        [HttpPut("{commissionId}/status")]
        public async Task<ActionResult<CommissionDTO>> UpdateCommissionStatus(int commissionId, [FromBody] string status)
        {
            try
            {
                return Ok(await _commissionService.UpdateCommissionStatus(commissionId, status));
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "Error updating commission status", error = ex.Message });
            }
        }
    }
} 