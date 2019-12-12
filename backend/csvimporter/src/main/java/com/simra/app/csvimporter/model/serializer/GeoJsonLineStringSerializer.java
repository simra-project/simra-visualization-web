package com.simra.app.csvimporter.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;

import java.io.IOException;

public class GeoJsonLineStringSerializer extends StdSerializer<GeoJsonLineString> {

    public GeoJsonLineStringSerializer() {
        this(null);
    }

    private GeoJsonLineStringSerializer(Class<GeoJsonLineString> t) {
        super(t);
    }

    @Override
    public void serialize(GeoJsonLineString to_serialize, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("type", to_serialize.getType());
        jsonGenerator.writeArrayFieldStart("coordinates");
        for (Point point : to_serialize.getCoordinates()) {
            jsonGenerator.writeArray(new double[]{point.getY(), point.getX()}, 0, 2);
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
