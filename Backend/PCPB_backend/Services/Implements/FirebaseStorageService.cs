using Google.Cloud.Storage.V1;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Services.Configuration;
using Services.Interfaces;
using System;
using System.IO;
using System.Threading.Tasks;

namespace Services.Implements
{
    public class FirebaseStorageService : IFirebaseStorageService
    {
        private readonly StorageClient _storageClient;
        private readonly FirebaseOptions _firebaseOptions;
        private readonly ILogger<FirebaseStorageService> _logger;

        public FirebaseStorageService(IOptions<FirebaseOptions> firebaseOptions, ILogger<FirebaseStorageService> logger)
        {
            _firebaseOptions = firebaseOptions.Value ?? throw new ArgumentNullException(nameof(firebaseOptions));
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));

            // Validate configuration
            if (!_firebaseOptions.IsValid())
            {
                throw new InvalidOperationException("Firebase configuration is invalid. Please check your appsettings.json");
            }

            // Initialize Firebase Storage client
            _storageClient = InitializeStorageClient();
            _logger.LogInformation("Firebase Storage Service initialized successfully with bucket: {BucketName}", _firebaseOptions.StorageBucket);
        }

        private StorageClient InitializeStorageClient()
        {
            try
            {
                // Method 1: Check environment variable first (if enabled)
                if (_firebaseOptions.UseEnvironmentCredentials)
                {
                    var envCredentials = Environment.GetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS");
                    _logger.LogDebug("Environment credentials enabled. GOOGLE_APPLICATION_CREDENTIALS = {EnvVar}", envCredentials ?? "null");

                    if (!string.IsNullOrEmpty(envCredentials) && File.Exists(envCredentials))
                    {
                        _logger.LogInformation("Using Firebase credentials from environment variable: {Path}", envCredentials);
                        return StorageClient.Create();
                    }
                    else if (!string.IsNullOrEmpty(envCredentials))
                    {
                        _logger.LogWarning("Environment variable GOOGLE_APPLICATION_CREDENTIALS is set but file does not exist: {Path}", envCredentials);
                    }
                }

                // Method 2: Try configuration-based path
                var credentialsPath = _firebaseOptions.GetResolvedCredentialsPath();
                _logger.LogDebug("Configuration credentials path: {ConfigPath} -> Resolved: {ResolvedPath}",
                    _firebaseOptions.CredentialsPath, credentialsPath ?? "null");

                if (!string.IsNullOrEmpty(credentialsPath))
                {
                    if (File.Exists(credentialsPath))
                    {
                        Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", credentialsPath);
                        _logger.LogInformation("Using Firebase credentials from configuration: {Path}", credentialsPath);
                        return StorageClient.Create();
                    }
                    else
                    {
                        _logger.LogWarning("Configuration credentials file does not exist: {Path}", credentialsPath);
                    }
                }

                // Method 3: Try default application credentials (works on GCP, local development with gcloud auth)
                _logger.LogInformation("Attempting to use default application credentials");
                return StorageClient.Create();
            }
            catch (Exception ex)
            {
                var currentDir = Directory.GetCurrentDirectory();
                var envVar = Environment.GetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS");
                var configPath = _firebaseOptions.GetResolvedCredentialsPath();

                _logger.LogError(ex, "Failed to initialize Firebase Storage client. Current directory: {CurrentDir}, " +
                    "Environment variable: {EnvVar}, Configuration path: {ConfigPath}",
                    currentDir, envVar ?? "null", configPath ?? "null");

                throw new InvalidOperationException(
                    $"Failed to initialize Firebase Storage client. " +
                    $"Current directory: {currentDir}. " +
                    $"Environment variable GOOGLE_APPLICATION_CREDENTIALS: {envVar ?? "not set"}. " +
                    $"Configuration path: {configPath ?? "not configured"}. " +
                    $"Please ensure Firebase credentials are properly configured.",
                    ex);
            }
        }

        public async Task<string> UploadImageAsync(IFormFile file, string folder = "products")
        {
            if (file == null || file.Length == 0)
                throw new ArgumentException("File is empty or null");

            if (!IsValidImage(file))
                throw new ArgumentException("Invalid image file");

            try
            {
                // Use default folder if not specified
                if (string.IsNullOrWhiteSpace(folder))
                    folder = _firebaseOptions.DefaultImageFolder;

                // Generate unique filename
                var fileExtension = Path.GetExtension(file.FileName).ToLowerInvariant();
                var fileName = $"{Guid.NewGuid()}{fileExtension}";
                var objectName = $"{folder}/{fileName}";

                // Upload to Firebase Storage
                using var stream = file.OpenReadStream();

                await _storageClient.UploadObjectAsync(_firebaseOptions.StorageBucket, objectName, file.ContentType, stream);

                // Return the public URL
                var publicUrl = $"https://storage.googleapis.com/{_firebaseOptions.StorageBucket}/{objectName}";
                _logger.LogInformation("Successfully uploaded image: {FileName} to {ObjectName}", file.FileName, objectName);

                return publicUrl;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to upload image: {FileName}", file.FileName);
                throw new Exception($"Failed to upload image: {ex.Message}", ex);
            }
        }

        public async Task<bool> DeleteImageAsync(string imageUrl)
        {
            if (string.IsNullOrEmpty(imageUrl))
                return false;

            try
            {
                // Extract object name from URL
                var uri = new Uri(imageUrl);
                var objectName = uri.AbsolutePath.TrimStart('/');

                // Remove bucket name from path if present
                var bucketPrefix = _firebaseOptions.StorageBucket + "/";
                if (objectName.StartsWith(bucketPrefix))
                {
                    objectName = objectName[bucketPrefix.Length..];
                }

                await _storageClient.DeleteObjectAsync(_firebaseOptions.StorageBucket, objectName);
                _logger.LogInformation("Successfully deleted image: {ObjectName}", objectName);
                return true;
            }
            catch (Exception ex)
            {
                _logger.LogWarning(ex, "Failed to delete image: {ImageUrl}", imageUrl);
                return false;
            }
        }

        public bool IsValidImage(IFormFile file)
        {
            if (file == null || file.Length == 0)
                return false;

            // Check file size
            if (file.Length > _firebaseOptions.MaxFileSize)
                return false;

            // Check file extension
            var extension = Path.GetExtension(file.FileName).ToLowerInvariant();
            if (!_firebaseOptions.AllowedExtensions.Contains(extension))
                return false;

            // Check MIME type
            if (!_firebaseOptions.AllowedMimeTypes.Contains(file.ContentType))
                return false;

            return true;
        }

        public async Task<bool> TestConnectionAsync()
        {
            try
            {
                // Try to list objects in the bucket (this will verify both authentication and bucket access)
                var objects = _storageClient.ListObjectsAsync(_firebaseOptions.StorageBucket, options: new ListObjectsOptions { PageSize = 1 });
                await foreach (var obj in objects)
                {
                    // If we can iterate even one object, the connection works
                    break;
                }
                _logger.LogInformation("Firebase Storage connection test successful");
                return true;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Firebase Storage connection test failed");
                return false;
            }
        }
    }
}
