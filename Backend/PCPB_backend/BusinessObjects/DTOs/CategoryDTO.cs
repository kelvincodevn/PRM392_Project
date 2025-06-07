using System;

namespace BusinessObjects.DTOs
{
    public class CategoryDTO
    {
        public string CategoryName { get; set; } = null!;
        public int? ParentCategoryId { get; set; }
    }
} 