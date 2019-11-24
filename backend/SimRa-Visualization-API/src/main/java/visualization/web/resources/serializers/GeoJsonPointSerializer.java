package visualization.web.resources.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.IOException;

public class GeoJsonPointSerializer extends StdSerializer<GeoJsonPoint> {

    public GeoJsonPointSerializer() {
        this(null);
    }

    private GeoJsonPointSerializer(Class<GeoJsonPoint> t) {
        super(t);
    }

    @Override
    public void serialize(GeoJsonPoint to_serialize, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("type", to_serialize.getType());
        jsonGenerator.writeArrayFieldStart("coordinates");
        jsonGenerator.writeNumber(to_serialize.getX());
        jsonGenerator.writeNumber(to_serialize.getY());
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
