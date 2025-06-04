using BusinessObjects.DTOs;
using DAOs;

namespace Services.Interfaces
{
    public interface IThirdPartyService
    {
        Task<ThirdPartyDTO> CreateThirdParty(ThirdPartyDTO thirdPartyDto);
        Task<ThirdPartyDTO> GetThirdPartyById(int id);
        Task<List<ThirdPartyDTO>> GetAllThirdParties();
        Task<ThirdPartyDTO> UpdateThirdParty(ThirdPartyDTO thirdPartyDto);
    }
} 