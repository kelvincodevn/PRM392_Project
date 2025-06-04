using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using BusinessObjects.DTOs.Cart;
using BusinessObjects.DTOs.CartItems;
using DAOs;
using Repositories.Interfaces;
using Services.Interfaces;
using AutoMapper;

namespace Services
{
    public class CartService : ICartService
    {
        private readonly IUnitOfWork _unitOfWork;
        private readonly IMapper _mapper;

        public CartService(IUnitOfWork unitOfWork, IMapper mapper)
        {
            _unitOfWork = unitOfWork;
            _mapper = mapper;
        }

        public async Task<CartDTO> GetCartByUserId(int userId)
        {
            var cart = await _unitOfWork.Carts.GetCartByUserId(userId);
            if (cart == null)
            {
                // Create a new cart if one doesn't exist
                cart = new Cart { UserId = userId };
                cart = await _unitOfWork.Carts.CreateCart(cart);
            }

            return _mapper.Map<CartDTO>(cart);
        }

        public async Task<CartDTO> AddItemToCart(int userId, int productId, int quantity)
        {
            var cart = await _unitOfWork.Carts.GetCartByUserId(userId);
            if (cart == null)
            {
                cart = new Cart { UserId = userId };
                cart = await _unitOfWork.Carts.CreateCart(cart);
            }

            var existingItem = cart.CartItems.FirstOrDefault(ci => ci.ProductId == productId);
            if (existingItem != null)
            {
                existingItem.Quantity += quantity;
            }
            else
            {
                var cartItem = new CartItem
                {
                    CartId = cart.CartId,
                    ProductId = productId,
                    Quantity = quantity,
                    AddedAt = DateTime.Now
                };
                cart.CartItems.Add(cartItem);
            }

            await _unitOfWork.Carts.UpdateCart(cart);
            return _mapper.Map<CartDTO>(cart);
        }

        public async Task<CartDTO> UpdateCartItem(int userId, int cartItemId, int quantity)
        {
            var cart = await _unitOfWork.Carts.GetCartByUserId(userId);
            if (cart == null)
                throw new Exception("Cart not found");

            var cartItem = cart.CartItems.FirstOrDefault(ci => ci.CartItemId == cartItemId);
            if (cartItem == null)
                throw new Exception("Cart item not found");

            cartItem.Quantity = quantity;
            await _unitOfWork.Carts.UpdateCart(cart);
            return _mapper.Map<CartDTO>(cart);
        }

        public async Task<bool> RemoveCartItem(int userId, int cartItemId)
        {
            var cart = await _unitOfWork.Carts.GetCartByUserId(userId);
            if (cart == null)
                return false;

            var cartItem = cart.CartItems.FirstOrDefault(ci => ci.CartItemId == cartItemId);
            if (cartItem == null)
                return false;

            _unitOfWork.CartItems.Delete(cartItem);
            await _unitOfWork.SaveChangesAsync();
            return true;
        }

        public async Task<bool> ClearCart(int userId)
        {
            var cart = await _unitOfWork.Carts.GetCartByUserId(userId);
            if (cart == null)
                return false;

            foreach (var item in cart.CartItems.ToList())
            {
                _unitOfWork.CartItems.Delete(item);
            }

            await _unitOfWork.SaveChangesAsync();
            return true;
        }
    }
}

