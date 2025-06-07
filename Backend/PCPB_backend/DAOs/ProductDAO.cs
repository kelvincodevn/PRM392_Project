using BusinessObjects.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DAOs
{
    public class ProductDAO
    {
        private readonly PCPBContext _context;

        public ProductDAO(PCPBContext context)
        {
            _context = context;
        }

        // Create
        public async Task<Product> CreateProduct(Product product)
        {
            if (product == null)
            {
                throw new ArgumentNullException(nameof(product));
            }

            await _context.Products.AddAsync(product);
            await _context.SaveChangesAsync();
            return product;
        }

        // Read
        public async Task<Product> GetProductById(int id)
        {
            return await _context.Products
                .Include(p => p.Category)
                .Include(p => p.ThirdParty)
                .FirstOrDefaultAsync(p => p.ProductId == id);
        }

        public async Task<List<Product>> GetAllProducts()
        {
            return await _context.Products
                .Include(p => p.Category)
                .Include(p => p.ThirdParty)
                .ToListAsync();
        }

        public async Task<List<Product>> GetProductsByThirdParty(int thirdPartyId)
        {
            return await _context.Products
                .Include(p => p.Category)
                .Include(p => p.ThirdParty)
                .Where(p => p.ThirdPartyId == thirdPartyId)
                .ToListAsync();
        }

        // Update
        public async Task<Product> UpdateProduct(Product product)
        {
            if (product == null)
            {
                throw new ArgumentNullException(nameof(product));
            }

            _context.Entry(product).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return product;
        }

        // Delete
        public async Task<bool> DeleteProduct(int id)
        {
            var product = await _context.Products.FindAsync(id);
            if (product == null)
                return false;

            _context.Products.Remove(product);
            await _context.SaveChangesAsync();
            return true;
        }

        // Additional helper methods
        public async Task<bool> ProductExists(int id)
        {
            return await _context.Products.AnyAsync(p => p.ProductId == id);
        }

        public async Task<bool> IsProductOwnedByThirdParty(int productId, int thirdPartyId)
        {
            return await _context.Products
                .AnyAsync(p => p.ProductId == productId && p.ThirdPartyId == thirdPartyId);
        }
    }
} 