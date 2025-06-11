using BusinessObjects.Models;
using DAOs;
using Repositories.Implements;
using Repositories.Interfaces;
using Services.Interfaces;
using System.Collections.Generic;
using System.Linq; // Added for .Any()
using System.Threading.Tasks;
using System; // Added for Exception

namespace Services.Implements
{
    public class ProductService : IProductService
    {
        private readonly IUnitOfWork _unitOfWork;
        private readonly IProductRepository _productRepository;

        public ProductService(IUnitOfWork unitOfWork, IProductRepository productRepository)
        {
            _unitOfWork = unitOfWork;
            _productRepository = productRepository;
        }

        public async Task<Product> CreateProduct(Product product, int thirdPartyId)
        {
            // Validate category exists
            var categoryExists = await _unitOfWork.Categories.FindAsync(c => c.CategoryId == product.CategoryId);
            if (!categoryExists.Any())
            {
                throw new Exception("Category does not exist");
            }

            // Set the third party ID
            product.ThirdPartyId = thirdPartyId;

            await _unitOfWork.Products.AddAsync(product);
            await _unitOfWork.SaveChangesAsync();
            return product;
        }

        public async Task<Product> GetProductById(int id)
        {
            return await _productRepository.GetProductById(id);
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
            // Check if product exists and belongs to the third party
            if (!await _productRepository.IsProductOwnedByThirdParty(product.ProductId, thirdPartyId))
            {
                throw new Exception("Product not found or you don't have permission to update this product");
            }

            // Validate category exists
            var categoryExists = await _unitOfWork.Categories.FindAsync(c => c.CategoryId == product.CategoryId);
            if (!categoryExists.Any())
            {
                throw new Exception("Category does not exist");
            }

            return await _productRepository.UpdateProduct(product);
        }

        public async Task<bool> DeleteProduct(int id, int thirdPartyId)
        {
            if (!await _productRepository.IsProductOwnedByThirdParty(id, thirdPartyId))
            {
                throw new Exception("Product not found or you don't have permission to delete this product");
            }

            return await _productRepository.DeleteProduct(id);
        }
    }
}