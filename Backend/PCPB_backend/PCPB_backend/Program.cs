using DAOs;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Repositories.Implements;
using Repositories.Interfaces;
using Services.Implements;
using Services.Interfaces;
using System.Text;
using Microsoft.OpenApi.Models;
using Services;
using Repositories;
using Helpers.Mappers;
using AutoMapper;
using Services.Configuration;

var builder = WebApplication.CreateBuilder(args);

// Add JWT Configuration
builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,
        ValidIssuer = builder.Configuration["Jwt:Issuer"],
        ValidAudience = builder.Configuration["Jwt:Audience"],
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(builder.Configuration["Jwt:Key"]))
    };
});

// Add authorization policies
builder.Services.AddAuthorization(options =>
{
    options.AddPolicy("AdminOnly", policy => policy.RequireRole("Admin"));
    options.AddPolicy("StaffOnly", policy => policy.RequireRole("Staff"));
    options.AddPolicy("ThirdPartyOnly", policy => policy.RequireRole("ThirdParty"));
    options.AddPolicy("CustomerOnly", policy => policy.RequireRole("Customer"));
});

// Add services to the container.
builder.Services.AddDbContext<PCPBContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("PCPBConnection"),
        sqlOptions => sqlOptions.EnableRetryOnFailure()));
builder.Services.AddScoped(typeof(IGenericRepository<>), typeof(GenericRepository<>));
builder.Services.AddScoped<IUnitOfWork, UnitOfWork>();
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<CategoryDAO>();
builder.Services.AddScoped<ICategoryRepository, CategoryRepository>();
builder.Services.AddScoped<ICategoryService, CategoryService>();
builder.Services.AddScoped<IStaffService, StaffService>();
builder.Services.AddScoped<ProductDAO>();
builder.Services.AddScoped<IProductRepository, ProductRepository>();
builder.Services.AddScoped<IProductService, ProductService>();
builder.Services.AddScoped<IThirdPartyService, ThirdPartyService>();
builder.Services.AddScoped<ICommissionService, CommissionService>();
builder.Services.AddScoped<ICartRepository, CartRepository>();
builder.Services.AddScoped<ICartService, CartService>();
builder.Services.AddScoped<IOrderRepository, OrderRepository>();
builder.Services.AddScoped<IOrderItemRepository, OrderItemRepository>();
builder.Services.AddScoped<IOrderService, OrderService>();
builder.Services.AddScoped<IOrderItemService, OrderItemService>();

// Configure Firebase options
builder.Services.Configure<FirebaseOptions>(
    builder.Configuration.GetSection(FirebaseOptions.SectionName));

builder.Services.AddScoped<IFirebaseStorageService, FirebaseStorageService>();

builder.Services.AddControllers()
    .AddJsonOptions(options =>
    {
        options.JsonSerializerOptions.ReferenceHandler = System.Text.Json.Serialization.ReferenceHandler.IgnoreCycles;
        options.JsonSerializerOptions.MaxDepth = 32;
    });
builder.Services.AddEndpointsApiExplorer();

// Add AutoMapper
builder.Services.AddAutoMapper(typeof(Program).Assembly, 
                              typeof(MapperProfile).Assembly);

// Configure Swagger to use JWT authentication
builder.Services.AddSwaggerGen(c =>
{
    var swaggerSettings = builder.Configuration.GetSection("SwaggerSettings");
    c.SwaggerDoc("v1", new OpenApiInfo { 
        Title = swaggerSettings["Title"] ?? "PC Builder API", 
        Version = swaggerSettings["Version"] ?? "v1",
        Description = swaggerSettings["Description"] ?? "API for PC Builder Application"
    });
    
    // Define the security scheme
    c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Description = "JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"",
        Name = "Authorization",
        In = ParameterLocation.Header,
        Type = SecuritySchemeType.ApiKey,
        Scheme = "Bearer"
    });
    
    // Make sure swagger UI requires a Bearer token specified
    c.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference
                {
                    Type = ReferenceType.SecurityScheme,
                    Id = "Bearer"
                }
            },
            new string[] {}
        }
    });
});

// Add CORS configuration
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", builder =>
    {
        builder.AllowAnyOrigin()
               .AllowAnyMethod()
               .AllowAnyHeader();
    });
    
    // Add specific policy for local development
    options.AddPolicy("LocalDevelopment", builder =>
    {
        builder.WithOrigins("https://localhost:7182", "http://localhost:5232")
               .AllowAnyMethod()
               .AllowAnyHeader()
               .AllowCredentials();
    });
});

var app = builder.Build();

// Configure the HTTP request pipeline.
// Enable Swagger for local development and testing
var enableSwagger = app.Configuration.GetValue<bool>("EnableSwagger", false) || 
                   app.Environment.IsDevelopment() || 
                   app.Environment.IsEnvironment("Local");

if (enableSwagger)
{
    app.UseSwagger();
    app.UseSwaggerUI(c =>
    {
        c.SwaggerEndpoint("/swagger/v1/swagger.json", "PC Builder API V1");
        c.RoutePrefix = String.Empty;
        c.DocumentTitle = app.Configuration.GetSection("SwaggerSettings")["Title"] ?? "PC Builder API";
    });
}

app.UseHttpsRedirection();
app.UseAuthentication();
app.UseCors("AllowAll");
app.UseAuthorization();
app.MapControllers();

app.Run();
