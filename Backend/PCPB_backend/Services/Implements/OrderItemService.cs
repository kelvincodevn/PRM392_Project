using BusinessObjects.DTOs;
using BusinessObjects.Models;
using Repositories.Interfaces;
using Services.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using DAOs;

namespace Services.Implements
{
    public class OrderItemService : IOrderItemService
    {
        private readonly IUnitOfWork _unitOfWork;
        private readonly IOrderItemRepository _orderItemRepository;
        private readonly IOrderRepository _orderRepository;
        private readonly IProductRepository _productRepository;

        public OrderItemService(
            IUnitOfWork unitOfWork,
            IOrderItemRepository orderItemRepository,
            IOrderRepository orderRepository,
            IProductRepository productRepository)
        {
            _unitOfWork = unitOfWork;
            _orderItemRepository = orderItemRepository;
            _orderRepository = orderRepository;
            _productRepository = productRepository;
        }

        public async Task<List<OrderItemDTO>> GetOrderItemsByOrderIdAsync(int orderId)
        {
            if (!await _orderRepository.OrderExistsAsync(orderId))
                throw new Exception("Order not found");

            var orderItems = await _orderItemRepository.GetOrderItemsByOrderIdAsync(orderId);
            return orderItems.Select(oi => MapToOrderItemDTO(oi)).ToList();
        }

        public async Task<OrderItemDTO> GetOrderItemByIdAsync(int id)
        {
            var orderItem = await _orderItemRepository.GetOrderItemByIdAsync(id);
            if (orderItem == null)
                throw new Exception("Order item not found");

            return MapToOrderItemDTO(orderItem);
        }

        public async Task<OrderItemDTO> CreateOrderItemAsync(int orderId, CreateOrderItemDTO orderItemDto)
        {
            if (!await _orderRepository.OrderExistsAsync(orderId))
                throw new Exception("Order not found");

            if (!await CanOrderItemBeUpdatedAsync(orderId, 0))
                throw new Exception("Cannot add items to order in its current status");

            if (!await ValidateOrderItemAsync(orderItemDto))
                throw new Exception("Invalid order item data");

            var product = await _productRepository.GetProductById(orderItemDto.ProductId);
            if (product == null)
                throw new Exception("Product not found");

            if (product.StockQuantity < orderItemDto.Quantity)
                throw new Exception($"Insufficient stock for product {product.ProductName}");

            var orderItem = new OrderItem
            {
                OrderId = orderId,
                ProductId = orderItemDto.ProductId,
                Quantity = orderItemDto.Quantity,
                PriceAtOrder = product.Price,
                Subtotal = product.Price * orderItemDto.Quantity,
                ThirdPartyId = product.ThirdPartyId
            };

            var createdOrderItem = await _orderItemRepository.CreateOrderItemAsync(orderItem);

            // Update product stock
            product.StockQuantity -= orderItemDto.Quantity;
            await _productRepository.UpdateProduct(product);

            // Update order totals
            await UpdateOrderTotalsAsync(orderId);

            await _unitOfWork.SaveChangesAsync();
            return MapToOrderItemDTO(createdOrderItem);
        }

        public async Task<OrderItemDTO> UpdateOrderItemAsync(int orderId, int orderItemId, UpdateOrderItemDTO orderItemDto)
        {
            if (!await _orderRepository.OrderExistsAsync(orderId))
                throw new Exception("Order not found");

            if (!await _orderItemRepository.IsOrderItemBelongsToOrderAsync(orderItemId, orderId))
                throw new Exception("Order item not found in this order");

            if (!await CanOrderItemBeUpdatedAsync(orderId, orderItemId))
                throw new Exception("Cannot update order item in its current status");

            var orderItem = await _orderItemRepository.GetOrderItemByIdAsync(orderItemId);
            var product = await _productRepository.GetProductById(orderItem.ProductId);

            // Calculate stock difference
            int stockDifference = orderItem.Quantity - orderItemDto.Quantity;
            if (product.StockQuantity + stockDifference < 0)
                throw new Exception($"Insufficient stock for product {product.ProductName}");

            // Update order item
            orderItem.Quantity = orderItemDto.Quantity;
            orderItem.Subtotal = orderItem.PriceAtOrder * orderItemDto.Quantity;

            var updatedOrderItem = await _orderItemRepository.UpdateOrderItemAsync(orderItem);

            // Update product stock
            product.StockQuantity += stockDifference;
            await _productRepository.UpdateProduct(product);

            // Update order totals
            await UpdateOrderTotalsAsync(orderId);

            await _unitOfWork.SaveChangesAsync();
            return MapToOrderItemDTO(updatedOrderItem);
        }

        public async Task<OrderItemDTO> UpdateOrderItemQuantityAsync(int orderId, int orderItemId, int quantity)
        {
            if (quantity <= 0)
                throw new Exception("Quantity must be greater than zero");

            var updateDto = new UpdateOrderItemDTO { Quantity = quantity };
            return await UpdateOrderItemAsync(orderId, orderItemId, updateDto);
        }

        public async Task<bool> DeleteOrderItemAsync(int orderId, int orderItemId)
        {
            if (!await _orderRepository.OrderExistsAsync(orderId))
                throw new Exception("Order not found");

            if (!await _orderItemRepository.IsOrderItemBelongsToOrderAsync(orderItemId, orderId))
                throw new Exception("Order item not found in this order");

            if (!await CanOrderItemBeDeletedAsync(orderId, orderItemId))
                throw new Exception("Cannot delete order item in its current status");

            var orderItem = await _orderItemRepository.GetOrderItemByIdAsync(orderItemId);
            var product = await _productRepository.GetProductById(orderItem.ProductId);

            // Restore product stock
            product.StockQuantity += orderItem.Quantity;
            await _productRepository.UpdateProduct(product);

            // Delete order item
            var result = await _orderItemRepository.DeleteOrderItemAsync(orderItemId);

            // Update order totals
            await UpdateOrderTotalsAsync(orderId);

            await _unitOfWork.SaveChangesAsync();
            return result;
        }

        //public async Task<OrderItemStatisticsDTO> GetOrderItemStatisticsAsync(int orderId)
        //{
        //    if (!await _orderRepository.OrderExistsAsync(orderId))
        //        throw new Exception("Order not found");

        //    return await _orderItemRepository.GetOrderItemStatisticsAsync(orderId);
        //}

        public async Task<bool> ValidateOrderItemAsync(CreateOrderItemDTO orderItemDto)
        {
            if (orderItemDto == null || orderItemDto.Quantity <= 0)
                return false;

            var product = await _productRepository.GetProductById(orderItemDto.ProductId);
            return product != null && product.StockQuantity >= orderItemDto.Quantity;
        }

        public async Task<decimal> CalculateOrderItemSubtotalAsync(int productId, int quantity)
        {
            var product = await _productRepository.GetProductById(productId);
            if (product == null)
                throw new Exception("Product not found");

            return product.Price * quantity;
        }

        public async Task<bool> CanOrderItemBeUpdatedAsync(int orderId, int orderItemId)
        {
            var order = await _orderRepository.GetOrderByIdAsync(orderId);
            return order != null && 
                   (order.OrderStatus == "Pending" || order.OrderStatus == "Processing");
        }

        public async Task<bool> CanOrderItemBeDeletedAsync(int orderId, int orderItemId)
        {
            var order = await _orderRepository.GetOrderByIdAsync(orderId);
            return order != null && order.OrderStatus == "Pending";
        }

        public async Task<List<OrderItemDTO>> GetOrderItemsByThirdPartyIdAsync(int thirdPartyId)
        {
            var orderItems = await _orderItemRepository.GetOrderItemsByThirdPartyIdAsync(thirdPartyId);
            return orderItems.Select(oi => MapToOrderItemDTO(oi)).ToList();
        }

        private async Task UpdateOrderTotalsAsync(int orderId)
        {
            var orderItems = await _orderItemRepository.GetOrderItemsByOrderIdAsync(orderId);
            var totalAmount = orderItems.Sum(oi => oi.Subtotal);
            var shippingFee = totalAmount > 1000000 ? 0 : 30000;
            var finalAmount = totalAmount + shippingFee;

            var order = await _orderRepository.GetOrderByIdAsync(orderId);
            order.TotalAmount = totalAmount;
            order.ShippingFee = shippingFee;
            order.FinalAmount = finalAmount;
            await _orderRepository.UpdateOrderAsync(order);
        }

        private OrderItemDTO MapToOrderItemDTO(OrderItem orderItem)
        {
            return new OrderItemDTO
            {
                OrderItemId = orderItem.OrderItemId,
                OrderId = orderItem.OrderId,
                ProductId = orderItem.ProductId,
                Quantity = orderItem.Quantity,
                PriceAtOrder = orderItem.PriceAtOrder,
                Subtotal = orderItem.Subtotal,
                ThirdPartyId = orderItem.ThirdPartyId,
                ProductName = orderItem.Product?.ProductName,
                ProductImage = orderItem.Product?.ImageUrl,
                ThirdPartyName = orderItem.ThirdParty?.CompanyName
            };
        }
    }
} 