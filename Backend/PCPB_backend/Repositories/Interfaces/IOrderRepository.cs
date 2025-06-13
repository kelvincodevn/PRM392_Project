using BusinessObjects.Models;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using DAOs;


namespace Repositories.Interfaces
{
    public interface IOrderRepository : IGenericRepository<Order>
    {
        // Get orders with pagination and filtering
        Task<(List<Order> Orders, int TotalCount)> GetOrdersAsync(
            int pageNumber, 
            int pageSize, 
            string orderStatus = null,
            DateTime? startDate = null,
            DateTime? endDate = null);

        // Get order by ID with related data
        Task<Order> GetOrderByIdAsync(int id);

        // Get orders by customer ID
        Task<List<Order>> GetOrdersByCustomerIdAsync(int customerId);

        // Get orders by staff ID
        Task<List<Order>> GetOrdersByStaffIdAsync(int staffId);

        // Get orders by third party ID
        Task<List<Order>> GetOrdersByThirdPartyIdAsync(int thirdPartyId);

        // Create new order
        Task<Order> CreateOrderAsync(Order order);

        // Update order
        Task<Order> UpdateOrderAsync(Order order);

        // Update order status
        Task<bool> UpdateOrderStatusAsync(int orderId, string status);

        // Soft delete order
        Task<bool> SoftDeleteOrderAsync(int orderId);

        // Get order statistics
        //Task<OrderStatisticsDTO> GetOrderStatisticsAsync(
        //    DateTime? startDate = null,
        //    DateTime? endDate = null);

        // Check if order exists
        Task<bool> OrderExistsAsync(int id);

        // Check if order belongs to customer
        Task<bool> IsOrderBelongsToCustomerAsync(int orderId, int customerId);

        // Check if order belongs to staff
        Task<bool> IsOrderBelongsToStaffAsync(int orderId, int staffId);

        // Check if order belongs to third party
        Task<bool> IsOrderBelongsToThirdPartyAsync(int orderId, int thirdPartyId);
    }
} 