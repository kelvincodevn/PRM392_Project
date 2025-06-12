using BusinessObjects.Models;
using DAOs;
using Repositories.Interfaces;

namespace Repositories.Implements
{
    public class UnitOfWork : IUnitOfWork
    {
        private readonly PCPBContext _context;
        private bool _disposed = false;

        // Repository instances
        private IGenericRepository<User> _users;
        private IGenericRepository<Product> _products;
        private IGenericRepository<Order> _orders;
        private IGenericRepository<OrderItem> _orderItems;
        private ICartRepository _carts; // Changed to ICartRepository
        private ICartItemRepository _cartItems;
        private IGenericRepository<Category> _categories;
        private IGenericRepository<Staff> _staffs;
        private IGenericRepository<ThirdParty> _thirdParties;
        private IGenericRepository<Commission> _commissions;
        private IGenericRepository<Payment> _payments;
        private IGenericRepository<Subscription> _subscriptions;
        private IGenericRepository<SubscriptionPlan> _subscriptionPlans;

        public UnitOfWork(PCPBContext context)
        {
            _context = context;
        }

        // Repository properties with lazy loading
        public IGenericRepository<User> Users => _users ??= new GenericRepository<User>(_context);
        public IGenericRepository<Product> Products => _products ??= new GenericRepository<Product>(_context);
        public IGenericRepository<Order> Orders => _orders ??= new GenericRepository<Order>(_context);
        public IGenericRepository<OrderItem> OrderItems => _orderItems ??= new GenericRepository<OrderItem>(_context);
        public ICartRepository Carts => _carts ??= new CartRepository(_context); // Changed return type and implementation
        public ICartItemRepository CartItems => _cartItems ??= new CartItemRepository(_context);
        public IGenericRepository<Category> Categories => _categories ??= new GenericRepository<Category>(_context);
        public IGenericRepository<Staff> Staffs => _staffs ??= new GenericRepository<Staff>(_context);
        public IGenericRepository<ThirdParty> ThirdParties => _thirdParties ??= new GenericRepository<ThirdParty>(_context);
        public IGenericRepository<Commission> Commissions => _commissions ??= new GenericRepository<Commission>(_context);
        public IGenericRepository<Payment> Payments => _payments ??= new GenericRepository<Payment>(_context);
        public IGenericRepository<Subscription> Subscriptions => _subscriptions ??= new GenericRepository<Subscription>(_context);
        public IGenericRepository<SubscriptionPlan> SubscriptionPlans => _subscriptionPlans ??= new GenericRepository<SubscriptionPlan>(_context);

        public async Task<int> SaveChangesAsync()
        {
            return await _context.SaveChangesAsync();
        }

        protected virtual void Dispose(bool disposing)
        {
            if (!_disposed && disposing)
            {
                _context.Dispose();
            }
            _disposed = true;
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }
    }
}
