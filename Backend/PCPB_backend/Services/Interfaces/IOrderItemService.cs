using BusinessObjects.DTOs;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Services.Interfaces
{
    public interface IOrderItemService
    {
        // Get order items by order ID
        Task<List<OrderItemDTO>> GetOrderItemsByOrderIdAsync(int orderId);

        // Get order item by ID
        Task<OrderItemDTO> GetOrderItemByIdAsync(int id);

        // Create new order item
        Task<OrderItemDTO> CreateOrderItemAsync(int orderId, CreateOrderItemDTO orderItemDto);

        // Update order item
        Task<OrderItemDTO> UpdateOrderItemAsync(int orderId, int orderItemId, UpdateOrderItemDTO orderItemDto);

        // Update order item quantity
        Task<OrderItemDTO> UpdateOrderItemQuantityAsync(int orderId, int orderItemId, int quantity);

        // Delete order item
        Task<bool> DeleteOrderItemAsync(int orderId, int orderItemId);

        // Get order item statistics
        //Task<OrderItemStatisticsDTO> GetOrderItemStatisticsAsync(int orderId);

        // Validate order item
        Task<bool> ValidateOrderItemAsync(CreateOrderItemDTO orderItemDto);

        // Calculate order item subtotal
        Task<decimal> CalculateOrderItemSubtotalAsync(int productId, int quantity);

        // Check if order item can be updated
        Task<bool> CanOrderItemBeUpdatedAsync(int orderId, int orderItemId);

        // Check if order item can be deleted
        Task<bool> CanOrderItemBeDeletedAsync(int orderId, int orderItemId);

        // Get order items by third party ID
        Task<List<OrderItemDTO>> GetOrderItemsByThirdPartyIdAsync(int thirdPartyId);
    }
} 