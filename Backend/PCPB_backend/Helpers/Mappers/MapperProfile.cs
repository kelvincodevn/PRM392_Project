using AutoMapper;
using BusinessObjects.DTOs.Cart;
using BusinessObjects.DTOs.CartItems;
using DAOs;

namespace Helpers.Mappers
{
    public class MapperProfile : Profile
    {
        public MapperProfile()
        {
            // Cart mappings
            CreateMap<Cart, CartDTO>();
            CreateMap<CartItem, CartItemDTO>();
            
            
        }
    }
}
