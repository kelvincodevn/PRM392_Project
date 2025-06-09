using System;

namespace BusinessObjects.DTOs.CartItems
{
    public class CartItemDTO
    {
        public int CartItemId { get; set; }
        public int ProductId { get; set; }
        public int Quantity { get; set; }
        public DateTime? AddedAt { get; set; }
        public ProductDTO Product { get; set; }
    }
}
