using System.ComponentModel.DataAnnotations;

namespace Helpers.DTOs.Authentication
{
    public class ForgotPasswordDTO
    {
        [Required]
        [EmailAddress]
        public string Email { get; set; }
    }
}