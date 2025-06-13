using BusinessObjects.DTOs;
using BusinessObjects.Models;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Services.Interfaces
{
    public interface IOrderService
    {
        // Get orders with pagination and filtering
        Task<(List<OrderDTO> Orders, int TotalCount)> GetOrdersAsync(
            int pageNumber,
            int pageSize,
            string orderStatus = null,
            DateTime? startDate = null,
            DateTime? endDate = null);

        // Get order by ID
        Task<OrderDTO> GetOrderByIdAsync(int id);

        // Get orders by customer ID
        Task<List<OrderDTO>> GetOrdersByCustomerIdAsync(int customerId);

        // Get orders by staff ID
        Task<List<OrderDTO>> GetOrdersByStaffIdAsync(int staffId);

        // Get orders by third party ID
        Task<List<OrderDTO>> GetOrdersByThirdPartyIdAsync(int thirdPartyId);

        // Create new order
        Task<OrderDTO> CreateOrderAsync(CreateOrderDTO orderDto, int customerId);

        // Update order
        Task<OrderDTO> UpdateOrderAsync(int orderId, UpdateOrderDTO orderDto);

        // Update order status
        Task<bool> UpdateOrderStatusAsync(int orderId, string status);

        // Cancel order
        Task<bool> CancelOrderAsync(int orderId, int customerId);

        // Get order statistics
        Task<OrderStatisticsDTO> GetOrderStatisticsAsync(
            DateTime? startDate = null,
            DateTime? endDate = null);

        // Validate order
        Task<bool> ValidateOrderAsync(CreateOrderDTO orderDto);

        // Calculate order totals
        Task<(decimal TotalAmount, decimal ShippingFee, decimal FinalAmount)> CalculateOrderTotalsAsync(
            List<CreateOrderItemDTO> orderItems);

        // Check if order can be cancelled
        Task<bool> CanOrderBeCancelledAsync(int orderId);

        // Check if order can be updated
        Task<bool> CanOrderBeUpdatedAsync(int orderId);

        // Get order tracking information
        Task<OrderTrackingDTO> GetOrderTrackingAsync(int orderId);
    }
} 