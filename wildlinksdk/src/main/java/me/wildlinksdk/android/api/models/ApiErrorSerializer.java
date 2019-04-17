package me.wildlinksdk.android.api.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import me.wildlinksdk.android.api.ApiError;

public class ApiErrorSerializer implements JsonDeserializer<ApiError> {

    public ApiErrorSerializer() {

    }

    @Override
    public ApiError deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
        throws JsonParseException {

        JsonObject object = je.getAsJsonObject();
        JsonElement errorMessageElement = object.get("error_message");
        String errorMessage = "";
        if (errorMessageElement != null) {
            errorMessage = errorMessageElement.getAsString();
        } else {
            JsonElement errorMessageElementV2 = object.get("ErrorMessage");
            if (errorMessageElementV2 != null) {
                errorMessage = errorMessageElementV2.getAsString();
            }
        }

        ApiError error = new ApiError();
        error.setMessage(errorMessage);

        return error;
    }
}
