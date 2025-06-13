using System;

namespace BusinessObjects.DTOs
{
    public class OrderTrackingDTO
    {
        public int OrderId { get; set; }
        public string CurrentStatus { get; set; }
        public DateTime OrderDate { get; set; }
        public DateTime? ProcessingDate { get; set; }
        public DateTime? ShippedDate { get; set; }
        public DateTime? DeliveredAt { get; set; }
        public string ShippingAddress { get; set; }
        public string StaffName { get; set; }
        public string StaffPhone { get; set; }
        public string EstimatedDeliveryDate { get; set; }
        public string TrackingNumber { get; set; }
        public string ShippingCompany { get; set; }
    }
} 