using BusinessObjects.DTOs;
using BusinessObjects.Models;
using Microsoft.AspNetCore.Mvc;
using Services;
using Services.Interfaces;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace PCPB_backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class CategoryController : ControllerBase
    {
        private readonly ICategoryService _categoryService;

        public CategoryController(ICategoryService categoryService)
        {
            _categoryService = categoryService;
        }

        /// <summary>
        /// Creates a new category
        /// </summary>
        /// <param name="categoryDto">The category data to create</param>
        /// <returns>The created category</returns>
        [HttpPost]
        [ProducesResponseType(typeof(Category), 201)]
        [ProducesResponseType(400)]
        public async Task<ActionResult<Category>> CreateCategory(CategoryDTO categoryDto)
        {
            try
            {
                var category = new Category
                {
                    CategoryName = categoryDto.CategoryName,
                    ParentCategoryId = categoryDto.ParentCategoryId
                };

                var createdCategory = await _categoryService.CreateCategory(category);
                return CreatedAtAction(nameof(GetCategoryById), new { id = createdCategory.CategoryId }, createdCategory);
            }
            catch (System.Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        /// <summary>
        /// Gets a category by ID
        /// </summary>
        /// <param name="id">The category ID</param>
        /// <returns>The category</returns>
        [HttpGet("{id}")]
        [ProducesResponseType(typeof(Category), 200)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<Category>> GetCategoryById(int id)
        {
            try
            {
                var category = await _categoryService.GetCategoryById(id);
                return Ok(category);
            }
            catch (System.Exception ex)
            {
                return NotFound(ex.Message);
            }
        }

        /// <summary>
        /// Gets all categories
        /// </summary>
        /// <returns>List of all categories</returns>
        [HttpGet]
        [ProducesResponseType(typeof(List<Category>), 200)]
        public async Task<ActionResult<List<Category>>> GetAllCategories()
        {
            var categories = await _categoryService.GetAllCategories();
            return Ok(categories);
        }

        /// <summary>
        /// Updates a category
        /// </summary>
        /// <param name="id">The category ID</param>
        /// <param name="categoryDto">The updated category data</param>
        /// <returns>The updated category</returns>
        [HttpPut("{id}")]
        [ProducesResponseType(typeof(Category), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<Category>> UpdateCategory(int id, CategoryDTO categoryDto)
        {
            try
            {
                var category = new Category
                {
                    CategoryId = id,
                    CategoryName = categoryDto.CategoryName,
                    ParentCategoryId = categoryDto.ParentCategoryId
                };

                var updatedCategory = await _categoryService.UpdateCategory(category);
                return Ok(updatedCategory);
            }
            catch (System.Exception ex)
            {
                return NotFound(ex.Message);
            }
        }

        /// <summary>
        /// Deletes a category
        /// </summary>
        /// <param name="id">The category ID</param>
        /// <returns>True if deleted successfully</returns>
        [HttpDelete("{id}")]
        [ProducesResponseType(typeof(bool), 200)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<bool>> DeleteCategory(int id)
        {
            try
            {
                var result = await _categoryService.DeleteCategory(id);
                return Ok(result);
            }
            catch (System.Exception ex)
            {
                return NotFound(ex.Message);
            }
        }

        /// <summary>
        /// Gets all subcategories for a parent category
        /// </summary>
        /// <param name="parentId">The parent category ID</param>
        /// <returns>List of subcategories</returns>
        [HttpGet("subcategories/{parentId}")]
        [ProducesResponseType(typeof(List<Category>), 200)]
        [ProducesResponseType(404)]
        public async Task<ActionResult<List<Category>>> GetSubCategories(int parentId)
        {
            try
            {
                var subCategories = await _categoryService.GetSubCategories(parentId);
                return Ok(subCategories);
            }
            catch (System.Exception ex)
            {
                return NotFound(ex.Message);
            }
        }
    }
} 