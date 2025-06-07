using BusinessObjects.DTOs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Services.Interfaces;

namespace PCPB_backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ThirdPartyController : ControllerBase
    {
        private readonly IThirdPartyService _thirdPartyService;

        public ThirdPartyController(IThirdPartyService thirdPartyService) => _thirdPartyService = thirdPartyService;

        [HttpGet]
        public async Task<ActionResult<List<ThirdPartyDTO>>> GetAllThirdParties()
        {
            try
            {
                return Ok(await _thirdPartyService.GetAllThirdParties());
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "Error retrieving third parties", error = ex.Message });
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<ThirdPartyDTO>> GetThirdParty(int id)
        {
            try
            {
                return Ok(await _thirdPartyService.GetThirdPartyById(id));
            }
            catch (Exception ex)
            {
                return ex.Message.Contains("not found") 
                    ? NotFound(new { message = ex.Message })
                    : StatusCode(500, new { message = "Error retrieving third party", error = ex.Message });
            }
        }

        [HttpPost]
        public async Task<ActionResult<ThirdPartyDTO>> CreateThirdParty(ThirdPartyDTO thirdPartyDto)
        {
            try
            {
                if (!ModelState.IsValid) return BadRequest(ModelState);
                var createdThirdParty = await _thirdPartyService.CreateThirdParty(thirdPartyDto);
                return CreatedAtAction(nameof(GetThirdParty), new { id = createdThirdParty.ThirdPartyId }, createdThirdParty);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "Error creating third party", error = ex.Message });
            }
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<ThirdPartyDTO>> UpdateThirdParty(int id, ThirdPartyDTO thirdPartyDto)
        {
            try
            {
                if (id != thirdPartyDto.ThirdPartyId) return BadRequest(new { message = "Third party ID mismatch" });
                if (!ModelState.IsValid) return BadRequest(ModelState);
                return Ok(await _thirdPartyService.UpdateThirdParty(thirdPartyDto));
            }
            catch (Exception ex)
            {
                return ex.Message.Contains("not found")
                    ? NotFound(new { message = ex.Message })
                    : StatusCode(500, new { message = "Error updating third party", error = ex.Message });
            }
        }
    }
} 