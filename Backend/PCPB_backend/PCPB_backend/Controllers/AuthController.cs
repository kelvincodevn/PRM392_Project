using Helpers.DTOs.Authentication;
using BusinessObjects.DTOs.Authentication;
using Microsoft.AspNetCore.Mvc;
using Services.Interfaces;
using System.Threading.Tasks;

namespace PCPB_backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginDTO model)
        {
            var user = await _authService.Authenticate(model.Username, model.Password);

            if (user == null)
                return Unauthorized(new { message = "Invalid username or password" });

            var token = await _authService.GenerateJwtToken(user);

            return Ok(new 
            { 
                token,
                userId = user.UserId,
                username = user.Username,
                email = user.Email,
                role = user.Role,
                fullName = user.FullName,
                phoneNumber = user.PhoneNumber
            });
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterDTO model)
        {
            try
            {
                // Validate model
                if (!ModelState.IsValid)
                {
                    var errors = ModelState.Values
                        .SelectMany(v => v.Errors)
                        .Select(e => e.ErrorMessage)
                        .ToList();
                    
                    return BadRequest(new { message = "Validation failed", errors });
                }
                
                // Register user
                var user = await _authService.Register(model);
                
                if (user == null)
                    return BadRequest(new { message = "Username or email already exists" });
                
                // Generate token for immediate login
                var token = await _authService.GenerateJwtToken(user);
                
                return Ok(new 
                { 
                    token,
                    userId = user.UserId,
                    username = user.Username,
                    email = user.Email,
                    role = user.Role,
                    fullName = user.FullName,
                    phoneNumber = user.PhoneNumber
                });
            }
            catch (ArgumentException ex)
            {
                // This will catch password validation errors
                return BadRequest(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                // Log the exception
                return StatusCode(500, new { message = "An error occurred during registration", error = ex.Message });
            }
        }

        [HttpPost("forgot-password")]
        public async Task<IActionResult> ForgotPassword([FromBody] ForgotPasswordDTO model)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    var errors = ModelState.Values
                        .SelectMany(v => v.Errors)
                        .Select(e => e.ErrorMessage)
                        .ToList();

                    return BadRequest(new { message = "Validation failed", errors });
                }

                var result = await _authService.ForgotPassword(model.Email);

                if (!result)
                    return BadRequest(new { message = "Email not found" });

                return Ok(new { message = "Password reset token has been generated. In a real application, this would be sent via email." });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "An error occurred during password reset request", error = ex.Message });
            }
        }

        [HttpPost("reset-password")]
        public async Task<IActionResult> ResetPassword([FromBody] ResetPasswordDTO model)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    var errors = ModelState.Values
                        .SelectMany(v => v.Errors)
                        .Select(e => e.ErrorMessage)
                        .ToList();

                    return BadRequest(new { message = "Validation failed", errors });
                }

                var result = await _authService.ResetPassword(model.Token, model.NewPassword);

                if (!result)
                    return BadRequest(new { message = "Invalid or expired reset token" });

                return Ok(new { message = "Password has been reset successfully" });
            }
            catch (ArgumentException ex)
            {
                // This will catch password validation errors
                return BadRequest(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "An error occurred during password reset", error = ex.Message });
            }
        }

        [HttpPost("validate-reset-token")]
        public async Task<IActionResult> ValidateResetToken([FromBody] string token)
        {
            try
            {
                if (string.IsNullOrEmpty(token))
                    return BadRequest(new { message = "Token is required" });

                var isValid = await _authService.ValidateResetToken(token);

                return Ok(new { isValid });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "An error occurred during token validation", error = ex.Message });
            }
        }
    }
}

