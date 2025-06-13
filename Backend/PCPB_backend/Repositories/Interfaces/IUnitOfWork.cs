using BusinessObjects.Models;
using DAOs;

namespace Repositories.Interfaces
{
    public interface IUnitOfWork : IDisposable
    {
        IGenericRepository<User> Users { get; }
        IGenericRepository<Product> Products { get; }
        IGenericRepository<Order> Orders { get; }
        IGenericRepository<OrderItem> OrderItems { get; }
        ICartRepository Carts { get; }
        ICartItemRepository CartItems { get; }
        IGenericRepository<Category> Categories { get; }
        IGenericRepository<Staff> Staffs { get; }
        IGenericRepository<ThirdParty> ThirdParties { get; }
        IGenericRepository<Commission> Commissions { get; }
        IGenericRepository<Payment> Payments { get; }
        IGenericRepository<Subscription> Subscriptions { get; }
        IGenericRepository<SubscriptionPlan> SubscriptionPlans { get; }
        
        Task<int> SaveChangesAsync();
    }
}
