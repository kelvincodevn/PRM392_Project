using BusinessObjects.Models;
using DAOs;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Repositories
{
    public interface ICategoryRepository
    {
        Task<BusinessObjects.Models.Category> CreateCategory(BusinessObjects.Models.Category category);
        Task<BusinessObjects.Models.Category> GetCategoryById(int id);
        Task<List<BusinessObjects.Models.Category>> GetAllCategories();
        Task<BusinessObjects.Models.Category> UpdateCategory(BusinessObjects.Models.Category category);
        Task<bool> DeleteCategory(int id);
        Task<List<BusinessObjects.Models.Category>> GetSubCategories(int parentId);
        Task<bool> CategoryExists(int id);
    }

    public class CategoryRepository : ICategoryRepository
    {
        private readonly CategoryDAO _categoryDAO;

        public CategoryRepository(CategoryDAO categoryDAO)
        {
            _categoryDAO = categoryDAO;
        }

        public async Task<BusinessObjects.Models.Category> CreateCategory(BusinessObjects.Models.Category category)
        {
            return await _categoryDAO.CreateCategory(category);
        }

        public async Task<BusinessObjects.Models.Category> GetCategoryById(int id)
        {
            return await _categoryDAO.GetCategoryById(id);
        }

        public async Task<List<BusinessObjects.Models.Category>> GetAllCategories()
        {
            return await _categoryDAO.GetAllCategories();
        }

        public async Task<BusinessObjects.Models.Category> UpdateCategory(BusinessObjects.Models.Category category)
        {
            return await _categoryDAO.UpdateCategory(category);
        }

        public async Task<bool> DeleteCategory(int id)
        {
            return await _categoryDAO.DeleteCategory(id);
        }

        public async Task<List<BusinessObjects.Models.Category>> GetSubCategories(int parentId)
        {
            return await _categoryDAO.GetSubCategories(parentId);
        }

        public async Task<bool> CategoryExists(int id)
        {
            return await _categoryDAO.CategoryExists(id);
        }
    }
} 