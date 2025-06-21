using BusinessObjects.DTOs;
using BusinessObjects.Models;
using DAOs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Services;
using Services.Interfaces;
using System.Collections.Generic;
using System.Security.Claims;
using System.Threading.Tasks;
using System.Linq;

namespace PCPB_backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ProductController : ControllerBase
    {
        private readonly IProductService _productService;

        public ProductController(IProductService productService)
        {
            _productService = productService;
        }

        /// <summary>
        /// Creates a new product (Third Party only)
        /// </summary>
        /// <param name="productDto">The product data to create</param>
        /// <returns>The created product</returns>
        [HttpPost]
        [Authorize]
        [ProducesResponseType(typeof(Product), 201)]
        [ProducesResponseType(400)]
        public async Task<ActionResult<Product>> CreateProduct(ProductDTO productDto)
        {
            try
            {
                var thirdPartyId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value);
                
                var product = new Product
                {
                    ProductName = productDto.ProductName,
                    Description = productDto.Description,
                    Price = productDto.Price,   
                    StockQuantity = productDto.StockQuantity,
                    CategoryId = productDto.CategoryId,
                    ImageUrl = productDto.ImageUrl
                };

                var createdProduct = await _productService.CreateProduct(product, 1);
                return CreatedAtAction(nameof(GetProductById), new { id = createdProduct.ProductId }, createdProduct);
            }
            catch (System.Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Gets a product by ID
        /// </summary>
        /// <param name="id">The product ID</param>
        /// <returns>The product</returns>
        [HttpGet("{id}")]
        [ProducesResponseType(typeof(Product), 200)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<Product>> GetProductById(int id)
        {
            try
            {
                var product = await _productService.GetProductById(id);
                return Ok(product);
            }
            catch (System.Exception ex)
            {
                return NotFound(ex.Message);
            }
        }

        /// <summary>
        /// Gets all products
        /// </summary>
        /// <returns>List of all products with company names</returns>
        [HttpGet]
        [ProducesResponseType(typeof(List<ProductWithCompanyDTO>), 200)]
        public async Task<ActionResult<List<ProductWithCompanyDTO>>> GetAllProducts()
        {
            var products = await _productService.GetAllProducts();
            var productDtos = products.Select(p => new ProductWithCompanyDTO
            {
                ProductId = p.ProductId,
                ProductName = p.ProductName,
                Description = p.Description,
                Price = p.Price,
                StockQuantity = p.StockQuantity,
                ImageUrl = p.ImageUrl,
                Status = p.Status,
                CreatedAt = p.CreatedAt,
                UpdatedAt = p.UpdatedAt,
                CompanyName = p.ThirdParty?.CompanyName
            }).ToList();
            
            return Ok(productDtos);
        }

        /// <summary>
        /// Gets all products for the current third party
        /// </summary>
        /// <returns>List of products</returns>
        [HttpGet("my-products")]
        [Authorize]
        [ProducesResponseType(typeof(List<Product>), 200)]
        public async Task<ActionResult<List<Product>>> GetMyProducts()
        {
            var thirdPartyId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value);
            var products = await _productService.GetProductsByThirdParty(1);
            return Ok(products);
        }

        /// <summary>
        /// Updates a product (Third Party only)
        /// </summary>
        /// <param name="id">The product ID</param>
        /// <param name="productDto">The updated product data</param>
        /// <returns>The updated product</returns>
        [HttpPut("{id}")]
        [Authorize]
        [ProducesResponseType(typeof(Product), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<Product>> UpdateProduct(int id, ProductDTO productDto)
        {
            try
            {
                var thirdPartyId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value);

                var product = new Product
                {
                    ProductId = id,
                    ProductName = productDto.ProductName,
                    Description = productDto.Description,
                    Price = productDto.Price,
                    StockQuantity = productDto.StockQuantity,
                    CategoryId = productDto.CategoryId,
                    ImageUrl = productDto.ImageUrl
                };

                var updatedProduct = await _productService.UpdateProduct(product, 1);
                return Ok(updatedProduct);
            }
            catch (System.Exception ex)
            {
                return NotFound(ex.Message);
            }
        }

        /// <summary>
        /// Deletes a product (Third Party only)
        /// </summary>
        /// <param name="id">The product ID</param>
        /// <returns>True if deleted successfully</returns>
        [HttpDelete("{id}")]
        [Authorize]
        [ProducesResponseType(typeof(bool), 200)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<bool>> DeleteProduct(int id)
        {
            try
            {
                var thirdPartyId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value);
                var result = await _productService.DeleteProduct(id, 1);
                return Ok(new { success = result });
            }
            catch (System.Exception ex)
            {
                return NotFound(ex.Message);
            }
        }

        /// <summary>
        /// Search products by name
        /// </summary>
        /// <param name="productName">The product name to search for</param>
        /// <returns>List of products matching the search criteria</returns>
        [HttpGet("search")]
        [ProducesResponseType(typeof(List<ProductWithCompanyDTO>), 200)]
        public async Task<ActionResult<List<ProductWithCompanyDTO>>> SearchProducts([FromQuery] string productName)
        {
            var products = await _productService.SearchProductsByName(productName);
            var productDtos = products.Select(p => new ProductWithCompanyDTO
            {
                ProductId = p.ProductId,
                ProductName = p.ProductName,
                Description = p.Description,
                Price = p.Price,
                StockQuantity = p.StockQuantity,
                ImageUrl = p.ImageUrl,
                Status = p.Status,
                CreatedAt = p.CreatedAt,
                UpdatedAt = p.UpdatedAt,
                CompanyName = p.ThirdParty?.CompanyName
            }).ToList();
            
            return Ok(productDtos);
        }
    }
} 