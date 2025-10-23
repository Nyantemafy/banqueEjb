using CompteDepot.Services;
using CompteDepot.Data;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container
builder.Services.AddRazorPages();
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// EF Core DbContext
var connStr = builder.Configuration.GetConnectionString("BanqueDb")
              ?? "Host=localhost;Port=5432;Database=banque_db;Username=postgres;Password=antema";
builder.Services.AddDbContext<BanqueContext>(options =>
    options.UseNpgsql(connStr));

// Register services (EF-based)
builder.Services.AddScoped<IDepotService, EfDepotService>();

// CORS
var allowedOrigins = builder.Configuration.GetSection("Cors:AllowedOrigins").Get<string[]>()
                      ?? new[] { "http://localhost:8080", "http://127.0.0.1:8080" };
builder.Services.AddCors(options =>
{
    options.AddPolicy("DefaultCors", policy =>
        policy.WithOrigins(allowedOrigins)
              .AllowAnyHeader()
              .AllowAnyMethod());
});

builder.Services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromMinutes(30);
    options.Cookie.HttpOnly = true;
    options.Cookie.IsEssential = true;
});

var app = builder.Build();

// Configure the HTTP request pipeline
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseStaticFiles();
app.UseRouting();
app.UseCors("DefaultCors");
app.UseSession();
app.UseAuthorization();

app.MapRazorPages();
app.MapControllers();

app.Run();