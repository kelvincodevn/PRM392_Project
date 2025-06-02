using System;
using System.Collections.Generic;

namespace DAOs;

public partial class SubscriptionPlan
{
    public int PlanId { get; set; }

    public string PlanName { get; set; } = null!;

    public string? Description { get; set; }

    public decimal Price { get; set; }

    public string? Currency { get; set; }

    public string DurationUnit { get; set; } = null!;

    public int DurationValue { get; set; }

    public string? Benefits { get; set; }

    public bool? IsActive { get; set; }

    public DateTime? CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public virtual ICollection<Subscription> Subscriptions { get; set; } = new List<Subscription>();
}
