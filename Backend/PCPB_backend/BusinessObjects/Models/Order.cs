using System;
using System.Collections.Generic;

namespace DAOs;

public partial class Order
{
    public Order()
    {
        OrderItems = new HashSet<OrderItem>();
        Payments = new HashSet<Payment>();
    }

    public int OrderId { get; set; }

    public int CustomerId { get; set; }

    public DateTime OrderDate { get; set; }

    public decimal TotalAmount { get; set; }

    public decimal ShippingFee { get; set; }

    public decimal FinalAmount { get; set; }

    public string OrderStatus { get; set; }

    public string ShippingAddress { get; set; }

    public int? StaffId { get; set; }

    public string PaymentMethod { get; set; }

    public string PaymentStatus { get; set; }

    public DateTime? DeliveredAt { get; set; }

    public bool IsDeleted { get; set; }

    public DateTime? CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public virtual User Customer { get; set; }

    public virtual Staff Staff { get; set; }

    public virtual ICollection<OrderItem> OrderItems { get; set; }

    public virtual ICollection<Payment> Payments { get; set; }
}
