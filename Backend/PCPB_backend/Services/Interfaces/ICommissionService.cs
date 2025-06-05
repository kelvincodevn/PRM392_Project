using BusinessObjects.DTOs;
using DAOs;

namespace Services.Interfaces
{
    public interface ICommissionService
    {
        Task<CommissionDTO> CalculateCommission(int orderItemId);
        Task<List<CommissionDTO>> GetCommissionsByThirdParty(int thirdPartyId);
        Task<decimal> GetTotalCommissionsByThirdParty(int thirdPartyId);
        Task<CommissionDTO> UpdateCommissionStatus(int commissionId, string status);
    }
} 