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

            // Ensure cart items is initialized as an empty collection, not null
            if (cart.CartItems == null)
            {
                cart.CartItems = new List<CartItem>();
            }

            var cartDto = _mapper.Map<CartDTO>(cart);
            
            // Ensure cartDto.CartItems is an empty list, not a list with default values
            if (cartDto.CartItems == null || !cartDto.CartItems.Any())
            {
                cartDto.CartItems = new List<CartItemDTO>();
            }
            
            return cartDto;
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

            return await _unitOfWork.CartItems.DeleteCartItem(cartItemId);
        }

        public async Task<bool> ClearCart(int userId)
        {
            var cart = await _unitOfWork.Carts.GetCartByUserId(userId);
            if (cart == null)
                return false;

            // Get all cart items for this cart
            var cartItems = await _unitOfWork.CartItems.GetCartItemsByCartId(cart.CartId);
            
            // Delete each cart item
            foreach (var item in cartItems)
            {
                await _unitOfWork.CartItems.DeleteCartItem(item.CartItemId);
            }
            
            // Clear the in-memory collection
            cart.CartItems.Clear();
            
            // Update the cart to reflect the changes
            await _unitOfWork.Carts.UpdateCart(cart);
            
            return true;
        }
    }
}

