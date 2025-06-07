using BusinessObjects.DTOs;
using DAOs;

namespace Services.Interfaces
{
    public interface IStaffService
    {
        Task<StaffDTO> CreateStaff(StaffDTO staffDto);
        Task<StaffDTO> GetStaffById(int id);
        Task<List<StaffDTO>> GetAllStaff();
        Task<StaffDTO> UpdateStaff(StaffDTO staffDto);
    }
} 