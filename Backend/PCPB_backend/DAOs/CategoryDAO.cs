using BusinessObjects.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DAOs
{
    public class CategoryDAO
    {
        private readonly PCPBContext _context;

        public CategoryDAO(PCPBContext context)
        {
            _context = context;
        }

        // Create
        public async Task<BusinessObjects.Models.Category> CreateCategory(BusinessObjects.Models.Category category)
        {
            if (category == null)
            {
                throw new ArgumentNullException(nameof(category));
            }

            await _context.Categories.AddAsync(category);
            await _context.SaveChangesAsync();
            return category;
        }

        // Read
        public async Task<BusinessObjects.Models.Category> GetCategoryById(int id)
        {
            return await _context.Categories
                .Include(c => c.ParentCategory)
                .Include(c => c.InverseParentCategory)
                .Include(c => c.Products)
                .FirstOrDefaultAsync(c => c.CategoryId == id);
        }

        public async Task<List<BusinessObjects.Models.Category>> GetAllCategories()
        {
            return await _context.Categories
                .Include(c => c.ParentCategory)
                .Include(c => c.InverseParentCategory)
                .Include(c => c.Products)
                .ToListAsync();
        }

        // Update
        public async Task<BusinessObjects.Models.Category> UpdateCategory(BusinessObjects.Models.Category category)
        {
            if (category == null)
            {
                throw new ArgumentNullException(nameof(category));
            }

            _context.Entry(category).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return category;
        }

        // Delete
        public async Task<bool> DeleteCategory(int id)
        {
            var category = await _context.Categories.FindAsync(id);
            if (category == null)
                return false;

            _context.Categories.Remove(category);
            await _context.SaveChangesAsync();
            return true;
        }

        // Additional helper methods
        public async Task<List<BusinessObjects.Models.Category>> GetSubCategories(int parentId)
        {
            return await _context.Categories
                .Where(c => c.ParentCategoryId == parentId)
                .Include(c => c.InverseParentCategory)
                .ToListAsync();
        }

        public async Task<bool> CategoryExists(int id)
        {
            return await _context.Categories.AnyAsync(c => c.CategoryId == id);
        }
    }
} 