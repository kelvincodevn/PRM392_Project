using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.DTOs
{
    public class ThirdPartyDTO
    {
        public int ThirdPartyId { get; set; }

        public int? UserId { get; set; }

        public string CompanyName { get; set; } = null!;

        public string? TaxId { get; set; }

        public string? BankAccountInfo { get; set; }

        public string? Status { get; set; }

        public decimal? Rating { get; set; }

        public DateTime? JoinedAt { get; set; }
    }
}
