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

        public ProductService(IUnitOfWork unitOfWork)
        {
            _unitOfWork = unitOfWork;
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
            var product = await _unitOfWork.Products.GetByIdAsync(id); // Using GetByIdAsync from generic repository
            if (product == null)
            {
                throw new Exception("Product not found");
            }
            return product;
        }

        public async Task<List<Product>> GetAllProducts()
        {
            return (await _unitOfWork.Products.GetAllAsync()).ToList(); // Using GetAllAsync from generic repository
        }

        public async Task<List<Product>> GetProductsByThirdParty(int thirdPartyId)
        {
            return (await _unitOfWork.Products.FindAsync(p => p.ThirdPartyId == thirdPartyId)).ToList(); // Using FindAsync
        }

        public async Task<Product> UpdateProduct(Product product, int thirdPartyId)
        {
            // Check if product exists and belongs to the third party in one go
            var existingProduct = await _unitOfWork.Products.FindAsync(p => p.ProductId == product.ProductId && p.ThirdPartyId == thirdPartyId);
            if (!existingProduct.Any())
            {
                throw new Exception("Product not found or you don't have permission to update this product");
            }

            // Get the actual product to update (might be different from the passed 'product' object if only some fields are updated)
            var productToUpdate = existingProduct.FirstOrDefault();
            if (productToUpdate == null) // Should not happen if .Any() is true, but good for null safety
            {
                throw new Exception("Product not found or you don't have permission to update this product");
            }

            // Validate category exists
            var categoryExists = await _unitOfWork.Categories.FindAsync(c => c.CategoryId == product.CategoryId);
            if (!categoryExists.Any())
            {
                throw new Exception("Category does not exist");
            }

            // Update the properties of the existing product with the new values
            productToUpdate.ProductName = product.ProductName;
            productToUpdate.Description = product.Description;
            productToUpdate.Price = product.Price;
            productToUpdate.StockQuantity = product.StockQuantity;
            productToUpdate.CategoryId = product.CategoryId;
            // productToUpdate.ThirdPartyId = thirdPartyId; // ThirdPartyId should not be changed, it's already set in the FindAsync filter

            _unitOfWork.Products.Update(productToUpdate); // Using Update from generic repository
            await _unitOfWork.SaveChangesAsync();
            return productToUpdate;
        }

        public async Task<bool> DeleteProduct(int id, int thirdPartyId)
        {
            // Check if product exists and belongs to the third party in one go
            var productToDelete = await _unitOfWork.Products.FindAsync(p => p.ProductId == id && p.ThirdPartyId == thirdPartyId);
            if (!productToDelete.Any())
            {
                throw new Exception("Product not found or you don't have permission to delete this product");
            }

            _unitOfWork.Products.Delete(productToDelete.FirstOrDefault()); // Using Remove from generic repository
            var rowsAffected = await _unitOfWork.SaveChangesAsync();
            return rowsAffected > 0;
        }
    }
}