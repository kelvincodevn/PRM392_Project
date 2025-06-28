using System.ComponentModel.DataAnnotations;

namespace Services.Configuration
{
    /// <summary>
    /// Configuration options for Firebase services
    /// </summary>
    public class FirebaseOptions
    {
        public const string SectionName = "Firebase";

        /// <summary>
        /// Firebase Storage bucket name
        /// </summary>
        [Required]
        public string StorageBucket { get; set; } = string.Empty;

        /// <summary>
        /// Path to Firebase service account credentials file
        /// Can be relative or absolute path
        /// </summary>
        public string CredentialsPath { get; set; } = string.Empty;

        /// <summary>
        /// Whether to use environment variable GOOGLE_APPLICATION_CREDENTIALS
        /// If true, will check environment variable first before using CredentialsPath
        /// </summary>
        public bool UseEnvironmentCredentials { get; set; } = true;

        /// <summary>
        /// Maximum file size for uploads in bytes (default: 10MB)
        /// </summary>
        public long MaxFileSize { get; set; } = 10 * 1024 * 1024;

        /// <summary>
        /// Allowed file extensions for image uploads
        /// </summary>
        public string[] AllowedExtensions { get; set; } = { ".jpg", ".jpeg", ".png", ".gif", ".webp" };

        /// <summary>
        /// Allowed MIME types for image uploads
        /// </summary>
        public string[] AllowedMimeTypes { get; set; } = { "image/jpeg", "image/png", "image/gif", "image/webp" };

        /// <summary>
        /// Default folder for storing uploaded images
        /// </summary>
        public string DefaultImageFolder { get; set; } = "products";

        /// <summary>
        /// Validates the Firebase configuration
        /// </summary>
        /// <returns>True if configuration is valid</returns>
        public bool IsValid()
        {
            return !string.IsNullOrWhiteSpace(StorageBucket) &&
                   MaxFileSize > 0 &&
                   AllowedExtensions?.Length > 0 &&
                   AllowedMimeTypes?.Length > 0;
        }

        /// <summary>
        /// Gets the resolved credentials path
        /// </summary>
        /// <returns>Full path to credentials file or null if not configured</returns>
        public string? GetResolvedCredentialsPath()
        {
            if (string.IsNullOrWhiteSpace(CredentialsPath))
                return null;

            if (Path.IsPathRooted(CredentialsPath))
                return CredentialsPath;

            return Path.Combine(Directory.GetCurrentDirectory(), CredentialsPath);
        }
    }
}
