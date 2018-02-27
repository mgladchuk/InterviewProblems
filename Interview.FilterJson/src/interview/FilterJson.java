package interview;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.List;

/**
 * Given JSON and a specification of attributes of interest, produce the corresponding JSON that only contains the specified
 * attributes.
 * <p>
 * <p>For instance, if the original JSON has employee records, the extraction specification might ask for just the first names
 * and the cities to be collected, while discarding everything else.</p>
 * <p>
 * <p>Note that the extracted attributes should appear in their original structure in the result.</p>
 */

public final class FilterJson {

    public static void extract(final JsonArray src, final List<DataExtractionAttribute> spec, final JsonArrayBuilder dest) {
        filterJson(src, dest, spec);
    }

    private static void filterJson(JsonArray src, JsonArrayBuilder dest, List<DataExtractionAttribute> attributes) {
        for (DataExtractionAttribute attribute : attributes) {
            filterJsonNode(src, dest, attribute);
        }

    }

    private static void filterJsonNode(JsonValue tree, JsonArrayBuilder dest, DataExtractionAttribute attribute) {
        JsonValue.ValueType i = tree.getValueType();
        if (i == JsonValue.ValueType.OBJECT) {
            JsonObject object = (JsonObject) tree;
            if (object.keySet().contains(attribute.getName())) {
                if (!attribute.getChildren().isEmpty()) {
                    dest.add(object);
                } else {
                    for (DataExtractionAttribute childAttr : attribute.getChildren()) {
                        filterJsonNode(object, dest, childAttr);
                    }
                }
            }

        } else if (i == JsonValue.ValueType.ARRAY) {
            JsonArray array = (JsonArray) tree;
            for (JsonValue val : array)
                filterJsonNode(val, dest, attribute);
        }

    }

}
