using System;
using System.Collections.Generic;

namespace BusinessObjects.DTOs
{
    public class OrderDTO
    {
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

        // Navigation properties
        public string CustomerPhone { get; set; }
        public string CustomerName { get; set; }
        public string StaffName { get; set; }
        public List<OrderItemDTO> OrderItems { get; set; }
    }

    public class CreateOrderDTO
    {
        public string ShippingAddress { get; set; }
        public string PaymentMethod { get; set; }
        public List<CreateOrderItemDTO> OrderItems { get; set; }
    }

    public class UpdateOrderDTO
    {
        public string ShippingAddress { get; set; }
        public string PaymentMethod { get; set; }
        public string OrderStatus { get; set; }
        public int? StaffId { get; set; }
    }

    public class OrderStatusDTO
    {
        public string OrderStatus { get; set; }
    }

    public class OrderStatisticsDTO
    {
        public int TotalOrders { get; set; }
        public int PendingOrders { get; set; }
        public int ProcessingOrders { get; set; }
        public int ShippedOrders { get; set; }
        public int DeliveredOrders { get; set; }
        public int CancelledOrders { get; set; }
        public int CompletedOrders { get; set; }
        public decimal TotalRevenue { get; set; }
    }
} 