using Microsoft.AspNetCore.Http;

namespace Services.Interfaces
{
    public interface IFirebaseStorageService
    {
        /// <summary>
        /// Uploads an image file to Firebase Storage
        /// </summary>
        /// <param name="file">The image file to upload</param>
        /// <param name="folder">The folder path in Firebase Storage (e.g., "products")</param>
        /// <returns>The public URL of the uploaded image</returns>
        Task<string> UploadImageAsync(IFormFile file, string folder = "products");

        /// <summary>
        /// Deletes an image from Firebase Storage
        /// </summary>
        /// <param name="imageUrl">The URL of the image to delete</param>
        /// <returns>True if deletion was successful</returns>
        Task<bool> DeleteImageAsync(string imageUrl);

        /// <summary>
        /// Validates if the uploaded file is a valid image
        /// </summary>
        /// <param name="file">The file to validate</param>
        /// <returns>True if the file is a valid image</returns>
        bool IsValidImage(IFormFile file);

        /// <summary>
        /// Tests the Firebase Storage connection
        /// </summary>
        /// <returns>True if connection is successful</returns>
        Task<bool> TestConnectionAsync();
    }
}
