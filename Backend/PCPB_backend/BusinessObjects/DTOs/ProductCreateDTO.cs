using Microsoft.AspNetCore.Http;
using System.ComponentModel.DataAnnotations;

namespace BusinessObjects.DTOs
{
    public class ProductCreateDTO
    {
        [Required]
        [StringLength(255)]
        public string ProductName { get; set; } = null!;

        [StringLength(1000)]
        public string? Description { get; set; }

        [Required]
        [Range(0.01, double.MaxValue, ErrorMessage = "Price must be greater than 0")]
        public decimal Price { get; set; }

        [Required]
        [Range(0, int.MaxValue, ErrorMessage = "Stock quantity must be non-negative")]
        public int StockQuantity { get; set; }

        [Required]
        public int CategoryId { get; set; }
        // Optional: Image file for upload
        public IFormFile? ImageFile { get; set; }

        // Optional: Direct image URL (if not uploading file)
        public string? ImageUrl { get; set; }
    }
}
