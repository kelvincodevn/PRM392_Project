using System.ComponentModel.DataAnnotations;
using BusinessObjects.Enums;

namespace Helpers.DTOs.Authentication
{
    public class RegisterDTO
    {
        [Required]
        [StringLength(255, MinimumLength = 8)]
        public string Username { get; set; }
        
        [Required]
        [EmailAddress]
        public string Email { get; set; }
        
        [Required]
        [StringLength(100, MinimumLength = 8)]
        public string Password { get; set; }
        
        [Required]
        public string FullName { get; set; }
        
        [Phone]
        public string? PhoneNumber { get; set; }        
        public string? Address { get; set; }
    }
}
