using BusinessObjects.Models;
using DAOs;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Repositories
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

    public class ProductRepository : IProductRepository
    {
        private readonly ProductDAO _productDAO;

        public ProductRepository(ProductDAO productDAO)
        {
            _productDAO = productDAO;
        }

        public async Task<Product> CreateProduct(Product product)
        {
            return await _productDAO.CreateProduct(product);
        }

        public async Task<Product> GetProductById(int id)
        {
            return await _productDAO.GetProductById(id);
        }

        public async Task<List<Product>> GetAllProducts()
        {
            return await _productDAO.GetAllProducts();
        }

        public async Task<List<Product>> GetProductsByThirdParty(int thirdPartyId)
        {
            return await _productDAO.GetProductsByThirdParty(thirdPartyId);
        }

        public async Task<Product> UpdateProduct(Product product)
        {
            return await _productDAO.UpdateProduct(product);
        }

        public async Task<bool> DeleteProduct(int id)
        {
            return await _productDAO.DeleteProduct(id);
        }

        public async Task<bool> ProductExists(int id)
        {
            return await _productDAO.ProductExists(id);
        }

        public async Task<bool> IsProductOwnedByThirdParty(int productId, int thirdPartyId)
        {
            return await _productDAO.IsProductOwnedByThirdParty(productId, thirdPartyId);
        }
    }
} 