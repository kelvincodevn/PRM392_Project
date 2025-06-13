using BusinessObjects.DTOs;
using BusinessObjects.Models;
using DAOs;
using Microsoft.EntityFrameworkCore;
using Repositories.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Repositories.Implements
{
    public class OrderRepository : GenericRepository<Order>, IOrderRepository
    {
        private readonly PCPBContext _context;

        public OrderRepository(PCPBContext context) : base(context)
        {
            _context = context;
        }

        public async Task<(List<Order> Orders, int TotalCount)> GetOrdersAsync(
            int pageNumber,
            int pageSize,
            string orderStatus = null,
            DateTime? startDate = null,
            DateTime? endDate = null)
        {
            var query = _context.Orders
                .Include(o => o.Customer)
                .Include(o => o.Staff)
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.Product)
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

            return (orders, totalCount);
        }

        public async Task<Order> GetOrderByIdAsync(int id)
        {
            return await _context.Orders
                .Include(o => o.Customer)
                .Include(o => o.Staff)
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.Product)
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.ThirdParty)
                .FirstOrDefaultAsync(o => o.OrderId == id && !o.IsDeleted);
        }

        public async Task<List<Order>> GetOrdersByCustomerIdAsync(int customerId)
        {
            return await _context.Orders
                .Include(o => o.Staff)
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.Product)
                .Where(o => o.CustomerId == customerId && !o.IsDeleted)
                .OrderByDescending(o => o.OrderDate)
                .ToListAsync();
        }

        public async Task<List<Order>> GetOrdersByStaffIdAsync(int staffId)
        {
            return await _context.Orders
                .Include(o => o.Customer)
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.Product)
                .Where(o => o.StaffId == staffId && !o.IsDeleted)
                .OrderByDescending(o => o.OrderDate)
                .ToListAsync();
        }

        public async Task<List<Order>> GetOrdersByThirdPartyIdAsync(int thirdPartyId)
        {
            return await _context.Orders
                .Include(o => o.Customer)
                .Include(o => o.Staff)
                .Include(o => o.OrderItems)
                    .ThenInclude(oi => oi.Product)
                .Where(o => o.OrderItems.Any(oi => oi.ThirdPartyId == thirdPartyId) && !o.IsDeleted)
                .OrderByDescending(o => o.OrderDate)
                .ToListAsync();
        }

        public async Task<Order> CreateOrderAsync(Order order)
        {
            order.CreatedAt = DateTime.Now;
            order.UpdatedAt = DateTime.Now;
            await _context.Orders.AddAsync(order);
            await _context.SaveChangesAsync();
            return order;
        }

        public async Task<Order> UpdateOrderAsync(Order order)
        {
            order.UpdatedAt = DateTime.Now;
            _context.Entry(order).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return order;
        }

        public async Task<bool> UpdateOrderStatusAsync(int orderId, string status)
        {
            var order = await _context.Orders.FindAsync(orderId);
            if (order == null || order.IsDeleted)
                return false;

            order.OrderStatus = status;
            order.UpdatedAt = DateTime.Now;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> SoftDeleteOrderAsync(int orderId)
        {
            var order = await _context.Orders.FindAsync(orderId);
            if (order == null || order.IsDeleted)
                return false;

            order.IsDeleted = true;
            order.UpdatedAt = DateTime.Now;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<OrderStatisticsDTO> GetOrderStatisticsAsync(
            DateTime? startDate = null,
            DateTime? endDate = null)
        {
            var query = _context.Orders.Where(o => !o.IsDeleted);

            if (startDate.HasValue)
            {
                query = query.Where(o => o.OrderDate >= startDate.Value);
            }

            if (endDate.HasValue)
            {
                query = query.Where(o => o.OrderDate <= endDate.Value);
            }

            var statistics = new OrderStatisticsDTO
            {
                TotalOrders = await query.CountAsync(),
                PendingOrders = await query.CountAsync(o => o.OrderStatus == "Pending"),
                ProcessingOrders = await query.CountAsync(o => o.OrderStatus == "Processing"),
                ShippedOrders = await query.CountAsync(o => o.OrderStatus == "Shipped"),
                DeliveredOrders = await query.CountAsync(o => o.OrderStatus == "Delivered"),
                CancelledOrders = await query.CountAsync(o => o.OrderStatus == "Cancelled"),
                TotalRevenue = await query.SumAsync(o => o.FinalAmount)
            };

            return statistics;
        }

        public async Task<bool> OrderExistsAsync(int id)
        {
            return await _context.Orders.AnyAsync(o => o.OrderId == id && !o.IsDeleted);
        }

        public async Task<bool> IsOrderBelongsToCustomerAsync(int orderId, int customerId)
        {
            return await _context.Orders.AnyAsync(o => 
                o.OrderId == orderId && 
                o.CustomerId == customerId && 
                !o.IsDeleted);
        }

        public async Task<bool> IsOrderBelongsToStaffAsync(int orderId, int staffId)
        {
            return await _context.Orders.AnyAsync(o => 
                o.OrderId == orderId && 
                o.StaffId == staffId && 
                !o.IsDeleted);
        }

        public async Task<bool> IsOrderBelongsToThirdPartyAsync(int orderId, int thirdPartyId)
        {
            return await _context.Orders
                .Include(o => o.OrderItems)
                .AnyAsync(o => 
                    o.OrderId == orderId && 
                    o.OrderItems.Any(oi => oi.ThirdPartyId == thirdPartyId) && 
                    !o.IsDeleted);
        }
    }
} 