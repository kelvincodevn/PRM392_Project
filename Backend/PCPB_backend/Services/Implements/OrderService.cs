using BusinessObjects.DTOs;
using BusinessObjects.Models;
using DAOs;
using Repositories.Interfaces;
using Services.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore; // For Include and other EF Core methods

namespace Services.Implements
{
    public class OrderService : IOrderService
    {
        private readonly IUnitOfWork _unitOfWork;

        public OrderService(IUnitOfWork unitOfWork)
        {
            _unitOfWork = unitOfWork;
        }

        public async Task<(List<OrderDTO> Orders, int TotalCount)> GetOrdersAsync(
            int pageNumber,
            int pageSize,
            string orderStatus = null,
            DateTime? startDate = null,
            DateTime? endDate = null)
        {
            IQueryable<Order> query = _unitOfWork.Orders.GetQueryable()
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.Product)
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.ThirdParty)
                .Include(o => o.Customer)
                .Where(o => !o.IsDeleted);

            if (!string.IsNullOrEmpty(orderStatus))
            {
                query = query.Where(o => o.OrderStatus == orderStatus);
            }

            if (startDate.HasValue)
            {
                query = query.Where(o => o.OrderDate >= startDate.Value);
            }

            if (endDate.HasValue)
            {
                query = query.Where(o => o.OrderDate <= endDate.Value);
            }

            var totalCount = await query.CountAsync();

            var orders = await query
                .OrderByDescending(o => o.OrderDate)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return (orders.Select(MapToDTO).ToList(), totalCount);
        }

        public async Task<OrderDTO> GetOrderByIdAsync(int id)
        {
            var order = await _unitOfWork.Orders.GetQueryable()
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.Product)
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.ThirdParty)
                .Include(o => o.Customer)
                .FirstOrDefaultAsync(o => o.OrderId == id && !o.IsDeleted);

            if (order == null)
            {
                throw new Exception("Order not found.");
            }
            return MapToDTO(order);
        }

        public async Task<List<OrderDTO>> GetOrdersByCustomerIdAsync(int customerId)
        {
            var orders = await _unitOfWork.Orders.GetQueryable()
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.Product)
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.ThirdParty)
                .Include(o => o.Customer)
                .Where(o => o.CustomerId == customerId && !o.IsDeleted)
                .ToListAsync();
            return orders.Select(MapToDTO).ToList();
        }

        public async Task<List<OrderDTO>> GetOrdersByStaffIdAsync(int staffId)
        {
            var orders = await _unitOfWork.Orders.GetQueryable()
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.Product)
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.ThirdParty)
                .Include(o => o.Customer)
                .Where(o => o.StaffId == staffId && !o.IsDeleted)
                .ToListAsync();
            return orders.Select(MapToDTO).ToList();
        }

        public async Task<List<OrderDTO>> GetOrdersByThirdPartyIdAsync(int thirdPartyId)
        {
            var orderItems = await _unitOfWork.OrderItems.GetOrderItemsByThirdPartyIdAsync(thirdPartyId);
            var orderIds = orderItems.Select(oi => oi.OrderId).Distinct().ToList();
            var orders = await _unitOfWork.Orders.GetQueryable()
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.Product)
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.ThirdParty)
                .Include(o => o.Customer)
                .Where(o => orderIds.Contains(o.OrderId) && !o.IsDeleted)
                .ToListAsync();
            return orders.Select(MapToDTO).ToList();
        }

        public async Task<OrderDTO> CreateOrderAsync(CreateOrderDTO orderDto, int customerId)
        {
            if (orderDto.OrderItems == null || !orderDto.OrderItems.Any())
            {
                throw new Exception("Order must contain at least one item.");
            }

            // Validate products exist and gather product prices
            foreach (var itemDto in orderDto.OrderItems)
            {
                var product = await _unitOfWork.Products.GetByIdAsync(itemDto.ProductId);
                if (product == null)
                {
                    throw new Exception($"Product with ID {itemDto.ProductId} not found.");
                }
                itemDto.PriceAtOrder = product.Price; // Ensure price is set from product
            }

            var (totalAmount, shippingFee, finalAmount) = await CalculateOrderTotalsAsync(orderDto.OrderItems.ToList());

            var order = new Order
            {
                CustomerId = customerId,
                OrderDate = DateTime.Now,
                TotalAmount = totalAmount,
                ShippingFee = 0,
                FinalAmount = finalAmount,
                OrderStatus = "Pending", // Initial status
                ShippingAddress = orderDto.ShippingAddress,
                StaffId = 4, //deafault 4 for now
                PaymentMethod = orderDto.PaymentMethod,
                PaymentStatus = "Pending", // Initial payment status
                IsDeleted = false,
                CreatedAt = DateTime.Now,
                UpdatedAt = DateTime.Now
            };

            await _unitOfWork.Orders.AddAsync(order);
            await _unitOfWork.SaveChangesAsync(); // Save order to get OrderId

            foreach (var itemDto in orderDto.OrderItems)
            {
                var orderItem = new OrderItem
                {
                    OrderId = order.OrderId,
                    ProductId = itemDto.ProductId,
                    Quantity = itemDto.Quantity,
                    PriceAtOrder = itemDto.PriceAtOrder,
                    Subtotal = itemDto.PriceAtOrder * itemDto.Quantity,
                    ThirdPartyId = 1, // Assuming ThirdPartyId is provided
                    CreatedAt = DateTime.Now,
                    UpdatedAt = DateTime.Now
                };
                await _unitOfWork.OrderItems.AddAsync(orderItem);
            }
            await _unitOfWork.SaveChangesAsync(); // Save order items

            return MapToDTO(order);
        }

        public async Task<OrderDTO> UpdateOrderAsync(int orderId, UpdateOrderDTO orderDto)
        {
            var existingOrder = await _unitOfWork.Orders.GetByIdAsync(orderId);
            if (existingOrder == null)
            {
                throw new Exception("Order not found.");
            }

            if (!await CanOrderBeUpdatedAsync(orderId))
            {
                throw new Exception("Order cannot be updated in its current status.");
            }

            // Update simple properties
            existingOrder.ShippingAddress = orderDto.ShippingAddress;
            existingOrder.PaymentMethod = orderDto.PaymentMethod;
            existingOrder.UpdatedAt = DateTime.Now;

            // Recalculate totals if order items are also updated (this is a simplified approach, a full update would involve more complex item management)
            // For now, assume order items are handled separately or only simple order properties are updated.
            // If order items were to be updated, we'd need to fetch existing order items, compare with new ones, add/update/delete as needed.

            await _unitOfWork.Orders.UpdateAsync(existingOrder);
            await _unitOfWork.SaveChangesAsync();

            return MapToDTO(existingOrder);
        }

        public async Task<bool> UpdateOrderStatusAsync(int orderId, string status)
        {
            var order = await _unitOfWork.Orders.GetByIdAsync(orderId);
            if (order == null)
            {
                throw new Exception("Order not found.");
            }

            order.OrderStatus = status;
            order.UpdatedAt = DateTime.Now;

            await _unitOfWork.Orders.UpdateAsync(order);
            return await _unitOfWork.SaveChangesAsync() > 0;
        }

        public async Task<bool> CancelOrderAsync(int orderId, int customerId)
        {
            var order = await _unitOfWork.Orders.GetByIdAsync(orderId);
            if (order == null)
            {
                throw new Exception("Order not found.");
            }

            if (order.CustomerId != customerId)
            {
                throw new UnauthorizedAccessException("Customer is not authorized to cancel this order.");
            }

            if (!await CanOrderBeCancelledAsync(orderId))
            {
                throw new Exception("Order cannot be cancelled in its current status.");
            }

            order.OrderStatus = "Cancelled";
            order.UpdatedAt = DateTime.Now;

            await _unitOfWork.Orders.UpdateAsync(order);
            return await _unitOfWork.SaveChangesAsync() > 0;
        }

        public async Task<OrderStatisticsDTO> GetOrderStatisticsAsync(DateTime? startDate = null, DateTime? endDate = null)
        {
            IQueryable<Order> query = _unitOfWork.Orders.GetQueryable()
                .Where(o => !o.IsDeleted);

            if (startDate.HasValue)
            {
                query = query.Where(o => o.OrderDate >= startDate.Value);
            }

            if (endDate.HasValue)
            {
                query = query.Where(o => o.OrderDate <= endDate.Value);
            }

            var orders = await query.ToListAsync();

            return new OrderStatisticsDTO
            {
                TotalOrders = orders.Count,
                TotalRevenue = orders.Sum(o => o.FinalAmount),
                PendingOrders = orders.Count(o => o.OrderStatus == "Pending"),
                CompletedOrders = orders.Count(o => o.OrderStatus == "Completed"),
                CancelledOrders = orders.Count(o => o.OrderStatus == "Cancelled")
            };
        }

        public async Task<bool> ValidateOrderAsync(CreateOrderDTO orderDto)
        {
            // Basic validation: check if customer and products exist
            if (orderDto.OrderItems == null || !orderDto.OrderItems.Any())
            {
                return false; // No order items
            }

            foreach (var itemDto in orderDto.OrderItems)
            {
                var productExists = await _unitOfWork.Products.ExistsAsync(p => p.ProductId == itemDto.ProductId);
                if (!productExists)
                {
                    return false; // Product not found
                }
            }
            return true;
        }

        public async Task<(decimal TotalAmount, decimal ShippingFee, decimal FinalAmount)> CalculateOrderTotalsAsync(List<CreateOrderItemDTO> orderItems)
        {
            decimal totalAmount = 0;
            decimal shippingFee = 0; // Simplified, in real app, this could depend on delivery location, etc.

            foreach (var itemDto in orderItems)
            {
                var product = await _unitOfWork.Products.GetByIdAsync(itemDto.ProductId);
                if (product == null)
                {
                    throw new Exception($"Product with ID {itemDto.ProductId} not found for total calculation.");
                }
                totalAmount += product.Price * itemDto.Quantity;
            }

            decimal finalAmount = totalAmount + shippingFee; // No discount or tax logic for now

            return (totalAmount, shippingFee, finalAmount);
        }

        public async Task<bool> CanOrderBeCancelledAsync(int orderId)
        {
            var order = await _unitOfWork.Orders.GetByIdAsync(orderId);
            if (order == null) return false;

            // Only allow cancellation if order is Pending or Processing
            return order.OrderStatus == "Pending" || order.OrderStatus == "Processing";
        }

        public async Task<bool> CanOrderBeUpdatedAsync(int orderId)
        {
            var order = await _unitOfWork.Orders.GetByIdAsync(orderId);
            if (order == null) return false;

            // Only allow update if order is Pending or Processing
            return order.OrderStatus == "Pending" || order.OrderStatus == "Processing";
        }

        public async Task<OrderTrackingDTO> GetOrderTrackingAsync(int orderId)
        {
            var order = await _unitOfWork.Orders.GetByIdAsync(orderId);
            if (order == null)
            {
                throw new Exception("Order not found.");
            }

            // In a real application, you might have a dedicated OrderTracking entity
            // For now, we'll construct it based on the order status and dates.
            return new OrderTrackingDTO
            {
                OrderId = order.OrderId,
                CurrentStatus = order.OrderStatus,
                OrderDate = order.OrderDate,
                DeliveredAt = order.DeliveredAt,
                // Add more tracking details if available, e.g., a list of status changes with timestamps
            };
        }

        private OrderDTO MapToDTO(Order order)
        {
            return new OrderDTO
            {
                OrderId = order.OrderId,
                CustomerId = order.CustomerId,
                CustomerPhone = order.Customer?.PhoneNumber,
                CustomerName = order.Customer?.FullName,
                OrderDate = order.OrderDate,
                TotalAmount = order.TotalAmount,
                ShippingFee = order.ShippingFee,
                FinalAmount = order.FinalAmount,
                OrderStatus = order.OrderStatus,
                ShippingAddress = order.ShippingAddress,
                StaffId = order.StaffId,
                PaymentMethod = order.PaymentMethod,
                PaymentStatus = order.PaymentStatus,
                DeliveredAt = order.DeliveredAt,
                IsDeleted = order.IsDeleted,
                CreatedAt = order.CreatedAt,
                UpdatedAt = order.UpdatedAt,
                OrderItems = order.OrderItems.Select(MapToDTO).ToList()
            };
        }

        private OrderItemDTO MapToDTO(OrderItem orderItem)
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
                CreatedAt = orderItem.CreatedAt,
                UpdatedAt = orderItem.UpdatedAt,
                ProductName = orderItem.Product?.ProductName, // Include product name
                ThirdPartyName = orderItem.ThirdParty?.CompanyName // Include third party name
            };
        }
    }
}
