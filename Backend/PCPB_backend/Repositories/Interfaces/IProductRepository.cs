using DAOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repositories.Interfaces
{
    public interface IProductRepository
    {
        Task<Product> CreateProduct(Product product);
        Task<Product> GetProductById(int id);
        Task<List<Product>> GetAllProducts();
        Task<List<Product>> GetProductsByThirdParty(int thirdPartyId);
        Task<Product> UpdateProduct(Product product);
        Task<bool> DeleteProduct(int id);
        Task<bool> ProductExists(int id);
        Task<bool> IsProductOwnedByThirdParty(int productId, int thirdPartyId);
    }
}
