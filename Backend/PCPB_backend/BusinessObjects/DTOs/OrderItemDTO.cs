using System;

namespace BusinessObjects.DTOs
{
    public class OrderItemDTO
    {
        public int OrderItemId { get; set; }
        public int OrderId { get; set; }
        public int ProductId { get; set; }
        public int Quantity { get; set; }
        public decimal PriceAtOrder { get; set; }
        public decimal Subtotal { get; set; }
        public int ThirdPartyId { get; set; }

        // Navigation properties
        public string ProductName { get; set; }
        public string ProductImage { get; set; }
        public string ThirdPartyName { get; set; }
        public DateTime? CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
    }

    public class CreateOrderItemDTO
    {
        public int ProductId { get; set; }
        public int Quantity { get; set; }
        public decimal PriceAtOrder { get; set; }
        public int ThirdPartyId { get; set; }
    }

    public class UpdateOrderItemDTO
    {
        public int Quantity { get; set; }
    }

    public class OrderItemStatisticsDTO
    {
        public int TotalItems { get; set; }
        public decimal TotalValue { get; set; }
        public int UniqueProducts { get; set; }
        public int UniqueThirdParties { get; set; }
    }
} 