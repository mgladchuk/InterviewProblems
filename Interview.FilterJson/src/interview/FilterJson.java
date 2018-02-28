package interview;

import interview.exception.WrongFilteringConditionsExceptions;

import javax.json.*;
import java.util.ArrayList;
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

    public static void main(String[] args) {

        // mess for test
        JsonObject model = Json.createObjectBuilder()
                .add("firstName", "Duke")
                .add("lastName", "Java")
                .add("age", 18)
                .add("streetAddress", "100 Internet Dr")
                .add("city", "JavaTown")
                .add("state", "JA")
                .add("postalCode", "12345")
                .add("phoneNumbers", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("type", "mobile")
                                .add("number", "111-111-1111"))
                        .add(Json.createObjectBuilder()
                                .add("type", "home")
                                .add("number", "222-222-2222")))
                .build();

        List<DataExtractionAttribute> spec = new ArrayList<>();
        DataExtractionAttribute attribute = new DataExtractionAttribute();
        DataExtractionAttribute attribute2 = new DataExtractionAttribute();
        attribute.setName("age");
        attribute2.setName("phoneNumbers");
        List<DataExtractionAttribute> sublist = new ArrayList<>();
        DataExtractionAttribute subAttr = new DataExtractionAttribute();
        subAttr.setName("number");
        sublist.add(subAttr);
        attribute2.setChildren(sublist);
        spec.add(attribute);
        spec.add(attribute2);
        JsonArrayBuilder arr = Json.createArrayBuilder();
        JsonArrayBuilder res = Json.createArrayBuilder();
        arr.add(model);
        extract(arr.build(), spec, res);
        System.out.println(res.build());
        System.out.println(model);
    }

    public static void extract(final JsonArray src, final List<DataExtractionAttribute> spec, final JsonArrayBuilder dest) {
        try {
            filterJson(src, dest, spec);
        } catch (WrongFilteringConditionsExceptions wrongFilteringConditionsExceptions) {
            System.err.println("Wrong DataExtractionAttribute, cause: " + wrongFilteringConditionsExceptions.getMessage());
        }
    }

    private static void filterJson(JsonArray src, JsonArrayBuilder dest, List<DataExtractionAttribute> attributes) throws WrongFilteringConditionsExceptions {
        for (DataExtractionAttribute attribute : attributes) {
            filterJsonNode(null, src, dest, attribute);
        }

    }

    private static void filterJsonNode(JsonArrayBuilder middle, JsonValue tree, JsonArrayBuilder dest, DataExtractionAttribute attribute) throws WrongFilteringConditionsExceptions {
        JsonValue.ValueType i = tree.getValueType();
        if (i == JsonValue.ValueType.OBJECT) {
            JsonObject object = (JsonObject) tree;
            String attrName = attribute.getName();
            JsonValue inside = object.get(attrName);
            if (inside != null) {
                List<DataExtractionAttribute> children = attribute.getChildren();
                if (children == null || children.isEmpty()) {
                    JsonObject filtered;
                    if (middle != null) {
                        JsonObject arrVal = Json.createObjectBuilder().add(attrName, inside).build();
                        middle.add(arrVal);

                    } else {
                        filtered = Json.createObjectBuilder().add(attrName, inside).build();
                        dest.add(filtered);
                    }
                } else {
                    filterAndAppendChildren(dest, attrName, inside, children);
                }
            }
        } else if (i == JsonValue.ValueType.ARRAY) {
            JsonArray array = (JsonArray) tree;
            for (JsonValue val : array)
                filterJsonNode(null, val, dest, attribute);
        }

    }

    private static void filterAndAppendChildren(JsonArrayBuilder dest, String attrName, JsonValue inside, List<DataExtractionAttribute> children) throws WrongFilteringConditionsExceptions {
        if (inside.getValueType() != JsonValue.ValueType.ARRAY) {
            throw new WrongFilteringConditionsExceptions(attrName + "is not an array");

        }
        JsonArray array = (JsonArray) inside;

        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (DataExtractionAttribute childAttr : children) {
            for (JsonValue jsonValue : array) {
                filterJsonNode(builder, jsonValue, dest, childAttr);
            }
        }
        JsonObject filtered = Json.createObjectBuilder().add(attrName, builder).build();
        dest.add(filtered);
    }


}
