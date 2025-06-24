using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace BusinessObjects.DTOs.Authentication
{
    public class ResetPasswordDTO
    {
        [Required]
        public string Token { get; set; }
        
        [Required]
        [StringLength(100, MinimumLength = 8)]
        public string NewPassword { get; set; }
        
        [Required]
        [Compare("NewPassword")]
        public string ConfirmPassword { get; set; }
    }
}