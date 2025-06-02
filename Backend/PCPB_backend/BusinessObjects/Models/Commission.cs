using System;
using System.Collections.Generic;

namespace DAOs;

public partial class Commission
{
    public int CommissionId { get; set; }

    public int OrderItemId { get; set; }

    public int ThirdPartyId { get; set; }

    public decimal CommissionPercentage { get; set; }

    public decimal CommissionAmount { get; set; }

    public DateTime? CalculatedAt { get; set; }

    public string? Status { get; set; }

    public DateTime? PaymentDate { get; set; }

    public virtual OrderItem OrderItem { get; set; } = null!;

    public virtual ThirdParty ThirdParty { get; set; } = null!;
}
