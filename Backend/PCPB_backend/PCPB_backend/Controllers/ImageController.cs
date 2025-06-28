using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Services.Interfaces;
using System.ComponentModel.DataAnnotations;

namespace PCPB_backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ImageController : ControllerBase
    {
        private readonly IFirebaseStorageService _firebaseStorageService;

        public ImageController(IFirebaseStorageService firebaseStorageService)
        {
            _firebaseStorageService = firebaseStorageService;
        }

        /// <summary>
        /// Uploads an image to Firebase Storage
        /// </summary>
        /// <param name="file">The image file to upload</param>
        /// <param name="folder">Optional folder name (defaults to "products")</param>
        /// <returns>The URL of the uploaded image</returns>
        [HttpPost("upload")]
        [Authorize]
        [ProducesResponseType(typeof(ImageUploadResponse), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(401)]
        public async Task<ActionResult<ImageUploadResponse>> UploadImage(
            [Required] IFormFile file, 
            [FromQuery] string folder = "products")
        {
            try
            {
                if (!_firebaseStorageService.IsValidImage(file))
                {
                    return BadRequest(new { message = "Invalid image file. Please upload a valid image (JPG, PNG, GIF, WEBP) under 5MB." });
                }

                var imageUrl = await _firebaseStorageService.UploadImageAsync(file, folder);
                
                return Ok(new ImageUploadResponse
                {
                    ImageUrl = imageUrl,
                    Message = "Image uploaded successfully"
                });
            }
            catch (ArgumentException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "An error occurred while uploading the image", details = ex.Message });
            }
        }

        /// <summary>
        /// Deletes an image from Firebase Storage
        /// </summary>
        /// <param name="imageUrl">The URL of the image to delete</param>
        /// <returns>Success status</returns>
        [HttpDelete]
        [Authorize]
        [ProducesResponseType(typeof(ImageDeleteResponse), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(401)]
        public async Task<ActionResult<ImageDeleteResponse>> DeleteImage([FromQuery] [Required] string imageUrl)
        {
            try
            {
                var success = await _firebaseStorageService.DeleteImageAsync(imageUrl);
                
                return Ok(new ImageDeleteResponse
                {
                    Success = success,
                    Message = success ? "Image deleted successfully" : "Failed to delete image"
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = "An error occurred while deleting the image", details = ex.Message });
            }
        }
    }

    public class ImageUploadResponse
    {
        public string ImageUrl { get; set; } = string.Empty;
        public string Message { get; set; } = string.Empty;
    }

    public class ImageDeleteResponse
    {
        public bool Success { get; set; }
        public string Message { get; set; } = string.Empty;
    }
}
