# Local Swagger Testing Guide

This guide explains how to test the PC Builder API using Swagger locally on port 7182.

## Prerequisites

- .NET 6.0 or later
- SQL Server (local or remote)
- Visual Studio 2022 or VS Code

## Running the Application Locally

### Option 1: Using Visual Studio
1. Open the solution in Visual Studio
2. Select the "https" profile from the dropdown in the toolbar
3. Click the "Run" button or press F5
4. The application will start on `https://localhost:7182`
5. Swagger UI will automatically open in your browser

### Option 2: Using Command Line
1. Open a terminal/command prompt
2. Navigate to the `PCPB_backend` directory
3. Run the following command:
   ```bash
   dotnet run --launch-profile "https"
   ```
4. Open your browser and navigate to `https://localhost:7182`

### Option 3: Using Local Testing Profile
1. Open a terminal/command prompt
2. Navigate to the `PCPB_backend` directory
3. Run the following command:
   ```bash
   dotnet run --launch-profile "Local Testing"
   ```
4. This will run with the "Local" environment and ensure Swagger is enabled

## Accessing Swagger UI

Once the application is running, you can access Swagger UI at:
- **Primary URL**: `https://localhost:7182`
- **Alternative URL**: `https://localhost:7182/swagger`

## Configuration Files

The application uses the following configuration files for local testing:
- `appsettings.json` - Base configuration
- `appsettings.Local.json` - Local development overrides
- `Properties/launchSettings.json` - Launch profiles

## Environment Variables

The following environment variables control Swagger behavior:
- `ASPNETCORE_ENVIRONMENT`: Set to "Development" or "Local"
- `EnableSwagger`: Set to "true" to force enable Swagger

## Troubleshooting

### Swagger Not Loading
1. Ensure you're using the "https" or "Local Testing" profile
2. Check that the application is running on port 7182
3. Verify that `EnableSwagger` is set to `true` in configuration

### HTTPS Certificate Issues
If you encounter HTTPS certificate issues:
1. Run the following command to trust the development certificate:
   ```bash
   dotnet dev-certs https --trust
   ```

### Port Already in Use
If port 7182 is already in use:
1. Change the port in `Properties/launchSettings.json`
2. Update the CORS policy in `Program.cs` if needed

## API Testing

Once Swagger is loaded, you can:
1. Browse available endpoints
2. Test API calls directly from the UI
3. View request/response schemas
4. Authenticate using JWT tokens (Bearer token)

## JWT Authentication

To test authenticated endpoints:
1. First call the login endpoint to get a JWT token
2. Click the "Authorize" button in Swagger UI
3. Enter the token in the format: `Bearer {your_token}`
4. Now you can test authenticated endpoints 