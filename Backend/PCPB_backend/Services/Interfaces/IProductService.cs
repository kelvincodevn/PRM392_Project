using DAOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Services.Interfaces
{
    public interface IProductService
    {
        Task<Product> CreateProduct(Product product, int thirdPartyId);
        Task<Product> GetProductById(int id);
        Task<List<Product>> GetAllProducts();
        Task<List<Product>> GetProductsByThirdParty(int thirdPartyId);
        Task<Product> UpdateProduct(Product product, int thirdPartyId);
        Task<bool> DeleteProduct(int id, int thirdPartyId);
        Task<List<Product>> SearchProductsByName(string productName);
    }
}
