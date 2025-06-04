using BusinessObjects.DTOs;
using BusinessObjects.Models;
using DAOs;
using Repositories.Interfaces;
using Services.Interfaces;

namespace Services.Implements
{
    public class StaffService : IStaffService
    {
        private readonly IUnitOfWork _unitOfWork;

        public StaffService(IUnitOfWork unitOfWork)
        {
            _unitOfWork = unitOfWork;
        }

        public async Task<StaffDTO> CreateStaff(StaffDTO staffDto)
        {
            if (staffDto.UserId.HasValue)
            {
                var userExists = await _unitOfWork.Users.FindAsync(u => u.UserId == staffDto.UserId.Value);
                if (!userExists.Any())
                {
                    throw new Exception("User does not exist");
                }
            }

            var staff = new Staff
            {
                UserId = staffDto.UserId,
                DeliveryRegion = staffDto.DeliveryRegion,
                Status = staffDto.Status,
                VehicleInfo = staffDto.VehicleInfo
            };

            await _unitOfWork.Staffs.AddAsync(staff);
            await _unitOfWork.SaveChangesAsync();

            return MapToDTO(staff);
        }

        public async Task<StaffDTO> GetStaffById(int id)
        {
            var staff = await _unitOfWork.Staffs.GetByIdAsync(id);
            if (staff == null)
            {
                throw new Exception("Staff not found");
            }
            return MapToDTO(staff);
        }

        public async Task<List<StaffDTO>> GetAllStaff()
        {
            var staff = await _unitOfWork.Staffs.GetAllAsync();
            return staff.Select(MapToDTO).ToList();
        }

        public async Task<StaffDTO> UpdateStaff(StaffDTO staffDto)
        {
            var staff = await _unitOfWork.Staffs.GetByIdAsync(staffDto.StaffId);
            if (staff == null)
            {
                throw new Exception("Staff not found");
            }

            if (staffDto.UserId.HasValue)
            {
                var userExists = await _unitOfWork.Users.FindAsync(u => u.UserId == staffDto.UserId.Value);
                if (!userExists.Any())
                {
                    throw new Exception("User does not exist");
                }
            }

            staff.UserId = staffDto.UserId;
            staff.DeliveryRegion = staffDto.DeliveryRegion;
            staff.Status = staffDto.Status;
            staff.VehicleInfo = staffDto.VehicleInfo;

            _unitOfWork.Staffs.Update(staff);
            await _unitOfWork.SaveChangesAsync();

            return MapToDTO(staff);
        }

        private static StaffDTO MapToDTO(Staff staff)
        {
            return new StaffDTO
            {
                StaffId = staff.StaffId,
                UserId = staff.UserId,
                DeliveryRegion = staff.DeliveryRegion,
                Status = staff.Status,
                VehicleInfo = staff.VehicleInfo
            };
        }
    }
} 