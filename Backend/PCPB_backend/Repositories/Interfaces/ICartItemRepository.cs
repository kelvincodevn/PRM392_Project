using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using DAOs;
using Repositories.Interfaces;

namespace Repositories.Interfaces
{
    public interface ICartItemRepository : IGenericRepository<CartItem>
    {
        Task<List<CartItem>> GetCartItemsByCartId(int cartId);
        Task<CartItem> GetCartItemById(int cartItemId);
        Task<CartItem> AddCartItem(CartItem cartItem);
        Task<CartItem> UpdateCartItem(CartItem cartItem);
        Task<bool> DeleteCartItem(int cartItemId);
    }
}