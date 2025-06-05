using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.DTOs
{
    public class CommissionDTO
    {
        public int CommissionId { get; set; }
        public int OrderItemId { get; set; }
        public int ThirdPartyId { get; set; }
        public decimal CommissionPercentage { get; set; }
        public decimal CommissionAmount { get; set; }
        public DateTime CalculatedAt { get; set; }
        public string Status { get; set; } = null!;
        public DateTime? PaymentDate { get; set; }
    }

    public class CommissionCreateDTO
    {
        public int ThirdPartyId { get; set; }
        public int ProductId { get; set; }
        public int Quantity { get; set; }
        public string Status { get; set; } = null!;
    }

    public class CommissionUpdateDTO
    {
        public string Status { get; set; } = null!;
    }
} 