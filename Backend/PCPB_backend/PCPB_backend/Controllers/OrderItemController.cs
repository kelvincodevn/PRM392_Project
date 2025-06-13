using BusinessObjects.DTOs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Services.Interfaces;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace PCPB_backend.Controllers
{
    [Route("api/orders/{orderId}/items")]
    [ApiController]
    // [Authorize] // Temporarily comment out for testing
    public class OrderItemController : ControllerBase
    {
        private readonly IOrderItemService _orderItemService;
        private readonly IOrderService _orderService;

        public OrderItemController(
            IOrderItemService orderItemService,
            IOrderService orderService)
        {
            _orderItemService = orderItemService;
            _orderService = orderService;
        }

        /// <summary>
        /// Gets all items in an order
        /// </summary>
        [HttpGet]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize] // Comment out for testing, if present on specific method
        [ProducesResponseType(typeof(List<OrderItemDTO>), 200)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<List<OrderItemDTO>>> GetOrderItems(int orderId)
        {
            try
            {
                var items = await _orderItemService.GetOrderItemsByOrderIdAsync(orderId);
                return Ok(items);
            }
            catch (Exception ex)
            {
                return NotFound(ex.Message);
            }
        }

        /// <summary>
        /// Gets a specific item in an order
        /// </summary>
        [HttpGet("{itemId}")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize] // Comment out for testing, if present on specific method
        [ProducesResponseType(typeof(OrderItemDTO), 200)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<OrderItemDTO>> GetOrderItem(int orderId, int itemId)
        {
            try
            {
                var item = await _orderItemService.GetOrderItemByIdAsync(itemId);
                if (item.OrderId != orderId)
                    return NotFound("Order item not found in this order");

                return Ok(item);
            }
            catch (Exception ex)
            {
                return NotFound(ex.Message);
            }
        }

        /// <summary>
        /// Adds an item to an order
        /// </summary>
        [HttpPost]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "Customer")] // Comment out for testing
        [ProducesResponseType(typeof(OrderItemDTO), 201)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<OrderItemDTO>> AddOrderItem(
            int orderId,
            CreateOrderItemDTO orderItemDto)
        {
            try
            {
                var item = await _orderItemService.CreateOrderItemAsync(orderId, orderItemDto);
                return CreatedAtAction(
                    nameof(GetOrderItem),
                    new { orderId, itemId = item.OrderItemId },
                    item);
            }
            catch (Exception ex)
            {
                return ex.Message.Contains("not found") ? NotFound(ex.Message) : BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Updates an item in an order
        /// </summary>
        [HttpPatch("{itemId}")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "Customer")] // Comment out for testing
        [ProducesResponseType(typeof(OrderItemDTO), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<OrderItemDTO>> UpdateOrderItem(
            int orderId,
            int itemId,
            UpdateOrderItemDTO orderItemDto)
        {
            try
            {
                var item = await _orderItemService.UpdateOrderItemAsync(orderId, itemId, orderItemDto);
                return Ok(item);
            }
            catch (Exception ex)
            {
                return ex.Message.Contains("not found") ? NotFound(ex.Message) : BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Updates the quantity of an item in an order
        /// </summary>
        [HttpPatch("{itemId}/quantity")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "Customer")] // Comment out for testing
        [ProducesResponseType(typeof(OrderItemDTO), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<OrderItemDTO>> UpdateOrderItemQuantity(
            int orderId,
            int itemId,
            [FromBody] int quantity)
        {
            try
            {
                var item = await _orderItemService.UpdateOrderItemQuantityAsync(orderId, itemId, quantity);
                return Ok(item);
            }
            catch (Exception ex)
            {
                return ex.Message.Contains("not found") ? NotFound(ex.Message) : BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Removes an item from an order
        /// </summary>
        [HttpDelete("{itemId}")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "Customer")] // Comment out for testing
        [ProducesResponseType(typeof(bool), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<bool>> RemoveOrderItem(int orderId, int itemId)
        {
            try
            {
                var result = await _orderItemService.DeleteOrderItemAsync(orderId, itemId);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return ex.Message.Contains("not found") ? NotFound(ex.Message) : BadRequest(ex.Message);
            }
        }

        ///// <summary>
        ///// Gets statistics for items in an order
        ///// </summary>
        //[HttpGet("statistics")]
        //[AllowAnonymous] // Allow anonymous access for testing
        ////[ProducesResponseType(typeof(OrderItemStatisticsDTO), 200)]
        ////[ProducesResponseType(404)]
        //public async Task<ActionResult<OrderItemStatisticsDTO>> GetOrderItemStatistics(int orderId)
        //{
        //    try
        //    {
        //        var statistics = await _orderItemService.GetOrderItemStatisticsAsync(orderId);
        //        return Ok(statistics);
        //    }
        //    catch (Exception ex)
        //    {
        //        return NotFound(ex.Message);
        //    }
        //}
    }
} 