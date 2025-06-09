using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using BusinessObjects.DTOs.Cart;

namespace Services.Interfaces
{
    public interface ICartService
    {
        Task<CartDTO> GetCartByUserId(int userId);
        Task<CartDTO> AddItemToCart(int userId, int productId, int quantity);
        Task<CartDTO> UpdateCartItem(int userId, int cartItemId, int quantity);
        Task<bool> RemoveCartItem(int userId, int cartItemId);
        Task<bool> ClearCart(int userId);
    }
}