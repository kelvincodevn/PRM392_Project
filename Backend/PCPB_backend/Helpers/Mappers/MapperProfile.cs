using AutoMapper;
using BusinessObjects.DTOs;
using BusinessObjects.DTOs.Cart;
using BusinessObjects.DTOs.CartItems;
using DAOs;

namespace Helpers.Mappers
{
    public class MapperProfile : Profile
    {
        public MapperProfile()
        {
            // Product mapping
            CreateMap<Product, ProductDTO>();
            
            // CartItem mapping
            CreateMap<CartItem, CartItemDTO>()
                .ForMember(dest => dest.Product, opt => opt.MapFrom(src => src.Product));
            
            // Cart mapping with explicit CartItems configuration
            CreateMap<Cart, CartDTO>()
                .ForMember(dest => dest.CartItems, opt => opt.MapFrom(src => src.CartItems));
        }
    }
}
