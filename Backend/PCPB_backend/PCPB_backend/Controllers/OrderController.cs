using BusinessObjects.DTOs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Services.Interfaces;
using System;
using System.Security.Claims;
using System.Threading.Tasks;

namespace PCPB_backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    // [Authorize] // Temporarily comment out for testing
    public class OrderController : ControllerBase
    {
        private readonly IOrderService _orderService;

        public OrderController(IOrderService orderService)
        {
            _orderService = orderService;
        }

        /// <summary>
        /// Gets a paginated list of orders with optional filtering
        /// </summary>
        [HttpGet]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "Admin,Staff")] // Comment out for testing
        [ProducesResponseType(typeof((List<OrderDTO> Orders, int TotalCount)), 200)]
        public async Task<ActionResult<(List<OrderDTO> Orders, int TotalCount)>> GetOrders(
            [FromQuery] int pageNumber = 1,
            [FromQuery] int pageSize = 10,
            [FromQuery] string orderStatus = null,
            [FromQuery] DateTime? startDate = null,
            [FromQuery] DateTime? endDate = null)
        {
            try
            {
                var result = await _orderService.GetOrdersAsync(
                    pageNumber, pageSize, orderStatus, startDate, endDate);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Gets an order by ID
        /// </summary>
        [HttpGet("{id}")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize] // Comment out for testing, if present on specific method
        [ProducesResponseType(typeof(OrderDTO), 200)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<OrderDTO>> GetOrderById(int id)
        {
            try
            {
                var order = await _orderService.GetOrderByIdAsync(id);
                return Ok(order);
            }
            catch (Exception ex)
            {
                return NotFound(ex.Message);
            }
        }

        /// <summary>
        /// Gets orders for the current customer
        /// </summary>
        [HttpGet("my-orders")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "Customer")] // Comment out for testing
        [ProducesResponseType(typeof(List<OrderDTO>), 200)]
        public async Task<ActionResult<List<OrderDTO>>> GetMyOrders()
        {
            try
            {
                // For testing without authorization, use a default customer ID
                int customerId;
                var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier);
                if (userIdClaim != null)
                {
                    customerId = int.Parse(userIdClaim.Value);
                }
                else
                {
                    // Use a default customer ID for testing
                    customerId = 1; // Assuming customer with ID 1 exists
                }

                var orders = await _orderService.GetOrdersByCustomerIdAsync(customerId);
                return Ok(orders);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Gets orders assigned to the current staff
        /// </summary>
        [HttpGet("my-deliveries")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "Staff")] // Comment out for testing
        [ProducesResponseType(typeof(List<OrderDTO>), 200)]
        public async Task<ActionResult<List<OrderDTO>>> GetMyDeliveries()
        {
            try
            {
                // Hard code staff ID to 4 for testing
                var staffId = 4;
                var orders = await _orderService.GetOrdersByStaffIdAsync(staffId);
                return Ok(orders);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Gets orders for the current third party
        /// </summary>
        [HttpGet("my-sales")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "ThirdParty")] // Comment out for testing
        [ProducesResponseType(typeof(List<OrderDTO>), 200)]
        public async Task<ActionResult<List<OrderDTO>>> GetMySales()
        {
            try
            {
                var thirdPartyId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value);
                var orders = await _orderService.GetOrdersByThirdPartyIdAsync(thirdPartyId);
                return Ok(orders);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Creates a new order
        /// </summary>
        [HttpPost]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "Customer")] // Comment out for testing
        [ProducesResponseType(typeof(OrderDTO), 201)]
        [ProducesResponseType(400)]
        public async Task<ActionResult<OrderDTO>> CreateOrder(CreateOrderDTO orderDto)
        {
            try
            {
                var customerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value);
                var order = await _orderService.CreateOrderAsync(orderDto, customerId);
                return CreatedAtAction(nameof(GetOrderById), new { id = order.OrderId }, order);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Updates an order
        /// </summary>
        [HttpPatch("{id}")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "Admin,Staff")] // Comment out for testing
        [ProducesResponseType(typeof(OrderDTO), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<OrderDTO>> UpdateOrder(int id, UpdateOrderDTO orderDto)
        {
            try
            {
                var order = await _orderService.UpdateOrderAsync(id, orderDto);
                return Ok(order);
            }
            catch (Exception ex)
            {
                return ex.Message.Contains("not found") ? NotFound(ex.Message) : BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Updates order status
        /// </summary>
        [HttpPatch("{id}/status")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "Admin,Staff")] // Comment out for testing
        [ProducesResponseType(typeof(bool), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<bool>> UpdateOrderStatus(int id, [FromBody] OrderStatusDTO statusDto)
        {
            try
            {
                var result = await _orderService.UpdateOrderStatusAsync(id, statusDto.OrderStatus);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return ex.Message.Contains("not found") ? NotFound(ex.Message) : BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Cancels an order
        /// </summary>
        [HttpDelete("{id}")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "Customer")] // Comment out for testing
        [ProducesResponseType(typeof(bool), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<bool>> CancelOrder(int id)
        {
            try
            {
                var customerId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value);
                var result = await _orderService.CancelOrderAsync(id, customerId);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return ex.Message.Contains("not found") ? NotFound(ex.Message) : BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Gets order statistics
        /// </summary>
        [HttpGet("statistics")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize(Roles = "Admin")] // Comment out for testing
        [ProducesResponseType(typeof(OrderStatisticsDTO), 200)]
        public async Task<ActionResult<OrderStatisticsDTO>> GetOrderStatistics(
            [FromQuery] DateTime? startDate = null,
            [FromQuery] DateTime? endDate = null)
        {
            try
            {
                var statistics = await _orderService.GetOrderStatisticsAsync(startDate, endDate);
                return Ok(statistics);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Gets order tracking information
        /// </summary>
        [HttpGet("{id}/tracking")]
        [AllowAnonymous] // Allow anonymous access for testing
        // [Authorize] // Comment out for testing, if present on specific method
        [ProducesResponseType(typeof(OrderTrackingDTO), 200)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<OrderTrackingDTO>> GetOrderTracking(int id)
        {
            try
            {
                var tracking = await _orderService.GetOrderTrackingAsync(id);
                return Ok(tracking);
            }
            catch (Exception ex)
            {
                return NotFound(ex.Message);
            }
        }
    }
} 