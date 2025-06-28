using AutoMapper;
using BusinessObjects.DTOs;
using BusinessObjects.Models;
using DAOs;
using Microsoft.AspNetCore.Http;
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
        private readonly IFirebaseStorageService _firebaseStorageService;
        private readonly IMapper _mapper;

        public ProductService(IUnitOfWork unitOfWork, IProductRepository productRepository,
            IFirebaseStorageService firebaseStorageService, IMapper mapper)
        {
            _unitOfWork = unitOfWork;
            _productRepository = productRepository;
            _firebaseStorageService = firebaseStorageService;
            _mapper = mapper;
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
        

            return await _productRepository.DeleteProduct(id);
        }

        public async Task<List<Product>> SearchProductsByName(string productName)
        {
            var products = await _unitOfWork.Products.FindAsync(p => p.ProductName.Contains(productName));
            return products.ToList();
        }

        public async Task<Product> CreateProductWithImageAsync(ProductCreateDTO productCreateDto, int thirdPartyId)
        {
            // Validate category exists
            var categoryExists = await _unitOfWork.Categories.FindAsync(c => c.CategoryId == productCreateDto.CategoryId);
            if (!categoryExists.Any())
            {
                throw new Exception("Category does not exist");
            }

            // Map DTO to Product entity
            var product = _mapper.Map<Product>(productCreateDto);

            // Handle image upload if provided
            if (productCreateDto.ImageFile != null)
            {
                if (!_firebaseStorageService.IsValidImage(productCreateDto.ImageFile))
                {
                    throw new ArgumentException("Invalid image file. Please upload a valid image (JPG, PNG, GIF, WEBP) under 5MB.");
                }

                product.ImageUrl = await _firebaseStorageService.UploadImageAsync(productCreateDto.ImageFile, "products");
            }
            else if (!string.IsNullOrEmpty(productCreateDto.ImageUrl))
            {
                product.ImageUrl = productCreateDto.ImageUrl;
            }

            // Set the third party ID
            product.ThirdPartyId = thirdPartyId;

            await _unitOfWork.Products.AddAsync(product);
            await _unitOfWork.SaveChangesAsync();
            return product;
        }

        public async Task<Product> UpdateProductWithImageAsync(int productId, ProductCreateDTO productCreateDto, int thirdPartyId)
        {
            // Get existing product
            var existingProduct = await GetProductById(productId);
            if (existingProduct == null)
            {
                throw new Exception("Product not found");
            }

            // Validate category exists
            var categoryExists = await _unitOfWork.Categories.FindAsync(c => c.CategoryId == productCreateDto.CategoryId);
            if (!categoryExists.Any())
            {
                throw new Exception("Category does not exist");
            }

            // Map DTO to existing product
            _mapper.Map(productCreateDto, existingProduct);
            existingProduct.ProductId = productId; // Ensure ID is preserved

            // Handle image upload if provided
            if (productCreateDto.ImageFile != null)
            {
                if (!_firebaseStorageService.IsValidImage(productCreateDto.ImageFile))
                {
                    throw new ArgumentException("Invalid image file. Please upload a valid image (JPG, PNG, GIF, WEBP) under 5MB.");
                }

                // Upload new image
                var newImageUrl = await _firebaseStorageService.UploadImageAsync(productCreateDto.ImageFile, "products");

                // Delete old image if it exists
                if (!string.IsNullOrEmpty(existingProduct.ImageUrl))
                {
                    await _firebaseStorageService.DeleteImageAsync(existingProduct.ImageUrl);
                }

                existingProduct.ImageUrl = newImageUrl;
            }
            else if (!string.IsNullOrEmpty(productCreateDto.ImageUrl))
            {
                // If a direct URL is provided and it's different from current, update it
                if (existingProduct.ImageUrl != productCreateDto.ImageUrl)
                {
                    // Delete old image if it exists
                    if (!string.IsNullOrEmpty(existingProduct.ImageUrl))
                    {
                        await _firebaseStorageService.DeleteImageAsync(existingProduct.ImageUrl);
                    }
                    existingProduct.ImageUrl = productCreateDto.ImageUrl;
                }
            }

            return await _productRepository.UpdateProduct(existingProduct);
        }
    }
}