using BusinessObjects.Models;
using DAOs;
using Repositories;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Services
{
    public interface IProductService
    {
        Task<Product> CreateProduct(Product product, int thirdPartyId);
        Task<Product> GetProductById(int id);
        Task<List<Product>> GetAllProducts();
        Task<List<Product>> GetProductsByThirdParty(int thirdPartyId);
        Task<Product> UpdateProduct(Product product, int thirdPartyId);
        Task<bool> DeleteProduct(int id, int thirdPartyId);
    }

    public class ProductService : IProductService
    {
        private readonly IProductRepository _productRepository;
        private readonly ICategoryRepository _categoryRepository;

        public ProductService(IProductRepository productRepository, ICategoryRepository categoryRepository)
        {
            _productRepository = productRepository;
            _categoryRepository = categoryRepository;
        }

        public async Task<Product> CreateProduct(Product product, int thirdPartyId)
        {
            // Validate category exists
            var categoryExists = await _categoryRepository.CategoryExists(product.CategoryId);
            if (!categoryExists)
            {
                throw new System.Exception("Category does not exist");
            }

            // Set the third party ID
            product.ThirdPartyId = thirdPartyId;

            return await _productRepository.CreateProduct(product);
        }

        public async Task<Product> GetProductById(int id)
        {
            var product = await _productRepository.GetProductById(id);
            if (product == null)
            {
                throw new System.Exception("Product not found");
            }
            return product;
        }

        public async Task<List<Product>> GetAllProducts()
        {
            return await _productRepository.GetAllProducts();
        }

        public async Task<List<Product>> GetProductsByThirdParty(int thirdPartyId)
        {
            return await _productRepository.GetProductsByThirdParty(thirdPartyId);
        }

        public async Task<Product> UpdateProduct(Product product, int thirdPartyId)
        {
            // Check if product exists
            var exists = await _productRepository.ProductExists(product.ProductId);
            if (!exists)
            {
                throw new System.Exception("Product not found");
            }

            // Check if product belongs to the third party
            var isOwned = await _productRepository.IsProductOwnedByThirdParty(product.ProductId, thirdPartyId);
            if (!isOwned)
            {
                throw new System.Exception("You don't have permission to update this product");
            }

            // Validate category exists
            var categoryExists = await _categoryRepository.CategoryExists(product.CategoryId);
            if (!categoryExists)
            {
                throw new System.Exception("Category does not exist");
            }

            // Ensure the third party ID cannot be changed
            product.ThirdPartyId = thirdPartyId;

            return await _productRepository.UpdateProduct(product);
        }

        public async Task<bool> DeleteProduct(int id, int thirdPartyId)
        {
            // Check if product exists
            var exists = await _productRepository.ProductExists(id);
            if (!exists)
            {
                throw new System.Exception("Product not found");
            }

            // Check if product belongs to the third party
            var isOwned = await _productRepository.IsProductOwnedByThirdParty(id, thirdPartyId);
            if (!isOwned)
            {
                throw new System.Exception("You don't have permission to delete this product");
            }

            return await _productRepository.DeleteProduct(id);
        }
    }
} 