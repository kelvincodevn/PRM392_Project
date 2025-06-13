using DAOs;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Repositories.Interfaces
{
    public interface IOrderItemRepository : IGenericRepository<OrderItem>
    {
        // Get order items by order ID
        Task<List<OrderItem>> GetOrderItemsByOrderIdAsync(int orderId);

        // Get order item by ID
        Task<OrderItem> GetOrderItemByIdAsync(int id);

        // Create new order item
        Task<OrderItem> CreateOrderItemAsync(OrderItem orderItem);

        // Update order item
        Task<OrderItem> UpdateOrderItemAsync(OrderItem orderItem);

        // Update order item quantity
        Task<bool> UpdateOrderItemQuantityAsync(int orderItemId, int quantity);

        // Delete order item
        Task<bool> DeleteOrderItemAsync(int orderItemId);

        // Get order item statistics
        //Task<OrderItemStatisticsDTO> GetOrderItemStatisticsAsync(int orderId);

        // Check if order item exists
        Task<bool> OrderItemExistsAsync(int id);

        // Check if order item belongs to order
        Task<bool> IsOrderItemBelongsToOrderAsync(int orderItemId, int orderId);

        // Check if order item belongs to third party
        Task<bool> IsOrderItemBelongsToThirdPartyAsync(int orderItemId, int thirdPartyId);

        // Get order items by third party ID
        Task<List<OrderItem>> GetOrderItemsByThirdPartyIdAsync(int thirdPartyId);
    }
} 