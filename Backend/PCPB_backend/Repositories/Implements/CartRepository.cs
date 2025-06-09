using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using DAOs;
using Microsoft.EntityFrameworkCore;
using Repositories.Interfaces;

namespace Repositories.Implements
{
    public class CartRepository : GenericRepository<Cart>, ICartRepository
    {
        public CartRepository(PCPBContext context) : base(context)
        {
        }

        public async Task<Cart> CreateCart(Cart cart)
        {
            cart.CreatedAt = DateTime.Now;
            cart.UpdatedAt = DateTime.Now;
            await AddAsync(cart);
            await _context.SaveChangesAsync();
            return cart;
        }

        public async Task<bool> DeleteCart(int cartId)
        {
            var cart = await _context.Carts.FindAsync(cartId);
            if (cart == null)
                return false;

            Delete(cart);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<Cart> GetCartByUserId(int userId)
        {
            try
            {
                return await _context.Carts.Include(c => c.CartItems)
                        .ThenInclude(ci => ci.Product)
                    .FirstOrDefaultAsync(c => c.UserId == userId);
            }
            catch (Exception ex)
            {
                throw new Exception("Cart not found");
            }
           
        }

        public async Task<Cart> UpdateCart(Cart cart)
        {
            cart.UpdatedAt = DateTime.Now;
            Update(cart);
            await _context.SaveChangesAsync();
            return cart;
        }
    }
}
