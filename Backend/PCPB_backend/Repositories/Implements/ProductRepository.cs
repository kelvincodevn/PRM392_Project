using BusinessObjects.Models;
using DAOs;
using Repositories.Interfaces;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Repositories.Implements
{
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