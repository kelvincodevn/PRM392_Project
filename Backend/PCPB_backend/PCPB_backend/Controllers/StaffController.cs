using BusinessObjects.DTOs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Services.Interfaces;

namespace PCPB_backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class StaffController : ControllerBase
    {
        private readonly IStaffService _staffService;

        public StaffController(IStaffService staffService) => _staffService = staffService;

        [HttpGet]
        public async Task<ActionResult<List<StaffDTO>>> GetAllStaff()
        {
            try
            {
                return Ok(await _staffService.GetAllStaff());
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "Error retrieving staff members", error = ex.Message });
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<StaffDTO>> GetStaff(int id)
        {
            try
            {
                return Ok(await _staffService.GetStaffById(id));
            }
            catch (Exception ex)
            {
                return ex.Message.Contains("not found") 
                    ? NotFound(new { message = ex.Message })
                    : StatusCode(500, new { message = "Error retrieving staff member", error = ex.Message });
            }
        }

        [HttpPost]
        public async Task<ActionResult<StaffDTO>> CreateStaff(StaffDTO staffDto)
        {
            try
            {
                if (!ModelState.IsValid) return BadRequest(ModelState);
                var createdStaff = await _staffService.CreateStaff(staffDto);
                return CreatedAtAction(nameof(GetStaff), new { id = createdStaff.StaffId }, createdStaff);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "Error creating staff member", error = ex.Message });
            }
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<StaffDTO>> UpdateStaff(int id, StaffDTO staffDto)
        {
            try
            {
                if (id != staffDto.StaffId) return BadRequest(new { message = "Staff ID mismatch" });
                if (!ModelState.IsValid) return BadRequest(ModelState);
                return Ok(await _staffService.UpdateStaff(staffDto));
            }
            catch (Exception ex)
            {
                return ex.Message.Contains("not found")
                    ? NotFound(new { message = ex.Message })
                    : StatusCode(500, new { message = "Error updating staff member", error = ex.Message });
            }
        }
    }
} 