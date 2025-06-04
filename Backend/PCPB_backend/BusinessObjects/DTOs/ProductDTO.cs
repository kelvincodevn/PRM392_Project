using System;

namespace BusinessObjects.DTOs
{
    public class ProductDTO
    {
        public string ProductName { get; set; } = null!;
        public string Description { get; set; } = null!;
        public decimal Price { get; set; }
        public int StockQuantity { get; set; }
        public int CategoryId { get; set; }
        public int ThirdPartyId { get; set; }
        public string? ImageUrl { get; set; }
        public string? Specifications { get; set; }
    }
} 