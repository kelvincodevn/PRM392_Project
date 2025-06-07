using BusinessObjects.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repositories.Interfaces
{
    public interface ICategoryRepository
    {
        Task<Category> CreateCategory(Category category);
        Task<Category> GetCategoryById(int id);
        Task<List<Category>> GetAllCategories();
        Task<Category> UpdateCategory(Category category);
        Task<bool> DeleteCategory(int id);
        Task<List<Category>> GetSubCategories(int parentId);
        Task<bool> CategoryExists(int id);
    }
}
