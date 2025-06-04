using BusinessObjects.Models;
using DAOs;
using Repositories.Interfaces;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Repositories.Implements
{
   
    public class CategoryRepository : ICategoryRepository
    {
        private readonly CategoryDAO _categoryDAO;

        public CategoryRepository(CategoryDAO categoryDAO)
        {
            _categoryDAO = categoryDAO;
        }

        public async Task<Category> CreateCategory(Category category)
        {
            return await _categoryDAO.CreateCategory(category);
        }

        public async Task<Category> GetCategoryById(int id)
        {
            return await _categoryDAO.GetCategoryById(id);
        }

        public async Task<List<Category>> GetAllCategories()
        {
            return await _categoryDAO.GetAllCategories();
        }

        public async Task<Category> UpdateCategory(Category category)
        {
            return await _categoryDAO.UpdateCategory(category);
        }

        public async Task<bool> DeleteCategory(int id)
        {
            return await _categoryDAO.DeleteCategory(id);
        }

        public async Task<List<Category>> GetSubCategories(int parentId)
        {
            return await _categoryDAO.GetSubCategories(parentId);
        }

        public async Task<bool> CategoryExists(int id)
        {
            return await _categoryDAO.CategoryExists(id);
        }
    }
}