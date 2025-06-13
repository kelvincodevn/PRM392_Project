using System;
using System.Collections.Generic;

namespace DAOs;

public partial class OrderItem
{
    public int OrderItemId { get; set; }

    public int OrderId { get; set; }

    public int ProductId { get; set; }

    public int Quantity { get; set; }

    public decimal PriceAtOrder { get; set; }

    public decimal Subtotal { get; set; }

    public int ThirdPartyId { get; set; }

    public DateTime? CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public virtual Commission? Commission { get; set; }

    public virtual Order Order { get; set; } = null!;

    public virtual Product Product { get; set; } = null!;

    public virtual ThirdParty ThirdParty { get; set; } = null!;
}
