using BusinessObjects.Models;
using Repositories;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Services
{
    public interface ICategoryService
    {
        Task<BusinessObjects.Models.Category> CreateCategory(BusinessObjects.Models.Category category);
        Task<BusinessObjects.Models.Category> GetCategoryById(int id);
        Task<List<BusinessObjects.Models.Category>> GetAllCategories();
        Task<BusinessObjects.Models.Category> UpdateCategory(BusinessObjects.Models.Category category);
        Task<bool> DeleteCategory(int id);
        Task<List<BusinessObjects.Models.Category>> GetSubCategories(int parentId);
    }

    public class CategoryService : ICategoryService
    {
        private readonly ICategoryRepository _categoryRepository;

        public CategoryService(ICategoryRepository categoryRepository)
        {
            _categoryRepository = categoryRepository;
        }

        public async Task<BusinessObjects.Models.Category> CreateCategory(BusinessObjects.Models.Category category)
        {
            if (category.ParentCategoryId.HasValue)
            {
                var parentExists = await _categoryRepository.CategoryExists(category.ParentCategoryId.Value);
                if (!parentExists)
                {
                    throw new System.Exception("Parent category does not exist");
                }
            }
            return await _categoryRepository.CreateCategory(category);
        }

        public async Task<BusinessObjects.Models.Category> GetCategoryById(int id)
        {
            var category = await _categoryRepository.GetCategoryById(id);
            if (category == null)
            {
                throw new System.Exception("Category not found");
            }
            return category;
        }

        public async Task<List<BusinessObjects.Models.Category>> GetAllCategories()
        {
            return await _categoryRepository.GetAllCategories();
        }

        public async Task<BusinessObjects.Models.Category> UpdateCategory(BusinessObjects.Models.Category category)
        {
            var exists = await _categoryRepository.CategoryExists(category.CategoryId);
            if (!exists)
            {
                throw new System.Exception("Category not found");
            }

            if (category.ParentCategoryId.HasValue)
            {
                var parentExists = await _categoryRepository.CategoryExists(category.ParentCategoryId.Value);
                if (!parentExists)
                {
                    throw new System.Exception("Parent category does not exist");
                }
            }

            return await _categoryRepository.UpdateCategory(category);
        }

        public async Task<bool> DeleteCategory(int id)
        {
            var exists = await _categoryRepository.CategoryExists(id);
            if (!exists)
            {
                throw new System.Exception("Category not found");
            }

            return await _categoryRepository.DeleteCategory(id);
        }

        public async Task<List<BusinessObjects.Models.Category>> GetSubCategories(int parentId)
        {
            var parentExists = await _categoryRepository.CategoryExists(parentId);
            if (!parentExists)
            {
                throw new System.Exception("Parent category not found");
            }

            return await _categoryRepository.GetSubCategories(parentId);
        }
    }
} 