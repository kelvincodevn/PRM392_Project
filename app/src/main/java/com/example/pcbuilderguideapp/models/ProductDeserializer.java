package com.example.pcbuilderguideapp.models;

import com.google.gson.*;
import java.lang.reflect.Type;
import android.util.Log;

public class ProductDeserializer implements JsonDeserializer<Product> {
    @Override
    public Product deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Log the raw JSON for debugging
        Log.e("ProductDeserializer", "Raw JSON: " + json.toString());
        JsonObject obj = json.getAsJsonObject();
        Product product = new Product();

        product.setId(obj.has("productId") ? obj.get("productId").getAsInt() : 0);
        product.setName(obj.has("productName") ? obj.get("productName").getAsString() : "");
        product.setDescription(obj.has("description") ? obj.get("description").getAsString() : "");
        product.setPrice(obj.has("price") ? obj.get("price").getAsDouble() : 0.0);
        product.setQuantity(obj.has("stockQuantity") ? obj.get("stockQuantity").getAsInt() : 0);
        product.setImageUrl(obj.has("imageUrl") ? obj.get("imageUrl").getAsString() : "");

        // Parse thirdParty if present
        if (obj.has("thirdParty") && obj.get("thirdParty").isJsonObject()) {
            JsonObject thirdPartyObj = obj.getAsJsonObject("thirdParty");
            ThirdParty thirdParty = new ThirdParty();
            thirdParty.setCompanyName(thirdPartyObj.has("companyName") ? thirdPartyObj.get("companyName").getAsString() : "");
            product.setThirdParty(thirdParty);
        }

        // Optionally parse category, etc.

        return product;
    }
} 