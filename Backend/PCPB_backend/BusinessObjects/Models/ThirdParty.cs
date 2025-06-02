using System;
using System.Collections.Generic;

namespace DAOs;

public partial class ThirdParty
{
    public int ThirdPartyId { get; set; }

    public int? UserId { get; set; }

    public string CompanyName { get; set; } = null!;

    public string? TaxId { get; set; }

    public string? BankAccountInfo { get; set; }

    public string? Status { get; set; }

    public decimal? Rating { get; set; }

    public DateTime? JoinedAt { get; set; }

    public virtual ICollection<Commission> Commissions { get; set; } = new List<Commission>();

    public virtual ICollection<OrderItem> OrderItems { get; set; } = new List<OrderItem>();

    public virtual ICollection<Product> Products { get; set; } = new List<Product>();

    public virtual User? User { get; set; }
}
