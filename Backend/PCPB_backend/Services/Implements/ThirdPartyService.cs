using BusinessObjects.DTOs;
using BusinessObjects.Models;
using DAOs;
using Repositories.Interfaces;
using Services.Interfaces;

namespace Services.Implements
{
    public class ThirdPartyService : IThirdPartyService
    {
        private readonly IUnitOfWork _unitOfWork;

        public ThirdPartyService(IUnitOfWork unitOfWork)
        {
            _unitOfWork = unitOfWork;
        }

        public async Task<ThirdPartyDTO> CreateThirdParty(ThirdPartyDTO thirdPartyDto)
        {
            if (thirdPartyDto.UserId.HasValue)
            {
                var userExists = await _unitOfWork.Users.FindAsync(u => u.UserId == thirdPartyDto.UserId.Value);
                if (!userExists.Any())
                {
                    throw new Exception("User does not exist");
                }
            }

            var thirdParty = new ThirdParty
            {
                UserId = thirdPartyDto.UserId,
                CompanyName = thirdPartyDto.CompanyName,
                TaxId = thirdPartyDto.TaxId,
                BankAccountInfo = thirdPartyDto.BankAccountInfo,
                Status = thirdPartyDto.Status,
                Rating = thirdPartyDto.Rating,
                JoinedAt = DateTime.Now
            };

            await _unitOfWork.ThirdParties.AddAsync(thirdParty);
            await _unitOfWork.SaveChangesAsync();

            return MapToDTO(thirdParty);
        }

        public async Task<ThirdPartyDTO> GetThirdPartyById(int id)
        {
            var thirdParty = await _unitOfWork.ThirdParties.GetByIdAsync(id);
            if (thirdParty == null)
            {
                throw new Exception("Third party not found");
            }
            return MapToDTO(thirdParty);
        }

        public async Task<List<ThirdPartyDTO>> GetAllThirdParties()
        {
            var thirdParties = await _unitOfWork.ThirdParties.GetAllAsync();
            return thirdParties.Select(MapToDTO).ToList();
        }

        public async Task<ThirdPartyDTO> UpdateThirdParty(ThirdPartyDTO thirdPartyDto)
        {
            var thirdParty = await _unitOfWork.ThirdParties.GetByIdAsync(thirdPartyDto.ThirdPartyId);
            if (thirdParty == null)
            {
                throw new Exception("Third party not found");
            }

            if (thirdPartyDto.UserId.HasValue)
            {
                var userExists = await _unitOfWork.Users.FindAsync(u => u.UserId == thirdPartyDto.UserId.Value);
                if (!userExists.Any())
                {
                    throw new Exception("User does not exist");
                }
            }

            thirdParty.UserId = thirdPartyDto.UserId;
            thirdParty.CompanyName = thirdPartyDto.CompanyName;
            thirdParty.TaxId = thirdPartyDto.TaxId;
            thirdParty.BankAccountInfo = thirdPartyDto.BankAccountInfo;
            thirdParty.Status = thirdPartyDto.Status;
            thirdParty.Rating = thirdPartyDto.Rating;

            await _unitOfWork.ThirdParties.UpdateAsync(thirdParty);
            await _unitOfWork.SaveChangesAsync();

            return MapToDTO(thirdParty);
        }

        private static ThirdPartyDTO MapToDTO(ThirdParty thirdParty)
        {
            return new ThirdPartyDTO
            {
                ThirdPartyId = thirdParty.ThirdPartyId,
                UserId = thirdParty.UserId,
                CompanyName = thirdParty.CompanyName,
                TaxId = thirdParty.TaxId,
                BankAccountInfo = thirdParty.BankAccountInfo,
                Status = thirdParty.Status,
                Rating = thirdParty.Rating,
                JoinedAt = thirdParty.JoinedAt
            };
        }
    }
} 