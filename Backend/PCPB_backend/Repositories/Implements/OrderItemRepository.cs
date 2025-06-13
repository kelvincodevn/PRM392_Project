using BusinessObjects.DTOs;
using DAOs;
using Microsoft.EntityFrameworkCore;
using Repositories.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Repositories.Implements
{
    public class OrderItemRepository : GenericRepository<OrderItem>, IOrderItemRepository
    {
        private readonly PCPBContext _context;

        public OrderItemRepository(PCPBContext context) : base(context)
        {
            _context = context;
        }

        public async Task<List<OrderItem>> GetOrderItemsByOrderIdAsync(int orderId)
        {
            return await _context.OrderItems
                .Include(oi => oi.Product)
                .Include(oi => oi.ThirdParty)
                .Where(oi => oi.OrderId == orderId)
                .ToListAsync();
        }

        public async Task<OrderItem> GetOrderItemByIdAsync(int id)
        {
            return await _context.OrderItems
                .Include(oi => oi.Product)
                .Include(oi => oi.ThirdParty)
                .Include(oi => oi.Order)
                .FirstOrDefaultAsync(oi => oi.OrderItemId == id);
        }

        public async Task<OrderItem> CreateOrderItemAsync(OrderItem orderItem)
        {
            orderItem.CreatedAt = DateTime.Now;
            orderItem.UpdatedAt = DateTime.Now;
            await _context.OrderItems.AddAsync(orderItem);
            await _context.SaveChangesAsync();
            return orderItem;
        }

        public async Task<OrderItem> UpdateOrderItemAsync(OrderItem orderItem)
        {
            orderItem.UpdatedAt = DateTime.Now;
            _context.Entry(orderItem).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return orderItem;
        }

        public async Task<bool> UpdateOrderItemQuantityAsync(int orderItemId, int quantity)
        {
            var orderItem = await _context.OrderItems.FindAsync(orderItemId);
            if (orderItem == null)
                return false;

            orderItem.Quantity = quantity;
            orderItem.Subtotal = orderItem.PriceAtOrder * quantity;
            orderItem.UpdatedAt = DateTime.Now;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> DeleteOrderItemAsync(int orderItemId)
        {
            var orderItem = await _context.OrderItems.FindAsync(orderItemId);
            if (orderItem == null)
                return false;

            _context.OrderItems.Remove(orderItem);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<OrderItemStatisticsDTO> GetOrderItemStatisticsAsync(int orderId)
        {
            var orderItems = await _context.OrderItems
                .Include(oi => oi.Product)
                .Include(oi => oi.ThirdParty)
                .Where(oi => oi.OrderId == orderId)
                .ToListAsync();

            var statistics = new OrderItemStatisticsDTO
            {
                TotalItems = orderItems.Sum(oi => oi.Quantity),
                TotalValue = orderItems.Sum(oi => oi.Subtotal),
                UniqueProducts = orderItems.Select(oi => oi.ProductId).Distinct().Count(),
                UniqueThirdParties = orderItems.Select(oi => oi.ThirdPartyId).Distinct().Count()
            };

            return statistics;
        }

        public async Task<bool> OrderItemExistsAsync(int id)
        {
            return await _context.OrderItems.AnyAsync(oi => oi.OrderItemId == id);
        }

        public async Task<bool> IsOrderItemBelongsToOrderAsync(int orderItemId, int orderId)
        {
            return await _context.OrderItems.AnyAsync(oi => 
                oi.OrderItemId == orderItemId && 
                oi.OrderId == orderId);
        }

        public async Task<bool> IsOrderItemBelongsToThirdPartyAsync(int orderItemId, int thirdPartyId)
        {
            return await _context.OrderItems.AnyAsync(oi => 
                oi.OrderItemId == orderItemId && 
                oi.ThirdPartyId == thirdPartyId);
        }

        public async Task<List<OrderItem>> GetOrderItemsByThirdPartyIdAsync(int thirdPartyId)
        {
            return await _context.OrderItems
                .Include(oi => oi.Product)
                .Include(oi => oi.Order)
                .Where(oi => oi.ThirdPartyId == thirdPartyId)
                .OrderByDescending(oi => oi.CreatedAt)
                .ToListAsync();
        }
    }
} 