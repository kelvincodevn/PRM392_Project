using Microsoft.AspNetCore.Mvc;
using Services.Interfaces;
using System;
using System.IO;
using System.Threading.Tasks;

namespace PCPB_backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class FirebaseTestController : ControllerBase
    {
        private readonly IFirebaseStorageService _firebaseStorageService;

        public FirebaseTestController(IFirebaseStorageService firebaseStorageService)
        {
            _firebaseStorageService = firebaseStorageService;
        }

        /// <summary>
        /// Test Firebase Storage connection
        /// </summary>
        /// <returns>Connection status</returns>
        [HttpGet("connection")]
        [ProducesResponseType(typeof(object), 200)]
        [ProducesResponseType(400)]
        public async Task<IActionResult> TestConnection()
        {
            try
            {
                var isConnected = await _firebaseStorageService.TestConnectionAsync();
                var credentialsPath = Environment.GetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS");
                
                return Ok(new
                {
                    isConnected = isConnected,
                    credentialsPath = credentialsPath ?? "Not set",
                    credentialsFileExists = !string.IsNullOrEmpty(credentialsPath) && System.IO.File.Exists(credentialsPath),
                    message = isConnected ? "Firebase Storage connection successful!" : "Firebase Storage connection failed!",
                    timestamp = DateTime.UtcNow
                });
            }
            catch (Exception ex)
            {
                return BadRequest(new
                {
                    isConnected = false,
                    error = ex.Message,
                    credentialsPath = Environment.GetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS") ?? "Not set",
                    message = "Firebase Storage connection test failed",
                    timestamp = DateTime.UtcNow
                });
            }
        }

        /// <summary>
        /// Get Firebase configuration info
        /// </summary>
        /// <returns>Configuration details</returns>
        [HttpGet("config")]
        [ProducesResponseType(typeof(object), 200)]
        public IActionResult GetConfig()
        {
            var credentialsPath = Environment.GetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS");
            var currentDirectory = Directory.GetCurrentDirectory();
            
            var possiblePaths = new[]
            {
                credentialsPath,
                Path.Combine(currentDirectory, "firebase-service-account.json"),
                Path.Combine(currentDirectory, "..", "firebase-service-account.json"),
                Path.Combine(currentDirectory, "firebase-credentials.json")
            };

            var pathsInfo = new List<object>();
            foreach (var path in possiblePaths)
            {
                if (!string.IsNullOrEmpty(path))
                {
                    pathsInfo.Add(new
                    {
                        path = path,
                        exists = System.IO.File.Exists(path),
                        isEnvironmentVariable = path == credentialsPath
                    });
                }
            }

            return Ok(new
            {
                currentDirectory = currentDirectory,
                environmentVariable = credentialsPath ?? "Not set",
                possibleCredentialPaths = pathsInfo,
                instructions = new
                {
                    step1 = "Download service account key from Firebase Console",
                    step2 = "Place it as 'firebase-service-account.json' in project root",
                    step3 = "Or set GOOGLE_APPLICATION_CREDENTIALS environment variable",
                    step4 = "Test connection using /api/firebasetest/connection"
                }
            });
        }
    }
}
