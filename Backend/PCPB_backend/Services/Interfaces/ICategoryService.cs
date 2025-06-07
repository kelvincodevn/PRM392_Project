using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Services.Interfaces
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
}
