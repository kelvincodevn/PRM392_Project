using DAOs;
using Helpers.DTOs.Authentication;

namespace Services.Interfaces
{
    public interface IAuthService
    {
        Task<string> GenerateJwtToken(User user);
        Task<User> Authenticate(string username, string password);
        Task<bool> ValidatePassword(string password, string passwordHash);
        Task<User> Register(RegisterDTO model);
    }
}
