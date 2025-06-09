using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using DAOs;

namespace Repositories.Interfaces
{
    public interface ICartRepository : IGenericRepository<Cart>
    {
        Task<Cart> GetCartByUserId(int userId);
        Task<Cart> CreateCart(Cart cart);
        Task<Cart> UpdateCart(Cart cart);
        Task<bool> DeleteCart(int cartId);
    }
}