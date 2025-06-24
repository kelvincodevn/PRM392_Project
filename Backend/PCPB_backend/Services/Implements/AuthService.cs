using BusinessObjects.Enums;
using DAOs;
using Helpers.DTOs.Authentication;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using Services.Interfaces;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;

namespace Services.Implements
{
    public class AuthService : IAuthService
    {
        private readonly IConfiguration _configuration;
        private readonly PCPBContext _context;

        public AuthService(IConfiguration configuration, PCPBContext context)
        {
            _configuration = configuration;
            _context = context;
        }

        public async Task<User> Authenticate(string username, string password)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == username);

            if (user == null || !await ValidatePassword(password, user.PasswordHash))
                return null;

            return user;
        }

        public async Task<string> GenerateJwtToken(User user)
        {
            var securityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration["Jwt:Key"]));
            var credentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);

            var claims = new List<Claim>
            {
                new Claim(ClaimTypes.NameIdentifier, user.UserId.ToString()),
                new Claim(ClaimTypes.Name, user.Username),
                new Claim(ClaimTypes.Email, user.Email),
                new Claim(ClaimTypes.Role, user.Role)
            };

            var token = new JwtSecurityToken(
                issuer: _configuration["Jwt:Issuer"],
                audience: _configuration["Jwt:Audience"],
                claims: claims,
                expires: DateTime.Now.AddMinutes(Convert.ToDouble(_configuration["Jwt:ExpiryInMinutes"])),
                signingCredentials: credentials
            );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }

        public async Task<bool> ValidatePassword(string password, string passwordHash)
        {
            // Implement password validation (compare hash)
            // This is a simple example - use a proper hashing library in production
            return BCrypt.Net.BCrypt.Verify(password, passwordHash);
        }

        public async Task<User> Register(RegisterDTO model)
        {
            // Validate password
            string errorMessage;
            if (!Helpers.Validators.PasswordValidator.IsValid(model.Password, out errorMessage))
            {
                throw new ArgumentException(errorMessage);
            }

            // Check if user already exists
            if (await _context.Users.AnyAsync(u => u.Username == model.Username || u.Email == model.Email))
                return null;

            // Create new user
            var user = new User
            {
                Username = model.Username,
                Email = model.Email,
                PasswordHash = HashPassword(model.Password),
                FullName = model.FullName,
                PhoneNumber = model.PhoneNumber,
                Address = model.Address,
                Role = AccountRole.Customer.ToString(), //Customer
                CreatedAt = DateTime.Now,
                UpdatedAt = DateTime.Now
            };

            // Add user to database
            await _context.Users.AddAsync(user);
            await _context.SaveChangesAsync();

            return user;
        }
        // Add these methods to the AuthService class
        public async Task<bool> ForgotPassword(string email)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Email == email);
            if (user == null)
                return false;

            // Generate a random token
            var token = GenerateRandomToken();

            // Store the token with expiry time
            user.ResetToken = token;
            user.ResetTokenExpiry = DateTime.Now.AddHours(24);

            await _context.SaveChangesAsync();

            // In a real application, you would send an email with the reset link
            // For now, we'll just return true to indicate success
            return true;
        }

        public async Task<bool> ResetPassword(string token, string newPassword)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u =>
                u.ResetToken == token &&
                u.ResetTokenExpiry > DateTime.Now);

            if (user == null)
                return false;

            // Validate password
            string errorMessage;
            if (!Helpers.Validators.PasswordValidator.IsValid(newPassword, out errorMessage))
            {
                throw new ArgumentException(errorMessage);
            }

            // Update password
            user.PasswordHash = HashPassword(newPassword);
            user.ResetToken = null;
            user.ResetTokenExpiry = null;

            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> ValidateResetToken(string token)
        {
            return await _context.Users.AnyAsync(u =>
                u.ResetToken == token &&
                u.ResetTokenExpiry > DateTime.Now);
        }

        private string GenerateRandomToken()
        {
            // Generate a random token
            var randomBytes = new byte[32];
            using (var rng = RandomNumberGenerator.Create())
            {
                rng.GetBytes(randomBytes);
            }
            return Convert.ToBase64String(randomBytes);
        }

        private string HashPassword(string password)
        {
            return BCrypt.Net.BCrypt.HashPassword(password, 12); // 12 is the work factor
        }

    }
}


