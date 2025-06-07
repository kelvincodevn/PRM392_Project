using System;
using System.Collections.Generic;

namespace DAOs;

public partial class Order
{
    public int OrderId { get; set; }

    public int CustomerId { get; set; }

    public DateTime? OrderDate { get; set; }

    public decimal TotalAmount { get; set; }

    public decimal? ShippingFee { get; set; }

    public decimal FinalAmount { get; set; }

    public string? OrderStatus { get; set; }

    public string ShippingAddress { get; set; } = null!;

    public int? StaffId { get; set; }

    public string? PaymentMethod { get; set; }

    public string? PaymentStatus { get; set; }

    public DateTime? DeliveredAt { get; set; }

    public virtual User Customer { get; set; } = null!;

    public virtual ICollection<OrderItem> OrderItems { get; set; } = new List<OrderItem>();

    public virtual ICollection<Payment> Payments { get; set; } = new List<Payment>();

    public virtual Staff? Staff { get; set; }
}
