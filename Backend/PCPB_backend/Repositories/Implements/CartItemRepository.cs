using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using DAOs;
using Microsoft.EntityFrameworkCore;
using Repositories.Interfaces;

namespace Repositories.Implements
{
    public class CartItemRepository : GenericRepository<CartItem>, ICartItemRepository
    {
        public CartItemRepository(PCPBContext context) : base(context)
        {
        }

        public async Task<CartItem> AddCartItem(CartItem cartItem)
        {
            cartItem.AddedAt = DateTime.Now;
            await AddAsync(cartItem);
            await _context.SaveChangesAsync();
            return cartItem;
        }

        public async Task<bool> DeleteCartItem(int cartItemId)
        {
            var cartItem = await _context.CartItems.FindAsync(cartItemId);
            if (cartItem == null)
                return false;

            Delete(cartItem);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<CartItem> GetCartItemById(int cartItemId)
        {
            return await _context.CartItems
                .Include(ci => ci.Product)
                .FirstOrDefaultAsync(ci => ci.CartItemId == cartItemId);
        }

        public async Task<List<CartItem>> GetCartItemsByCartId(int cartId)
        {
            return await _context.CartItems
                .Include(ci => ci.Product)
                .Where(ci => ci.CartId == cartId)
                .ToListAsync();
        }

        public async Task<CartItem> UpdateCartItem(CartItem cartItem)
        {
            _context.Entry(cartItem).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return cartItem;
        }
    }
}