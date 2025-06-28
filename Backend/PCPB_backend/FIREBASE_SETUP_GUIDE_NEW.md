# Firebase Setup Guide - Improved Configuration

## 🔥 Complete Firebase Setup for PC Builder App

This guide covers the improved Firebase configuration system that provides better maintainability, type safety, and team collaboration support.

## Features of the New Configuration

✅ **Type-safe configuration** with `FirebaseOptions` class  
✅ **Environment-specific settings** (Development, Production)  
✅ **Proper dependency injection** with `IOptions<T>`  
✅ **Comprehensive logging** for debugging  
✅ **Configuration validation** at startup  
✅ **Cleaner credential resolution** with clear priority order  

## Step 1: Download Service Account Key

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: `pcpb-1368f`
3. Go to **Project Settings** (gear icon) → **Service accounts** tab
4. Click **"Generate new private key"**
5. Download the JSON file (e.g., `pcpb-1368f-firebase-adminsdk-xxxxx.json`)
6. Rename it to `firebase-service-account.json`

## Step 2: Configuration Overview

### Current Configuration Files

The new system uses environment-specific configuration files:

**appsettings.json** (Base configuration)
```json
{
  "Firebase": {
    "StorageBucket": "pcpb-1368f.firebasestorage.app",
    "CredentialsPath": "",
    "UseEnvironmentCredentials": true
  }
}
```

**appsettings.Development.json** (Local development)
```json
{
  "Firebase": {
    "StorageBucket": "pcpb-1368f.firebasestorage.app",
    "CredentialsPath": "../../firebase-service-account.json",
    "UseEnvironmentCredentials": false
  }
}
```

**appsettings.Production.json** (Production deployment)
```json
{
  "Firebase": {
    "StorageBucket": "pcpb-1368f.firebasestorage.app",
    "CredentialsPath": "",
    "UseEnvironmentCredentials": true,
    "MaxFileSize": 5242880,
    "AllowedExtensions": [".jpg", ".jpeg", ".png", ".gif", ".webp"],
    "AllowedMimeTypes": ["image/jpeg", "image/png", "image/gif", "image/webp"],
    "DefaultImageFolder": "products"
  }
}
```

## Method 1: Local Development Setup (Recommended)

### 1. Place the credentials file
Put your `firebase-service-account.json` file in the `Backend` folder:
```
PC-Builder-App/
├── Backend/
│   ├── firebase-service-account.json  ← Place here
│   ├── PCPB_backend/
│   └── ...
```

### 2. Configuration is already set
The `appsettings.Development.json` is already configured to use the local file.

### 3. Run the application
```bash
cd Backend/PCPB_backend/PCPB_backend
dotnet run
```

## Method 2: Environment Variable Setup (Production)

### Windows (PowerShell)
```powershell
# Set environment variable for current session
$env:GOOGLE_APPLICATION_CREDENTIALS="F:\Project\EXE\PC-Builder-App\Backend\firebase-service-account.json"

# Set permanently for current user
[Environment]::SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", "F:\Project\EXE\PC-Builder-App\Backend\firebase-service-account.json", "User")
```

### Linux/macOS (Terminal)
```bash
# Set environment variable for current session
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your/PC-Builder-App/Backend/firebase-service-account.json"

# Add to ~/.bashrc or ~/.zshrc for permanent setup
echo 'export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your/PC-Builder-App/Backend/firebase-service-account.json"' >> ~/.bashrc
source ~/.bashrc
```

## Step 3: Credential Resolution Priority

The new system resolves credentials in this order:

1. **Environment Variable** (if `UseEnvironmentCredentials: true`)
   - Checks `GOOGLE_APPLICATION_CREDENTIALS` environment variable
   
2. **Configuration Path** (if `CredentialsPath` is set)
   - Uses the path specified in `Firebase:CredentialsPath`
   - Supports both relative and absolute paths
   
3. **Default Application Credentials**
   - Falls back to Google Cloud SDK default credentials
   - Works automatically on Google Cloud Platform

## Step 4: Testing the Setup

### 1. Test Firebase connection
```bash
GET /api/firebasetest/connection
```

### 2. Check application logs
Look for these log messages:
- ✅ `Firebase Storage Service initialized successfully with bucket: pcpb-1368f.firebasestorage.app`
- ✅ `Using Firebase credentials from configuration: /path/to/credentials`
- ✅ `Firebase Storage connection test successful`

### 3. Upload test image
```bash
POST /api/product/with-image
```

## Step 5: Team Collaboration

### .gitignore Setup
Make sure your `.gitignore` includes:
```
# Firebase credentials
firebase-service-account.json
**/firebase-service-account.json

# But keep configuration files
!appsettings*.json
```

### Team Member Setup
1. Each team member downloads their own service account key
2. Places it as `Backend/firebase-service-account.json`
3. No code changes needed - configuration handles the rest

## Troubleshooting

### Common Issues

1. **"Firebase configuration is invalid"**
   - Check that `StorageBucket` is set in appsettings.json
   - Verify the bucket name is correct

2. **"Failed to initialize Firebase Storage client"**
   - Verify credentials file exists at the specified path
   - Check file permissions
   - Ensure the service account has Storage Admin role

3. **"Firebase Storage connection test failed"**
   - Verify internet connectivity
   - Check Firebase project is active
   - Ensure Storage is enabled in Firebase console

### Debug Logging
Enable detailed logging by adding to appsettings.Development.json:
```json
{
  "Logging": {
    "LogLevel": {
      "Services.Implements.FirebaseStorageService": "Debug"
    }
  }
}
```

## Migration from Old Setup

If you're migrating from the old Firebase setup:

1. ✅ Configuration files are already updated
2. ✅ Service registration is already updated in Program.cs
3. ✅ FirebaseStorageService is already improved
4. ✅ Just place your credentials file and run!

The new system is backward compatible and will work with existing credentials placement.
