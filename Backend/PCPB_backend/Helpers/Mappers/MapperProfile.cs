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
            CreateMap<ProductDTO, Product>();
            CreateMap<ProductCreateDTO, Product>()
                .ForMember(dest => dest.ImageUrl, opt => opt.Ignore()) // Will be set by service
                .ForMember(dest => dest.ProductId, opt => opt.Ignore())
                .ForMember(dest => dest.ThirdPartyId, opt => opt.Ignore())
                .ForMember(dest => dest.Status, opt => opt.Ignore())
                .ForMember(dest => dest.CreatedAt, opt => opt.Ignore())
                .ForMember(dest => dest.UpdatedAt, opt => opt.Ignore())
                .ForMember(dest => dest.CartItems, opt => opt.Ignore())
                .ForMember(dest => dest.Category, opt => opt.Ignore())
                .ForMember(dest => dest.OrderItems, opt => opt.Ignore())
                .ForMember(dest => dest.ThirdParty, opt => opt.Ignore());

            // CartItem mapping
            CreateMap<CartItem, CartItemDTO>()
                .ForMember(dest => dest.Product, opt => opt.MapFrom(src => src.Product));

            // Cart mapping with explicit CartItems configuration
            CreateMap<Cart, CartDTO>()
                .ForMember(dest => dest.CartItems, opt => opt.MapFrom(src => src.CartItems));
        }
    }
}
