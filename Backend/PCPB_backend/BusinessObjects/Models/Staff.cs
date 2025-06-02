using System;
using System.Collections.Generic;

namespace DAOs;

public partial class Staff
{
    public int StaffId { get; set; }

    public int? UserId { get; set; }

    public string? DeliveryRegion { get; set; }

    public string? Status { get; set; }

    public string? VehicleInfo { get; set; }

    public virtual ICollection<Order> Orders { get; set; } = new List<Order>();

    public virtual User? User { get; set; }
}
