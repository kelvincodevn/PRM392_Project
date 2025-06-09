using System;
using System.Security.Claims;
using System.Threading.Tasks;
using BusinessObjects.DTOs.Cart;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Services;
using Services.Interfaces;

namespace PCPB_backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class CartController : ControllerBase
    {
        private readonly ICartService _cartService;

        public CartController(ICartService cartService)
        {
            _cartService = cartService;
        }

        /// <summary>
        /// Gets the current user's cart
        /// </summary>
        /// <returns>The user's cart</returns>
        [HttpGet]
        [ProducesResponseType(typeof(CartDTO), 200)]
        public async Task<ActionResult<CartDTO>> GetCart()
        {
            try
            {
                var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value);
                var cart = await _cartService.GetCartByUserId(userId);
                return Ok(cart);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Adds an item to the cart
        /// </summary>
        /// <param name="productId">Product ID</param>
        /// <param name="quantity">Quantity to add</param>
        /// <returns>The updated cart</returns>
        [HttpPost("items")]
        [ProducesResponseType(typeof(CartDTO), 200)]
        [ProducesResponseType(400)]
        public async Task<ActionResult<CartDTO>> AddItemToCart(int productId, int quantity)
        {
            try
            {
                if (quantity <= 0)
                    return BadRequest("Quantity must be greater than zero");

                var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value);
                var cart = await _cartService.AddItemToCart(userId, productId, quantity);
                return Ok(cart);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Updates a cart item quantity
        /// </summary>
        /// <param name="cartItemId">Cart item ID</param>
        /// <param name="quantity">New quantity</param>
        /// <returns>The updated cart</returns>
        [HttpPut("items/{cartItemId}")]
        [ProducesResponseType(typeof(CartDTO), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<CartDTO>> UpdateCartItem(int cartItemId, int quantity)
        {
            try
            {
                if (quantity <= 0)
                    return BadRequest("Quantity must be greater than zero");

                var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value);
                var cart = await _cartService.UpdateCartItem(userId, cartItemId, quantity);
                return Ok(cart);
            }
            catch (Exception ex)
            {
                return ex.Message.Contains("not found") ? NotFound(ex.Message) : BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Removes an item from the cart
        /// </summary>
        /// <param name="cartItemId">Cart item ID to remove</param>
        /// <returns>Success status</returns>
        [HttpDelete("items/{cartItemId}")]
        [ProducesResponseType(204)]
        [ProducesResponseType(404)]
        public async Task<ActionResult> RemoveCartItem(int cartItemId)
        {
            var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value);
            var result = await _cartService.RemoveCartItem(userId, cartItemId);
            
            if (!result)
                return NotFound("Cart item not found");
                
            return NoContent();
        }

        /// <summary>
        /// Clears all items from the cart
        /// </summary>
        /// <returns>Success status</returns>
        [HttpDelete("clear")]
        [ProducesResponseType(204)]
        [ProducesResponseType(404)]
        public async Task<ActionResult> ClearCart()
        {
            var userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value);
            var result = await _cartService.ClearCart(userId);
            
            if (!result)
                return NotFound("Cart not found");
                
            return NoContent();
        }
    }
}