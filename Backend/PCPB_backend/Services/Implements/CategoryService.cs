using BusinessObjects.Models;
using Repositories.Implements;
using Repositories.Interfaces;
using Services.Interfaces;
using System.Collections.Generic;
using System.Linq; // Added for .Any() and .ToList()
using System.Threading.Tasks;
using System; // Added for Exception

namespace Services.Implements
{
    public class CategoryService : ICategoryService
    {
        private readonly IUnitOfWork _unitOfWork;

        public CategoryService(IUnitOfWork unitOfWork) // Changed constructor to use IUnitOfWork
        {
            _unitOfWork = unitOfWork;
        }

        public async Task<Category> CreateCategory(Category category)
        {
            // Validate parent category exists if ParentCategoryId is provided
            if (category.ParentCategoryId.HasValue)
            {
                var parentExists = await _unitOfWork.Categories.FindAsync(c => c.CategoryId == category.ParentCategoryId.Value);
                if (!parentExists.Any()) // Using FindAsync and .Any() for existence check
                {
                    throw new Exception("Parent category does not exist");
                }
            }

            await _unitOfWork.Categories.AddAsync(category); // Using AddAsync from generic repository
            await _unitOfWork.SaveChangesAsync(); // Save changes through the unit of work
            return category;
        }

        public async Task<Category> GetCategoryById(int id)
        {
            var category = await _unitOfWork.Categories.GetByIdAsync(id); // Using GetByIdAsync from generic repository
            if (category == null)
            {
                throw new Exception("Category not found");
            }
            return category;
        }

        public async Task<List<Category>> GetAllCategories()
        {
            return (await _unitOfWork.Categories.GetAllAsync()).ToList(); // Using GetAllAsync from generic repository
        }

        public async Task<Category> UpdateCategory(Category category)
        {
            // Check if category exists
            var existingCategory = await _unitOfWork.Categories.GetByIdAsync(category.CategoryId);
            if (existingCategory == null)
            {
                throw new Exception("Category not found");
            }

            // Validate parent category exists if ParentCategoryId is provided
            if (category.ParentCategoryId.HasValue)
            {
                var parentExists = await _unitOfWork.Categories.FindAsync(c => c.CategoryId == category.ParentCategoryId.Value);
                if (!parentExists.Any()) // Using FindAsync and .Any() for existence check
                {
                    throw new Exception("Parent category does not exist");
                }
            }

            // Update the properties of the existing category with the new values
            existingCategory.CategoryName = category.CategoryName;
            existingCategory.InverseParentCategory = category.InverseParentCategory;
            existingCategory.ParentCategoryId = category.ParentCategoryId;

            _unitOfWork.Categories.Update(existingCategory); // Using Update from generic repository
            await _unitOfWork.SaveChangesAsync(); // Save changes through the unit of work
            return existingCategory;
        }

        public async Task<bool> DeleteCategory(int id)
        {
            // Check if category exists
            var categoryToDelete = await _unitOfWork.Categories.GetByIdAsync(id);
            if (categoryToDelete == null)
            {
                throw new Exception("Category not found");
            }

            _unitOfWork.Categories.Delete(categoryToDelete); // Using Remove from generic repository
            var rowsAffected = await _unitOfWork.SaveChangesAsync(); // Save changes through the unit of work
            return rowsAffected > 0;
        }

        public async Task<List<Category>> GetSubCategories(int parentId)
        {
            // Check if parent category exists
            var parentExists = await _unitOfWork.Categories.FindAsync(c => c.CategoryId == parentId);
            if (!parentExists.Any()) // Using FindAsync and .Any() for existence check
            {
                throw new Exception("Parent category not found");
            }

            // Find subcategories by ParentCategoryId
            return (await _unitOfWork.Categories.FindAsync(c => c.ParentCategoryId == parentId)).ToList(); // Using FindAsync
        }
    }
}