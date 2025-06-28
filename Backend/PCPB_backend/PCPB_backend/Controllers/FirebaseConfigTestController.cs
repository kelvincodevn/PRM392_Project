using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using Services.Configuration;
using Services.Interfaces;

namespace PCPB_backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class FirebaseConfigTestController : ControllerBase
    {
        private readonly FirebaseOptions _firebaseOptions;
        private readonly IFirebaseStorageService _firebaseStorageService;
        private readonly ILogger<FirebaseConfigTestController> _logger;

        public FirebaseConfigTestController(
            IOptions<FirebaseOptions> firebaseOptions,
            IFirebaseStorageService firebaseStorageService,
            ILogger<FirebaseConfigTestController> logger)
        {
            _firebaseOptions = firebaseOptions.Value;
            _firebaseStorageService = firebaseStorageService;
            _logger = logger;
        }

        /// <summary>
        /// Test Firebase configuration and display current settings
        /// </summary>
        [HttpGet("config")]
        public IActionResult GetFirebaseConfig()
        {
            try
            {
                var resolvedPath = _firebaseOptions.GetResolvedCredentialsPath();
                var config = new
                {
                    CurrentDirectory = Directory.GetCurrentDirectory(),
                    StorageBucket = _firebaseOptions.StorageBucket,
                    CredentialsPath = _firebaseOptions.CredentialsPath,
                    UseEnvironmentCredentials = _firebaseOptions.UseEnvironmentCredentials,
                    MaxFileSize = _firebaseOptions.MaxFileSize,
                    AllowedExtensions = _firebaseOptions.AllowedExtensions,
                    AllowedMimeTypes = _firebaseOptions.AllowedMimeTypes,
                    DefaultImageFolder = _firebaseOptions.DefaultImageFolder,
                    IsValid = _firebaseOptions.IsValid(),
                    ResolvedCredentialsPath = resolvedPath,
                    CredentialsFileExists = !string.IsNullOrEmpty(resolvedPath) && System.IO.File.Exists(resolvedPath),
                    EnvironmentVariable = Environment.GetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS"),
                    // Debug information
                    Debug = new
                    {
                        WorkingDirectory = Directory.GetCurrentDirectory(),
                        ExpectedCredentialsPath = resolvedPath,
                        FileExists = !string.IsNullOrEmpty(resolvedPath) && System.IO.File.Exists(resolvedPath),
                        DirectoryContents = Directory.Exists(Path.GetDirectoryName(resolvedPath) ?? "")
                            ? Directory.GetFiles(Path.GetDirectoryName(resolvedPath) ?? "", "*.json")
                            : new string[] { "Directory not found" }
                    }
                };

                _logger.LogInformation("Firebase configuration requested");
                return Ok(config);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error retrieving Firebase configuration");
                return StatusCode(500, new { error = ex.Message });
            }
        }

        /// <summary>
        /// Test Firebase Storage connection
        /// </summary>
        [HttpGet("connection")]
        public async Task<IActionResult> TestConnection()
        {
            try
            {
                var isConnected = await _firebaseStorageService.TestConnectionAsync();
                
                var result = new
                {
                    IsConnected = isConnected,
                    StorageBucket = _firebaseOptions.StorageBucket,
                    Timestamp = DateTime.UtcNow,
                    Message = isConnected ? "Firebase Storage connection successful" : "Firebase Storage connection failed"
                };

                if (isConnected)
                {
                    _logger.LogInformation("Firebase Storage connection test successful");
                    return Ok(result);
                }
                else
                {
                    _logger.LogWarning("Firebase Storage connection test failed");
                    return StatusCode(503, result);
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error testing Firebase Storage connection");
                return StatusCode(500, new { 
                    IsConnected = false,
                    Error = ex.Message,
                    Timestamp = DateTime.UtcNow
                });
            }
        }

        /// <summary>
        /// Get Firebase service health status
        /// </summary>
        [HttpGet("health")]
        public async Task<IActionResult> GetHealthStatus()
        {
            try
            {
                var configValid = _firebaseOptions.IsValid();
                var connectionTest = await _firebaseStorageService.TestConnectionAsync();
                var credentialsPath = _firebaseOptions.GetResolvedCredentialsPath();
                var credentialsExist = !string.IsNullOrEmpty(credentialsPath) && System.IO.File.Exists(credentialsPath);

                var health = new
                {
                    Status = configValid && connectionTest ? "Healthy" : "Unhealthy",
                    Checks = new
                    {
                        ConfigurationValid = configValid,
                        ConnectionSuccessful = connectionTest,
                        CredentialsFileExists = credentialsExist,
                        EnvironmentVariableSet = !string.IsNullOrEmpty(Environment.GetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS"))
                    },
                    Timestamp = DateTime.UtcNow
                };

                var statusCode = health.Status == "Healthy" ? 200 : 503;
                return StatusCode(statusCode, health);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error checking Firebase service health");
                return StatusCode(500, new { 
                    Status = "Error",
                    Error = ex.Message,
                    Timestamp = DateTime.UtcNow
                });
            }
        }
    }
}
