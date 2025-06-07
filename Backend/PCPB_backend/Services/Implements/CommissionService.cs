using BusinessObjects.DTOs;
using BusinessObjects.Models;
using DAOs;
using Repositories.Interfaces;
using Services.Interfaces;

namespace Services.Implements
{
    public class CommissionService : ICommissionService
    {
        private readonly IUnitOfWork _unitOfWork;
        private const decimal COMMISSION_PERCENTAGE = 0.05m; // 5%

        public CommissionService(IUnitOfWork unitOfWork)
        {
            _unitOfWork = unitOfWork;
        }

        public async Task<CommissionDTO> CalculateCommission(int orderItemId)
        {
            var orderItem = await _unitOfWork.OrderItems.GetByIdAsync(orderItemId);
            if (orderItem == null)
            {
                throw new Exception("Order item not found");
            }

            // Check if commission already exists
            var existingCommission = await _unitOfWork.Commissions.FindAsync(c => c.OrderItemId == orderItemId);
            if (existingCommission.Any())
            {
                throw new Exception("Commission already calculated for this order item");
            }

            // Calculate commission amount (5% of subtotal)
            var commissionAmount = orderItem.Subtotal * COMMISSION_PERCENTAGE;

            var commission = new Commission
            {
                OrderItemId = orderItemId,
                ThirdPartyId = orderItem.ThirdPartyId,
                CommissionPercentage = COMMISSION_PERCENTAGE,
                CommissionAmount = commissionAmount,
                CalculatedAt = DateTime.Now,
                Status = "Pending"
            };

            await _unitOfWork.Commissions.AddAsync(commission);
            await _unitOfWork.SaveChangesAsync();

            return MapToDTO(commission);
        }

        public async Task<List<CommissionDTO>> GetCommissionsByThirdParty(int thirdPartyId)
        {
            var commissions = await _unitOfWork.Commissions.FindAsync(c => c.ThirdPartyId == thirdPartyId);
            return commissions.Select(MapToDTO).ToList();
        }

        public async Task<decimal> GetTotalCommissionsByThirdParty(int thirdPartyId)
        {
            var commissions = await _unitOfWork.Commissions.FindAsync(c => c.ThirdPartyId == thirdPartyId);
            return commissions.Sum(c => c.CommissionAmount);
        }

        public async Task<CommissionDTO> UpdateCommissionStatus(int commissionId, string status)
        {
            var commission = await _unitOfWork.Commissions.GetByIdAsync(commissionId);
            if (commission == null)
            {
                throw new Exception("Commission not found");
            }

            commission.Status = status;
            if (status == "Paid")
            {
                commission.PaymentDate = DateTime.Now;
            }

            _unitOfWork.Commissions.Update(commission);
            await _unitOfWork.SaveChangesAsync();

            return MapToDTO(commission);
        }

        private static CommissionDTO MapToDTO(Commission commission)
        {
            return new CommissionDTO
            {
                CommissionId = commission.CommissionId,
                OrderItemId = commission.OrderItemId,
                ThirdPartyId = commission.ThirdPartyId,
                CommissionPercentage = commission.CommissionPercentage,
                CommissionAmount = commission.CommissionAmount,
                CalculatedAt = (DateTime)commission.CalculatedAt,
                Status = commission.Status,
                PaymentDate = commission.PaymentDate
            };
        }
    }
} 