using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace PCPB_backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class TestController : ControllerBase
    {
        [HttpGet("public")]
        public IActionResult PublicEndpoint()
        {
            return Ok(new { message = "This is a public endpoint. Anyone can access it." });
        }
        
        [HttpGet("protected")]
        [Authorize]
        public IActionResult ProtectedEndpoint()
        {
            var userId = User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value;
            var username = User.FindFirst(System.Security.Claims.ClaimTypes.Name)?.Value;
            var role = User.FindFirst(System.Security.Claims.ClaimTypes.Role)?.Value;
            
            return Ok(new { 
                message = "This is a protected endpoint. Only authenticated users can access it.",
                userId,
                username,
                role
            });
        }
        
        [HttpGet("admin-only")]
        [Authorize(Roles = "Admin")]
        public IActionResult AdminOnlyEndpoint()
        {
            return Ok(new { message = "This is an admin-only endpoint. Only users with the Admin role can access it." });
        }
        
        [HttpGet("customer-only")]
        [Authorize(Roles = "Customer")]
        public IActionResult CustomerOnlyEndpoint()
        {
            return Ok(new { message = "This is a customer-only endpoint. Only users with the Customer role can access it." });
        }
    }
}